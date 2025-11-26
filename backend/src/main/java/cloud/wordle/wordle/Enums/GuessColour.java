package cloud.wordle.wordle.Enums;

public enum GuessColour {
    GREEN("GREEN"),
    GREY("GREY"),
    YELLOW("YELLOW");

    private final String value;

    GuessColour(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

