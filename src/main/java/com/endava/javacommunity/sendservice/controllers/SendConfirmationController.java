package com.endava.javacommunity.sendservice.controllers;

import com.endava.javacommunity.sendservice.data.request.SendConfirmationRequestDto;
import com.endava.javacommunity.sendservice.data.response.SendConfirmationResponseDto;
import com.endava.javacommunity.sendservice.services.SendConfirmationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/confirm")
public class SendConfirmationController {

  private final SendConfirmationService sendConfirmationService;

  public SendConfirmationController(SendConfirmationService sendConfirmationService) {
    this.sendConfirmationService = sendConfirmationService;
  }

  @PostMapping("/batch")
  public Mono<SendConfirmationResponseDto> confirmBatch(@RequestBody SendConfirmationRequestDto body) {
    return Mono.fromRunnable(() -> log.info("POST /confirm/batch called with {}", body))
        .then(sendConfirmationService.confirmBatchedSend(body.getBatchString()))
        .doOnNext(response -> log.info("POST /confirm/batch returning: {}", response.isConfirmed()));
  }

}
