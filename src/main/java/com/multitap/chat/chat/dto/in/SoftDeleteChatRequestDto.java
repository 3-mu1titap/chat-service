package com.multitap.chat.chat.dto.in;

import com.multitap.chat.chat.domain.Chat;
import com.multitap.chat.chat.domain.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class SoftDeleteChatRequestDto {

    private String id;
    private String mentoringSessionUuid;
    private String memberUuid;
    private String message;
    private boolean isDeleted;
    private MessageType messageType;
    private String mediaUrl;

    public Chat softDeleteChat() {
        return Chat.builder()
                .id(id)
                .mentoringSessionUuid(mentoringSessionUuid)
                .memberUuid(memberUuid)
                .message(message)
                .isDeleted(true)
                .messageType(messageType)
                .mediaUrl(mediaUrl)
                .build();
    }


    public static SoftDeleteChatRequestDto from(Chat chat) {
        return SoftDeleteChatRequestDto.builder()
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
