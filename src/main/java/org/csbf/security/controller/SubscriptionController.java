package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.service.SubscriptionService;
import org.csbf.security.utils.helperclasses.HelperDto;
import org.csbf.security.utils.helperclasses.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/secure")
@SecurityRequirement(name = "ApiKey")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "SubscriptionController", description = "This controller contains endpoints for subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    @PostMapping(value = "/ecomiest/subscriptions")
    @Operation(summary = "Create Subscriptions", description = "Create new subscription", tags = { "ECOMIEST" })
    protected ResponseEntity<HelperDto.SubscriptionFullDto> createSubscription(@RequestBody HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
        return new ResponseEntity<>(subscriptionService.subscribe(subscriptionCreateDto), HttpStatus.CREATED);
    }

    @PostMapping(value = "/admin/subscriptions/user/{userId}")
    @Operation(summary = "Subscribe User", description = "Add a user to a session", tags = { "ADMIN" })
    public ResponseEntity<HelperDto.SubscriptionFullDto> subscribeUser(@PathVariable(name = "userId") UUID userId, @RequestBody HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
        return new ResponseEntity<>(subscriptionService.subscribeUser(userId, subscriptionCreateDto), HttpStatus.PARTIAL_CONTENT);
    }

    @DeleteMapping(value = "/admin/subscriptions/{subscriptionId}")
    @Operation(summary = "Unsubscribe User", description = "Delete/Remove subscription", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> removeUserFromSession(@PathVariable(name = "subscriptionId") UUID subscriptionId) {
        return new ResponseEntity<>(subscriptionService.unSubscribeUser(subscriptionId), HttpStatus.PARTIAL_CONTENT);
    }

    @DeleteMapping(value = "/admin/subscriptions/{sessionId}/unsubscribe/user/{userId}")
    @Operation(summary = "Unsubscribe User", description = "Remove a user from a session", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> removeUserFromSession(@PathVariable(name = "sessionId") UUID sessionId, @PathVariable(name = "userId") UUID userId) {
        return new ResponseEntity<>(subscriptionService.removeUserFromSession(sessionId, userId), HttpStatus.PARTIAL_CONTENT);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/admin/subscriptions/{subscriptionId}")
    @Operation(summary = "Update Subscription", description = "Update subscription", tags = { "ADMIN" })
    public HelperDto.SubscriptionFullDto updateSubscription(@PathVariable(name = "subscriptionId") UUID subscriptionId, @RequestBody HelperDto.SubscriptionCreateDto subscriptionCreateDto) {
        return subscriptionService.update(subscriptionId, subscriptionCreateDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/ecomiest/subscriptions/{subscriptionId}")
    @Operation(summary = "Get Subscription", description = "Get subscription", tags = { "ECOMIEST" })
    public HelperDto.SubscriptionFullDto getSubscription(@PathVariable(name = "subscriptionId") UUID subscriptionId) {
        return subscriptionService.getSubscription(subscriptionId);
    }

    @GetMapping(value = "/admin/subscriptions")
    @Operation(summary = "Get Subscriptions", description = "Get all subscriptions", tags = { "ADMIN" })
    public ResponseEntity<List<HelperDto.SubscriptionFullDto>> getSubscriptions() {
        return ResponseEntity.ok(subscriptionService.getSubscriptions());
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/ecomiest/subscriptions/sessions/{sessionId}")
    @Operation(summary = "Get Subscription", description = "Get subscription", tags = { "ECOMIEST" })
    public HelperDto.SubscriptionFullDto getSessionSubscription(@PathVariable(name = "sessionId") UUID sessionId) {
        return subscriptionService.getSessionSubscription(sessionId);
    }


}
