package com.multitap.chat.chat.application;

import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import com.multitap.chat.chat.dto.in.SoftDeleteChatRequestDto;
import com.multitap.chat.chat.dto.out.ChatResponseDto;
import com.multitap.chat.chat.infrastructure.ChatRepository;
import com.multitap.chat.chat.infrastructure.ReactiveChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final ReactiveChatRepository reactiveChatRepository;
    private final ChatRepository chatRepository;

    @Override
    public void createChat(CreateChatRequestDto createChatRequestDto) {
        log.info("Create chat request: {}", createChatRequestDto);
        reactiveChatRepository.save(createChatRequestDto.toChat()).subscribe();
    }

    @Override
    public void softDeleteChat(String id) {
        log.info("Delete chat: {}", id);
        reactiveChatRepository.findById(id).flatMap(chat -> reactiveChatRepository.save(
                SoftDeleteChatRequestDto.builder()
                        .id(chat.getId())
                        .mentoringSessionUuid(chat.getMentoringSessionUuid())
                        .memberUuid(chat.getMemberUuid())
                        .message(chat.getMessage())
                        .messageType(chat.getMessageType())
                        .mediaUrl(chat.getMediaUrl())
                        .isDeleted(true)
                        .build().toChat())).subscribe();
    }

    @Override
    public Flux<ChatResponseDto> getChatByMentoringSessionUuid(String mentoringSessionUuid) {
        log.info("Get chat by mentoring session uuid: {}", mentoringSessionUuid);
        return reactiveChatRepository.findChatByMentoringSessionUuid(mentoringSessionUuid)
                .map(chat -> ChatResponseDto.builder()
                        .id(chat.getId())
                        .mentoringSessionUuid(chat.getMentoringSessionUuid())
                        .memberUuid(chat.getMemberUuid())
                        .message(chat.getMessage())
                        .messageType(chat.getMessageType())
                        .mediaUrl(chat.getMediaUrl())
                        .isDeleted(chat.isDeleted())
                        .createdAt(chat.getCreatedAt())
                        .build());
    }

    @Override
    public List<ChatResponseDto> getChatsByMentoringSessionUuid(String mentoringSessionUuid, LocalDateTime cursorTimestamp, int limit, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, limit, Sort.by(Sort.Direction.DESC, "createdAt"));

        LocalDateTime date = (cursorTimestamp != null) ? cursorTimestamp : LocalDateTime.now();

        return chatRepository.findByMentoringSessionUuidAndCreatedAtLessThan(mentoringSessionUuid, date, pageable)
                .stream()
                .map(chat -> ChatResponseDto.builder()
                        .id(chat.getId())
                        .mentoringSessionUuid(chat.getMentoringSessionUuid())
                        .memberUuid(chat.getMemberUuid())
                        .message(chat.getMessage())
                        .messageType(chat.getMessageType())
                        .mediaUrl(chat.getMediaUrl())
                        .isDeleted(chat.isDeleted())
                        .createdAt(chat.getCreatedAt())
                        .build())
                .toList();
    }
}

