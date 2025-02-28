package Wordle;

public class LetterStatus {
    private char letter;
    private Status status;

    public enum Status {
        CORRECT,
        MISPLACED,
        INCORRECT
    }

    public LetterStatus(char letter, Status status) {
        this.letter = letter;
        this.status = status;
    }

    public char getLetter() {
        return letter;
    }

    public Status getStatus() {
        return status;
    }

    public static LetterStatus[] getFeedback(String word, String referenceWord) {
        LetterStatus[] feedback = new LetterStatus[5];
        boolean[] matched = new boolean[5];
        // First pass: mark correct positions
        for (int i = 0; i < 5; i++) {
            char letter = word.charAt(i);
            if (referenceWord.charAt(i) == letter) {
                feedback[i] = new LetterStatus(letter, Status.CORRECT);
                matched[i] = true;
            }
        }
        // Second pass: mark incorrect and misplaced positions
        for (int i = 0; i < 5; i++) {
            if (feedback[i] == null) {
                char letter = word.charAt(i);
                boolean found = false;
                // Find first unmatched letter in reference word
                for (int j = 0; j < 5; j++) {
                    if (!matched[j] && referenceWord.charAt(j) == letter) {
                        feedback[i] = new LetterStatus(letter, Status.MISPLACED);
                        matched[j] = true;
                        found = true;
                        break;
                    }
                }
                // If no match found, letter is incorrect
                if (!found) {
                    feedback[i] = new LetterStatus(letter, Status.INCORRECT);
                }
            }
        }
        return feedback;
    }
}
