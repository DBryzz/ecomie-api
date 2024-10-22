package org.csbf.security.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.config.AuthContext;
import org.csbf.security.constant.Role;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.exceptions.BaseException;
import org.csbf.security.exceptions.InvalidUuidException;
import org.csbf.security.exceptions.ResourceNotFoundException;
import org.csbf.security.model.User;
import org.csbf.security.repository.ChallengeRepository;
import org.csbf.security.repository.SessionRepository;
import org.csbf.security.repository.SubscriptionRepository;
import org.csbf.security.repository.UserRepository;
import org.csbf.security.service.FileUploadService;
import org.csbf.security.service.UserService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final SessionRepository sessionRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final ChallengeRepository challengeRepo;
    private final AuthContext authContext;

    private final Environment environment;

    @Override
    public ResponseMessage updateAuthUserProfile(
            Optional<MultipartFile> file,
            String jsonData
    ) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        log.info("{}", authContext.getAuthUser());
        User user = userRepository.findByEmail(authContext.getAuthUser().getName())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return getUpdateResponseMessage(file, jsonData, user);
    }

    @Override
    public ResponseMessage updateUserProfile(
            UUID userId,
            Optional<MultipartFile> file,
            String jsonData
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        return getUpdateResponseMessage(file, jsonData, user);
    }

    @Override
    public ResponseMessage changeUserRole(String email, String role) {
        var user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!EnumUtils.isValidEnum(Role.class, role.toUpperCase()))
            throw new BadRequestException("Invalid session status");
//        if(!user.getRoles().contains(role.toUpperCase()))
//            user.setRole(user.getRoles()+"-"+role.toUpperCase());
        if(!user.getRole().name().equals(role.toUpperCase())) {
            user.setRole(Role.valueOf(role.toUpperCase()));
            user = userRepository.save(user);
        }
        return new ResponseMessage.SuccessResponseMessage("User role updated - " + user.getRole());
    }

    private ResponseMessage getUpdateResponseMessage(Optional<MultipartFile> file, String jsonData, User user) {
        HelperDto.UpdateUserProfileRequest userProfileRequest;
        try {
            log.info(jsonData + ": {}", HelperDto.UpdateUserProfileRequest.class );
            userProfileRequest = new ObjectMapper()
                    .readValue(jsonData, HelperDto.UpdateUserProfileRequest.class);
            log.info("Test Country {}", userProfileRequest.country());
        } catch (Exception ex) {
            throw new BadRequestException("Invalid json string");
        }

        user.setFirstname(userProfileRequest.firstname());
        user.setLastname(userProfileRequest.lastname());
        user.setPhoneNumber(userProfileRequest.phoneNumber());
        user.setCountry(userProfileRequest.country());
        user.setRegion(userProfileRequest.region());
        user.setCity(userProfileRequest.city());
        user.setLanguage(userProfileRequest.language());

        String imageFileName;
        String oldFile = user.getProfilePictureFileName();

        if (file.isPresent()) {
            try {
                imageFileName = fileUploadService.save(file.get());
                log.info("imageFileName: {}", imageFileName);
                log.info("Uploaded the file successfully");
            } catch (Exception e) {
                throw new BaseException("Could not upload the file !");
            }
            if (oldFile != null && !imageFileName.equals(oldFile) && fileUploadService.resourceExist(oldFile)) {
                fileUploadService.deleteFile(oldFile);
            }

            log.info("old file name {}", oldFile);
            log.info("{}",
                    (imageFileName != null)
                            ? "image successfully uploaded"
                            : "image with name '" +
                            file.get().getOriginalFilename() +
                            "' already exist"
            );
            user.setProfilePictureFileName(imageFileName);

        }
        userRepository.save(user);
        return new ResponseMessage.SuccessResponseMessage("Updated!");
    }

    @Override
    public HelperDto.UserFullDto getUserProfile(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User does not exist"));

        return new HelperDto.UserFullDto(user);
    }

    @Override
    public HelperDto.UserDto getAuthUserProfile() {
        log.info("TEST: In method");
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        log.info("{}", authContext.getAuthUser());

        User user = userRepository.findByEmail(authContext.getAuthUser().getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new HelperDto.UserDto(user);
    }

    @Override
    public void deleteUserProfilePic(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User does not exist"));


        String fileName = user.getProfilePictureFileName();
        if (fileName == null) throw new BadRequestException("User has no profile image");
        fileUploadService.deleteFile(fileName);
        user.setProfilePictureFileName(null);
        userRepository.save(user);
    }

//    @Override
//    public Resource loadUserImage(UUID userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        String fileName = user.getProfilePictureFileName();
//        if (fileName == null) return null;
//        return fileUploadService.load(fileName);
//    }

    @Override
    public Resource loadImage(HelperDto.EmailRequest emailRequest) {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository
                .findByEmail(emailRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String fileName = user.getProfilePictureFileName();
        if (fileName == null) return null;
        return fileUploadService.load(fileName);
    }

    @Override
    public byte[] getProfilePicture() {
//        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository
                .findByEmail(authContext.getAuthUser().getName())
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        Resource resource = loadImage(new HelperDto.EmailRequest(user.getEmail()));
        try {
//            return resource == null ? null : ByteStreams.toByteArray(resource.getInputStream());
            return resource == null ? null : resource.getInputStream().readAllBytes();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to extract user profile picture");
        }
    }

    @Override
    public byte[] getUserProfilePicture(UUID userId) {
        User user = userRepository
                .findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));
        Resource resource = loadImage(new HelperDto.EmailRequest(user.getEmail()));
        try {
//            return resource == null ? null : ByteStreams.toByteArray(resource.getInputStream());
            return resource == null ? null : resource.getInputStream().readAllBytes();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to extract user profile picture");
        }
    }

    @Override
    public void deleteUserProfile(UUID userId) {
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User does not exist"));
        userRepository.deleteById(userId);
    }

    // Soft delete user account
    @Override
    public ResponseMessage softDelete(UUID userId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User profile not found"));
        user.setAccountSoftDeleted(!user.isAccountSoftDeleted());
        userRepository.save(user);

        return user.isAccountSoftDeleted() ? new ResponseMessage.SuccessResponseMessage("Account soft deleted") : new ResponseMessage.SuccessResponseMessage("Account restored");
    }

    @Override
    public List<HelperDto.UserFullDto> getUsersInASession(UUID sessionId, boolean blocked, Optional<String> optionalChallengeId) {
        var session = sessionRepo.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found"));
        List<HelperDto.UserFullDto> users = new ArrayList<>();

        if(optionalChallengeId.isPresent()) {
            String challengeIdString = optionalChallengeId.get();
            try {
                UUID challengeId = UUID.fromString(challengeIdString);
                var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
                subscriptionRepo.selectAllUsersSubscribedToSessionViaChallenge(session, challenge).forEach(user -> users.add(new HelperDto.UserFullDto(user)));
            }catch (IllegalArgumentException e) {
                throw new InvalidUuidException(challengeIdString);
            }
        } else if(blocked) {
            subscriptionRepo.selectAllUsersBlockedInSession(session).forEach(user -> users.add(new HelperDto.UserFullDto(user)));
        } else {
            subscriptionRepo.selectAllUsersSubscribedToSession(session).forEach(user -> users.add(new HelperDto.UserFullDto(user)));

        }
        return users;
    }

