package org.csbf.security.service;

import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    ResponseMessage updateAuthUserProfile(
            Optional<MultipartFile> file,
            String jsonData
    );

    ResponseMessage updateUserProfile(
            UUID userId,
            Optional<MultipartFile> file,
            String jsonData
    );

    ResponseMessage changeUserRole(String email, String role);

    HelperDto.UserFullDto getUserProfile(UUID userId);

    HelperDto.UserDto getAuthUserProfile();

    void deleteUserProfilePic(UUID userId);

    Resource loadImage(HelperDto.EmailRequest emailRequest);

    byte[] getProfilePicture();

    byte[] getUserProfilePicture(UUID userId);

    // Soft delete user account
    ResponseMessage softDelete(UUID userId);

    void deleteUserProfile(UUID userId);

    List<HelperDto.UserFullDto> getUsersInASession(UUID sessionId, boolean blocked, Optional<String> optionalChallengeId);

//    List<HelperDto.UserFullDto> getUsersSubscribedToSessionViaChallenge(UUID sessionId, UUID challengeId);

    List<HelperDto.UserFullDto> getAllUsers();
}
