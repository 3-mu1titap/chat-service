package com.multitap.chat.chat.application;

import com.multitap.chat.chat.domain.Chat;
import com.multitap.chat.chat.domain.MessageType;
import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import com.multitap.chat.chat.dto.out.ChatResponseDto;
import com.multitap.chat.chat.infrastructure.ChatRepository;
import com.multitap.chat.chat.infrastructure.ReactiveChatRepository;
import com.multitap.chat.chat.kafka.producer.ChatMessageDto;
import com.multitap.chat.chat.kafka.producer.KafkaProducerService;
import com.multitap.chat.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.bson.Document;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.multitap.chat.chat.domain.MessageType.NOTICE;
import static com.multitap.chat.chat.domain.MessageType.TEXT;
import static com.multitap.chat.common.response.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final ReactiveChatRepository reactiveChatRepository;
    private final ChatRepository chatRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final KafkaProducerService kafkaProducerService;

    private final RedisTemplate<String, String> redisTemplate;
    private static final Duration timeoutDuration = Duration.ofSeconds(60); // 60초 타임아웃

    @Override
    @Transactional
    public Mono<Void> createChat(CreateChatRequestDto createChatRequestDto) {
        log.info("Create chat request: {}", createChatRequestDto);

//        ChatMessageDto chatMessageDto = ChatMessageDto.from(createChatRequestDto.toChat());
//
//        log.info("Create chat chatMessageDto: {}", chatMessageDto.toString());
//
//        kafkaProducerService.sendCreateChatMessageList(chatMessageDto);
//
//        return reactiveChatRepository.save(createChatRequestDto.toChat()).then();

        return reactiveChatRepository.save(createChatRequestDto.toChat())
                .flatMap(savedChat -> {
                    // ChatMessageDto 생성
                    ChatMessageDto chatMessageDto = ChatMessageDto.from(savedChat);
                    log.info("Create chat chatMessageDto: {}", chatMessageDto.toString());

                    // Kafka에 메시지 전송
                    if (createChatRequestDto.getMessageType() == TEXT) {
                        kafkaProducerService.sendCreateChatMessageList(chatMessageDto);
                    }

                    // void 반환
                    return Mono.empty();
                });
    }

    @Override
    @Transactional
    public Mono<Void> softDeleteChat(String id, String memberUuid) {
        log.info("Delete chat: {}", id);
        return reactiveChatRepository.findChatByIdAndMemberUuid(id, memberUuid)
                .switchIfEmpty(Mono.error(new BaseException(NO_DELETE_CHAT_AUTHORITY)))
                .flatMap(chat -> {
                    chat.softDelete(true);
                    return reactiveChatRepository.save(chat);
                }).then();
    }

    @Override
    public Flux<ChatResponseDto> getChatByMentoringSessionUuid(String mentoringSessionUuid) {
        log.info("Get chat by mentoring session uuid: {}", mentoringSessionUuid);
        return reactiveChatRepository.findChatByMentoringSessionUuid(mentoringSessionUuid)
                .map(ChatResponseDto::from);
    }

    @Override
    public Page<ChatResponseDto> getChatsByMentoringSessionUuid(String mentoringSessionUuid, LocalDateTime cursorTimestamp, int limit, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        LocalDateTime date = (cursorTimestamp != null) ? cursorTimestamp : LocalDateTime.now();

        return chatRepository.findByMentoringSessionUuidAndCreatedAtLessThan(mentoringSessionUuid, date, pageable)
                .map(ChatResponseDto::from);

    }

