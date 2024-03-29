package info3.game.entity;

public enum Direction {
    LEFT(-1, 0),
    RIGHT(1, 0),
    UPPER(0, -1),
    BOTTOM(0, 1),

    LEFT_TOP(-1, -1),
    RIGHT_TOP(1, -1),
    LEFT_BOTTOM(-1, +1),
    RIGHT_BOTTOM(1, +1),
    IDLE(0, 0),

    EMPTY(0, 0);

    public final int x;
    public final int y;

    private Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Direction fromString(String directionString) {
        switch (directionString.toUpperCase()) {
            case "NW":
                return LEFT_TOP;
            case "NE":
                return RIGHT_TOP;
            case "SW":
                return LEFT_BOTTOM;
            case "SE":
                return RIGHT_BOTTOM;
            case "N":
                return UPPER;
            case "S":
                return BOTTOM;
            case "W":
            case "B":
            case "L":
                return LEFT;
            case "E":
            case "F":
            case "R":
                return RIGHT;
            case "NONE":
            case "H":
                return EMPTY;
            default:
                throw new IllegalArgumentException("Invalid direction string: " + directionString);
        }
    }
}
