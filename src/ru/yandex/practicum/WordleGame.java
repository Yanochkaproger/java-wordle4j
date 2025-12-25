package ru.yandex.practicum;

import ru.yandex.practicum.exception.InvalidWordException;
import ru.yandex.practicum.exception.WordNotInDictionary;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {
    private final String answer;
    private int stepsLeft;
    private final WordleDictionary dictionary;
    private final List<GuessResult> history;
    private final PrintWriter log;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord();
        this.stepsLeft = 6;
        this.history = new ArrayList<>();
        log.println("Загадано слово: " + answer); // только в лог!
    }

    // Возвращает feedback для attempt
    public String giveFeedback(String attempt) {
        String normAttempt = normalize(attempt);
        if (!dictionary.contains(normAttempt)) {
            throw new WordNotInDictionary(attempt);
        }
        if (normAttempt.length() != 5) {
            throw new InvalidWordException(attempt);
        }

        return computeFeedback(normAttempt, answer);
    }

    private String normalize(String word) {
        if (word == null) return "";
        return word.toLowerCase()
                .replace('ё', 'е')
                .replaceAll("[^а-я]", "");
    }

    private String computeFeedback(String attempt, String answer) {
        StringBuilder feedback = new StringBuilder("-----"); // пока всё '-'
        int[] answerUsed = new int[5]; // 0 = свободно, 1 = занято под '+', 2 = занято под '^'


        for (int i = 0; i < 5; i++) {
            if (attempt.charAt(i) == answer.charAt(i)) {
                feedback.setCharAt(i, '+');
                answerUsed[i] = 1;
            }
        }

        //правильная буква, но не на месте
        for (int i = 0; i < 5; i++) {
            if (feedback.charAt(i) == '-') { // ещё не обработано
                char c = attempt.charAt(i);
                // ищем эту букву в ответе, где она ещё не занята
                for (int j = 0; j < 5; j++) {
                    if (answer.charAt(j) == c && answerUsed[j] == 0) {
                        feedback.setCharAt(i, '^');
                        answerUsed[j] = 2;
                        break;
                    }
                }
            }
        }

        return feedback.toString();
    }

    // Главный игровой ход
    public GuessResult makeGuess(String input) {
        if (input == null || input.trim().isEmpty()) {
            // Пользователь запросил подсказку!
            return suggestHint();
        }

        String normInput = normalize(input);
        if (normInput.length() != 5) {
            throw new InvalidWordException(input);
        }

        String feedback = giveFeedback(normInput); // это может кинуть исключение
        GuessResult result = new GuessResult(normInput, feedback);
        history.add(result);
        stepsLeft--;

        return result;
    }

    private GuessResult suggestHint() {
        List<String> candidates = dictionary.filterByGuesses(history);
        log.println("Подсказка: подходящих слов = " + candidates.size());

        String hint;
        if (candidates.isEmpty()) {
            log.println("⚠️ Нет подходящих слов! Возможно, противоречивая история.");
            hint = dictionary.getRandomWord(); // fallback
        } else {
            hint = candidates.get(new Random().nextInt(candidates.size()));
        }

        return new GuessResult("(подсказка)", hint);
    }

    public boolean isGameOver() {
        return stepsLeft <= 0 || isSolved();
    }

    public boolean isSolved() {
        if (history.isEmpty()) return false;
        GuessResult last = history.get(history.size() - 1);
        return "+++++".equals(last.getFeedback());
    }

    public int getStepsLeft() {
        return stepsLeft;
    }

    public String getAnswer() {
        return answer;
    }

    public List<GuessResult> getHistory() {
        return new ArrayList<>(history);
    }

    WordleGame(WordleDictionary dictionary, String fixedAnswer, PrintWriter log) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = normalize(fixedAnswer);
        this.stepsLeft = 6;
        this.history = new ArrayList<>();
        log.println("[TEST] Загадано слово: " + this.answer);
    }
}