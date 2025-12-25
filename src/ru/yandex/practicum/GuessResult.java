package ru.yandex.practicum;

public class GuessResult {
    private final String attempt;
    private final String feedback;

    public GuessResult(String attempt, String feedback) {
        this.attempt = attempt;
        this.feedback = feedback;
    }

    public String getAttempt() {
        return attempt;
    }

    public String getFeedback() {
        return feedback;
    }

    @Override
    public String toString() {
        return attempt + " → " + feedback;
    }
}