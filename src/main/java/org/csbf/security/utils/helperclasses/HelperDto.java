package org.csbf.security.utils.helperclasses;

import jakarta.persistence.*;
import lombok.Builder;
import org.csbf.security.model.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.*;

public class HelperDto {

    @Builder
    public record RegisterRequest(String firstname, String lastname, String email, String password) {
    }

    @Builder
    public record AuthenticationRequest(String email, String password) {
    }

    @Builder
    public record AuthenticationResponse(String token, String message, boolean success, UserBasicDto user) {
        public AuthenticationResponse(String token, String message, boolean success) {
            this(token, message, success, null);
        }
    }

    @Builder
    public record ConfirmEmailResponse(String token, ResponseMessage responseMessage) {

    }

    @Builder
    public record UserBasicDto(UUID id, String firstname, String lastname, String email, String role, boolean accountEnabled,
                               boolean accountBlocked, boolean accountSoftDeleted) {
    }

    @Builder
    public record UserDto(String firstname, String lastname, String email, String role, String phoneNumber, String country,
                          String region, String city, String language, String profilePictureFileName) {
        public UserDto(User user) {
            this(user.getFirstname(), user.getLastname(), user.getEmail(), user.getRole().name(), user.getPhoneNumber(), user.getCountry(),
                    user.getRegion(), user.getCity(), user.getLanguage(), user.getProfilePictureFileName());
        }
    }

    @Builder
    public record UserFullDto(UUID id, String firstname, String lastname, String email, String role, String phoneNumber,
                              String country, String region, String city, String language,
                              String profilePictureFileName, boolean accountEnabled, boolean accountBlocked,
                              boolean accountSoftDeleted) {
        public UserFullDto(User user) {
            this(user.getId(), user.getFirstname(), user.getLastname(), user.getEmail(), user.getRole().name(), user.getPhoneNumber(),
                    user.getCountry(), user.getRegion(), user.getCity(), user.getLanguage(),
                    user.getProfilePictureFileName(), user.isAccountEnabled(), user.isAccountBlocked(),
                    user.isAccountSoftDeleted());
        }

//        public static UserFullDto justMinimal(User user) {
//            return new SessionFullDto(session.getId(), session.getName(), session.getDescription(), null,
//                    null, session.getStartDate(), session.getEndDate(), session.getStatus().name(),
//                    session.getCreatedAt(), session.getUpdatedAt());
//        }
    }


    public record ResendVerificationEmailDTO(String email) {
    }

    public record UpdateUserProfileRequest(String firstname, String lastname, String phoneNumber, String country,
                                           String region, String city, String language) {
    }

    public record EmailRequest(String email) {
    }


    /**
     * Session Helper DTO
     */
    @Builder
    public record SessionFullDto(UUID id, String name, String description, List<ChallengeFullDto> challenges,
                                 List<SubscriptionFullDto> subscriptions, LocalDateTime startDate, LocalDateTime endDate,
                                 String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        public SessionFullDto(Session session) {
            this(session.getId(), session.getName(), session.getDescription(), getMinimalChallenges(session.getChallenges()),
                    getMinimalSubscriptions(session.getSubscriptions()), session.getStartDate(), session.getEndDate(), session.getStatus().name(),
                    session.getCreatedAt(), session.getUpdatedAt());
        }

        public static SessionFullDto justMinimal(Session session) {
            return new SessionFullDto(session.getId(), session.getName(), session.getDescription(), null,
                    null, session.getStartDate(), session.getEndDate(), session.getStatus().name(),
                    session.getCreatedAt(), session.getUpdatedAt());
        }
    }

//    @Builder
//    public record MinimalSessionDto(UUID id, String name, String description, LocalDateTime startDate, LocalDateTime endDate,
//                                 String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
//        public MinimalSessionDto(Session session) {
//            this(session.getId(), session.getName(), session.getDescription(), session.getStartDate(),
//                    session.getEndDate(), session.getStatus().name(), session.getCreatedAt(), session.getUpdatedAt());
//        }
//    }


    @Builder
    public record SessionCreateDto(String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
    }

    public static List<SessionFullDto> getMinimalSessions(List<Session> sessions) {
        if(Objects.nonNull(sessions)) {
            List<SessionFullDto> sessionDtos = new ArrayList<>();
            sessions.forEach(session -> sessionDtos.add(SessionFullDto.justMinimal(session)));
            return sessionDtos;
        }
        return null;
    }


