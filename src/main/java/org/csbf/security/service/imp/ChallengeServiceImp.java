package org.csbf.security.service.imp;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.config.AuthContext;
import org.csbf.security.constant.ChallengeType;
import org.csbf.security.constant.Role;
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
    private final AuthContext authContext;

    @Override
    @Transactional
    public ResponseMessage store(HelperDto.ChallengeCreateDto challengeCreateDto) {

        if (!EnumUtils.isValidEnum(ChallengeType.class, challengeCreateDto.type().toUpperCase())) {
            throw new BadRequestException("Invalid challenge type");
        }
        var type = challengeCreateDto.type().toUpperCase();
        challengeRepo.findByName(challengeCreateDto.name()).ifPresent(challenge -> {throw new ResourceExistsException("Challenge with the name '" + challenge.getName() + "' already exist");});
        var challenge = Challenge.builder()
                .name(challengeCreateDto.name())
                .description(challengeCreateDto.description())
                .type(ChallengeType.valueOf(type))
                .target(challengeCreateDto.target())
                .build();

        addSessionToChallenge(challengeCreateDto.sessions(), challenge);
        challengeRepo.save(challenge);
        return new ResponseMessage.SuccessResponseMessage("Challenge created. Type: " + type);

    }

    @Override
    public ResponseMessage changeType(UUID id, String status) {
        var challenge = challengeRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        if (!EnumUtils.isValidEnum(ChallengeType.class, status.toUpperCase())) {
            throw new BadRequestException("Invalid challenge type");
        }
        challenge.setType(ChallengeType.valueOf(status.toUpperCase()));

        var updatedChallenge = challengeRepo.save(challenge);
        return new ResponseMessage.SuccessResponseMessage("Challenge type changed. Type: " + updatedChallenge.getType());
    }

    @Override
    @Transactional
    public ResponseMessage update(UUID challengeId, HelperDto.ChallengeCreateDto challengeCreateDto) {
        var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
        challengeRepo.findByName(challengeCreateDto.name()).ifPresent(chal -> {
            throw new ResourceExistsException("Challenge with the name '" + chal.getName() + "' already exist");
        });

        if (!EnumUtils.isValidEnum(ChallengeType.class, challengeCreateDto.type().toUpperCase())) {
            throw new BadRequestException("Invalid challenge type");
        }

        challenge.setName(challengeCreateDto.name());
        challenge.setDescription(challenge.getDescription());
        challenge.setTarget(challengeCreateDto.target());

        addSessionToChallenge(challengeCreateDto.sessions(), challenge);
        challengeRepo.save(challenge);

        return new ResponseMessage.SuccessResponseMessage("Challenge updated. Type: " + challenge.getType());

    }

    private Challenge addSessionToChallenge(UUID[] sessionIds, Challenge challenge) {
        if (sessionIds.length > 0) {
            Arrays.asList(sessionIds).forEach(id -> {
                var session = sessionRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Session not found"));
                challenge.addSession(session);
            });
        }
        return challenge;
    }

    @Override
    public HelperDto.ChallengeFullDto getChallenge(UUID challengeId) {
        Authentication authUser = authContext.getAuthUser();
        var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));

        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))){
//            var user = userRepo.findByEmail(authUser.getName()).orElseThrow(()->new ResourceNotFoundException("User not found"));
//            if (subscriptionRepo.findAllByUserAndChallenge(user, challenge).isEmpty()) {
//                throw new BadRequestException.InvalidAuthenticationRequestException("User not subscribed");
//            }
            return HelperDto.ChallengeFullDto.justMinimal(challenge);
        }

        return new HelperDto.ChallengeFullDto(challenge);
    }

    @Override
    public List<HelperDto.ChallengeFullDto> getChallenges() {
        Authentication authUser = authContext.getAuthUser();

        var challenges = challengeRepo.findAll();
        ArrayList challengeFullDtos = new ArrayList<HelperDto.ChallengeFullDto>();

        if (authUser.getAuthorities().stream().noneMatch(authority -> authority.getAuthority().contains(Role.ADMIN.name()))){
//            var user = userRepo.findByEmail(authUser.getName()).orElseThrow(()->new ResourceNotFoundException("User not found"));
            challenges.forEach(challenge -> {
//                if (subscriptionRepo.findAllByUserAndChallenge(user, challenge).isEmpty()) {
//                    throw new BadRequestException.InvalidAuthenticationRequestException("User not subscribed");
//                }
                challengeFullDtos.add(HelperDto.ChallengeFullDto.justMinimal(challenge));
            });
            return challengeFullDtos;

        }

        challenges.forEach(challenge -> challengeFullDtos.add(new HelperDto.ChallengeFullDto(challenge)));

        return challengeFullDtos;
    }

}
