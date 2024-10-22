package org.csbf.security.service;

import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;

import java.util.List;
import java.util.UUID;

public interface ChallengeService {
    ResponseMessage store(HelperDto.ChallengeCreateDto challengeCreateDto);
    ResponseMessage changeType(UUID id, String status);

    ResponseMessage update(UUID id, HelperDto.ChallengeCreateDto challengeCreateDto);
    HelperDto.ChallengeFullDto getChallenge(UUID id);
    List<HelperDto.ChallengeFullDto> getChallenges();
}
