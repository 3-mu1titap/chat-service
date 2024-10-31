package com.multitap.chat.chat.dto.in;

import com.multitap.chat.chat.domain.Chat;
import com.multitap.chat.chat.domain.MessageType;
import com.multitap.chat.chat.vo.in.CreateChatRequestVo;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class CreateChatRequestDto {

    private String mentoringSessionUuid;
    private String memberUuid;
    private String message;
    private boolean isDeleted;
    private MessageType messageType;
    private String mediaUrl;

    public Chat toChat() {
        return Chat.builder()
                .mentoringSessionUuid(mentoringSessionUuid)
                .memberUuid(memberUuid)
                .message(message)
                .isDeleted(isDeleted)
                .messageType(messageType)
                .mediaUrl(mediaUrl)
                .build();
    }

    public static CreateChatRequestDto from(CreateChatRequestVo createChatRequestVo) {
        return CreateChatRequestDto.builder()
                .mentoringSessionUuid(createChatRequestVo.getMentoringSessionUuid())
                .memberUuid(createChatRequestVo.getMemberUuid())
                .message(createChatRequestVo.getMessage())
                .messageType(createChatRequestVo.getMessageType())
                .mediaUrl(createChatRequestVo.getMediaUrl())
                .build();
    }

    @Builder
    public CreateChatRequestDto(String mentoringSessionUuid, String memberUuid, String message, MessageType messageType, String mediaUrl) {
        this.mentoringSessionUuid = mentoringSessionUuid;
        this.memberUuid = memberUuid;
        this.message = message;
        this.messageType = messageType;
        this.mediaUrl = mediaUrl;
        this.isDeleted = false;

    }
}
