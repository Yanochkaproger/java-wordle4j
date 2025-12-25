package ru.yandex.practicum.exception;

public class WordNotInDictionary extends GameException {
    public WordNotInDictionary(String word) {
        super("Слово \"" + word + "\" отсутствует в словаре.");
    }
}
