package org.csbf.security.repository;

import org.csbf.security.model.Challenge;
import org.csbf.security.model.Session;
import org.csbf.security.model.Subscription;
import org.csbf.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    Optional<Subscription> findByIdAndUser(UUID subscriptionId, User user);
    List<Subscription> findAllByUserAndChallenge(User user, Challenge challenge);
    Optional<Subscription> findBySession(Session session);
    Optional<Subscription> findBySessionAndBlocked(Session session, boolean blocked);
    Optional<Subscription> findBySessionAndUser(Session session, User user);
    Optional<Subscription> findBySessionAndUserAndBlocked(Session session, User user, boolean blocked);
    Optional<Subscription> findBySessionAndChallenge(Session session, Challenge challenge);
    Optional<Subscription> findBySessionAndChallengeAndUser(Session session, Challenge challenge, User user);
    Optional<Subscription> findBySessionAndChallengeAndUserAndBlocked(Session session, Challenge challenge, User user, boolean blocked);

    @Query(value = "SELECT sub.session from Subscription sub where sub.user=:user")
    List<Session> selectAllSessionsThisUserHasSubscribedTo(@Param("user") User user);
    @Query(value = "SELECT sub.session from Subscription sub where sub.session=:session and sub.blocked=true")
    List<User> selectAllUsersBlockedInSession(@Param("session") Session session);
    @Query(value = "SELECT sub.user from Subscription sub where sub.session=:session and sub.challenge=:challenge")
    List<User> selectAllUsersSubscribedToSessionViaChallenge(@Param("session") Session session, @Param("challenge") Challenge challenge);
    @Query(value = "SELECT sub.user from Subscription sub where sub.session=:session")
    List<User> selectAllUsersSubscribedToSession(@Param("session") Session session);
}
