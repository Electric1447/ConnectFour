package eparon.connectfour.Util;

import java.util.ArrayList;
import java.util.Arrays;

public class c4utils {

    /**
     * Returns a deep copy of the given game state. Used in {@code CPUPlayer} to prevent the existing game state from being modified between moves generated.
     *
     * @param cState the current game state
     * @return a deep copy of the game state
     */
    public static int[][] deepCopyState (int[][] cState) {
        if (cState == null) return null;

        // Just copy every entry into a new 2D array and return it.
        int[][] ret = new int[cState.length][cState[0].length];
        for (int i = 0; i < cState.length; i++)
            ret[i] = Arrays.copyOf(cState[i], cState[i].length);

        return ret;
    }

    /**
     * Returns the row that a piece would get placed in given a specific {@code column}.
     * Returns -1 if the column is full.
     *
     * @param col the column to insert into
     * @param arr the current game state
     * @param BOARD_SIZE the board's size
     * @return the lowest row in which a piece can be placed
     */
    public static int lowestRow (int col, int[][] arr, int BOARD_SIZE) {
        // Start looking from the bottom since connect four pieces fall as far down as possible
        for (int i = BOARD_SIZE - 1; i >= 0; i--)
            if (arr[i][col] == 0)
                return i;

        return -1; // If there was no valid move, return -1 to denote this
    }

    /**
     * Returns an ArrayList consisting of the possible moves that can
     * be made on a board. Each list item is an integer array of the form
     * {@code [row, column]}.
     *
     * @param currentState the current game state
     * @param BOARD_SIZE the board's size
     * @return all possible moves
     */
    public static ArrayList<Integer[]> possibleMoves (int[][] currentState, int BOARD_SIZE) {
        ArrayList<Integer[]> possible = new ArrayList<>(BOARD_SIZE);

        for (int i = 0; i < BOARD_SIZE; i++) {
            // If this column is not empty, we can add the (row, column) pair to the list
            int row = lowestRow(i, currentState, BOARD_SIZE);
            if (row != -1)
                possible.add(new Integer[] {row, i});
        }

        return possible;
    }

    /**
     * Given a particular position at {@code (row, column)}, determines if a piece played at the position resulted in a win for the player.
     *
     * @param row    the row of the piece
     * @param col    the column of the piece
     * @param arr    the game state in which to look for a win
     * @param player the player to check a win for
     * @param BOARD_SIZE the board's size
     * @return TODO
     */
    public static boolean booleanWinChecker (int row, int col, int[][] arr, int player, int BOARD_SIZE) {
        // dx and dy are used to check "lines" around the row and column
        final int[] dx = {0, 1, 1, 1};
        final int[] dy = {-1, -1, 0, 1};

        // At each iteration, reset the row and column and look through a new set of delta values
        for (int i = 0, r = row, c = col; i < dx.length; i++, r = row, c = col) {

            // The delta values to use during this iteration
            int x = dx[i];
            int y = dy[i];

            // Look in the "positive" direction of these delta values
            int count = 0;
            while (r < BOARD_SIZE && r >= 0 && c < BOARD_SIZE && c >= 0 && count < 4 && arr[r][c] == player) {
                count++;
                r += y;
                c += x;
            }

            // Now look in the "opposite" direction using the same delta values.
            // Do not look at the same starting position
            r = row - y;
            c = col - x;

            while (r < BOARD_SIZE && r >= 0 && c < BOARD_SIZE && c >= 0 && count < 4 && arr[r][c] == player) {
                count++;
                r -= y;
                c -= x;
            }

            if (count == 4)
                return true;
        }

        return false;
    }

}
