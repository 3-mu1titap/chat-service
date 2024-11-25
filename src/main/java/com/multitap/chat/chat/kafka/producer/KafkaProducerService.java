package com.multitap.chat.chat.kafka.producer;

public interface KafkaProducerService {

    //채팅 리스트 조회 관련 메시지 전송
    void sendCreateChatMessageList(ChatMessageDto chatMessageDto);
}
