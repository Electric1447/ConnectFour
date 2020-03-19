package eparon.connectfour.AI;

import android.util.Log;

import eparon.connectfour.Util.c4utils;

@SuppressWarnings("WeakerAccess")
public class CPUPlayer {

    // The depth through which CPU searches for a valuable move.
    private final int MAX_DEPTH;

    // Sum of all utilities.
    private static final int[] utilsSum = new int[] {108, 176, 260, 360, 476, 608, 756};

    // Stores the overall utilities of all positions on a board.
    // Each utility value determines how valuable a position is based on the number of winning scenarios that includes the given position.
    private static final int[][][] allUtilities = new int[][][] {{{3, 4, 5, 7, 5, 4, 3}, {4, 6, 8, 10, 8, 6, 4}, {6, 8, 11, 13, 11, 8, 5}, {7, 10, 13, 16, 13, 10, 7}, {5, 8, 11, 13, 11, 8, 5}, {4, 6, 8, 10, 8, 6, 4}, {3, 4, 5, 7, 5, 4, 3}}, // 7
            {{3, 4, 5, 7, 7, 5, 4, 3}, {4, 6, 8, 10, 10, 8, 6, 4}, {5, 8, 11, 13, 13, 11, 8, 5}, {7, 10, 13, 16, 16, 13, 10, 7}, {7, 10, 13, 16, 16, 13, 10, 7}, {5, 8, 11, 13, 13, 11, 8, 5}, {4, 6, 8, 10, 10, 8, 6, 4}, {3, 4, 5, 7, 7, 5, 4, 3}}, // 8
            {{3, 4, 5, 7, 7, 7, 5, 4, 3}, {4, 6, 8, 10, 10, 10, 8, 6, 4}, {5, 8, 11, 13, 13, 13, 11, 8, 5}, {7, 10, 13, 16, 16, 16, 13, 10, 7}, {7, 10, 13, 16, 16, 16, 13, 10, 7}, {7, 10, 13, 16, 16, 16, 13, 10, 7}, {5, 8, 11, 13, 13, 13, 11, 8, 5}, {4, 6, 8, 10, 10, 10, 8, 6, 4}, {3, 4, 5, 7, 7, 7, 5, 4, 3}}, // 9
            {{3, 4, 5, 7, 7, 7, 7, 5, 4, 3}, {4, 6, 8, 10, 10, 10, 10, 8, 6, 4}, {5, 8, 11, 13, 13, 13, 13, 11, 8, 5}, {7, 10, 13, 16, 16, 16, 16, 13, 10, 7}, {7, 10, 13, 16, 16, 16, 16, 13, 10, 7}, {7, 10, 13, 16, 16, 16, 16, 13, 10, 7}, {7, 10, 13, 16, 16, 16, 16, 13, 10, 7}, {5, 8, 11, 13, 13, 13, 13, 11, 8, 5}, {4, 6, 8, 10, 10, 10, 10, 8, 6, 4}, {3, 4, 5, 7, 7, 7, 7, 5, 4, 3}}, // 10
            {{3, 4, 5, 7, 7, 7, 7, 7, 5, 4, 3}, {4, 6, 8, 10, 10, 10, 10, 10, 8, 6, 4}, {5, 8, 11, 13, 13, 13, 13, 13, 11, 8, 5}, {7, 10, 13, 16, 16, 16, 16, 16, 13, 10, 7}, {7, 10, 13, 16, 16, 16, 16, 16, 13, 10, 7}, {7, 10, 13, 16, 16, 16, 16, 16, 13, 10, 7}, {7, 10, 13, 16, 16, 16, 16, 16, 13, 10, 7}, {7, 10, 13, 16, 16, 16, 16, 16, 13, 10, 7}, {5, 8, 11, 13, 13, 13, 13, 13, 11, 8, 5}, {4, 6, 8, 10, 10, 10, 10, 10, 8, 6, 4}, {3, 4, 5, 7, 7, 7, 7, 7, 5, 4, 3}}}; // 11

    private int BOARD_SIZE;

    /**
     * Constructor for CPUPLayer
     *
     * @param boardSize the board's size
     */
    public CPUPlayer (int boardSize) {
        this.BOARD_SIZE = boardSize;

        if (boardSize >= 9)
            this.MAX_DEPTH = 8;
        else
            this.MAX_DEPTH = 9;
    }

