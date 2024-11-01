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

    public Chat toChat() {
        return Chat.builder()
                .id(id)
                .mentoringSessionUuid(mentoringSessionUuid)
                .memberUuid(memberUuid)
                .message(message)
                .isDeleted(isDeleted)
                .messageType(messageType)
                .mediaUrl(mediaUrl)
                .build();
    }
}
