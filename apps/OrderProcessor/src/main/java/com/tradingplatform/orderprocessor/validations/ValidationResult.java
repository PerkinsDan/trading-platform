package com.tradingplatform.orderprocessor.validations;

public class ValidationResult {

  public final boolean isValid;
  public final String errorMessage;

  private ValidationResult(boolean isValid, String errorMessage) {
    this.isValid = isValid;
    this.errorMessage = errorMessage;
  }

  public static ValidationResult ok() {
    return new ValidationResult(true, null);
  }

  public static ValidationResult fail(String errorMessage) {
    return new ValidationResult(false, errorMessage);
  }
}
