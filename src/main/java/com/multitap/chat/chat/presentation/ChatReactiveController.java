package com.multitap.chat.chat.presentation;

import com.multitap.chat.chat.application.ChatService;
import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import com.multitap.chat.chat.vo.in.CreateChatRequestVo;
import com.multitap.chat.chat.vo.out.ChatResponseVo;
import com.multitap.chat.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatReactiveController {

    private final ChatService chatService;

    @PostMapping
    public Mono<Void> createChat(
            @RequestHeader ("userUuid") String memberUuid,
            @RequestBody CreateChatRequestVo createChatRequestVo) {
        log.info("createChatRequestVo: {}", createChatRequestVo);
        CreateChatRequestDto createChatRequestDto = CreateChatRequestDto.from(createChatRequestVo, memberUuid);
        return chatService.createChat(createChatRequestDto);
    }

    @PutMapping("/softDelete/{id}")
    public Mono<Void> softDeleteChat(
            @RequestHeader ("userUuid") String memberUuid,
            @PathVariable String id) {
        log.info("deleteChat: {}", id);
        return chatService.softDeleteChat(id, memberUuid);
    }

//    @GetMapping(value = "/{mentoringSessionUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<ChatResponseVo> getChatByMentoringSessionUuid(@PathVariable String mentoringSessionUuid) {
//        log.info("getChatByMentoringSessionUuid: {}", mentoringSessionUuid);
//        return chatService.getChatByMentoringSessionUuid(mentoringSessionUuid).subscribeOn(Schedulers.boundedElastic())
//                .map(ChatResponseVo::from);
//    }

    @GetMapping(value = "/real-time/{mentoringSessionUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> getRealTimeChatMessageByMentoringSessionUuid(@PathVariable String mentoringSessionUuid) {
        return chatService.getRealTimeChatByMentoringSessionUuid(mentoringSessionUuid)
                .map(ChatResponseVo::from);
    }

    @PostMapping("/join/{mentoringSessionUuid}")
    public Mono<Void> userJoin(
            @RequestHeader ("userUuid") String memberUuid,
            @RequestParam String nickName,
            @PathVariable String mentoringSessionUuid) {
        return chatService.handleUserJoin(memberUuid, nickName, mentoringSessionUuid);
    }

    @PostMapping("/leave/{mentoringSessionUuid}")
    public Mono<Void> userLeave(
            @RequestHeader ("userUuid") String memberUuid,
            @RequestParam String nickName,
            @PathVariable String mentoringSessionUuid) {
        return chatService.handleUserLeave(memberUuid, nickName, mentoringSessionUuid);
    }

    @PostMapping("/heartbeat/{mentoringSessionUuid}")
    public Mono<Void> handleHeartbeat(
            @RequestHeader("userUuid") String memberUuid,
            @RequestParam String nickName,
            @PathVariable String mentoringSessionUuid) {
        return chatService.updateHeartbeat(memberUuid, nickName, mentoringSessionUuid);
    }
}
