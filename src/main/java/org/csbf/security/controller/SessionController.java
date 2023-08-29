package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
@RequestMapping("/api/v1")
@SecurityRequirement(name = "ApiKey")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "SessionController", description = "This controller contains endpoints for sessions")
public class SessionController {

    private final SessionService sessionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/secure/admin/session")
    @Operation(summary = "Create Session", description = "Create new session", tags = { "ADMIN" })
    protected ResponseMessage createSession(@RequestBody HelperDto.SessionCreateDto sessionCreateDto) {
        return sessionService.store(sessionCreateDto);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/secure/admin/session/{sessionId}")
    @Operation(summary = "Update Session", description = "Update session", tags = { "ADMIN" })
    public ResponseMessage updateSession(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody HelperDto.SessionCreateDto sessionCreateDto) {
        return sessionService.update(sessionId, sessionCreateDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/secure/user/session/{sessionId}")
    @Operation(summary = "Get Session", description = "Get session", tags = { "USER" })
    public HelperDto.SessionFullDto getSession(@PathVariable(name = "sessionId") UUID sessionId) {
        return sessionService.getSession(sessionId);
    }

    @GetMapping(value = "/secure/admin/sessions")
    @Operation(summary = "Get Sessions", description = "Get all sessions", tags = { "ADMIN" })
    public ResponseEntity<List<HelperDto.SessionFullDto>> getSessions() {
        return ResponseEntity.ok(sessionService.getSessions());
    }

    @PostMapping(value = "/secure/admin/session-status/{sessionId}")
    @Operation(summary = "Update Session Status", description = "Update session's status. valid values {INACTIVE, ONGOING, PAUSED, ENDED}", tags = { "ADMIN" })
    public ResponseEntity<ResponseMessage> changeSessionStatus(@PathVariable(name = "sessionId") UUID sessionId, @RequestParam String status) {
        return new ResponseEntity<>(sessionService.changeStatus(sessionId, status), HttpStatus.PARTIAL_CONTENT);
    }

    @PostMapping(value = "/secure/admin/session/{sessionId}/assign-challenges")
    @Operation(summary = "Assign Challenges", description = "Assign challenges to session", tags = { "ADMIN" })
    public ResponseEntity<HelperDto.SessionFullDto> assignChallenges(@PathVariable(name = "sessionId") UUID sessionId, @RequestParam UUID[] challengeIds) {
        return new ResponseEntity<>(sessionService.assignChallenges(sessionId, challengeIds), HttpStatus.PARTIAL_CONTENT);
    }

    @GetMapping(value = "/secure/admin/session/user/{userId}")
    @Operation(summary = "Get Sessions", description = "Get all user's sessions", tags = { "ADMIN" })
    public ResponseEntity<List<HelperDto.SessionFullDto>> getUserSessions(@PathVariable(name = "userId") UUID userId) {
        return ResponseEntity.ok(sessionService.getUserSessions(userId));
    }

}
