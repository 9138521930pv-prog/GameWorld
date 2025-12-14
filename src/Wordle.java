import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Wordle {
    public static void main(String[] args) {
        System.out.println("Текущая рабочая папка: " + new File("").getAbsolutePath());

        new File("logs").mkdirs();

        try (
                PrintWriter log = new PrintWriter(
                        new OutputStreamWriter(
                                new FileOutputStream("logs/game.log", true), StandardCharsets.UTF_8), true
                );
                Scanner scanner = new Scanner(System.in)
        ) {
            log.println("=== Новая игра начата ===");
            // Загружаем словарь
            WordleDictionaryLoader loader = new WordleDictionaryLoader();
            WordleDictionary dictionary = loader.load("src/words.txt", log);
            log.println("Словарь загружен. Размер: " + dictionary.getWords().size());

            WordleGame game = new WordleGame(dictionary, log);

            System.out.println("Добро пожаловать в Wordle У вас 6 попыток.");
            System.out.println("Введите 5-буквенное слово. Нажмите Enter для подсказки.");

            while (!game.isGameOver()) {
                System.out.print("> ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    String suggestion = game.getSuggestion();
                    System.out.println("Подсказка: " + suggestion);
                    continue;
                }

                if (input.length() != 5 || !input.matches("[а-яА-ЯёЁ]+")) {
                    System.out.println("Введите корректное 5-буквенное слово на русском.");
                    continue;
                }

                String word = WordleDictionary.normalize(input);

                try {
                    game.makeGuess(word);
                    System.out.println(word);
                    System.out.println(game.getLastFeedback()); // ✅ Правильно: последняя подсказка
                } catch (InvalidWordException e) {
                    System.out.println("Слово не найдено в словаре.");
                    log.println("Неверное слово: " + word);
                }
            }

            if (game.isWin()) {
                System.out.println("🎉 Поздравляем Вы угадали слово!");
            } else {
                System.out.println("💀 К сожалению, попытки закончились.");
                System.out.println("Загаданное слово: " + game.getTarget()); // ✅ Должен быть метод getTarget()
            }

            log.println("Игра завершена. Победа: " + game.isWin());

        } catch (FileNotFoundException e) {
            System.err.println("Ошибка: не найден файл словаря. Проверьте путь: src/words.txt");
            System.err.println("Текущая папка: " + new File("").getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Ошибка ввода-вывода: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Неизвестная ошибка: " + e.getMessage());
            e.printStackTrace();
        }
        // PrintWriter и Scanner автоматически закрыты благодаря try-with-resources
    }
}

class InvalidWordException extends Exception {
    public InvalidWordException(String message) {
        super(message);
    }
}
