package com.multitap.chat.chat.infrastructure;

import com.multitap.chat.chat.domain.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatRepository extends MongoRepository<Chat, String> {

    @Query("{ 'mentoringSessionUuid': ?0, 'createdAt': { $lt: ?1 } }")
    Page<Chat> findByMentoringSessionUuidAndCreatedAtLessThan(
            String mentoringSessionUuid, LocalDateTime createdAt, Pageable pageable);

}
