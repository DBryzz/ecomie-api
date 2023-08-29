package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.event.OnRegistrationCompleteEvent;
import org.csbf.security.service.AuthenticationService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "AuthenticationController", description = "This controller contains endpoints for authentication")
public class AuthenticationController {
    private final AuthenticationService service;
    private final ApplicationEventPublisher applicationEventPublisher;



    @PostMapping("/register")
    @Operation(summary = "Register", description = "Create account", tags = {"Unauthenticated"})
    public ResponseEntity<HelperDto.AuthenticationResponse> register(@RequestBody HelperDto.RegisterRequest request, HttpServletRequest servletRequest) {
        /**For Production */
        /*
        * if (EmailValidator.getInstance().isValid(request.email()))
        *    throw new BadRequestException("Invalid '" + request.email() + "' email address");
        * */

        HelperDto.AuthenticationResponse response = service.register(request);
        log.info("{}", response.toString());
        if (response.success()) {
            applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(servletRequest.getHeader("host"), request.email()));
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Authenticate", description = "Authenticate user using email and password", tags = {"Unauthenticated"})
    public ResponseEntity<HelperDto.AuthenticationResponse> authenticate(@RequestBody HelperDto.AuthenticationRequest request, HttpServletRequest servletRequest) {
        String appUrl = servletRequest.getContextPath();

        /**For Production */
        /*
         * if (EmailValidator.getInstance().isValid(request.email()))
         *    throw new BadRequestException("Invalid '" + request.email() + "' email address");
         * */

        HelperDto.AuthenticationResponse response = service.authenticate(request);
        var user = response.user();
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "Confirm Account", description = "Confirm users email address", tags = {"Unauthenticated"})
    public ResponseEntity<HelperDto.ConfirmEmailResponse> confirmUserAccount(@Email @RequestParam("email") String email, @RequestParam("token") String token) {
        log.info("Email {}", email);
        return ResponseEntity.ok(service.confirmEmail(email, token));
    }

    @RequestMapping(value = "/resend-link", method = {RequestMethod.POST})
    @Operation(summary = "Resend Confirmation Link", description = "Sends confirmation link to the email sent in request body", tags = {"Unauthenticated"})
    public ResponseEntity<ResponseMessage> resendUserEmailConfirmationLink(@RequestBody HelperDto.ResendVerificationEmailDTO emailDTO, HttpServletRequest servletRequest) {
        log.info("Email {}", emailDTO);
        /**For Production */
        /*
         * if (EmailValidator.getInstance().isValid(emailDTO.email()))
         *    throw new BadRequestException("Invalid '" + emailDTO.email() + "' email address");
         * */

        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(servletRequest.getHeader("host"), emailDTO.email()));

        return ResponseEntity.ok(new ResponseMessage.SuccessResponseMessage("email resent"));
    }
}
