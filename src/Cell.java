import java.util.Collections;
import java.util.List;

/**
 * Cells location is immutable but possibilities can be set later.
 * A cell is considered "determined" when the number of possibilities is exactly 1.
 */
public class Cell {
    private final int row, col;

    private List<Integer> possibilities;

    public Cell(int row, int col, int value) {
        this.row = row;
        this.col = col;
        if (value != 0) {
            possibilities = Collections.singletonList(value);
        }
    }

    public String toString() {
        return isDetermined() ? getValue() + "" : "?";
    }

    public String toPossibilitiesString() {
        return isDetermined() ? getValue() + "" : possibilities.toString();
    }

    public boolean isDetermined() {
        if (possibilities == null)
            return false;

        return possibilities.size() == 1;
    }

    public int column() {
        return col;
    }

    public int row() {
        return row;
    }

    public int getValue() {
        if (possibilities == null || possibilities.size() != 1) {
            throw new IllegalStateException("Cant call get value if not determined");
        }

        return possibilities.get(0);
    }

    public void setPossibilties(List<Integer> possibilties) {
        this.possibilities = possibilties;
    }

    public List<Integer> getPossibilities() {
        // todo: maybe return a defensive immutable copy
        return possibilities;
    }
}
