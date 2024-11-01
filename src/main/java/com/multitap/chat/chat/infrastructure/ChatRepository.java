package com.multitap.chat.chat.infrastructure;

import com.multitap.chat.chat.domain.Chat;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {

    @Tailable
    @Query("{ 'mentoringSessionUuid' : ?0 }")
    Flux<Chat> findChatByMentoringSessionUuid(String mentoringSessionUuid);
}
