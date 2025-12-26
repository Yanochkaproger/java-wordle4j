package ru.yandex.practicum;

import ru.yandex.practicum.exception.DictionaryLoadException;
import ru.yandex.practicum.exception.GameException;

import java.io.*;
import java.util.Scanner;

public class Wordle {
    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {
        PrintWriter log = null;
        try {
            // Создаём лог-файл
            log = new PrintWriter(new FileWriter(LOG_FILE, true), true); // autoflush
            log.println("\n=== Новая игра начата: " + new java.util.Date() + " ===");

            // Загрузка словаря
            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.loadDictionary(DICTIONARY_FILE, log);

            // Создаём игру
            WordleGame game = new WordleGame(dictionary, log);

            // Цикл игры
            Scanner scanner = new Scanner(System.in);
            System.out.println("🎮 Добро пожаловать в Wordle (Русская версия)!");
            System.out.println("Компьютер загадал 5-буквенное существительное.");
            System.out.println("У вас 6 попыток. Введите слово или нажмите Enter для подсказки.");
            System.out.println("Подсказка: + = верная позиция, ^ = есть, но не здесь, - = нет такой буквы.\n");

            while (!game.isGameOver()) {
                System.out.printf("Осталось: %d ходов, %d подсказок. Ваш ход (введите 'стоп' для выхода): ",
                        game.getStepsLeft(), game.getHintsLeft());

                String input = scanner.nextLine();

                if ("стоп".equalsIgnoreCase(input.trim()) || "exit".equalsIgnoreCase(input.trim())) {
                    System.out.println("\n Игра прервана. Загаданное слово было: " + game.getAnswer());
                    return;
                }

                try {
                    GuessResult result = game.makeGuess(input);

                    if ("(подсказка)".equals(result.getAttempt())) {
                        System.out.println("💡 Подсказка: " + result.getFeedback());
                    } else {
                        System.out.println("> " + result.getAttempt());
                        System.out.println("> " + result.getFeedback());
                        if (game.isSolved()) {
                            break;
                        }
                    }

                } catch (GameException e) {
                    System.out.println("❌ Ошибка: " + e.getMessage() + "\nПопробуйте снова.");
                    log.println("Пользовательская ошибка: " + e.getMessage());
                }
            }

            // Финал
            if (game.isSolved()) {
                System.out.println("\n🎉 Поздравляем! Вы угадали слово \"" + game.getAnswer() + "\"!");
            } else {
                System.out.println("\n😞 К сожалению, попытки и подсказки исчерпаны.");
                System.out.println("Загаданное слово: " + game.getAnswer());
            }

            // Вывод истории
            System.out.println("\n📜 История попыток:");
            for (GuessResult gr : game.getHistory()) {
                System.out.println(gr);
            }

        } catch (DictionaryLoadException e) {
            System.err.println("КРИТИЧЕСКАЯ ОШИБКА: " + e.getMessage());
            if (log != null) log.println("КРИТИЧЕСКАЯ ОШИБКА: " + e.getMessage());
            e.printStackTrace(System.err);
        } catch (IOException e) {
            System.err.println("Не удалось создать лог-файл: " + e.getMessage());
            e.printStackTrace(System.err);
        } catch (Exception e) {
            System.err.println("НЕПРЕДВИДЕННАЯ ОШИБКА: " + e.getMessage());
            if (log != null) {
                log.println("НЕПРЕДВИДЕННАЯ ОШИБКА: " + e.getMessage());
                e.printStackTrace(log);
            }
            e.printStackTrace(System.err);
        } finally {
            if (log != null) {
                log.println("=== Игра завершена ===\n");
                log.close();
            }
        }
    }
}
