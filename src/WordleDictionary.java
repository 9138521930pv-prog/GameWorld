import java.util.*;

public class WordleDictionary {
    private final List<String> words;

    public WordleDictionary(List<String> words) {
        this.words = new ArrayList<>(words);
    }

    public List<String> getWords() {
        return new ArrayList<>(words);
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public String getRandomWord() {
        if (words.isEmpty()) throw new IllegalStateException("Словарь пуст");
        return words.get(new Random().nextInt(words.size()));
    }

    public static String normalize(String word) {
        return word.toLowerCase().replace('ё', 'е');
    }

    public List<String> filterByGuesses(List<String> guesses, List<String> feedbacks) {
        List<String> candidates = new ArrayList<>(words);

        for (int k = 0; k < guesses.size(); k++) {
            String guess = guesses.get(k);
            String fb = feedbacks.get(k);

            Map<Character, Integer> requiredCount = new HashMap<>();
            Set<Character> absent = new HashSet<>();
            Map<Integer, Character> exact = new HashMap<>();

            for (int i = 0; i < 5; i++) {
                char c = fb.charAt(i);
                char g = guess.charAt(i);
                if (c == '+') {
                    exact.put(i, g);
                    requiredCount.put(g, requiredCount.getOrDefault(g, 0) + 1);
                } else if (c == '^') {
                    requiredCount.put(g, requiredCount.getOrDefault(g, 0) + 1);
                } else if (c == '-') {
                    absent.add(g);
                }
            }

            candidates = candidates.stream().filter(word -> {
                // Точное совпадение
                for (Map.Entry<Integer, Character> e : exact.entrySet()) {
                    if (word.charAt(e.getKey()) != e.getValue()) return false;
                }

                // Подсчёт букв
                Map<Character, Integer> wordCount = countLetters(word);
                for (Map.Entry<Character, Integer> e : requiredCount.entrySet()) {
                    if (wordCount.getOrDefault(e.getKey(), 0) < e.getValue()) return false;
                }

                // Отсутствие ненужных
                for (char c : absent) {
                    if (word.indexOf(c) != -1 && !requiredCount.containsKey(c)) return false;
                }

                return true;
            }).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }

        return candidates;
    }

    private Map<Character, Integer> countLetters(String word) {
        Map<Character, Integer> count = new HashMap<>();
        for (char c : word.toCharArray()) {
            count.put(c, count.getOrDefault(c, 0) + 1);
        }
        return count;
    }
}
