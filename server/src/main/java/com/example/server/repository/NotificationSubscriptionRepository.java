package com.example.server.repository;

import com.example.server.domain.NotificationSubscription;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationSubscriptionRepository extends CrudRepository<NotificationSubscription, Long> {

    List<NotificationSubscription> findByUserId(Long userId);
}
