import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Board {

    private Cell[][] cells = new Cell[9][9];

    public Board(int... values) {
        int row = 0;
        int col = 0;
        for(int value : values) {
            cells[row][col] = new Cell(row, col, value);

            if (col == 8) {
                col = 0;
                row++;
            } else {
                col++;
            }
        }

        updatePossibilities();
    }

    private void updatePossibilities() {
        visitUndetermined(cell -> {
            List<Cell> row = getRow(cell);
            List<Cell> column = getColumn(cell);
            List<Cell> sector = getSector(cell);

            List<Integer> possibilties = IntStream.range(1, 9+1).boxed().collect(Collectors.toList());

            possibilties.removeAll(getDeterminedValues(row));
            possibilties.removeAll(getDeterminedValues(column));
            possibilties.removeAll(getDeterminedValues(sector));

            cell.setPossibilties(possibilties);

            return true;
        });
    }

    private List<Integer> getDeterminedValues(List<Cell> cells) {
        return cells.stream().filter(c -> c.isDetermined()).map(c -> c.getValue()).collect(Collectors.toList());
    }


    public void visitAll(CellVisitor visitor) {
        for (int row = 0; row<=8; row++) {
            for (int col = 0; col <= 8; col++) {
                boolean keepGoing = visitor.visit(cells[row][col]);
                if (!keepGoing) {
                    return;
                }
            }
        }
    }

    public void visitUndetermined(CellVisitor visitor) {
        for (int row = 0; row<=8; row++) {
            for (int col = 0; col <= 8; col++) {
                if (!cells[row][col].isDetermined()) {
                    boolean keepGoing = visitor.visit(cells[row][col]);
                    if (!keepGoing) {
                        return;
                    }
                }
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        visitAll(cell -> {
            buf.append(cell.toPossibilitiesString()).append(" ");
            if (cell.column() == 2 || cell.column() == 5) {
                buf.append(" ");
            }
            if (cell.column() == 8 ) {
                buf.append("\n");
                if (cell.row() == 2 || cell.row() == 5) {
                    buf.append("\n");
                }
            }
            return true;
        });

        return buf.toString();
    }

    public boolean isComplete() {
        AtomicBoolean isComplete = new AtomicBoolean(true);
        visitAll(cell -> {
                if (!cell.isDetermined()) {
                   isComplete.set(false);
                   return false;
                }
                return true;
            });
        return isComplete.get();
    }

    public List<Cell> getRow(int rowNum) {
        List<Cell> row = new ArrayList<>();
        for (int col = 0; col <= 8; col++) {
            row.add(cells[rowNum][col]);
        }
        return row;
    }

    public List<Cell> getRow(Cell cell) {
        return getRow(cell.row());
    }

    public List<Cell> getColumn(int column) {
        List<Cell> col = new ArrayList<>();
        for (int row = 0; row <= 8; row++) {
            col.add(cells[row][column]);
        }
        return col;
    }

    public List<Cell> getColumn(Cell cell) {
        return getColumn(cell.column());
    }

    public List<Cell> getSector(int sector) {
        int rowBase, colBase;
        switch (sector) {
            case 0 : rowBase = 0; colBase = 0; break;
            case 1 : rowBase = 0; colBase = 3; break;
            case 2 : rowBase = 0; colBase = 6; break;
            case 3 : rowBase = 3; colBase = 0; break;
            case 4 : rowBase = 3; colBase = 3; break;
            case 5 : rowBase = 3; colBase = 6; break;
            case 6 : rowBase = 6; colBase = 0; break;
            case 7 : rowBase = 6; colBase = 3; break;
            case 8 : rowBase = 6; colBase = 6; break;
            default: throw new IllegalArgumentException("Illegal sector: " + sector);
        }

        List<Cell> result = getSectorCells(rowBase, colBase);
        return result;
    }

    public List<Cell> getSector(Cell cell) {
        int sectorRowBase = Math.floorDiv(cell.row(), 3) * 3;
        int sectorColBase = Math.floorDiv(cell.column(), 3) * 3;

        return getSectorCells(sectorRowBase, sectorColBase);
    }

    private List<Cell> getSectorCells(int rowBase, int colBase) {
        List<Cell> result = new ArrayList<>();
        for (int row = rowBase; row < rowBase + 3; row++) {
            for (int col = colBase; col < colBase + 3; col++) {
                result.add(cells[row][col]);
            }
        }
        return result;
    }

    /**
     * True iff all cells have at least on possibility, and all the rules are followed
     */
    public boolean isValid() {
        AtomicBoolean valid = new AtomicBoolean(true);

        visitAll(cell -> {
            if (cell.getPossibilities().size() < 1) {
                valid.set(false);
                return false;
            }

            for (int i=0; i<=8; i++) {
                if (hasDuplicate(getDeterminedValues(getRow(i)))) {
                    valid.set(false);
                    return false;
                }
                if (hasDuplicate(getDeterminedValues(getColumn(i)))) {
                    valid.set(false);
                    return false;
                }
                if (hasDuplicate(getDeterminedValues(getSector(i)))) {
                    valid.set(false);
                    return false;
                }
            }

            return true;
        });

        return valid.get();
    }

    public static <T> boolean hasDuplicate(Iterable<T> all) {
        Set<T> set = new HashSet<T>();
        // Set#add returns false if the set does not change, which
        // indicates that a duplicate element has been added.
        for (T each: all) if (!set.add(each)) return true;
        return false;
    }

    /**
     * Returns a new board that is the same as this one except the cell at the given position has the given value.
     * The value MUST be a valid possibilty.
     */
    public Board setValue(Cell cell, int value) {
        if (!cell.getPossibilities().contains(value)) {
            throw new IllegalArgumentException("Illegal cell value");
        }

        List<Integer> values = new ArrayList<>();
        visitAll(c -> {
            if (c.isDetermined()) {
                values.add(c.getValue());
            }
            else if (c.equals(cell)) {
                values.add(value);
            }
            else {
                values.add(0);
            }
            return true;
        });

        return new Board(values.stream().mapToInt(i->i).toArray());
    }
}
