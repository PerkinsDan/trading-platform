package com.setap.tradingplatformapi.Validations;

import io.vertx.core.json.JsonObject;

@FunctionalInterface
public interface Validation {

    ValidationResult validate(JsonObject body);

}