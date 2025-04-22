package com.setap.tradingplatformapi.Validations;

import io.vertx.core.json.JsonObject;

interface Validation {

    Boolean validate(JsonObject body) throws Exception;

}