package ru.yandex.practicum;

import ru.yandex.practicum.exception.DictionaryLoadException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    public WordleDictionary loadDictionary(String filename, PrintWriter log) throws DictionaryLoadException {
        log.println("Загрузка словаря из файла: " + filename);
        List<String> rawWords = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), "UTF-8"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                rawWords.add(line.trim());
            }
        } catch (IOException e) {
            throw new DictionaryLoadException("Не удалось прочитать файл словаря: " + filename, e);
        }

        log.println("Прочитано " + rawWords.size() + " строк из словаря.");

        WordleDictionary dictionary = new WordleDictionary(rawWords, log);
        log.println("Словарь обработан. Подходящих слов: " + dictionary.getWords().size());
        return dictionary;
    }
}