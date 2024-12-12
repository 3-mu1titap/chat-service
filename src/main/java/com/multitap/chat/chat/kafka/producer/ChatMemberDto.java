package com.multitap.chat.chat.kafka.producer;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class ChatMemberDto {

    private String mentoringSessionUuid;
    private String memberUuid;

    @Builder
    public ChatMemberDto(String mentoringSessionUuid, String memberUuid) {
        this.mentoringSessionUuid = mentoringSessionUuid;
        this.memberUuid = memberUuid;
    }

    public static ChatMemberDto of(String mentoringSessionUuid, String memberUuid) {
        return ChatMemberDto.builder()
                .mentoringSessionUuid(mentoringSessionUuid)
                .memberUuid(memberUuid)
                .build();
    }
}
