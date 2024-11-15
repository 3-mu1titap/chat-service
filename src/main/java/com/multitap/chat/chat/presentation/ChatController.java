package com.multitap.chat.chat.presentation;

import com.multitap.chat.chat.application.ChatService;
import com.multitap.chat.chat.dto.out.ChatResponseDto;
import com.multitap.chat.chat.vo.out.ChatResponseVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/pagingSearch/{mentoringSessionUuid}")
    public List<ChatResponseVo> getChatsByMentoringSessionUuid(
            @PathVariable String mentoringSessionUuid,
            @RequestParam(required = false) LocalDateTime cursorTimestamp, // 마지막 메시지의 createdAt
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "0") int pageNumber) {
        log.info("getChatsByMentoringSessionUuid: {}", mentoringSessionUuid);
        return chatService.getChatsByMentoringSessionUuid(mentoringSessionUuid, cursorTimestamp, limit, pageNumber)
                .stream()
                .map(ChatResponseDto::toResponseVo)
                .toList();
    }
}
