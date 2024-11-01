package com.multitap.chat.chat.application;

import com.multitap.chat.chat.domain.Chat;
import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import com.multitap.chat.chat.dto.in.SoftDeleteChatRequestDto;
import com.multitap.chat.chat.infrastructure.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    public void createChat(CreateChatRequestDto createChatRequestDto) {
        log.info("Create chat request: {}", createChatRequestDto);
        chatRepository.save(createChatRequestDto.toChat()).subscribe();
    }

    @Override
    public void softDeleteChat(String id) {
        log.info("Delete chat: {}", id);
        chatRepository.findById(id).flatMap(chat -> chatRepository.save(
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
}