    /**
     * Challenge Helper DTO
     */
    @Builder
    public record ChallengeFullDto(UUID id, String name, String description, int target,
//                                   List<Subscription> subscriptions, List<Session> sessions, String type,
                                   List<SubscriptionFullDto> subscriptions, List<SessionFullDto> sessions, String type,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {

        public ChallengeFullDto(Challenge challenge) {
            this(challenge.getId(), challenge.getName(), challenge.getDescription(), challenge.getTarget(),
//                    challenge.getSubscriptions(), challenge.getSessions(), challenge.getType().name(), challenge.getCreatedAt(),
                    getMinimalSubscriptions(challenge.getSubscriptions()) , getMinimalSessions(challenge.getSessions()), challenge.getType().name(), challenge.getCreatedAt(),
                    challenge.getUpdatedAt());
        }

        public static ChallengeFullDto justMinimal (Challenge challenge) {
            return new ChallengeFullDto(challenge.getId(), challenge.getName(), challenge.getDescription(), challenge.getTarget(),
                    null, null, challenge.getType().name(), challenge.getCreatedAt(),
                    challenge.getUpdatedAt());
        }
    }

    @Builder
    public record MinimalChallengeDto(UUID id, String name, String description, int target,
                                   String type, LocalDateTime createdAt, LocalDateTime updatedAt) {

        public MinimalChallengeDto(Challenge challenge) {
            this(challenge.getId(), challenge.getName(), challenge.getDescription(), challenge.getTarget(),
                    challenge.getType().name(), challenge.getCreatedAt(), challenge.getUpdatedAt());
        }
    }


    @Builder
    public record ChallengeCreateDto(String name, String description, int target, String type, UUID[] sessions) {
    }

    public static List<ChallengeFullDto> getMinimalChallenges(List<Challenge> challenges) {
        List<ChallengeFullDto> challengeFullDtos = new ArrayList<>();
        challenges.forEach(challenge -> challengeFullDtos.add(ChallengeFullDto.justMinimal(challenge)));
        return challengeFullDtos;
    }

    /**
     * Subscription Helper DTO
     */
    @Builder
    public record SubscriptionFullDto(UUID id, int target, boolean blocked, UserFullDto user, ChallengeFullDto challenge,
                                      SessionFullDto session, List<ChallengeReportFullDto> reports, LocalDateTime createdAt, LocalDateTime updatedAt) {
        public SubscriptionFullDto(Subscription subscription) {
            this(subscription.getId(), subscription.getTarget(), subscription.isBlocked(), new UserFullDto(subscription.getUser()),
                    ChallengeFullDto.justMinimal(subscription.getChallenge()), SessionFullDto.justMinimal(subscription.getSession()), getMinimalChallengeReport(subscription.getChallengeReports()), subscription.getCreatedAt(),
                    subscription.getUpdatedAt());
        }

        public static SubscriptionFullDto justMinimal (Subscription subscription) {
            return new SubscriptionFullDto(subscription.getId(), subscription.getTarget(), subscription.isBlocked(), null,
                    null, null, null,  subscription.getCreatedAt(),
                    subscription.getUpdatedAt());
        }
    }


    @Builder
    public record SubscriptionCreateDto(int target, UUID challengeId) {
//        public SubscriptionCreateDto withUserId(UUID userId) {
//            return new SubscriptionCreateDto(target, userId, challengeId, sessionId);
//        }

    }

    public static List<SubscriptionFullDto> getMinimalSubscriptions(List<Subscription> subscriptions) {
        if (Objects.nonNull(subscriptions)) {
            List<SubscriptionFullDto> subscriptionFullDtos = new ArrayList<>();
            subscriptions.forEach(subscription -> subscriptionFullDtos.add(SubscriptionFullDto.justMinimal(subscription)));
            return subscriptionFullDtos;
        }
        return null;
    }



    /**
     * ChallengeReport Helper DTO
     */
    @Builder
    public record ChallengeReportCreateDto(int numberEvangelizedTo, int numberOfNewConverts,
                                           int numberFollowedUp, String difficulties, String remark) {
    }

    @Builder
    public record ChallengeReportFullDto(UUID id, Subscription subscription, int numberEvangelizedTo,
                                         int numberOfNewConverts, int numberFollowedUp, String difficulties,
                                         String remark, LocalDateTime createdAt, LocalDateTime updatedAt) {
        public ChallengeReportFullDto(ChallengeReport challengeReport) {
            this(challengeReport.getId(), challengeReport.getSubscription(), challengeReport.getNumberEvangelizedTo(),
                    challengeReport.getNumberOfNewConverts(), challengeReport.getNumberFollowedUp(),
                    challengeReport.getDifficulties(), challengeReport.getRemark(), challengeReport.getCreatedAt(),
                    challengeReport.getUpdatedAt());
        }

        public static ChallengeReportFullDto justMinimal(ChallengeReport challengeReport) {
            return new ChallengeReportFullDto(challengeReport.getId(), null, challengeReport.getNumberEvangelizedTo(),
                    challengeReport.getNumberOfNewConverts(), challengeReport.getNumberFollowedUp(),
                    challengeReport.getDifficulties(), challengeReport.getRemark(), challengeReport.getCreatedAt(),
                    challengeReport.getUpdatedAt());
        }
    }

    public static List<ChallengeReportFullDto> getMinimalChallengeReport(List<ChallengeReport> reports) {
        if(Objects.nonNull(reports)) {
            List<ChallengeReportFullDto> challengeReportFullDtos = new ArrayList<>();
            reports.forEach(report -> challengeReportFullDtos.add(ChallengeReportFullDto.justMinimal(report)));
            return challengeReportFullDtos;
        }
        return null;
    }

    public record RequestProps(UUID id, String status, String type, String role, UUID[] ids, boolean blocked, UUID challengeId) {
    }
}
