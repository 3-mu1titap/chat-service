package com.multitap.chat.chat.dto.out;

import com.multitap.chat.chat.domain.Chat;
import com.multitap.chat.chat.domain.MessageType;
import com.multitap.chat.chat.dto.in.SoftDeleteChatRequestDto;
import com.multitap.chat.chat.vo.out.ChatResponseVo;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatResponseDto {

    private String id;
    private String mentoringSessionUuid;
    private String memberUuid;
    private String message;
    private boolean isDeleted;
    private MessageType messageType;
    private String mediaUrl;
    private LocalDateTime createdAt;

    public ChatResponseVo toResponseVo() {
        return ChatResponseVo.builder()
                .id(id)
                .mentoringSessionUuid(mentoringSessionUuid)
                .memberUuid(memberUuid)
                .message(message)
                .isDeleted(isDeleted)
                .messageType(messageType)
                .mediaUrl(mediaUrl)
                .createdAt(createdAt)
                .build();
    }

    public static ChatResponseDto from(Chat chat) {
        return ChatResponseDto.builder()
                .id(chat.getId())
                .mentoringSessionUuid(chat.getMentoringSessionUuid())
                .memberUuid(chat.getMemberUuid())
                .message(chat.getMessage())
                .isDeleted(chat.isDeleted())
                .messageType(chat.getMessageType())
                .mediaUrl(chat.getMediaUrl())
                .build();
    }
}