    /**
     * Generates the best possible move by looking 9 moves ahead in all possibilities.
     *
     * @param cState the current game state
     * @return the best CPU move
     */
    public int generateMove (int[][] cState, int player) {
        // Start by calling maximizePlay since we want to maximize CPU's score.
        int[] bestMove = maximizePlay(cState, 1, player, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return bestMove[0]; // maximizePlay returns a 1D int array as [column of best move, static eval].
    }

    /**
     * Searches through all possible plays in the 7 game board columns for the move that will maximize the current {@code player}'s score.
     * Utilizes alpha-beta pruning to significantly reduce move generation and static evaluation.
     *
     * @param cState the current game state
     * @param depth  the current depth at which a move is being searched. Method terminates and returns the board's utility evaluation if {@code MAX_DEPTH} is reached.
     * @param player the current player for whom a move is being optimized
     * @param alpha  the alpha value used for alpha-beta pruning (lower bound)
     * @param beta   the beta value used for alpha beta pruning (upper bound)
     * @return an integer array constructed using {@code [col of best move, static eval]}
     */
    public int[] maximizePlay (int[][] cState, int depth, int player, int alpha, int beta) {
        int[] ret = new int[] {-1, Integer.MIN_VALUE};

        // If max depth is reached, simply return static eval.
        if (depth == MAX_DEPTH) {
            ret[1] = utilityStaticEval(cState, player);
            return ret;
        }

        // Get all possible moves and start iterating to find best move.
        for (Integer[] move : c4utils.possibleMoves(cState, BOARD_SIZE)) {
            // Get a deep copy of the board to prevent modifications to original calling board.
            int[][] newState = c4utils.deepCopyState(cState);
            int row = move[0], col = move[1];
            newState[row][col] = player;

            // If the player wins, there is no point in checking further. Returns a massive static eval so that the calling minimizePlay node knows not to pick this move.
            if (c4utils.booleanWinChecker(row, col, newState, player, BOARD_SIZE)) {
                ret[0] = col;
                ret[1] = 1000000;
                break;
            }

            int[] minimizedPlay = minimizePlay(newState, depth + 1, otherPlayer(player), alpha, beta); // Call on next depth level.

            // Update best move and alpha as necessary
            if (minimizedPlay[1] > ret[1] || ret[0] == -1) {
                ret[0] = move[1];
                ret[1] = minimizedPlay[1];
            }

            alpha = Math.max(alpha, ret[1]);
            if (beta <= alpha) break;
        }

        return ret;
    }

    /**
     * Searches through all possible plays in the 7 game board columns for the move that will minimize the current {@code player}'s score.
     * Utilizes alpha-beta pruning just like maximizePlay.
     *
     * @param cState the current game state
     * @param depth  the current depth at which a move is being searched. Method terminates and returns the board's utility evaluation if {@code MAX_DEPTH} is reached.
     * @param player the current player for whom a move is being minimized
     * @param alpha  the alpha value used for alpha-beta pruning (lower bound)
     * @param beta   the beta value used for alpha beta pruning (upper bound)
     * @return an integer array constructed using {@code [col of best move, static eval]}
     */
    public int[] minimizePlay (int[][] cState, int depth, int player, int alpha, int beta) {
        // All functionality is essentially the same as maximizePlay, except we're looking for the lowest possible score now.
        int[] ret = new int[] {-1, Integer.MAX_VALUE};

        if (depth == MAX_DEPTH) {
            ret[1] = utilityStaticEval(cState, player);
            return ret;
        }

        for (Integer[] move : c4utils.possibleMoves(cState, BOARD_SIZE)) {
            int[][] newState = c4utils.deepCopyState(cState);
            int row = move[0], col = move[1];
            newState[row][col] = player;

            // Return a large negative number so that calling maximizePlay node knows not to go down this path.
            if (c4utils.booleanWinChecker(row, col, newState, player, BOARD_SIZE)) {
                ret[0] = col;
                ret[1] = -1000000;
                break;
            }

            int[] maximizedPlay = maximizePlay(newState, depth + 1, otherPlayer(player), alpha, beta);

            if (maximizedPlay[1] < ret[1] || ret[0] == -1) {
                ret[0] = move[1];
                ret[1] = maximizedPlay[1];
            }

            // Instead of alpha, update beta for pruning.
            beta = Math.min(beta, ret[1]);
            if (beta <= alpha) break;
        }

        return ret;
    }

    /**
     * A fast utility evaluator to determine how useful the current board is for the given player.
     * This is determined by how many high utility spots the player has vs. how many high utility spots the opponent has.
     * Conceptually borrowed from https://softwareengineering.stackexchange.com/questions/263514/why-does-this-evaluation-function-work-in-a-connect-four-game-in-java.
     *
     * @param currentState the current game state
     * @param player       the player to evaluate for
     * @return the static evaluation
     */
    public int utilityStaticEval (int[][] currentState, int player) {
        int[][] cAllUtil = allUtilities[currentState.length - 7];
        int sum = 0, utility = utilsSum[currentState.length - 7];

        // Loop through all utilities.
        // If current player has a position, add the utility, otherwise subtract the utility as a penalty.
        for (int row = 0; row < cAllUtil.length; row++)
            for (int col = 0; col < cAllUtil[row].length; col++)
                if (currentState[row][col] == player)
                    sum += cAllUtil[row][col];
                else if (currentState[row][col] == otherPlayer(player))
                    sum -= cAllUtil[row][col];

        return utility + sum;
    }

    /**
     * Returns the second player.
     *
     * @param player the current player
     * @return the second player
     */
    private static int otherPlayer (int player) {
        return (player == 2) ? 1 : 2;
    }

}