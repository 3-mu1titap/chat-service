package com.multitap.chat.chat.application;

import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import com.multitap.chat.chat.dto.out.ChatResponseDto;
import org.springframework.data.domain.Page;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ChatService {

    Mono<Void> createChat(CreateChatRequestDto createChatRequestDto);
    Mono<Void> softDeleteChat(String id, String memberUuid);
    Flux<ChatResponseDto> getChatByMentoringSessionUuid(String mentoringSessionUuid);
    Page<ChatResponseDto> getChatsByMentoringSessionUuid(String mentoringSessionUuid, LocalDateTime cursorTimestamp, int limit, int pageNumber);
//    List<ChatResponseDto> getChatsByMentoringSessionUuid(String mentoringSessionUuid, LocalDateTime cursorTimestamp, int limit, int pageNumber);
    Flux<ChatResponseDto> getRealTimeChatByMentoringSessionUuid(String mentoringSessionUuid);
    Mono<Void> handleUserJoin(String memberUuid, String nickName, String mentoringSessionUuid);
    Mono<Void> handleUserLeave(String memberUuid, String nickName, String mentoringSessionUuid);
    Mono<Void> updateHeartbeat(String memberUuid, String nickName, String mentoringSessionUuid);
    Mono<List<String>> getUsersInMentoringSession(String mentoringSessionUuid);
}
