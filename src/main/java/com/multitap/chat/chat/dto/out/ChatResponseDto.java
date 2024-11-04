package com.multitap.chat.chat.dto.out;

import com.multitap.chat.chat.domain.MessageType;
import com.multitap.chat.chat.vo.out.ChatResponseVo;
import lombok.Builder;
import lombok.Getter;

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

    public ChatResponseVo toResponseVo() {
        return ChatResponseVo.builder()
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
