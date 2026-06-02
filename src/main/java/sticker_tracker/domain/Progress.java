package sticker_tracker.domain;

public record Progress(int collected, int total) {

    private static final int EMPTY_COUNT = 0;
    private static final double PERCENTAGE_FULL = 100.0;
    private static final double PERCENTAGE_EMPTY = 0.0;

    public int missing() {
        return total - collected;
    }

    public double percentage() {
        if (total == EMPTY_COUNT) {
            return PERCENTAGE_EMPTY;
        }

        return (double) collected / total * PERCENTAGE_FULL;
    }
}
