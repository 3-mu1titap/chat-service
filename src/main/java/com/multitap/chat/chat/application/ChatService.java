package com.multitap.chat.chat.application;

import com.multitap.chat.chat.domain.Chat;
import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import com.multitap.chat.chat.dto.out.ChatResponseDto;
import reactor.core.publisher.Flux;

public interface ChatService {

    void createChat(CreateChatRequestDto createChatRequestDto);
    void softDeleteChat(String id);
    Flux<ChatResponseDto> getChatByMentoringSessionUuid(String mentoringSessionUuid);
}
