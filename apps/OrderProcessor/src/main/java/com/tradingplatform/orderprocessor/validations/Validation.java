package com.tradingplatform.orderprocessor.validations;

import io.vertx.core.json.JsonObject;

@FunctionalInterface
public interface Validation {
  ValidationResult validate(JsonObject body);
}
