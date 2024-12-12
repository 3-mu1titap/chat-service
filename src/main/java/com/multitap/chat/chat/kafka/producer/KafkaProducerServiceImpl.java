package com.multitap.chat.chat.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendCreateChatMessageList(ChatMessageDto chatMessageDto) {
        try {
            kafkaTemplate.send("create-chat-topic", chatMessageDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void sendChatMember(ChatMemberDto chatMemberDto) {
        try {
            kafkaTemplate.send("chat-member-topic", chatMemberDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
