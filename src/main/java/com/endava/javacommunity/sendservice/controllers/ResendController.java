package com.endava.javacommunity.sendservice.controllers;

import com.endava.javacommunity.sendservice.data.response.FailedBatchResponseDto;
import com.endava.javacommunity.sendservice.services.FailedBatchService;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/resend")
public class ResendController {

  private final FailedBatchService failedBatchService;

  public ResendController(FailedBatchService failedBatchService) {
    this.failedBatchService = failedBatchService;
  }

  @GetMapping("/failedBatches")
  public Flux<FailedBatchResponseDto> findFailedBatches(
      @RequestParam(value = "batchId") Optional<String> batchId,
      @RequestParam(value = "currencySymbol") Optional<String> currencySymbol) {
    return Mono.fromRunnable(() -> log.info("POST /resend/failedBatches called"))
        .thenMany(failedBatchService.findFailedBatches(batchId, currencySymbol));
  }

  @PostMapping("/batch/{batchId}")
  public Mono<Void> resendBatch(@PathVariable String batchId) {
    return Mono.fromRunnable(() -> log.info("POST /resend/batch called with {}", batchId))
        .then(failedBatchService.resendBatch(batchId));
  }

}
