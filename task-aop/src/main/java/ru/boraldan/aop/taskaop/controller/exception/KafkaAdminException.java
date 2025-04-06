package ru.boraldan.aop.taskaop.controller.exception;

public class KafkaAdminException extends RuntimeException {
  public KafkaAdminException(String message) {
    super(message);
  }
}
