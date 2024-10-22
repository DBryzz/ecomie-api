package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.service.SessionService;
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
@Tag(name = "SessionController", description = "This controller contains endpoints for sessions")
public class SessionController {

    private final SessionService sessionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/admin/sessions")
    @Operation(summary = "Create Session", description = "Create new session", tags = { "ADMIN" })
    protected ResponseMessage createSession(@RequestBody HelperDto.SessionCreateDto sessionCreateDto) {
        return sessionService.store(sessionCreateDto);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/admin/sessions/{sessionId}")
    @Operation(summary = "Update Session", description = "Update session", tags = { "ADMIN" })
    public ResponseMessage updateSession(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody HelperDto.SessionCreateDto sessionCreateDto) {
        return sessionService.update(sessionId, sessionCreateDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/user/sessions/{sessionId}")
    @Operation(summary = "Get Session", description = "Get session", tags = { "USER, ADMIN" })
    public HelperDto.SessionFullDto getSession(@PathVariable(name = "sessionId") UUID sessionId) {
        return sessionService.getSession(sessionId);
    }

    @GetMapping(value = "/user/sessions")
    @Operation(summary = "Get Sessions", description = "Get all sessions", tags = { "USER, ADMIN" })
    public ResponseEntity<List<HelperDto.SessionFullDto>> getSessions() {
        return ResponseEntity.ok(sessionService.getSessions());
    }

    @PutMapping(value = "/admin/sessions/{sessionId}/status")
    @Operation(summary = "Update Session Status", description = "Update session's status. valid values {INACTIVE, ONGOING, PAUSED, ENDED}", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> changeSessionStatus(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody HelperDto.RequestProps props ) {
        return new ResponseEntity<>(sessionService.changeStatus(sessionId, props.status()), HttpStatus.PARTIAL_CONTENT);
    }

    @PostMapping(value = "/admin/sessions/{sessionId}/assign/challenges")
    @Operation(summary = "Assign Challenges", description = "Assign challenges to session", tags = { "ADMIN" })
    public ResponseEntity<HelperDto.SessionFullDto> assignChallenges(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody HelperDto.RequestProps props) {
        return new ResponseEntity<>(sessionService.assignChallenges(sessionId, props.ids()), HttpStatus.PARTIAL_CONTENT);
    }
    @PostMapping(value = "/admin/sessions/{sessionId}/remove/challenges")
    @Operation(summary = "Remove Challenges", description = "Remove challenges from session", tags = { "ADMIN" })
    public ResponseEntity<HelperDto.SessionFullDto> removeChallenges(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody HelperDto.RequestProps props) {
        return new ResponseEntity<>(sessionService.removeChallenges(sessionId, props.ids()), HttpStatus.PARTIAL_CONTENT);
    }
    @PostMapping(value = "/admin/sessions/{sessionId}/assign/challenges/{challengeId}")
    @Operation(summary = "Assign Challenge", description = "Assign a challenge to session", tags = { "ADMIN" })
    public ResponseEntity<HelperDto.SessionFullDto> assignChallenge(@PathVariable(name = "sessionId") UUID sessionId, @PathVariable(name = "challengeId") UUID challengeId) {
        return new ResponseEntity<>(sessionService.assignChallenge(sessionId, challengeId), HttpStatus.PARTIAL_CONTENT);
    }
    @PostMapping(value = "/admin/sessions/{sessionId}/remove/challenges/{challengeId}")
    @Operation(summary = "Remove A Challenge", description = "Remove a challenge from session", tags = { "ADMIN" })
    public ResponseEntity<HelperDto.SessionFullDto> removeChallenge(@PathVariable(name = "sessionId") UUID sessionId, @PathVariable(name = "challengeId") UUID challengeId) {
        return new ResponseEntity<>(sessionService.removeChallenge(sessionId, challengeId), HttpStatus.PARTIAL_CONTENT);
    }

    @GetMapping(value = "/admin/users/{userId}/sessions")
    @Operation(summary = "Get Sessions", description = "Get all user's sessions", tags = { "ADMIN" })
    public ResponseEntity<List<HelperDto.SessionFullDto>> getUserSessions(@PathVariable(name = "userId") UUID userId) {
        return ResponseEntity.ok(sessionService.getUserSessions(userId));
    }

}
