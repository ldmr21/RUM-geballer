package pp.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RandomPositionIteratorTest {
    public static final int WIDTH = 15;
    public static final int HEIGHT = 25;

    @Test
    public void permutation() {
        for (int i = 0; i < 10; i++) {
            final List<Position> permutation = new ArrayList<>();
            new RandomPositionIterator(WIDTH, HEIGHT).forEachRemaining(permutation::add);
            assertEquals(WIDTH * HEIGHT, permutation.size());
            assertEquals(permutation.size(), new HashSet<>(permutation).size());
            for (Position w : permutation) {
                assertTrue(w.toString(), 0 <= w.getX() && w.getX() < WIDTH);
                assertTrue(w.toString(), 0 <= w.getY() && w.getY() < HEIGHT);
            }
        }
    }
}