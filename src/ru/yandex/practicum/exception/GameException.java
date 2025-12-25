package ru.yandex.practicum.exception;

public class GameException extends RuntimeException {

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}
//базовое непроверяемое игровое исключение