public interface CellVisitor {

    /**
     * Return false to stop visiting
     */
    boolean visit(Cell cell);
}

