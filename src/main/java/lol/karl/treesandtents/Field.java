package lol.karl.treesandtents;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import static lol.karl.treesandtents.Spot.*;

public class Field {
    private Spot[][] spots;
    private final int[] rowTentSums;
    private final int[] columnTentSums;

    /**
     * All spots in the row are figured out
     */
    private boolean[] rowComplete;

    /**
     * All spots in the columns are figured out
     */
    private boolean[] columnComplete;

    public Field(Spot[][] spots, int[] rowTentSums, int[] columnTentSums) {
        this.spots = spots;
        this.rowTentSums = rowTentSums;
        this.columnTentSums = columnTentSums;
        this.rowComplete = new boolean[spots.length];
        this.columnComplete = new boolean[spots[0].length];
    }

    public void solve() {
        // Some one-off rules to apply
        applyToEachSpot(this::rePlaceTent);
        applyToEachSpot(this::markSpotsEmptyIfDiagonalToATree);

        boolean spotsUpdated = true;

        // Keep looping through rules until there are no changes
        while (spotsUpdated) {
            spotsUpdated = false;

            if (markEmptiesIfAllTentsFoundInRowOrColumn()) {
                spotsUpdated = true;
            }

            if (placeTentsOnUnmarkedSpotsIfTheyMustBePlacedToMatchEdgeCounts()) {
                spotsUpdated = true;
            }

            if (applyToEachSpot(this::placeTentNextToTreeIfOnlyOneUnmarkedSpotLeft)) {
                spotsUpdated = true;
            }
        }

        print(spots);
    }

    private boolean applyToEachSpot(BiFunction<Integer, Integer, Boolean> functionToApply) {
        boolean spotsUpdated = false;

        for (int row = 0; row < spots.length; row++) {
            for (int column = 0; column < spots[0].length; column++) {
                if (functionToApply.apply(row, column)) {
                    spotsUpdated = true;
                }
            }
        }
        return spotsUpdated;
    }

    private boolean rePlaceTent(int row, int column) {
        if (isTent(row, column)) {
            return placeTent(row, column);
        } else {
            return false;
        }
    }

    private boolean placeTentNextToTreeIfOnlyOneUnmarkedSpotLeft(int row, int column) {
        // Quick exit if this spot is not a TREE
        if (spots[row][column] != TREE) {
            return false;
        }

        final int[] deltaRow = new int[]{-1, 0, 1, 0};
        final int[] deltaColumn = new int[]{0, 1, 0, -1};

        int[] unmarkedSpot = null;

        for (int i = 0; i < 4; i++) {
            int newRow = row + deltaRow[i];
            int newColumn = column + deltaColumn[i];
            if (isOnTheField(newRow, newColumn)) {
                if (isUnmarked(newRow, newColumn)) {
                    if (unmarkedSpot == null) {
                        unmarkedSpot = new int[]{newRow, newColumn};
                    } else {
                        // Do not place tent if there are multiple unmarked spots
                        return false;
                    }
                } else if (isTent(newRow, newColumn)) {
                    // Do not place tent if there is already a tent next to the tree
                    return false;
                }
            }
        }

        if (unmarkedSpot != null) {
            placeTent(unmarkedSpot[0], unmarkedSpot[1]);
            return true;
        }

        return false;
    }

    private boolean markSpotsEmptyIfDiagonalToATree(int row, int column) {
        // Quick exit if this spot is not unmarked
        if (spots[row][column] != null) {
            return false;
        }

        boolean spotsUpdated = false;
        final int[] deltaRow = new int[]{-1, -1, 1, 1};
        final int[] deltaColumn = new int[]{-1, 1, 1, -1};

        for (int i = 0; i < 4; i++) {
            int newRow = row + deltaRow[i];
            int newColumn = column + deltaColumn[i];
            if (isOnTheField(newRow, newColumn) && isTree(newRow, newColumn)) {
                spots[row][column] = EMPTY;
                spotsUpdated = true;
            }
        }

        return spotsUpdated;
    }

