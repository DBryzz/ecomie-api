package org.csbf.security.model;

import jakarta.persistence.*;
import lombok.*;
import org.csbf.security.repository.ChallengeReportRepository;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@RequiredArgsConstructor
@Table(name = "challenge_report")
public class ChallengeReport {
    @Transient
    ChallengeReportRepository reportRepo;
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;
    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User ecomist;
    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Session session;
    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Challenge challenge;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private long numberEvangelizedTo;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private long numberOfNewConverts;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private long numberFollowedUp;
    @Column(nullable = true)
    private String difficulties;
    @Column(nullable = true)
    private String remark;
    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public long getTotalNumberThisEcomistEvangelizedToViaThisChallengeInThisSession() {
        return reportRepo.numberAnEcomistEvangelizedToViaAChallengeInASession(this.session,this.ecomist, this.challenge);
    }

    public long getTotalNumberLeftToMeetTarget() {
        LocalDateTime now = LocalDateTime.now();

        return this.challenge.getTarget() -  getTotalNumberThisEcomistEvangelizedToViaThisChallengeInThisSession();
    }

    public boolean isCompleted() {
        return (getTotalNumberThisEcomistEvangelizedToViaThisChallengeInThisSession() == this.challenge.getTarget());
    }
}
