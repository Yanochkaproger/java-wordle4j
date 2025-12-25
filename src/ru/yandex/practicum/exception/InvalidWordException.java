package ru.yandex.practicum.exception;

public class InvalidWordException extends GameException {
  public InvalidWordException(String word) {
    super("Некорректное слово: \"" + word + "\". Оно должно состоять из 5 русских букв.");
  }
}