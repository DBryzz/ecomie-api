package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.constant.SessionStatus;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.exceptions.ResourceExistsException;
import org.csbf.security.exceptions.ResourceNotFoundException;
import org.csbf.security.model.Challenge;
import org.csbf.security.model.Session;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.SessionService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SessionServiceImp implements SessionService {
    private final SessionRepository sessionRepo;
    private final ChallengeRepository challengeRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;

    @Override
    public ResponseMessage store(HelperDto.SessionCreateDto sessionCreateDto) {
        sessionRepo.findByName(sessionCreateDto.name()).ifPresent(session -> {throw new ResourceExistsException("session '" + session.getName() + "' exists");});
        var session = Session.builder()
                .name(sessionCreateDto.name())
                .description(sessionCreateDto.description())
                .startDate(sessionCreateDto.startDate())
                .endDate(sessionCreateDto.endDate())
                .build();
        var createdSession = sessionRepo.save(session);
        return new ResponseMessage.SuccessResponseMessage("session created. Status: " + createdSession.getStatus());
    }

    @Override
    public ResponseMessage changeStatus(UUID id, String status) {
        var session = sessionRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("session not found"));
        if (!EnumUtils.isValidEnum(SessionStatus.class, status.toUpperCase()))
            throw new BadRequestException("Invalid session status");
        session.setStatus(status.toUpperCase());

        var updatedSession = sessionRepo.save(session);
        return new ResponseMessage.SuccessResponseMessage("session status changed. Status: " + updatedSession.getStatus());
    }

    @Override
    public ResponseMessage update(UUID id, HelperDto.SessionCreateDto sessionCreateDto) {
        var session = sessionRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("session not found"));
        sessionRepo.findByName(sessionCreateDto.name()).ifPresent(ses -> {throw new ResourceExistsException("session '" + ses.getName() + "' exists");});

        session.setName(sessionCreateDto.name());
        session.setDescription(sessionCreateDto.description());
        if(session.getStatus()!=SessionStatus.ONGOING.name()) {
            if (sessionCreateDto.startDate() != null)
                session.setStartDate(sessionCreateDto.startDate());
            if (sessionCreateDto.endDate() != null)
                session.setEndDate(sessionCreateDto.endDate());
        }

        var updatedSession = sessionRepo.save(session);
        return new ResponseMessage.SuccessResponseMessage("session updated. Status: " + updatedSession.getStatus());
    }

    @Override
    public HelperDto.SessionFullDto getSession(UUID id) {
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        var session = sessionRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("session not found"));

        if (!authUser.getAuthorities().contains("ADMIN")){
            var user = userRepo.findByEmail(authUser.getName()).orElseThrow(()->new ResourceNotFoundException("user not found"));
            subscriptionRepo.findBySessionAndUser(session, user).orElseThrow(()->new BadRequestException.InvalidAuthenticationRequestException("Forbidden Request. User not subscribed"));
        }
        return new HelperDto.SessionFullDto(session);
    }

    @Override
    public List<HelperDto.SessionFullDto> getSessions() {
        var sessions = sessionRepo.findAll();
        ArrayList sessionDtos = new ArrayList<HelperDto.SessionFullDto>();

        sessions.forEach(session -> {sessionDtos.add(new HelperDto.SessionFullDto(session));});

        return sessionDtos;
    }

    @Override
    @Transactional
    public HelperDto.SessionFullDto assignChallenges(UUID sessionId, UUID[] challengeIds) {
        var session = sessionRepo.findById(sessionId).orElseThrow(()->new ResourceNotFoundException("session not found"));
//        ArrayList challenges = new ArrayList<Challenge>();
        Arrays.asList(challengeIds).forEach(challId -> {
            var challenge = challengeRepo.findById(challId).orElseThrow(()->new ResourceNotFoundException("challenge not found"));
            session.addChallenge(challenge);

            challengeRepo.save(challenge);
        });

//        session.setChallenges(challenges);

        return new HelperDto.SessionFullDto(sessionRepo.save(session));
    }

    @Override
    public List<HelperDto.SessionFullDto> getUserSessions(UUID userId) {
        var user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        ArrayList sessionDtos = new ArrayList<HelperDto.SessionFullDto>();
        subscriptionRepo.selectAllSessionsThisUserHasSubscribedTo(user).forEach(session -> {sessionDtos.add(new HelperDto.SessionFullDto(session));});

        return sessionDtos;
    }

}
