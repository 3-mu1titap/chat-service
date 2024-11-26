package com.multitap.chat.chat.presentation;

import com.multitap.chat.chat.application.ChatService;
import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import com.multitap.chat.chat.vo.in.CreateChatRequestVo;
import com.multitap.chat.chat.vo.out.ChatResponseVo;
import com.multitap.chat.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/chat")
public class ChatReactiveController {

    private final ChatService chatService;

    @Operation(summary = "채팅 생성(채팅 보내기)", description = "채팅을 보냅니다.(채팅 데이터를 생성합니다.)")
    @PostMapping
    public Mono<Void> createChat(
            @RequestHeader ("userUuid") String memberUuid,
            @RequestBody CreateChatRequestVo createChatRequestVo) {
        log.info("createChatRequestVo: {}", createChatRequestVo);
        CreateChatRequestDto createChatRequestDto = CreateChatRequestDto.from(createChatRequestVo, memberUuid);
        return chatService.createChat(createChatRequestDto);
    }

    @Operation(summary = "채팅 삭제", description = "채팅을 삭제합니다.(softDelete)")
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

    @Operation(summary = "채팅 실시간 조회", description = "채팅방에서 실시간으로 채팅을 조회합니다.")
    @GetMapping(value = "/real-time/{mentoringSessionUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatResponseVo> getRealTimeChatMessageByMentoringSessionUuid(@PathVariable String mentoringSessionUuid) {
        return chatService.getRealTimeChatByMentoringSessionUuid(mentoringSessionUuid)
                .map(ChatResponseVo::from);
    }

    @Operation(summary = "채팅방 입장", description = "채팅방에 입징합니다.")
    @PostMapping("/join/{mentoringSessionUuid}")
    public Mono<Void> userJoin(
            @RequestHeader ("userUuid") String memberUuid,
            @RequestParam String nickName,
            @PathVariable String mentoringSessionUuid) {
        return chatService.handleUserJoin(memberUuid, nickName, mentoringSessionUuid);
    }

    @Operation(summary = "채팅방 퇴장", description = "채팅방에서 퇴징합니다.")
    @PostMapping("/leave/{mentoringSessionUuid}")
    public Mono<Void> userLeave(
            @RequestHeader ("userUuid") String memberUuid,
            @RequestParam String nickName,
            @PathVariable String mentoringSessionUuid) {
        return chatService.handleUserLeave(memberUuid, nickName, mentoringSessionUuid);
    }

    @Operation(summary = "채팅방 온라인 유지 체크 하트비트", description = "하트비트를 체크합니다.")
    @PostMapping("/heartbeat/{mentoringSessionUuid}")
    public Mono<Void> handleHeartbeat(
            @RequestHeader("userUuid") String memberUuid,
            @RequestParam String nickName,
            @PathVariable String mentoringSessionUuid) {
        return chatService.updateHeartbeat(memberUuid, nickName, mentoringSessionUuid);
    }

    @Operation(summary = "채팅방 온라인 유저 조회", description = "채팅방에 입징해있는 유저를 조회합니다.")
    @GetMapping("/getParticipants/{mentoringSessionUuid}")
    public Mono<BaseResponse<List<String>>> getUsersInRoom(@PathVariable String mentoringSessionUuid) {
        return chatService.getUsersInMentoringSession(mentoringSessionUuid)
                .map(users -> new BaseResponse<>(users))
                .defaultIfEmpty(new BaseResponse<>(List.of()));
    }
}
