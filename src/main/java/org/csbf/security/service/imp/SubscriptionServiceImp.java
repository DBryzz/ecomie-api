package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.csbf.security.config.AuthContext;
import org.csbf.security.constant.Role;
import org.csbf.security.constant.SessionStatus;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.exceptions.ResourceExistsException;
import org.csbf.security.exceptions.ResourceNotFoundException;
import org.csbf.security.model.Challenge;
import org.csbf.security.model.Session;
import org.csbf.security.model.Subscription;
import org.csbf.security.model.User;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.SubscriptionService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImp implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;
    private final SessionRepository sessionRepo;
    private final ChallengeRepository challengeRepo;
    private final AuthContext authContext;

    @Override
    @Transactional
    public HelperDto.SubscriptionFullDto subscribe(HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmail(authContext.getAuthUser().getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return createSubscription(subscriptionCreateDto, user);

    }



    @Override
    @Transactional
    public HelperDto.SubscriptionFullDto subscribeUser(UUID id, HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return createSubscription(subscriptionCreateDto, user);
    }



    @Override
    @Transactional
    public ResponseMessage removeUserFromSession(UUID sessionId, UUID userId) {
        Subscription subscription = subscriptionRepo.findBySession_IdAndUser_Id(sessionId, userId).orElseThrow(() -> new ResourceNotFoundException("User not subscribed to session"));
        return unSubscribeUser(subscription.getId());
    }


    @Override
    @Transactional
    public ResponseMessage unSubscribeUser(UUID subscriptionId) {
        Subscription subscription = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        Session session = subscription.getSession();
        session.removeSubscription(subscription);
        sessionRepo.save(session);

        Challenge challenge = subscription.getChallenge();
        challenge.removeSubscription(subscription);
        challengeRepo.save(challenge);

        subscriptionRepo.delete(subscription);
        return new ResponseMessage.SuccessResponseMessage("Subscription deleted");
    }

    @Override
    public HelperDto.SubscriptionFullDto update(UUID subscriptionId,HelperDto.@NotNull SubscriptionCreateDto subscriptionCreateDto) {
        Subscription subscription = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
        if (!subscription.getSession().getStatus().equals(SessionStatus.ONGOING)) {
            throw new BadRequestException("Cannot modify subscription of session that is not ongoing");
        }


        subscription.setTarget(subscriptionCreateDto.target());
        subscription.setChallenge(challengeRepo.findById(subscriptionCreateDto.challengeId()).orElseThrow(() -> new ResourceNotFoundException("Challenge not found")));

        return new HelperDto.SubscriptionFullDto(subscriptionRepo.save(subscription));
    }

    @Override
    public HelperDto.SubscriptionFullDto getSubscription(UUID subscriptionId) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        var subscription = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));
//        if (!authUser.getAuthorities().contains("ADMIN")) {
        if (authContext.getAuthUser().getAuthorities().stream().noneMatch(authority -> authority.getAuthority().equals(Role.ADMIN.name()))) {
            if (!subscription.getUser().getEmail().equals(authContext.getAuthUser().getName())) {
                throw new BadRequestException.InvalidAuthenticationRequestException("User not subscribed");

            }
        }

                return new HelperDto.SubscriptionFullDto(subscription);
    }

    @Override
    public List<HelperDto.SubscriptionFullDto> getSubscriptions() {
        List subscriptionFullDtos = new ArrayList<HelperDto.SubscriptionFullDto>();
        subscriptionRepo.findAll().forEach(sub -> {subscriptionFullDtos.add(new HelperDto.SubscriptionFullDto(sub));});
        return subscriptionFullDtos;
    }

    @Override
    public HelperDto.SubscriptionFullDto getSessionSubscription(UUID sessionId) {
                Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmail(authUser.getName()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Session session = sessionRepo.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        Subscription subscription = subscriptionRepo.findBySessionAndUser(session, user).orElseThrow(() -> new ResourceNotFoundException("User not subscribed to session"));
        return new HelperDto.SubscriptionFullDto(subscription);
    }


    private HelperDto.SubscriptionFullDto createSubscription(HelperDto.SubscriptionCreateDto subscriptionCreateDto, @NotNull User user) {
//        if(!user.getRoles().contains("ECOMIEST")) {
        if(!user.getRole().equals(Role.ECOMIEST)) {
            throw new BadRequestException("User not an ECOMIEST");
        }
        if (sessionRepo.existsBySubscriptions_UserId(user.getId())){
            throw new ResourceExistsException("Subscription already exists");
        }
        Session session = sessionRepo.findByStatus(SessionStatus.ONGOING).orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        Challenge challenge = challengeRepo.findById(subscriptionCreateDto.challengeId()).orElseThrow(() -> new ResourceNotFoundException("Challenge does not exist"));

        var subscription = Subscription.builder()
//                .id(UUID.randomUUID())
                .target(subscriptionCreateDto.target())
                .session(session)
                .user(user)
                .challenge(challenge)
                .build();

//        session.addSubscription(subscription);
//        session = sessionRepo.save(session);
//        challenge.addSubscription(subscription);
//        challenge = challengeRepo.save(challenge);
//        user.addSubscription(subscription);
//        user = userRepo.save(user);

        subscription = subscriptionRepo.save(subscription);

        return new HelperDto.SubscriptionFullDto(subscription);
    }


}
