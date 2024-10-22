package org.csbf.security.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subscription")
public class Subscription {

    @Id
    @UuidGenerator
//    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private UUID id;

    @Column(unique = true, nullable = false)
    private int target;

    @Column(unique = true, nullable = false)
    private boolean blocked;
//    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;
//    @Column(nullable = false)
    @ManyToOne(fetch=FetchType.LAZY, optional = false)
    private Challenge challenge;

    @Column(nullable = true)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "subscription")
    private List<ChallengeReport> challengeReports;
//    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Session session;
//    @Column(nullable = false)
//    @OneToMany(mappedBy = "subscription")
//    private List<ChallengeReport> challengeReports;
//    private String challengeType;
//    private LocalDateTime targetDeadLine;
//    private LocalDateTime startDate;
//    private LocalDateTime endDate;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID();
    }

    public void addChallengeReport(ChallengeReport challengeReport) {
        this.challengeReports.add(challengeReport);
        challengeReport.setSubscription(this);
    }

    public void removeChallengeReport(ChallengeReport challengeReport) {
        this.challengeReports.remove(challengeReport);
        challengeReport.setSubscription(null);
    }
}
