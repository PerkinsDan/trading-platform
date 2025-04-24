package com.setap.tradingplatformapi.Validations;

import io.vertx.core.json.JsonObject;

public interface Validation {

    Boolean validate(JsonObject body) throws Exception;

}