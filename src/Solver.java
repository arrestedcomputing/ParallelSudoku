import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.*;
import java.util.concurrent.Executors;

public class Solver {

    private static final int THREADS = 8;

    ListeningExecutorService executor;
    List<Board> solutions = new ArrayList<>();
    List<ListenableFuture> allFutures = Collections.synchronizedList(new ArrayList<>());

    // Stats
    int guesses = 0;
    int deadEnds = 0;
    Stopwatch stopwatch = Stopwatch.createUnstarted();

    public List<Board> solve(Board initialBoard) {
        executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(THREADS));

        stopwatch.start();

        submitBoard(initialBoard);

        // This waits until there are no more pending futures
        try {
            while (true) {
                int done = 0;

                for (ListenableFuture future : ImmutableList.copyOf(allFutures)) {
                    future.get();
                    done++;
                }

                if (done == allFutures.size()) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        stopwatch.stop();
        System.out.println("Search Complete");

        // Shutdown the executor service
        executor.shutdown();
        while (!executor.isTerminated()) {
        }

        return solutions;
    }

    public void printStats() {
        System.out.println("Threads = " + THREADS);
        System.out.println("Guesses = " + guesses);
        System.out.println("Dead Ends = " + deadEnds);
        System.out.println("Time = " + stopwatch.elapsed().getSeconds() + "s");
    }

    /**
     * Submit this board configuration for more processing
     */
    private void submitBoard(final Board board) {
        ListenableFuture<Void> future = executor.submit(() -> {
            // If the board is not valid, stop working on it
            if (!board.isValid()) {
                deadEnds++;
                return null;
            }

            // If the board is complete and valid it's a soluton and we are done with it
            if (board.isComplete()) {
                solutions.add(board);
                System.out.println("Solution Found");
                return null;
            }

            // Otherwise the board is valid but incomplete, so we need to make guesses and submit for more processing
            for (Board guess : makeGuesses(board)) {
                guesses++;
                submitBoard(guess);
            }
            return null;
        });
        allFutures.add(future);
    }

    private Collection<Board> makeGuesses(Board board) {
        List<Board> guesses = new ArrayList<>();

        SortedSet<Cell> cellsByNumPossibilities = new TreeSet<>(Comparator.comparing(c -> c.getPossibilities().size()));

        board.visitUndetermined(cell -> {
            cellsByNumPossibilities.add(cell);
            return true;
        });

        Cell cellWithFewestPossibilities = cellsByNumPossibilities.first();
        for (int possibility : cellWithFewestPossibilities.getPossibilities()) {
            Board guess = board.setValue(cellWithFewestPossibilities, possibility);
            guesses.add(guess);
        }
        return guesses;
    }
}
