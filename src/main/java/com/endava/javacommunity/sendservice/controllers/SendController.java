package com.endava.javacommunity.sendservice.controllers;

import com.endava.javacommunity.sendservice.data.request.SendRequestDto;
import com.endava.javacommunity.sendservice.data.response.SendResponseDto;
import com.endava.javacommunity.sendservice.services.SendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/send")
public class SendController {

  private final SendService sendService;

  public SendController(SendService sendService) {
    this.sendService = sendService;
  }

  @PostMapping("/transaction")
  public Mono<SendResponseDto> sendTransaction(@RequestBody SendRequestDto body) {
    return Mono.fromRunnable(() -> log.info("POST /send/transaction called with {}", body))
        .then(sendService.sendTransaction(body))
        .doOnNext(response -> log.info("POST /send/transaction returning: {}", response.getMessage()));
  }

}
