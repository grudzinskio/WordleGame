package Wordle.Statistics;

public class HighScore {
    private String username;
    private int guesses;
    private int timeSeconds;

    public HighScore(String username, int guesses, int timeSeconds) {
        this.username = username;
        this.guesses = guesses;
        this.timeSeconds = timeSeconds;
    }

    public String getUsername() {
        return username;
    }

    public int getGuesses() {
        return guesses;
    }

    public int getTimeSeconds() {
        return timeSeconds;
    }

    @Override
    public String toString() {
        String guessText = (guesses == 1) ? " guess" : " guesses";
        return username + " - " + guesses + guessText + " in " + timeSeconds + "s";
    }
}