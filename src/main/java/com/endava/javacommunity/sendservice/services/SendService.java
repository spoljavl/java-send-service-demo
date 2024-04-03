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
        .switchIfEmpty(sendHandler.addSend(request.getAccountId(), request.getTransactionId(), request.getIban(), request.getAmount(),
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

    notBlank(request.getAccountId(), "accountId", errors);
    notBlank(request.getTransactionId(), "transactionId", errors);
    notBlank(request.getIban(), "iban", errors);
    notZero(request.getAmount(), "amount", errors);
    notBlank(request.getCurrencySymbol(), "currencySymbol", errors);

    if (request.getIban() != null && !request.getIban().isBlank() && !ibanValidator.isSupportedIban(request.getIban())) {
      errors.add(String.format("Unsupported iban:  %s", request.getIban()));
    }

    if (request.getCurrencySymbol() != null && !request.getCurrencySymbol().isBlank() && !currencyValidator.isSupportedCurrency(
        request.getCurrencySymbol())) {
      errors.add(String.format("Unsupported currency:  %s", request.getCurrencySymbol()));
    }

    return errors;
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
