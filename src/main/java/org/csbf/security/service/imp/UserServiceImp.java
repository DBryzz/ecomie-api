package org.csbf.security.service.imp;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.constant.Role;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.exceptions.BaseException;
import org.csbf.security.exceptions.ResourceNotFoundException;
import org.csbf.security.model.User;
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

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final Environment environment;

    @Override
    public ResponseMessage updateAuthUserProfile(
            Optional<MultipartFile> file,
            String jsonData
    ) {
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        log.info("{}", authUser);
        User user = userRepository.findByEmail(authUser.getName())
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
        var user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("user not found"));
        if (!EnumUtils.isValidEnum(Role.class, role.toUpperCase()))
            throw new BadRequestException("Invalid session status");
        if(!user.getRoles().contains(role.toUpperCase()))
            user.setRoles(user.getRoles()+"-"+role.toUpperCase());

        var updatedUser = userRepository.save(user);
        return new ResponseMessage.SuccessResponseMessage("user role updated" + updatedUser.getRoles());
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
            if (
                    oldFile != null && !imageFileName.equals(oldFile)
            ) fileUploadService.deleteFile(oldFile);

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
//        String application_host = Objects.requireNonNull(
//                environment.getProperty("APPLICATION_HOST")
//        );
        /*String profilePictureLink =
                application_host + "/profile/user-profile-pic/" + user.getUserId();*/
        return new HelperDto.UserFullDto(user);
    }

    @Override
    public HelperDto.UserDto getAuthUserProfile() {
        log.info("TEST: In method");
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();
        log.info("{}", authUser);

        User user = userRepository.findByEmail(authUser.getName())
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
        Authentication authUser = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository
                .findByEmail(authUser.getName())
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
