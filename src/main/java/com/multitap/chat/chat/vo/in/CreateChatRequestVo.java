package com.multitap.chat.chat.vo.in;

import com.multitap.chat.chat.domain.MessageType;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class CreateChatRequestVo {
    private String mentoringSessionUuid;
    private String memberUuid;
    private String message;
    private MessageType messageType;
    private String mediaUrl;
}
