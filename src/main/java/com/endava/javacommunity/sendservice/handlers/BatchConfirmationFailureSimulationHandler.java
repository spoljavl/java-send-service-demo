package com.endava.javacommunity.sendservice.handlers;

import com.endava.javacommunity.sendservice.annotations.SimulateBatchConfirmationFailure;
import com.endava.javacommunity.sendservice.data.model.Batch;
import com.endava.javacommunity.sendservice.mappers.CustomMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class BatchConfirmationFailureSimulationHandler {

  private final CustomMapper customMapper;

  public BatchConfirmationFailureSimulationHandler(CustomMapper customMapper) {
    this.customMapper = customMapper;
  }

  @Around("@annotation(com.endava.javacommunity.sendservice.annotations.SimulateBatchConfirmationFailure)")
  public Object simulateBatchConfirmationFailure(ProceedingJoinPoint joinPoint) throws Throwable {
    log.debug("Simulating batch confirmation failure...");

    String batchString = Arrays.stream(joinPoint.getArgs())
        .filter(obj -> obj instanceof String)
        .map(obj -> (String) obj)
        .findFirst()
        .orElse(null);

    Batch batch = customMapper.deserializeBatch(batchString);

    SimulateBatchConfirmationFailure simulateBatchConfirmationFailure = (((MethodSignature) joinPoint.getSignature()).getMethod()).getAnnotation(
        SimulateBatchConfirmationFailure.class);

    if (batch != null) {
      if (batch.getResends() != simulateBatchConfirmationFailure.resends() || isBatchWithoutFailureAmount(batch,
          simulateBatchConfirmationFailure.amount())) {
        return joinPoint.proceed();
      } else {
        throw new RuntimeException("Simulation failure");
      }
    }

    return joinPoint.proceed();
  }

  private boolean isBatchWithoutFailureAmount(Batch batch, double amount) {
    return batch.getSends().stream()
        .noneMatch(send -> send.getAmount().compareTo(new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP)) == 0);
  }

}
