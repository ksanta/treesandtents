package lol.karl.treesandtents;

public enum Spot {
    UNMARKED(" "), TREE("T"), TENT("A"), EMPTY("x");

    private final String displayChar;

    Spot(String displayChar) {
        this.displayChar = displayChar;
    }

    @Override
    public String toString() {
        return this.displayChar;
    }

    public boolean isUnmarked() {
        return this == UNMARKED;
    }

    public boolean isTent() {
        return this == TENT;
    }

    public boolean isTentOrUnmarked() {
        return isTent() || isUnmarked();
    }

    public boolean isTree() {
        return this == TREE;
    }
}
