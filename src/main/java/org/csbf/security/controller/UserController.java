package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.csbf.security.constant.Role;
import org.csbf.security.constant.SessionStatus;
import org.csbf.security.event.OnRoleChangeRequestEvent;
import org.csbf.security.exceptions.BadRequestException;
import org.csbf.security.service.UserService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/secure")
@SecurityRequirement(name = "ApiKey")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "UserController", description = "This controller contains endpoints for users")
public class UserController {

    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;


    @PostMapping(value = "/user/update", consumes = { "multipart/form-data" }, produces = { "application/json" })
    @Operation(summary = "Edit User Profile", description = "Modify currently authenticated user's profile information", tags = { "USER" })
    public ResponseEntity<ResponseMessage> updateAuthUserProfile( @RequestParam("image") Optional<MultipartFile> file, @RequestParam("jsonData") String jsonData) {
        return new ResponseEntity<>(userService.updateAuthUserProfile(file, jsonData), HttpStatus.CREATED);
    }

    @PostMapping(value = "/admin/update/user/{userId}", consumes = { "multipart/form-data" }, produces = { "application/json" })
    @Operation(summary = "Edit User Profile", description = "Modify user's profile information using his id", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> updateUserProfile(@PathVariable(name = "userId") UUID userId, @RequestParam("image") Optional<MultipartFile> file, @RequestParam("jsonData") String jsonData) {
        return new ResponseEntity<>(userService.updateUserProfile(userId, file, jsonData), HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @GetMapping(value = "/admin/update-user-role")
    @Operation(summary = "Assign New Role", description = "Change user's role using userId", tags = { "ADMIN" })
    public ResponseMessage updateUserRole(@RequestParam("email") String email, @RequestParam("role") String role) { return userService.changeUserRole(email, role); }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/admin/user/{userId}")
    @Operation(summary = "Get User Profile", description = "Get user's profile information using userId", tags = { "ADMIN" })
    public HelperDto.UserFullDto getUserProfile(@PathVariable(name = "userId") UUID userId) { return userService.getUserProfile(userId); }

//    @PreAuthorize("hasAuthority('USER') ")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/user/get")
    @Operation(summary = "Get User Profile", description = "Get currently authenticated user's information", tags = { "USER" })
    public HelperDto.UserDto getAuthUserProfile() {
        log.info("TEST: In Controller");

        return userService.getAuthUserProfile();
    }

    @GetMapping("/admin/user-pix/{userId}")
    @Operation(summary = "Get User's Profile Image", description = "Get user's profile image using userId", tags = { "ADMIN" })
    public ResponseEntity<byte[]> getUserProfileImage(@PathVariable(name = "userId") UUID userId) {
        return new ResponseEntity<>(userService.getUserProfilePicture(userId), HttpStatus.OK);
    }

    @GetMapping("/user/pix")
    @Operation(summary = "Get User's Profile Image", description = "Get currently auth user's profile image", tags = { "USER" })
    public ResponseEntity<byte[]> getProfileImage() {
        return new ResponseEntity<>(userService.getProfilePicture(), HttpStatus.OK);
    }

    @DeleteMapping("/admin/user-pix/{userId}/del")
    @Operation(summary = "Delete User's Profile Image", description = "Delete user's profile image using userId", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> deleteProfileImage(@PathVariable(name = "userId") UUID userId) {
        userService.deleteUserProfilePic(userId);
        return new ResponseEntity<>(new ResponseMessage.SuccessResponseMessage("Image deleted"), HttpStatus.PARTIAL_CONTENT);
    }

    @DeleteMapping("/admin/del/user/{userId}")
    @Operation(summary = "Delete User's Profile Image", description = "Delete user using userId", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> hardDeleteProfile(@PathVariable(name = "userId") UUID userId) {
        userService.deleteUserProfile(userId);
        return new ResponseEntity<>(new ResponseMessage.SuccessResponseMessage("User deleted"), HttpStatus.PARTIAL_CONTENT);
    }

    @PostMapping("/admin/soft-del/user/{userId}")
    @Operation(summary = "Soft delete User's Profile Image", description = "Soft delete user using userId", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> softDeleteProfile(@PathVariable(name = "userId") UUID userId) {
        return new ResponseEntity<>(userService.softDelete(userId), HttpStatus.PARTIAL_CONTENT);
    }

    @PostMapping("/user/request-to-become/{role}")
    @Operation(summary = "Soft delete User's Profile Image", description = "Soft delete user using userId", tags = { "USER" })
    public ResponseEntity<ResponseMessage> becomeAn(@RequestParam("email") @Email String email, @PathVariable(name = "role") String role, HttpServletRequest servletRequest) {
        if (!EnumUtils.isValidEnum(Role.class, role.toUpperCase()))
            throw new BadRequestException("Invalid user role");
        applicationEventPublisher.publishEvent(new OnRoleChangeRequestEvent(servletRequest.getHeader("host"), email, role));

        return new ResponseEntity<>(new ResponseMessage.SuccessResponseMessage("Request sent successfully"), HttpStatus.OK);
    }

    @GetMapping("/admin/users/session/{sessionId}")
    @Operation(summary = "Get Session Users", description = "Get all blocked and/or unblocked users in a session", tags = { "ADMIN" })
    public ResponseEntity<List<HelperDto.UserFullDto>> getUsersInASession(@PathVariable(name = "sessionId") UUID sessionId, @RequestParam(name = "blocked") boolean blocked, @RequestParam(name = "challengeId") Optional<UUID> optionalChallengeId) {
        return new ResponseEntity<>(userService.getUsersInASession(sessionId, blocked, optionalChallengeId), HttpStatus.OK);
    }

    @GetMapping("/admin/users")
    @Operation(summary = "Get All Users", description = "Get all users in the system", tags = { "ADMIN" })
    public ResponseEntity<List<HelperDto.UserFullDto>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }
}
