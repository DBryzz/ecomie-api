package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.constant.ChallengeType;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.exceptions.ResourceExistsException;
import org.csbf.security.exceptions.ResourceNotFoundException;
import org.csbf.security.model.Challenge;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.ChallengeService;
import org.csbf.security.service.FileUploadService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeServiceImp implements ChallengeService {

    private final ChallengeRepository challengeRepo;
    private final SessionRepository sessionRepo;
    private final UserRepository userRepo;
    private final SubscriptionRepository subscriptionRepo;

    @Override
    @Transactional
    public ResponseMessage store(HelperDto.ChallengeCreateDto challengeCreateDto, UUID[] sessionIds) {

        if (!EnumUtils.isValidEnum(ChallengeType.class, challengeCreateDto.type().toUpperCase()))
            throw new BadRequestException("Invalid challenge type");
        challengeRepo.findByName(challengeCreateDto.name()).ifPresent(chal -> {throw new ResourceExistsException("challenge with the name '" + chal.getName() + "' already exist");});
        var challenge = Challenge.builder()
                .name(challengeCreateDto.name())
                .description(challengeCreateDto.description())
                .type(challengeCreateDto.type())
                .target(challengeCreateDto.target())
                .build();

        addSessionToChallenge(sessionIds, challenge);
        return new ResponseMessage.SuccessResponseMessage("challenge created. Type: " + challenge.getType());

    }

    @Override
    public ResponseMessage changeType(UUID id, String status) {
        var challenge = challengeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("challenge not found"));
        if (!EnumUtils.isValidEnum(ChallengeType.class, status.toUpperCase()))
            throw new BadRequestException("Invalid session status");
        challenge.setType(status.toUpperCase());

        var updatedChallenge = challengeRepo.save(challenge);
        return new ResponseMessage.SuccessResponseMessage("session status changed. Status: " + updatedChallenge.getType());
    }

    @Override
    @Transactional
    public ResponseMessage update(UUID chalId, HelperDto.ChallengeCreateDto challengeCreateDto, UUID[] sessionIds) {
        var challenge = challengeRepo.findById(chalId).orElseThrow(() -> new ResourceNotFoundException("challenge not found"));
        challengeRepo.findByName(challengeCreateDto.name()).ifPresent(chal -> {
            throw new ResourceExistsException("challenge with the name '" + chal.getName() + "' already exist");
        });

        if (!EnumUtils.isValidEnum(ChallengeType.class, challengeCreateDto.type().toUpperCase()))
            throw new BadRequestException("Invalid challenge type");

        challenge.setName(challengeCreateDto.name());
        challenge.setDescription(challenge.getDescription());
        challenge.setTarget(challengeCreateDto.target());

        addSessionToChallenge(sessionIds, challenge);

        return new ResponseMessage.SuccessResponseMessage("challenge updated. Type: " + challenge.getType());

    }

    private void addSessionToChallenge(UUID[] sessionIds, Challenge challenge) {
        if (sessionIds.length > 0) {
            Arrays.asList(sessionIds).forEach(id -> {
                var session = sessionRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("session not found"));
                challenge.addSession(session);
                sessionRepo.save(session);
            });
        }
        challengeRepo.save(challenge);
    }

    @Override
    public HelperDto.ChallengeFullDto getChallenge(UUID chalId) {
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        var challenge = challengeRepo.findById(chalId).orElseThrow(() -> new ResourceNotFoundException("challenge not found"));

        if (!authUser.getAuthorities().contains("ADMIN")){
            var user = userRepo.findByEmail(authUser.getName()).orElseThrow(()->new ResourceNotFoundException("user not found"));
            if (subscriptionRepo.findAllByUserAndChallenge(user, challenge).isEmpty()) {
                throw new BadRequestException.InvalidAuthenticationRequestException("Forbidden Request. User not subscribed");
            }
        }

        return new HelperDto.ChallengeFullDto(challenge);
    }

    @Override
    public List<HelperDto.ChallengeFullDto> getChallenges() {
        var challenges = challengeRepo.findAll();
        ArrayList challengeDto = new ArrayList<HelperDto.ChallengeFullDto>();

        challenges.forEach(session -> {challengeDto.add(new HelperDto.ChallengeFullDto(session));});

        return challengeDto;
    }

}
