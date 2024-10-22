package org.csbf.security.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csbf.security.service.ChallengeReportService;
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
@Tag(name = "ChallengeReportController", description = "This controller contains endpoints for reports")
public class ChallengeReportController {

private final ChallengeReportService reportService;
    @PostMapping(value = "/ecomiest/report/session/{sessionId}")
    @Operation(summary = "Create Challenge Report", description = "Create new report", tags = { "ECOMIEST" })
    protected ResponseEntity<ResponseMessage> createChallengeReport(@PathVariable(name = "sessionId") UUID sessionId, @RequestBody HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
        return new ResponseEntity<>(reportService.storeReport(sessionId, challengeReportCreateDto), HttpStatus.CREATED);
    }

    @PostMapping(value = "/admin/report/user/{userId}/session/{sessionId}")
    @Operation(summary = "Create Challenge Report", description = "Create new report", tags = { "ADMIN" })
    protected ResponseEntity<ResponseMessage> createChallengeReportForUser(@PathVariable(name = "userId") UUID userId, @PathVariable(name = "sessionId") UUID sessionId, @RequestBody HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
        return new ResponseEntity<>(reportService.storeUserReport(userId, sessionId, challengeReportCreateDto), HttpStatus.CREATED);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/ecomiest/report/{reportId}")
    @Operation(summary = "Update Challenge Report", description = "Update challenge report", tags = { "ECOMIEST" })
    public ResponseMessage updateChallengeReport(@PathVariable(name = "reportId") UUID reportId, @RequestBody HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
        return reportService.updateChallengeReport(reportId, challengeReportCreateDto);
    }

    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @PostMapping(value = "/admin/report/{reportId}")
    @Operation(summary = "Update Challenge Report", description = "Update challenge report", tags = { "ADMIN" })
    public ResponseMessage updateChallengeReportForUser(@PathVariable(name = "reportId") UUID reportId, @RequestBody HelperDto.ChallengeReportCreateDto challengeReportCreateDto) {
        return reportService.updateChallengeReportForUser(reportId, challengeReportCreateDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/ecomiest/report/{reportId}")
    @Operation(summary = "Get Challenge Report", description = "Get challenge report", tags = { "ECOMIEST" })
    public HelperDto.ChallengeReportFullDto getChallengeReport(@PathVariable(name = "reportId") UUID reportId) {
        return reportService.getChallengeReport(reportId);
    }

    @GetMapping(value = "/admin/reports")
    @Operation(summary = "Get Challenge Reports", description = "Get all challenge reports", tags = { "ADMIN" })
    public ResponseEntity<List<HelperDto.ChallengeReportFullDto>> getChallengeReports() {
        return ResponseEntity.ok(reportService.getChallengeReports());
    }
}
