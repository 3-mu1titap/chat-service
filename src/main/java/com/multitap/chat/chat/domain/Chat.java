package com.multitap.chat.chat.domain;

import com.multitap.chat.common.response.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@NoArgsConstructor
@Document(collection = "chat")
@ToString
public class Chat extends BaseEntity {

    @Id
    private String id;
    @NotNull
    private String mentoringSessionUuid;
    @NotNull
    private String memberUuid;
    @NotNull
    private String message;
    @NotNull
    private boolean isDeleted;
    @NotNull
    private MessageType messageType;
    private String mediaUrl;

    @Builder
    public Chat(String id,
                String mentoringSessionUuid,
                String memberUuid,
                String message,
                boolean isDeleted,
                MessageType messageType,
                String mediaUrl) {
        this.id = id;
        this.mentoringSessionUuid = mentoringSessionUuid;
        this.memberUuid = memberUuid;
        this.message = message;
        this.isDeleted = isDeleted;
        this.messageType = messageType;
        this.mediaUrl = mediaUrl;
    }
}
