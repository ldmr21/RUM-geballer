package pp.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * An iterator that creates a random permutation of positions (x,y) where x and y are integer values
 * in the range {0, ..., width-1} x {0, ..., height-1}. All permutations are uniformly distributed
 * using the Fisher–Yates shuffle (also known as Knuth shuffle).
 */
public class RandomPositionIterator implements Iterator<Position> {
    private final Random random = new Random();
    private final int height;
    private final Map<Integer, Position> movedMap = new HashMap<>();
    private int remaining;

    /**
     * Creates a new permutation iterator creating a random permutation of positions (x,y)
     * where x and y are integer values in the range {0, ..., width-1} x {0, ..., height-1}.
     *
     * @param width  the width of the rectangle
     * @param height the height of the rectangle.
     */
    public RandomPositionIterator(int width, int height) {
        this.height = height;
        this.remaining = width * height;
    }

    @Override
    public boolean hasNext() {
        return remaining > 0;
    }

    @Override
    public Position next() {
        if (hasNext()) {
            final int idx = random.nextInt(remaining--); // note that remaining is decremented
            final Position result = getWhere(idx);
            if (idx < remaining)
                movedMap.put(idx, getWhere(remaining));
            movedMap.remove(remaining);
            return result;
        }
        throw new NoSuchElementException();
    }

    private Position getWhere(int idx) {
        final Position movedWhere = movedMap.get(idx);
        if (movedWhere != null)
            return movedWhere;
        final int x = idx / height;
        final int y = idx % height;
        return new FloatPoint(x, y);
    }
}
