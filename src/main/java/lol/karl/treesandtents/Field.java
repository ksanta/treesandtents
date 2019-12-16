package lol.karl.treesandtents;

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
        for (int rowIndex = 0; rowIndex < spots.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < spots[0].length; columnIndex++) {
                if (isTent(rowIndex, columnIndex)) {
                    placeTent(rowIndex, columnIndex);
                } else if (spots[rowIndex][columnIndex] == null) {
                    markEmptyIfDiagonalToATree(rowIndex, columnIndex);
                }
            }
        }

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

            for (int row = 0; row < spots.length; row++) {
                for (int column = 0; column < spots[0].length; column++) {
                    if (isTree(row, column)) {
                        if (placeTentNextToTreeIfOnlyOneUnmarkedSpotLeft(row, column)) {
                            spotsUpdated = true;
                        }
                    }
                }
            }
        }

        print(spots);
    }

    private boolean placeTentNextToTreeIfOnlyOneUnmarkedSpotLeft(int row, int column) {
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

    private void markEmptyIfDiagonalToATree(int row, int column) {
        final int[] deltaRow = new int[]{-1, -1, 1, 1};
        final int[] deltaColumn = new int[]{-1, 1, 1, -1};

        for (int i = 0; i < 4; i++) {
            int newRow = row + deltaRow[i];
            int newColumn = column + deltaColumn[i];
            if (isOnTheField(newRow, newColumn) && isTree(newRow, newColumn)) {
                spots[row][column] = EMPTY;
            }
        }
    }

    private boolean markEmptiesIfAllTentsFoundInRowOrColumn() {
        boolean spotsUpdated = false;

        // Scan rows
        for (int row = 0; row < spots.length; row++) {
            if (!rowComplete[row] && currentNumTentsInRow(row) == rowTentSums[row]) {
                convertUnmarkedSpotsToEmptiesInRow(row);
                spotsUpdated = true;
                rowComplete[row] = true;
            }
        }

        // Scan columns
        for (int column = 0; column < spots[0].length; column++) {
            if (!columnComplete[column] && currentNumTentsInColumn(column) == columnTentSums[column]) {
                convertUnmarkedSpotsToEmptiesInColumn(column);
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
            if (!rowComplete[row] && currentNumTentsAndUnmarkedInRow(row) == rowTentSums[row]) {
                convertUnmarkedSpotsToTentsInRow(row);
                spotsUpdated = true;
                rowComplete[row] = true;
            }
        }

        // Scan columns
        for (int column = 0; column < spots[0].length; column++) {
            if (!columnComplete[column] && currentNumTentsAndUnmarkedInColumn(column) == columnTentSums[column]) {
                convertUnmarkedSpotsToTentsInColumn(column);
                spotsUpdated = true;
                columnComplete[column] = true;
            }
        }

        return spotsUpdated;
    }

    private int currentNumTentsAndUnmarkedInRow(int row) {
        int tentCount = 0;
        for (Spot spot : spots[row]) {
            if (spot == TENT || spot == null) {
                tentCount++;
            }
        }
        return tentCount;
    }

    private int currentNumTentsInRow(int row) {
        int tentCount = 0;
        for (Spot spot : spots[row]) {
            if (spot == TENT) {
                tentCount++;
            }
        }
        return tentCount;
    }

    private int currentNumTentsInColumn(int column) {
        int tentCount = 0;
        for (Spot[] row : spots) {
            if (row[column] == TENT) {
                tentCount++;
            }
        }
        return tentCount;
    }

    private int currentNumTentsAndUnmarkedInColumn(int column) {
        int tentCount = 0;
        for (Spot[] row : spots) {
            if (row[column] == TENT || row[column] == null) {
                tentCount++;
            }
        }
        return tentCount;
    }

    private void convertUnmarkedSpotsToEmptiesInRow(int row) {
        for (int column = 0; column < spots[0].length; column++) {
            if (spots[row][column] == null) {
                spots[row][column] = EMPTY;
            }
        }
    }

    private void convertUnmarkedSpotsToTentsInRow(int row) {
        for (int column = 0; column < spots[0].length; column++) {
            if (spots[row][column] == null) {
                placeTent(row, column);
            }
        }
    }

    private void convertUnmarkedSpotsToEmptiesInColumn(int column) {
        for (int row = 0; row < spots.length; row++) {
            if (spots[row][column] == null) {
                spots[row][column] = EMPTY;
            }
        }
    }

    private void convertUnmarkedSpotsToTentsInColumn(int column) {
        for (int row = 0; row < spots.length; row++) {
            if (spots[row][column] == null) {
                placeTent(row, column);
            }
        }
    }

    /**
     * Places a tent and marks the surrounding spots as EMPTY
     */
    private void placeTent(int rowIndex, int columnIndex) {
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