    private boolean markEmptiesIfAllTentsFoundInRowOrColumn() {
        boolean spotsUpdated = false;

        // Scan rows
        for (int row = 0; row < spots.length; row++) {
            if (!rowComplete[row] && countInRow(row, spot -> spot == TENT) == rowTentSums[row]) {
                applyToUnmarkedSpotsInRow(row, (r, c) -> spots[r][c] = EMPTY);
                spotsUpdated = true;
                rowComplete[row] = true;
            }
        }

        // Scan columns
        for (int column = 0; column < spots[0].length; column++) {
            if (!columnComplete[column] && countInColumn(column, spot -> spot == TENT) == columnTentSums[column]) {
                applyToUnmarkedSpotsInColumn(column, (r, c) -> spots[r][c] = EMPTY);
                spotsUpdated = true;
                columnComplete[column] = true;
            }
        }

        return spotsUpdated;
    }

    private boolean placeTentsOnUnmarkedSpotsIfTheyMustBePlacedToMatchEdgeCounts() {
        boolean spotsUpdated = false;

        // Scan rows
        for (int row = 0; row < spots.length; row++) {
            if (!rowComplete[row] && countInRow(row, spot -> spot == TENT || spot == null) == rowTentSums[row]) {
                applyToUnmarkedSpotsInRow(row, this::placeTent);
                spotsUpdated = true;
                rowComplete[row] = true;
            }
        }

        // Scan columns
        for (int column = 0; column < spots[0].length; column++) {
            if (!columnComplete[column] && countInColumn(column, spot -> spot == TENT || spot == null) == columnTentSums[column]) {
                applyToUnmarkedSpotsInColumn(column, this::placeTent);
                spotsUpdated = true;
                columnComplete[column] = true;
            }
        }

        return spotsUpdated;
    }

    private int countInRow(int row, Predicate<Spot> spotPredicate) {
        int count = 0;
        for (Spot spot : spots[row]) {
            if (spotPredicate.test(spot)) {
                count++;
            }
        }
        return count;
    }

    private int countInColumn(int column, Predicate<Spot> spotPredicate) {
        int count = 0;
        for (Spot[] row : spots) {
            Spot spot = row[column];
            if (spotPredicate.test(spot)) {
                count++;
            }
        }
        return count;
    }

    private void applyToUnmarkedSpotsInRow(int row, BiConsumer<Integer, Integer> consumerToApply) {
        for (int column = 0; column < spots[0].length; column++) {
            if (spots[row][column] == null) {
                consumerToApply.accept(row, column);
            }
        }
    }

    private void applyToUnmarkedSpotsInColumn(int column, BiConsumer<Integer, Integer> consumerToApply) {
        for (int row = 0; row < spots.length; row++) {
            if (spots[row][column] == null) {
                consumerToApply.accept(row, column);
            }
        }
    }

    /**
     * Places a tent and marks the surrounding spots as EMPTY
     */
    private boolean placeTent(int rowIndex, int columnIndex) {
        spots[rowIndex][columnIndex] = TENT;

        int[] deltaRow = new int[]{-1, -1, -1, 0, 0, 1, 1, 1};
        int[] deltaColumn = new int[]{-1, 0, 1, -1, 1, -1, 0, 1};

        for (int i = 0; i < 8; i++) {
            int newRow = rowIndex + deltaRow[i];
            int newColumn = columnIndex + deltaColumn[i];

            if (isOnTheField(newRow, newColumn) && isUnmarked(newRow, newColumn)) {
                spots[newRow][newColumn] = EMPTY;
            }
        }

        return true;
    }

    private boolean isOnTheField(int row, int column) {
        return row >= 0 && row < spots.length
                && column >= 0 && column < spots[0].length;
    }

    private boolean isUnmarked(int row, int column) {
        return spots[row][column] == null;
    }

    private boolean isTent(int row, int column) {
        return spots[row][column] == TENT;
    }

    private boolean isTree(int row, int column) {
        return spots[row][column] == TREE;
    }

    private boolean isEmpty(int row, int column) {
        return spots[row][column] == EMPTY;
    }

    private void print(Spot[][] spots) {
        // todo: print the totals along the edge, too
        for (int rowIndex = 0; rowIndex < spots.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < spots[0].length; columnIndex++) {
                Spot spot = spots[rowIndex][columnIndex];
                String emoji = spot != null ? spot.toString() : " ";
                System.out.print(emoji + " ");
            }
            System.out.println();
        }
    }
}
