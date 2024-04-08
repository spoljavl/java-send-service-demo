package com.endava.javacommunity.sendservice.services;

import com.endava.javacommunity.sendservice.data.request.SendRequestDto;
import com.endava.javacommunity.sendservice.data.response.SendResponseDto;
import com.endava.javacommunity.sendservice.handlers.SendHandler;
import com.endava.javacommunity.sendservice.validators.CurrencyValidator;
import com.endava.javacommunity.sendservice.validators.IbanValidator;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SendService {

  private final IbanValidator ibanValidator;
  private final CurrencyValidator currencyValidator;
  private final SendHandler sendHandler;

  public SendService(IbanValidator ibanValidator, CurrencyValidator currencyValidator, SendHandler sendHandler) {
    this.ibanValidator = ibanValidator;
    this.currencyValidator = currencyValidator;
    this.sendHandler = sendHandler;
  }

  public Mono<SendResponseDto> sendTransaction(SendRequestDto request) {
    return validateRequest(request)
        .switchIfEmpty(sendHandler.addSend(UUID.randomUUID().toString(), request.getSenderIban(), request.getRecipientIban(), request.getAmount(),
                request.getCurrencySymbol())
            .thenReturn("send transaction submitted"))
        .map(message -> SendResponseDto.builder().message(message).build());
  }

  private Mono<String> validateRequest(SendRequestDto request) {
    final List<String> errors = getErrors(request);

    if (!errors.isEmpty()) {
      log.warn("Validation errors: {}", getErrorString(errors));
      return Mono.just(String.format("validation failed with %d error(s)", errors.size()));
    }

    return Mono.empty();
  }

  private List<String> getErrors(SendRequestDto request) {
    final List<String> errors = new ArrayList<>();

    notBlank(request.getSenderIban(), "senderIban", errors);
    notBlank(request.getRecipientIban(), "recipientIban", errors);
    notEquals(request.getSenderIban(), request.getRecipientIban(), "senderIban", "recipientIban", errors);
    notZero(request.getAmount(), "amount", errors);
    notBlank(request.getCurrencySymbol(), "currencySymbol", errors);

    if (request.getSenderIban() != null && !request.getSenderIban().isBlank() && !ibanValidator.isSupportedIban(request.getSenderIban())) {
      errors.add(String.format("Unsupported iban:  %s", request.getSenderIban()));
    }

    if (request.getRecipientIban() != null && !request.getRecipientIban().isBlank() && !ibanValidator.isSupportedIban(request.getRecipientIban())) {
      errors.add(String.format("Unsupported iban:  %s", request.getRecipientIban()));
    }

    if (request.getCurrencySymbol() != null && !request.getCurrencySymbol().isBlank() && !currencyValidator.isSupportedCurrency(
        request.getCurrencySymbol())) {
      errors.add(String.format("Unsupported currency:  %s", request.getCurrencySymbol()));
    }

    return errors;
  }

  private static void notEquals(String value1, String value2, String fieldName1, String fieldName2, List<String> errors) {
    final String message = "Field '%s' can't be the same as field '%s'";
    if (value1 != null && value1.equalsIgnoreCase(value2)) {
      errors.add(String.format(message, fieldName1, fieldName2));
    }
  }

  private static void notBlank(String value, String fieldName, List<String> errors) {
    final String message = "Field '%s' can't be empty or null";
    if (value == null || value.isBlank()) {
      errors.add(String.format(message, fieldName));
    }
  }

  private static void notZero(BigDecimal value, String fieldName, List<String> errors) {
    final String message = "Field '%s' can't be null and needs to be higher then zero";
    if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
      errors.add(String.format(message, fieldName));
    }
  }

  private static String getErrorString(List<String> errors) {
    StringJoiner joiner = new StringJoiner("; ");
    for (String error : errors) {
      joiner.add(error);
    }
    return joiner.toString();
  }

}
