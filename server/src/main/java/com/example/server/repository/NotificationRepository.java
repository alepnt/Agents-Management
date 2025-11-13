package com.example.server.repository;

import com.example.server.domain.Notification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface NotificationRepository extends CrudRepository<Notification, Long> {

    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByTeamIdOrderByCreatedAtDesc(Long teamId);

    List<Notification> findByUserIdAndCreatedAtAfterOrderByCreatedAtDesc(Long userId, Instant createdAfter);

    List<Notification> findByTeamIdAndCreatedAtAfterOrderByCreatedAtDesc(Long teamId, Instant createdAfter);
}
