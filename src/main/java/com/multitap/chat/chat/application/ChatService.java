package com.multitap.chat.chat.application;

import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import com.multitap.chat.chat.dto.out.ChatResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatService {

    Mono<Void> createChat(CreateChatRequestDto createChatRequestDto);
    Mono<Void> softDeleteChat(String id, String memberUuid);
    Flux<ChatResponseDto> getChatByMentoringSessionUuid(String mentoringSessionUuid);
    List<ChatResponseDto> getChatsByMentoringSessionUuid(String mentoringSessionUuid, LocalDateTime cursorTimestamp, int limit, int pageNumber);
    Flux<ChatResponseDto> getRealTimeChatByMentoringSessionUuid(String mentoringSessionUuid);
    Mono<Void> handleUserJoin(String memberUuid, String nickName, String mentoringSessionUuid);
}
