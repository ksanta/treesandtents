package lol.karl.treesandtents;

import org.junit.Test;

import static lol.karl.treesandtents.Spot.TENT;
import static lol.karl.treesandtents.Spot.TREE;

public class FieldTest {

    @Test
    public void testSolve() {
        Spot[][] spots = new Spot[][]{
                new Spot[]{TREE, null, TREE, null, null, null, TREE, null, TREE, null},
                new Spot[]{null, null, null, null, null, null, null, null, null, TREE},
                new Spot[]{null, null, null, null, null, TREE, TREE, null, null, null},
                new Spot[]{null, null, TREE, null, null, null, null, null, null, null},
                new Spot[]{null, null, null, null, null, TREE, TREE, null, null, null},
                new Spot[]{TREE, null, null, null, null, null, null, null, null, TREE},
                new Spot[]{null, null, TREE, TENT, null, null, null, TREE, null, TREE},
                new Spot[]{null, TREE, null, null, null, null, null, null, null, null},
                new Spot[]{null, null, null, null, null, null, null, null, null, null},
                new Spot[]{null, TREE, null, TREE, null, TREE, null, TREE, null, null},
        };

        final int[] rowTreeCount =    new int[]{3, 1, 4, 0, 4, 0, 1, 3, 2, 2};
        final int[] columnTreeCount = new int[]{4, 0, 1, 3, 2, 2, 0, 4, 1, 3};

        Field field = new Field(spots, rowTreeCount, columnTreeCount);

        field.solve();
    }
}