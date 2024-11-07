package com.multitap.chat.chat.vo.out;

import com.multitap.chat.chat.domain.MessageType;
import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import com.multitap.chat.chat.dto.out.ChatResponseDto;
import com.multitap.chat.chat.vo.in.CreateChatRequestVo;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseVo {
    private String id;
    private String mentoringSessionUuid;
    private String memberUuid;
    private String message;
    private boolean isDeleted;
    private MessageType messageType;
    private String mediaUrl;
    private LocalDateTime createdAt;

    public static ChatResponseVo from(ChatResponseDto chatResponseDto) {
        return ChatResponseVo.builder()
                .id(chatResponseDto.getId())
                .mentoringSessionUuid(chatResponseDto.getMentoringSessionUuid())
                .memberUuid(chatResponseDto.getMemberUuid())
                .message(chatResponseDto.getMessage())
                .messageType(chatResponseDto.getMessageType())
                .mediaUrl(chatResponseDto.getMediaUrl())
                .build();
    }
}
