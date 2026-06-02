package sticker_tracker.domain;

public enum SectionType {
    TEAM,
    SPECIAL,
    REGIONAL;

    public static SectionType fromString(String value) {
        return switch (value.toLowerCase()) {
            case "team" -> TEAM;
            case "special" -> SPECIAL;
            case "regional" -> REGIONAL;
            default -> throw new IllegalArgumentException("Unknown section type: " + value);
        };
    }
}
