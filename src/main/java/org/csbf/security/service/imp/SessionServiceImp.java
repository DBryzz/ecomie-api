package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.config.AuthContext;
import org.csbf.security.constant.Role;
import org.csbf.security.constant.SessionStatus;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.exceptions.ResourceExistsException;
import org.csbf.security.exceptions.ResourceNotFoundException;
import org.csbf.security.model.Session;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.SessionService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SessionServiceImp implements SessionService {
    private final SessionRepository sessionRepo;
    private final ChallengeRepository challengeRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final UserRepository userRepo;
    private final AuthContext authContext;


    @Override
    public ResponseMessage store(HelperDto.SessionCreateDto sessionCreateDto) {
        sessionRepo.findByName(sessionCreateDto.name()).ifPresent(session -> {
            throw new ResourceExistsException("Session '" + session.getName() + "' exists");
        });
        var session = Session.builder()
                .name(sessionCreateDto.name())
                .description(sessionCreateDto.description())
                .startDate(sessionCreateDto.startDate())
                .endDate(sessionCreateDto.endDate())
                .status(SessionStatus.INACTIVE)
                .build();
        var createdSession = sessionRepo.save(session);
        return new ResponseMessage.SuccessResponseMessage("Session created. Status: " + createdSession.getStatus());
    }

    @Override
    public ResponseMessage changeStatus(UUID id, String status) {
        var session = sessionRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        String statusUpperCase = status.toUpperCase();
        if (!EnumUtils.isValidEnum(SessionStatus.class, statusUpperCase)) {
            throw new BadRequestException("Invalid session status");
        }
        if ( SessionStatus.ONGOING.name().equals(statusUpperCase) && sessionRepo.existsByStatus(SessionStatus.valueOf(statusUpperCase))) {
            throw new ResourceExistsException("Only one session can be active at a time. Please end currently ongoing session first");
        }
        session.setStatus(SessionStatus.valueOf(statusUpperCase));

        var updatedSession = sessionRepo.save(session);
        return new ResponseMessage.SuccessResponseMessage("Session status changed. Status: " + updatedSession.getStatus());
    }

    @Override
    public ResponseMessage update(UUID id, HelperDto.SessionCreateDto sessionCreateDto) {
        var session = sessionRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        sessionRepo.findByName(sessionCreateDto.name()).ifPresent(ses -> {
            throw new ResourceExistsException("Session '" + ses.getName() + "' exists");
        });

        session.setName(sessionCreateDto.name());
        session.setDescription(sessionCreateDto.description());
        if (session.getStatus() != SessionStatus.ONGOING) {
            if (sessionCreateDto.startDate() != null)
                session.setStartDate(sessionCreateDto.startDate());
            if (sessionCreateDto.endDate() != null)
                session.setEndDate(sessionCreateDto.endDate());
        }

        var updatedSession = sessionRepo.save(session);
        return new ResponseMessage.SuccessResponseMessage("Session updated. Status: " + updatedSession.getStatus());
    }

    @Override
    public HelperDto.SessionFullDto getSession(UUID id) {
        Authentication authUser = authContext.getAuthUser();
        var session = sessionRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))){
//            var user = userRepo.findByEmail(authUser.getName()).orElseThrow(()->new ResourceNotFoundException("User not found"));
//            if (subscriptionRepo.findAllByUserAndChallenge(user, challenge).isEmpty()) {
//                throw new BadRequestException.InvalidAuthenticationRequestException("User not subscribed");
//            }
            return HelperDto.SessionFullDto.justMinimal(session);
        }
        return new HelperDto.SessionFullDto(session);
    }

    @Override
    public List<HelperDto.SessionFullDto> getSessions() {
        Authentication authUser = authContext.getAuthUser();
        var sessions = sessionRepo.findAll();
        ArrayList sessionDtos = new ArrayList<HelperDto.SessionFullDto>();

        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))) {
//            var user = userRepo.findByEmail(authUser.getName()).orElseThrow(()->new ResourceNotFoundException("User not found"));
            sessions.forEach(session -> {
//                if (subscriptionRepo.findAllByUserAndChallenge(user, challenge).isEmpty()) {
//                    throw new BadRequestException.InvalidAuthenticationRequestException("User not subscribed");
//                }
                sessionDtos.add(HelperDto.SessionFullDto.justMinimal(session));
            });
            return sessionDtos;
        }

        sessions.forEach(session -> sessionDtos.add(new HelperDto.SessionFullDto(session)));

        return sessionDtos;
    }

    @Override
    @Transactional
    public HelperDto.SessionFullDto assignChallenges(UUID sessionId, UUID[] challengeIds) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        Arrays.asList(challengeIds).forEach(challengeId -> {
            var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
            session.addChallenge(challenge);
        });

        return new HelperDto.SessionFullDto(sessionRepo.save(session));
    }

    @Override
    @Transactional
    public HelperDto.SessionFullDto assignChallenge(UUID sessionId, UUID challengeId) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        session.addChallenge(challenge);

        return new HelperDto.SessionFullDto(sessionRepo.save(session));
    }

    @Override
    @Transactional
    public HelperDto.SessionFullDto removeChallenges(UUID sessionId, UUID[] challengeIds) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        Arrays.asList(challengeIds).forEach(challengeId -> {
            var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
            session.removeChallenge(challenge);
        });

        return new HelperDto.SessionFullDto(sessionRepo.save(session));
    }

    @Override
    @Transactional
    public HelperDto.SessionFullDto removeChallenge(UUID sessionId, UUID challengeId) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        session.removeChallenge(challenge);

        return new HelperDto.SessionFullDto(sessionRepo.save(session));
    }

    @Override
    public List<HelperDto.SessionFullDto> getUserSessions(UUID userId) {
        var user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        ArrayList sessionDtos = new ArrayList<HelperDto.SessionFullDto>();
        subscriptionRepo.selectAllSessionsThisUserHasSubscribedTo(user).forEach(session -> {
            sessionDtos.add(new HelperDto.SessionFullDto(session));
        });

        return sessionDtos;
    }

}