//    @Override
//    public List<ChatResponseDto> getChatsByMentoringSessionUuid(String mentoringSessionUuid, LocalDateTime cursorTimestamp, int limit, int pageNumber) {
//        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
//
//        LocalDateTime date = (cursorTimestamp != null) ? cursorTimestamp : LocalDateTime.now();
//
//        return chatRepository.findByMentoringSessionUuidAndCreatedAtLessThan(mentoringSessionUuid, date, pageable)
//                .stream()
//                .map(ChatResponseDto::from)
//                .toList();
//    }

    @Override
    public Flux<ChatResponseDto> getRealTimeChatByMentoringSessionUuid(String mentoringSessionUuid) {
        // ChangeStreamOptions 설정
        ChangeStreamOptions options = ChangeStreamOptions.builder()
                .filter(Aggregation.newAggregation( Aggregation.match(Criteria.where("fullDocument.mentoringSessionUuid").is(mentoringSessionUuid)))) // 특정 채팅방의 메시지만 필터링
                .build();
        log.info("option = {}", options);

        return reactiveMongoTemplate.changeStream("chat", options, Document.class)
                .doOnNext(test -> {
                    log.info("test {}", test);
                })
                .map(ChangeStreamEvent::getBody)
                .map(document -> {
                    // Enum 변환
                    String messageTypeString = document.getString("messageType");
                    MessageType messageType = null;
                    try {
                        messageType = MessageType.valueOf(messageTypeString);  // Enum으로 변환
                    } catch (IllegalArgumentException e) {
                        log.error("Invalid messageType value: {}", messageTypeString);
                        messageType = MessageType.TEXT;  // 기본값 설정 (예: DEFAULT)
                    }

                    // isDeleted가 true인 경우 "삭제된 메시지입니다."로 메시지 내용 설정
                    String message = document.getBoolean("isDeleted") ? "삭제된 메시지입니다." : document.getString("message");

                    Date createdAtDate = document.get("created_at", Date.class);
                    LocalDateTime createdAt = createdAtDate != null
                            ? LocalDateTime.ofInstant(createdAtDate.toInstant(), ZoneId.systemDefault())
                            : null;
                    log.info("createdAt = {}", createdAt);

                    return ChatResponseDto.builder()
                            .id(document.get("_id", ObjectId.class).toString())
                            .mentoringSessionUuid(document.getString("mentoringSessionUuid"))
                            .memberUuid(document.getString("memberUuid"))
                            .messageType(messageType)
                            .message(message)
                            .isDeleted(document.getBoolean("isDeleted"))
                            .mediaUrl(document.getString("mediaUrl"))
                            .createdAt(createdAt)
                            .build();
                });
    }

    @Override
    @Transactional
    public Mono<Void> handleUserJoin(String memberUuid, String nickName, String mentoringSessionUuid) {
        String message = nickName + "님이 채팅방에 입장하셨습니다.";

        // Redis에 사용자 정보 저장
        String key = generateUserKey(memberUuid, nickName, mentoringSessionUuid);
        String currentTime = LocalDateTime.now().toString();
        redisTemplate.opsForValue().set(key, currentTime, 600000, TimeUnit.SECONDS); // 60초 TTL 설정

        return reactiveChatRepository.save(CreateChatRequestDto
                        .of(mentoringSessionUuid, memberUuid, message, NOTICE, null).toChat())
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> handleUserLeave(String memberUuid, String nickName, String mentoringSessionUuid) {
        String message = nickName + "님이 채팅방에서 퇴장하셨습니다.";

        String key = generateUserKey(memberUuid, nickName, mentoringSessionUuid);
        redisTemplate.delete(key); // Redis에서 제거
        log.info("key 삭제");

        return reactiveChatRepository.save(CreateChatRequestDto
                        .of(mentoringSessionUuid, memberUuid, message, NOTICE, null).toChat())
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> updateHeartbeat(String memberUuid, String nickName, String mentoringSessionUuid) {
        String key = generateUserKey(memberUuid, nickName, mentoringSessionUuid);
        String now = LocalDateTime.now().toString(); // 현재 시간을 문자열로 저장
        redisTemplate.opsForValue().set(key, now, 60, TimeUnit.SECONDS); // Heartbeat 갱신 시 TTL 설정
        return Mono.empty();
    }

    @Override
    public Mono<List<String>> getUsersInMentoringSession(String mentoringSessionUuid) {
        // 레디스에서 해당 멘토링 세션에 참가 중인 유저의 키들을 조회
        Set<String> keys = redisTemplate.keys("chat:" + mentoringSessionUuid + ":*"); // 특정 mentoringSessionUuid에 해당하는 유저들만 조회

        // 키들이 존재한다면, 유저 리스트를 반환
        if (keys != null && !keys.isEmpty()) {
            List<String> users = keys.stream()
                    .map(key -> key.split(":")[2])  // key를 "chat:mentoringSessionUuid:memberUuid:nickName" 형식으로 사용
                    .collect(Collectors.toList());

            return Mono.just(users);
        } else {
            return Mono.empty();  // 유저가 없으면 빈 리스트 반환
        }
    }

    private String generateUserKey(String memberUuid, String nickName, String mentoringSessionUuid) {
        log.info("memberUuid : {}", memberUuid );
        log.info("nickName : {}", nickName );
        log.info("mentoringSessionUuid : {}", mentoringSessionUuid );

        log.info("uuid - chat:" + mentoringSessionUuid + ":" + memberUuid + ":" + nickName);
        return "chat:" + mentoringSessionUuid + ":" + memberUuid + ":" + nickName;
    }

    @Scheduled(fixedRate = 30000) // 30초마다 실행
    @Transactional
    public void checkHeartbeatTimeout() {
        Set<String> keys = redisTemplate.keys("chat:*"); // Redis에 저장된 모든 키 조회
//        log.info("checkHeartbeatTimeout : {} ", "안녕하세요.");
        if (keys == null || keys.isEmpty()) return;
        LocalDateTime now = LocalDateTime.now();
        log.info(keys.toString());

        for (String key : keys) {
            String lastHeartbeatStr = redisTemplate.opsForValue().get(key);
            if (lastHeartbeatStr == null) {
                continue;
            }

            LocalDateTime lastHeartbeat = LocalDateTime.parse(lastHeartbeatStr);

            if (Duration.between(lastHeartbeat, now).compareTo(timeoutDuration) > 0) {
                handleUserTimeout(key);
            }
        }
    }

    private void handleUserTimeout(String key) {
        String[] parts = key.split(":"); // 키를 "chat:mentoringSessionUuid:memberUuid:nickName"로 가정
        String mentoringSessionUuid = parts[1];
        String memberUuid = parts[2];
        String nickName = parts[3];
        log.info("유저 타임아웃 발생: {}", memberUuid);

        // 퇴장 처리 로직 호출
         handleUserLeave(memberUuid, nickName, mentoringSessionUuid).subscribe();
    }

}

