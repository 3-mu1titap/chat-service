package com.multitap.chat.chat.vo.out;

import com.multitap.chat.chat.domain.MessageType;
import lombok.*;

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
}
