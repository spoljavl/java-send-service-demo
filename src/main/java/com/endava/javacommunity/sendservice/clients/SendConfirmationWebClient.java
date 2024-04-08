package com.endava.javacommunity.sendservice.clients;

import com.endava.javacommunity.sendservice.data.model.Batch;
import com.endava.javacommunity.sendservice.data.request.SendConfirmationRequestDto;
import com.endava.javacommunity.sendservice.data.response.SendConfirmationResponseDto;
import com.endava.javacommunity.sendservice.mappers.CustomMapper;
import java.net.URI;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SendConfirmationWebClient {

  private final CustomMapper customMapper;
  private final WebClient webClient;
  private final String host;
  private final int port;
  private final String scheme;
  private final String path;

  public SendConfirmationWebClient(CustomMapper customMapper, WebClient webClient,
      @Value("${client.host}") String host,
      @Value("${client.port}") int port,
      @Value("${client.scheme}") String scheme,
      @Value("${client.path}") String path) {
    this.customMapper = customMapper;
    this.webClient = webClient;
    this.host = host;
    this.port = port;
    this.scheme = scheme;
    this.path = path;
  }

  public Mono<SendConfirmationResponseDto> batchedSend(Batch batch) {
    log.info("Sending {} batch...", batch.getCurrencySymbol());

    final URI uri = UriComponentsBuilder.newInstance()
        .scheme(scheme)
        .host(host)
        .port(port)
        .path(path)
        .build(Optional.empty());

    return Mono.defer(() -> webClient
        .post()
        .uri(uri)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .bodyValue(SendConfirmationRequestDto.builder().batchString(customMapper.serializeToJson(batch)).build())
        .retrieve()
        .onRawStatus(status -> status >= 500, clientResponse -> Mono.just(clientResponse)
            .doOnNext(cr -> log.error("Sending batch resulted in status {}", cr.statusCode()))
            .flatMap(cr -> cr.bodyToMono(String.class))
            .doOnNext(b -> log.error("Sending batch error body {}", b))
            .doOnError(e -> log.error("Sending batch error class: {} message: {}", e.getClass().getSimpleName(), e.getMessage()))
            .thenReturn(new RuntimeException(clientResponse.statusCode().toString())))
        .bodyToMono(SendConfirmationResponseDto.class));
  }

}
