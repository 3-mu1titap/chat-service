package com.multitap.chat.chat.presentation;

import com.multitap.chat.chat.application.ChatService;
import com.multitap.chat.chat.dto.out.ChatResponseDto;
import com.multitap.chat.chat.vo.out.ChatResponseVo;
import com.multitap.chat.chat.vo.out.PagedResponseVo;
import com.multitap.chat.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "채팅 내역 조회", description = "특정 유저의 특정 채팅방의 채팅 내역을 무한스크롤로 조회합니다.")
    @GetMapping("/pagingSearch/{mentoringSessionUuid}")
    public BaseResponse<PagedResponseVo> getChatsByMentoringSessionUuid(
            @PathVariable String mentoringSessionUuid,
            @RequestParam(required = false) LocalDateTime cursorTimestamp, // 마지막 메시지의 createdAt
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int pageNumber) {
        log.info("getChatsByMentoringSessionUuid: {}", mentoringSessionUuid);

        // 서비스 호출
        Page<ChatResponseVo> chats = chatService.getChatsByMentoringSessionUuid(mentoringSessionUuid, cursorTimestamp, limit, pageNumber)
                .map(ChatResponseVo::from);

        // 데이터 추출
        boolean hasNext = chats.hasNext();

        // PagedResponseVo 생성
        PagedResponseVo pagedResponseVo = PagedResponseVo.of(chats, hasNext);

        // BaseResponse에 담아 반환
        return new BaseResponse<>(pagedResponseVo);
    }

//    @GetMapping("/pagingSearch/{mentoringSessionUuid}")
//    public List<ChatResponseVo> getChatsByMentoringSessionUuid(
//            @PathVariable String mentoringSessionUuid,
//            @RequestParam(required = false) LocalDateTime cursorTimestamp, // 마지막 메시지의 createdAt
//            @RequestParam(defaultValue = "10") int limit,
//            @RequestParam(defaultValue = "0") int pageNumber) {
//        log.info("getChatsByMentoringSessionUuid: {}", mentoringSessionUuid);
//        return chatService.getChatsByMentoringSessionUuid(mentoringSessionUuid, cursorTimestamp, limit, pageNumber)
//                .stream()
//                .map(ChatResponseDto::toResponseVo)
//                .toList();
//    }
}
