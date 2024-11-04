package com.multitap.chat.chat.presentation;

import com.multitap.chat.chat.application.ChatService;
import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import com.multitap.chat.chat.vo.in.CreateChatRequestVo;
import com.multitap.chat.chat.vo.out.ChatResponseVo;
import com.multitap.chat.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatReactiveController {

    private final ChatService chatService;

    @PostMapping
    public BaseResponse<Void> createChat(@RequestBody CreateChatRequestVo createChatRequestVo) {
        log.info("createChatRequestVo: {}", createChatRequestVo);
        CreateChatRequestDto createChatRequestDto = CreateChatRequestDto.from(createChatRequestVo);
        chatService.createChat(createChatRequestDto);
        return new BaseResponse<>();
    }

    @PutMapping("/{id}/delete")
    public BaseResponse<Void> deleteChat(@PathVariable String id) {
        log.info("deleteChat: {}", id);
        chatService.softDeleteChat(id);
        return new BaseResponse<>();
    }

    @GetMapping(value = "/{mentoringSessionUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> getChatByRoomId(@PathVariable String mentoringSessionUuid) {
        log.info("getChatByMentoringSessionUuid: {}", mentoringSessionUuid);
        return chatService.getChatByMentoringSessionUuid(mentoringSessionUuid).subscribeOn(Schedulers.boundedElastic())
                .map(chatResponseDto ->
                    ChatResponseVo.builder()
                            .id(chatResponseDto.getId())
                            .mentoringSessionUuid(chatResponseDto.getMentoringSessionUuid())
                            .memberUuid(chatResponseDto.getMemberUuid())
                            .message(chatResponseDto.getMessage())
                            .messageType(chatResponseDto.getMessageType())
                            .mediaUrl(chatResponseDto.getMediaUrl())
                            .isDeleted(chatResponseDto.isDeleted())
                            .build()
                );
    }
}
