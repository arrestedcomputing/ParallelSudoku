import java.util.List;

public class Main {

    public static void main(String[] args) {

        Board easy = new Board(
          0,0,7, 0,8,0, 0,0,4,
          0,4,6, 9,0,3, 0,0,0,
          8,0,0, 6,7,0, 3,0,0,

          0,0,1, 0,9,0, 8,4,0,
          7,0,3, 4,0,6, 5,0,9,
          0,2,4, 0,3,0, 7,0,0,

          0,0,8, 0,5,7, 0,0,1,
          0,0,0, 1,0,9, 2,3,0,
          1,0,0, 0,4,0, 6,0,0
        );

        Board hard = new Board(
          8,0,0, 0,0,0, 0,0,0,
          0,0,3, 6,0,0, 0,0,0,
          0,7,0, 0,9,0, 2,0,0,

          0,5,0, 0,0,7, 0,0,0,
          0,0,0, 0,4,5, 7,0,0,
          0,0,0, 1,0,0, 0,3,0,

          0,0,1, 0,0,0, 0,6,8,
          0,0,8, 5,0,0, 0,1,0,
          0,9,0, 0,0,0, 4,0,0
        );

        Board hard2 = new Board(
          1,0,0, 0,0,7, 0,9,0,
          0,3,0, 0,2,0, 0,0,8,
          0,0,9, 6,0,0, 5,0,0,

          0,0,5, 3,0,0, 9,0,0,
          0,1,0, 0,8,0, 0,0,2,
          6,0,0, 0,0,4, 0,0,0,

          3,0,0, 0,0,0, 0,1,0,
          0,4,0, 0,0,0, 0,0,7,
          0,0,7, 0,0,0, 3,0,0
        );

        System.out.println(easy);

        Solver solver = new Solver();
        List<Board> solutions = solver.solve(easy);

        for (Board solution : solutions) {
            System.out.println();
            System.out.println(solution);
            System.out.println();
        }

        solver.printStats();
    }
}


