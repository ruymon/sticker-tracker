package sticker_tracker.domain;

public record Progress(int collected, int total) {

    public int missing() {
        return total - collected;
    }

    public double percentage() {
        if (total == 0) {
            return 0.0;
        }

        return (double) collected / total * 100;
    }
}
