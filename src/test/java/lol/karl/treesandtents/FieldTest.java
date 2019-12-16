package lol.karl.treesandtents;

import org.junit.Test;

import static lol.karl.treesandtents.Spot.*;

public class FieldTest {

    @Test
    public void testSolve() {
        Spot[][] spots = new Spot[][]{
                new Spot[]{TREE, UNMARKED, TREE, UNMARKED, UNMARKED, UNMARKED, TREE, UNMARKED, TREE, UNMARKED},
                new Spot[]{UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, TREE},
                new Spot[]{UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, TREE, TREE, UNMARKED, UNMARKED, UNMARKED},
                new Spot[]{UNMARKED, UNMARKED, TREE, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED},
                new Spot[]{UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, TREE, TREE, UNMARKED, UNMARKED, UNMARKED},
                new Spot[]{TREE, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, TREE},
                new Spot[]{UNMARKED, UNMARKED, TREE, TENT, UNMARKED, UNMARKED, UNMARKED, TREE, UNMARKED, TREE},
                new Spot[]{UNMARKED, TREE, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED},
                new Spot[]{UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED, UNMARKED},
                new Spot[]{UNMARKED, TREE, UNMARKED, TREE, UNMARKED, TREE, UNMARKED, TREE, UNMARKED, UNMARKED},
        };

        final int[] rowTreeCount =    new int[]{3, 1, 4, 0, 4, 0, 1, 3, 2, 2};
        final int[] columnTreeCount = new int[]{4, 0, 1, 3, 2, 2, 0, 4, 1, 3};

        Field field = new Field(spots, rowTreeCount, columnTreeCount);

        field.solve();
    }
}