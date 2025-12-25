package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    @Test
    void testBasicFunctionality() {
        // Создаём минимальный словарь
        List<String> words = List.of("герой", "гонец");
        WordleDictionary dict = new WordleDictionary(words, new PrintWriter(System.out));

        // Проверяем базовое: слова есть
        assertTrue(dict.contains("герой"));
        assertTrue(dict.contains("ГОНЕЦ"));

        // Проверяем размер
        assertEquals(2, dict.getWords().size());

        // Проверяем, что случайное слово — одно из двух
        String random = dict.getRandomWord();
        assertTrue(random.equals("герой") || random.equals("гонец"));
    }
}