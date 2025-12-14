import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {
    public WordleDictionary load(String filename, java.io.PrintWriter log) throws IOException {

        List<String> validWords = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (word.length() == 5 && word.matches("[а-яА-ЯёЁ]+")) {
                    validWords.add(WordleDictionary.normalize(word));
                }
            }
        }

        if (validWords.isEmpty()) {
            throw new IllegalStateException("Нет подходящих 5-буквенных слов в файле: " + filename);
        }

        log.println("Загружено " + validWords.size() + " слов.");
        return new WordleDictionary(validWords);
    }
}
