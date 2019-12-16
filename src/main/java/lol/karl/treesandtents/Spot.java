package lol.karl.treesandtents;

public enum Spot {
//    TREE("🌲"), TENT("⛺"), EMPTY("🐾");
    TREE("T"), TENT("A"), EMPTY("x");

    private final String emoji;

    Spot(String emoji) {
        this.emoji = emoji;
    }

    @Override
    public String toString() {
        return this.emoji;
    }
}
