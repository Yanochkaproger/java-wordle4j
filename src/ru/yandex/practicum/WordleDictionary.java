package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class WordleDictionary {
    private final List<String> words;
    private final Set<Character> allLetters = new HashSet<>();

    public WordleDictionary(List<String> rawWords, PrintWriter log) {
        this.words = rawWords.stream()
                .map(this::normalize)
                .filter(this::isValidWord)
                .distinct()
                .collect(Collectors.toList());

        // уникальные буквы для оптимизации фильтрации
        for (String word : this.words) {
            for (char c : word.toCharArray()) {
                allLetters.add(c);
            }
        }
        log.println("Уникальные буквы в словаре: " + allLetters);
    }

    private String normalize(String word) {
        if (word == null) return "";
        return word.toLowerCase()
                .replace('ё', 'е')
                .replaceAll("[^а-я]", ""); // удаляем всё, кроме кириллицы
    }

    private boolean isValidWord(String word) {
        return word.length() == 5;
    }

    public List<String> getWords() {
        return new ArrayList<>(words); // иммутабельная копия
    }

    public boolean contains(String word) {
        return words.contains(normalize(word));
    }

    public String getRandomWord() {
        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст!");
        }
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

    // Фильтрация по истории ходов: учимся на подсказках
    public List<String> filterByGuesses(List<GuessResult> history) {
        List<String> candidates = new ArrayList<>(words);

        for (GuessResult guess : history) {
            candidates = filterByGuess(candidates, guess);
        }
        return candidates;
    }

    private List<String> filterByGuess(List<String> candidates, GuessResult guess) {
        String attempt = guess.getAttempt();
        String feedback = guess.getFeedback();

        List<String> filtered = new ArrayList<>();

        for (String candidate : candidates) {
            if (matchesFeedback(attempt, candidate, feedback)) {
                filtered.add(candidate);
            }
        }
        return filtered;
    }

    // Проверяет, могло ли слово `candidate` дать такую `feedback` на `attempt`
    private boolean matchesFeedback(String attempt, String candidate, String feedback) {
        // Требования:
        // '+' -> буква на позиции совпадает
        // '^' -> буква есть в слове, но не на этой позиции
        // '-' -> буквы нет вообще (или количество исчерпано)

        // Считаем частоту букв в кандидате (для учёта повторов)
        Map<Character, Integer> candidateFreq = new HashMap<>();
        for (char c : candidate.toCharArray()) {
            candidateFreq.merge(c, 1, Integer::sum);
        }

        // Шаг 1: проверим '+' — строгие совпадения
        for (int i = 0; i < 5; i++) {
            if (feedback.charAt(i) == '+') {
                if (attempt.charAt(i) != candidate.charAt(i)) {
                    return false;
                }
                // уменьшаем частоту — эта буква "занята"
                candidateFreq.merge(attempt.charAt(i), -1, Integer::sum);
            }
        }

        // Шаг 2: проверим '^' и '-'
        for (int i = 0; i < 5; i++) {
            char a = attempt.charAt(i);
            char f = feedback.charAt(i);

            if (f == '^') {
                // буква должна быть в кандидате, но не на этой позиции
                if (attempt.charAt(i) == candidate.charAt(i)) {
                    return false; // на этой позиции — нельзя
                }
                if (!candidateFreq.containsKey(a) || candidateFreq.get(a) <= 0) {
                    return false; // буквы нет или уже использована
                }
                candidateFreq.merge(a, -1, Integer::sum); // "забираем" одну штуку

            } else if (f == '-') {
                // буква должна отсутствовать (вообще или уже исчерпана)
                if (candidateFreq.getOrDefault(a, 0) > 0) {
                    // но если буква есть и не использована под '+'/^ — ошибка
                    // однако: если буква была использована только под '+' — ок
                    // → уже учтено в уменьшении частоты выше
                    // → если после всех '+'/^ у неё freq > 0 — значит, лишняя
                    return false;
                }
            }
        }

        // Доп. проверка: все '^' буквы должны быть реально в слове в нужном кол-ве
        // (это уже учтено в частотах выше — если не хватило, мы вернули false)

        return true;
    }

    public Set<Character> getAllLetters() {
        return new HashSet<>(allLetters);
    }
}
