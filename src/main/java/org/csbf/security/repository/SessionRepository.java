package org.csbf.security.repository;

import org.csbf.security.constant.SessionStatus;
import org.csbf.security.model.Challenge;
import org.csbf.security.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<Session, UUID> {
    Optional<Session> findByName(String name);
    Optional<Session> findByStatus(SessionStatus status);
    List<Session> findSessionsByChallenges_Id(UUID challengeId);
    Optional<Session> findBySubscriptions_UserId(UUID subscriptionsUserId);
    boolean existsBySubscriptions_UserId(UUID subscriptionsUserId);
    boolean existsByStatus(SessionStatus status);
}
