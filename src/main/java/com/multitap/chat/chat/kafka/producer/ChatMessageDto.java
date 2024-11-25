package com.multitap.chat.chat.kafka.producer;

import com.multitap.chat.chat.domain.Chat;
import com.multitap.chat.chat.dto.in.CreateChatRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@NoArgsConstructor
public class ChatMessageDto {

    private String mentoringSessionUuid;
    private String memberUuid;
    private LocalDateTime lastMessageTime;
    private String lastMessage;

    @Builder
    public ChatMessageDto(String mentoringSessionUuid, String memberUuid, LocalDateTime lastMessageTime, String lastMessage) {
        this.mentoringSessionUuid = mentoringSessionUuid;
        this.memberUuid = memberUuid;
        this.lastMessageTime = lastMessageTime;
        this.lastMessage = lastMessage;
    }

    public static ChatMessageDto from(Chat chat) {
        return ChatMessageDto.builder()
                .mentoringSessionUuid(chat.getMentoringSessionUuid())
                .memberUuid(chat.getMemberUuid())
                .lastMessageTime(chat.getCreatedAt())
                .lastMessage(chat.getMessage())
                .build();
    }
}
