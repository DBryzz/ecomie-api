package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.csbf.security.exceptions.BadRequestException;
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

    @Override
    @Transactional
    public HelperDto.SubscriptionFullDto subscribe(HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByEmail(authUser.getName()).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        return createSubscription(subscriptionCreateDto, user);

    }



    @Override
    @Transactional
    public HelperDto.SubscriptionFullDto subscribeUser(UUID id, HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
        User user = userRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        return createSubscription(subscriptionCreateDto, user);
    }



    @Override
    @Transactional
    public ResponseMessage unSubscribeUser(UUID subscriptionId, UUID userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        Subscription subscription = subscriptionRepo.findByIdAndUser(subscriptionId, user).orElseThrow(() -> new ResourceNotFoundException("subscription not found"));

        Session session = subscription.getSession();
        session.removeSubscription(subscription);
        sessionRepo.save(session);

        Challenge challenge = subscription.getChallenge();
        challenge.removeSubscription(subscription);
        challengeRepo.save(challenge);

        subscriptionRepo.delete(subscription);
        return new ResponseMessage.SuccessResponseMessage("subscription deleted");
    }

    @Override
    public HelperDto.SubscriptionFullDto update(UUID subscriptionId, UUID userId, HelperDto.@NotNull SubscriptionCreateDto subscriptionCreateDto) {
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        Subscription subscription = subscriptionRepo.findByIdAndUser(subscriptionId, user).orElseThrow(() -> new ResourceNotFoundException("subscription not found"));

        subscription.setTarget(subscriptionCreateDto.target());
        subscription.setChallenge(challengeRepo.findById(subscriptionCreateDto.challengeId()).orElseThrow(() -> new ResourceNotFoundException("challenge not found")));
        subscription.setSession(sessionRepo.findById(subscriptionCreateDto.sessionId()).orElseThrow(() -> new ResourceNotFoundException("session not found")));

        return new HelperDto.SubscriptionFullDto(subscriptionRepo.save(subscription));
    }

    @Override
    public HelperDto.SubscriptionFullDto getSubscription(UUID subscriptionId) {
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        var subscription = subscriptionRepo.findById(subscriptionId).orElseThrow(() -> new ResourceNotFoundException("subscription not found"));
        if (!authUser.getAuthorities().contains("ADMIN")) {
            if (!subscription.getUser().getEmail().equals(authUser.getName())) {
                throw new BadRequestException.InvalidAuthenticationRequestException("Forbidden Request. User not subscribed");

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


    private HelperDto.SubscriptionFullDto createSubscription(HelperDto.SubscriptionCreateDto subscriptionCreateDto, @NotNull User user) {
        if(!user.getRoles().contains("ECOMIEST"))
            throw new BadRequestException("user not an ECOMIEST");
        Session session = sessionRepo.findById(subscriptionCreateDto.sessionId()).orElseThrow(() -> new ResourceNotFoundException("session not found"));
        Challenge challenge = challengeRepo.findById(subscriptionCreateDto.challengeId()).orElseThrow(() -> new ResourceNotFoundException("challenge does not exist"));

        var subscription = Subscription.builder()
                .target(subscriptionCreateDto.target())
                .session(session)
                .user(user)
                .challenge(challenge)
                .build();

        session.addSubscription(subscription);
        session = sessionRepo.save(session);
        challenge.addSubscription(subscription);
        challenge = challengeRepo.save(challenge);
        user.addSubscription(subscription);
        user = userRepo.save(user);

        subscription = subscriptionRepo.save(subscription);

        return new HelperDto.SubscriptionFullDto(subscription);
    }


}