//    @Override
//    public List<HelperDto.UserFullDto> getUsersSubscribedToSessionViaChallenge(UUID sessionId, UUID challengeId) {
//        var session = sessionRepo.findById(sessionId).orElseThrow(() -> new ResourceNotFoundException("Session not found"));
//        var challenge = challengeRepo.findById(challengeId).orElseThrow(() -> new ResourceNotFoundException("Challenge not found"));
//
//        List<HelperDto.UserFullDto> users = new ArrayList<>();
//        subscriptionRepo.selectAllUsersSubscribedToSessionViaChallenge(session, challenge).forEach(user -> {users.add(new HelperDto.UserFullDto(user));});
//
//        return users;
//    }

    @Override
    public List<HelperDto.UserFullDto> getAllUsers() {
        List<HelperDto.UserFullDto> users = new ArrayList<>();
        userRepository.findAll().forEach(user -> {users.add(new HelperDto.UserFullDto(user));});
        return users;
    }
    /**
     * / Create user basic user profile details from auth service route
     */
//    private UserProfile createUserProfileFromAuth(
//            String email,
//            String authHeader
//    ) {
//        String authServiceUrl = Objects.requireNonNull(
//                environment.getProperty("AUTH_SERVICE_URL")
//        );
//        String basicProfileRoute = "/auth/users/profile/" + email;
//        WebClient webClient = WebClient.create(authServiceUrl);
//        HelperDto.UserProfileRequest userProfileRequest = null;
//        try {
//            userProfileRequest =
//                    webClient
//                            .get()
//                            .uri(basicProfileRoute)
//                            .header("authorization", authHeader)
//                            .retrieve()
//                            .bodyToMono(HelperDto.UserProfileRequest.class)
//                            .block();
//        } catch (Exception ex) {
//            throw new ResourceNotFoundException("User Profile Not found");
//        }
//        log.info("FetchedAuthUser: {}", userProfileRequest);
//        if (userProfileRequest == null) throw new ResourceNotFoundException(
//                "User Profile Not found"
//        );
//        return register(userProfileRequest);
//    }

}
