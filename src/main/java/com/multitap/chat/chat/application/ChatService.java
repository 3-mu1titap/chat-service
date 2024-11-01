package com.multitap.chat.chat.application;

import com.multitap.chat.chat.domain.Chat;
import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import reactor.core.publisher.Mono;

public interface ChatService {

    void createChat(CreateChatRequestDto createChatRequestDto);
    void softDeleteChat(String id);
}
