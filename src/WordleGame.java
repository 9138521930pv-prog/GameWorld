import java.io.PrintWriter;
import java.util.*;

public class WordleGame {
    private final WordleDictionary dictionary;
    private final String target;
    private int attemptsLeft;
    private boolean gameOver;
    private final List<String> guesses;
    private final List<String> feedbacks;
    private final PrintWriter log;

    public WordleGame(WordleDictionary dictionary, PrintWriter log) {
        this.dictionary = dictionary;
        this.target = dictionary.getRandomWord();
        this.attemptsLeft = 6;
        this.gameOver = false;
        this.guesses = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
        this.log = log;
        log.println("Загадано слово: " + target);
    }

    public void makeGuess(String word) throws InvalidWordException {
        if (gameOver) return;

        if (!dictionary.contains(word)) {
            throw new InvalidWordException("Слово не найдено в словаре.");
        }

        String feedback = buildFeedback(word);
        guesses.add(word);
        feedbacks.add(feedback);
        attemptsLeft--;

        if (word.equals(target)) {
            gameOver = true;
            log.println("Победа: " + target);
        } else if (attemptsLeft == 0) {
            gameOver = true;
            log.println("Поражение: " + target);
        }
    }

    private String buildFeedback(String guess) {
        StringBuilder fb = new StringBuilder("-----");
        char[] targetArray = target.toCharArray();
        boolean[] used = new boolean[5];

        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == targetArray[i]) {
                fb.setCharAt(i, '+');
                used[i] = true;
            }
        }

        for (int i = 0; i < 5; i++) {
            if (fb.charAt(i) == '-') {
                char c = guess.charAt(i);
                for (int j = 0; j < 5; j++) {
                    if (targetArray[j] == c && !used[j]) {
                        fb.setCharAt(i, '^');
                        used[j] = true;
                        break;
                    }
                }
            }
        }

        return fb.toString();
    }

    public String getSuggestion() {
        List<String> candidates = dictionary.filterByGuesses(guesses, feedbacks);
        candidates.removeIf(guesses::contains);
        return candidates.isEmpty() ? dictionary.getRandomWord() : candidates.get(0);
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public boolean isWin() {
        return !guesses.isEmpty() && guesses.get(guesses.size() - 1).equals(target);
    }

    public String getLastFeedback() {
        return feedbacks.isEmpty() ? "" : feedbacks.get(feedbacks.size() - 1);
    }

    public String getTarget() {
        return target;
    }
}
