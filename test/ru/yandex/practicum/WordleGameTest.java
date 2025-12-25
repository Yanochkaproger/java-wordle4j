package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {
    private WordleGame game;
    private WordleDictionary dictionary;

    @BeforeEach
    void setUp() {
        // задаём слова — все в нижнем регистре, 5 букв, без мусора
        List<String> words = Arrays.asList("герой", "гонец", "мечта", "песок", "домик");
        dictionary = new WordleDictionary(words, new PrintWriter(System.out));

        // Отладка: проверяем, что слова прошли нормализацию
        System.out.println("Словарь содержит 'герой'? " + dictionary.contains("герой"));
        System.out.println("Словарь содержит 'Герой'? " + dictionary.contains("Герой"));
        System.out.println("Все слова в словаре: " + dictionary.getWords());

        //package-private конструктор для тестов
        game = new WordleGame(dictionary, "герой", new PrintWriter(System.out));
    }

    @Test
    void testFeedback() {
        // Проверяем напрямую через giveFeedback
        assertEquals("+++++", game.giveFeedback("герой"), "Слово 'герой' должно давать '+++++'");
        assertEquals("+^-^-", game.giveFeedback("гонец"), "Слово 'гонец' должно давать '+^-^-' (по ТЗ)");
    }

    @Test
    void testWin() {
        // слово должно быть принято и игра — выиграна
        game.makeGuess("герой");

        assertTrue(game.isSolved(), "После угадывания 'герой' игра должна быть решена");
        assertEquals(5, game.getStepsLeft(), "Должно остаться 5 попыток (6 - 1)");
    }

    @Test
    void testInvalidWord() {
        // Некорректные слова
        assertThrows(Exception.class, () -> game.giveFeedback("abcde"), "Латиница недопустима");
        assertThrows(Exception.class, () -> game.giveFeedback("просто"), "Слово длиннее 5 букв");
        assertThrows(Exception.class, () -> game.giveFeedback("пр"), "Слово короче 5 букв");
        assertThrows(Exception.class, () -> game.giveFeedback(""), "Пустая строка недопустима");
    }
}