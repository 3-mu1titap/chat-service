package com.multitap.chat.chat.application;

import com.multitap.chat.chat.domain.Chat;
import com.multitap.chat.chat.domain.MessageType;
import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import com.multitap.chat.chat.dto.in.SoftDeleteChatRequestDto;
import com.multitap.chat.chat.dto.out.ChatResponseDto;
import com.multitap.chat.chat.infrastructure.ChatRepository;
import com.multitap.chat.chat.infrastructure.ReactiveChatRepository;
import com.multitap.chat.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.multitap.chat.common.response.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final ReactiveChatRepository reactiveChatRepository;
    private final ChatRepository chatRepository;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    @Override
    public void createChat(CreateChatRequestDto createChatRequestDto) {
        log.info("Create chat request: {}", createChatRequestDto);
        reactiveChatRepository.save(createChatRequestDto.toChat()).subscribe();
    }

    @Override
    public void softDeleteChat(String id, String memberUuid) {
        log.info("Delete chat: {}", id);
        reactiveChatRepository.findChatByIdAndMemberUuid(id, memberUuid)
                .switchIfEmpty(Mono.error(new BaseException(NO_DELETE_CHAT_AUTHORITY)))
                .flatMap(chat -> {
                    chat.softDelete(true);
                    return reactiveChatRepository.save(chat);
                }).subscribe();
    }

    @Override
    public Flux<ChatResponseDto> getChatByMentoringSessionUuid(String mentoringSessionUuid) {
        log.info("Get chat by mentoring session uuid: {}", mentoringSessionUuid);
        return reactiveChatRepository.findChatByMentoringSessionUuid(mentoringSessionUuid)
                .map(ChatResponseDto::from);
    }

    @Override
    public List<ChatResponseDto> getChatsByMentoringSessionUuid(String mentoringSessionUuid, LocalDateTime cursorTimestamp, int limit, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        LocalDateTime date = (cursorTimestamp != null) ? cursorTimestamp : LocalDateTime.now();

        return chatRepository.findByMentoringSessionUuidAndCreatedAtLessThan(mentoringSessionUuid, date, pageable)
                .stream()
                .map(ChatResponseDto::from)
                .toList();
    }

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

                    Date createdAtDate = document.getDate("createdAt");
                    LocalDateTime createdAt = createdAtDate != null
                            ? LocalDateTime.ofInstant(createdAtDate.toInstant(), ZoneId.systemDefault())
                            : null;

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

}

