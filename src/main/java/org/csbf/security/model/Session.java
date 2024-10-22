package org.csbf.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.csbf.security.constant.SessionStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(nullable = true)
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @Column(nullable = true)
    @JoinTable(
            name = "session_challenge",
            joinColumns = @JoinColumn(name = "session_id"),
            inverseJoinColumns = @JoinColumn(name = "challenge_id"))
    private List<Challenge> challenges;

    @Column(nullable = true)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "session")
    private List<Subscription> subscriptions;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(nullable = false, updatable = false)
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(50) default 'INACTIVE'")
    private SessionStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public void addSubscription(Subscription subscription) {
        this.subscriptions.add(subscription);
        subscription.setSession(this);
    }

    public void removeSubscription(Subscription subscription) {
        this.subscriptions.remove(subscription);
        subscription.setSession(null);
    }

    public void addChallenge(Challenge challenge) {
        if (this.challenges.stream().anyMatch(challenge1 -> challenge1.getId().equals(challenge.getId()))) {
            return;
        }
        this.challenges.add(challenge);
        challenge.getSessions().add(this);
    }

    public void removeChallenge(Challenge challenge) {
        this.challenges.remove(challenge);
        challenge.getSessions().remove(this);
    }
}
