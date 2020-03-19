package eparon.connectfour;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import eparon.connectfour.AI.CPUPlayer;
import eparon.connectfour.Util.LineDrawer;

import static eparon.connectfour.Util.c4utils.lowestRow;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    public String PREFS_C4 = "C4PrefsFile";
    SharedPreferences prefs;

    int BOARD_SIZE = 9;
    boolean PLAYER_VS_CPU = false;

    int gameTurn = 0;
    boolean winner = false, generatingMove;
    int[][] indexArr;

    CPUPlayer cpu;
    LineDrawer lineDrawer;

    FrameLayout[][] fl;
    ImageButton[][] ib;
    ImageView[][] iv;

    TextView Text;
    ImageView currentPlayer;

    int[] soldiers = new int[] {R.drawable.soldier_white_border, R.drawable.soldier_red_border};
    int[] cpColors = new int[] {R.drawable.soldier_red, R.drawable.soldier_white};
    String[] colors = new String[] {"Red", "White"};

    @Override
    public void onBackPressed () {
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_C4, Context.MODE_PRIVATE);

        BOARD_SIZE = prefs.getInt("board_size", BOARD_SIZE);
        PLAYER_VS_CPU = prefs.getBoolean("mode", PLAYER_VS_CPU);

        indexArr = new int[BOARD_SIZE][BOARD_SIZE];

        fl = new FrameLayout[BOARD_SIZE][BOARD_SIZE];
        ib = new ImageButton[BOARD_SIZE][BOARD_SIZE];
        iv = new ImageView[BOARD_SIZE][BOARD_SIZE];

        cpu = new CPUPlayer(BOARD_SIZE);

        Text = findViewById(R.id.text);
        currentPlayer = findViewById(R.id.currentPlayer);

        Init();
        DrawBoard();
    }

    /**
     * This method initializes the game.
     */
    private void Init () {

        gameTurn = 0;
        winner = false;
        Text.setText(String.format("%s's turn", colors[1]));
        currentPlayer.setImageResource(cpColors[1]);
        generatingMove = false;

        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++) {
                indexArr[i][j] = 0;
                if (ib[i][j] != null) ib[i][j].setImageResource(R.drawable.empty_border);
                if (iv[i][j] != null) iv[i][j].setImageResource(android.R.color.transparent);
            }
    }

    /**
     * This method draws the game board.
     */
    private void DrawBoard () {
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int slotDimensions = (size.x - (int)(40 * scale + 0.5f)) / BOARD_SIZE;

        GridLayout gridLayout = findViewById(R.id.gl);
        gridLayout.setColumnCount(BOARD_SIZE);
        gridLayout.setRowCount(BOARD_SIZE);

        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE; j++) {

                View v = View.inflate(this, R.layout.cell, null);
                fl[i][j] = v.findViewById(R.id.fl);
                ib[i][j] = v.findViewById(R.id.image);
                iv[i][j] = v.findViewById(R.id.line);

                LayoutParams lp = new LayoutParams();
                lp.width = slotDimensions;
                lp.height = slotDimensions;
                gridLayout.addView(fl[i][j], lp);

                ib[i][j].setImageResource(R.drawable.empty_border);
                iv[i][j].setImageResource(android.R.color.transparent);

                final int finalJ = j;
                ib[i][j].setOnClickListener(view -> DoTurn(finalJ));
            }

        lineDrawer = new LineDrawer(iv, BOARD_SIZE);
    }

    //region DoTurn region

    /**
     * DoTurn method.
     *
     * @param col the column the user clicked on
     */
    private void DoTurn (int col) {
        if (winner || (PLAYER_VS_CPU && (gameTurn % 2 != 0 || generatingMove))) return;

        int row = lowestRow(col, indexArr, BOARD_SIZE);
        placePiece(row, col);
        if (!winner && PLAYER_VS_CPU) DoCPUTurn();
    }

    /**
     * DoTurn method for the CPUPlayer.
     */
    private void DoCPUTurn () {
        generatingMove = true;
        MoveGenerator moveGenerator = new MoveGenerator();
        moveGenerator.execute();
    }

    /**
     * This method places a piece on the board.
     *
     * @param r row position
     * @param c column position
     */
    private void placePiece (int r, int c) {
        if (winner || r == -1 || c == -1) return;

        indexArr[r][c] = gameTurn % 2 + 1;
        ib[r][c].setImageResource(soldiers[gameTurn % 2]);

        Text.setText(String.format("%s's turn", colors[gameTurn % 2]));
        currentPlayer.setImageResource(cpColors[gameTurn % 2]);

        gameTurn++;
        CheckWin();
    }
    //endregion

    //region Win region

    /**
     * Check win method.
     */
    private void CheckWin () {

        // Horizontal & Vertical Wins
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE - 3; j++)
                if (placeComparator(1 + i, j, 1 + i, j + 1, 1 + i, j + 2, 1 + i, j + 3)) { // Horizontal
                    lineDrawer.DrawLines(LineDrawer.HORIZONTAL_LINE, i, j);
                    Win2(false);
                    return;
                } else if (placeComparator(1 + j, i, 2 + j, i, 3 + j, i, 4 + j, i)) { // Vertical
                    lineDrawer.DrawLines(LineDrawer.VERTICAL_LINE, i, j);
                    Win2(false);
                    return;
                }

        // Diagonal Line Wins
        for (int i = 0; i < BOARD_SIZE - 3; i++)
            for (int j = 0; j < BOARD_SIZE - 3; j++)
                if (placeComparator(1 + i, j, 2 + i, j + 1, 3 + i, j + 2, 4 + i, j + 3)) { // Ascending
                    lineDrawer.DrawLines(LineDrawer.ASCENDING_DIAGONAL_LINE, i, j);
                    Win2(false);
                    return;
                } else if (placeComparator(4 + i, j, 3 + i, j + 1, 2 + i, j + 2, 1 + i, j + 3)) { // Descending
                    lineDrawer.DrawLines(LineDrawer.DESCENDING_DIAGONAL_LINE, i, j);
                    Win2(false);
                    return;
                }

        if (gameTurn == BOARD_SIZE * BOARD_SIZE) Win2(true);
    }

    /**
     * This method is the helper method of - 'Win'.
     *
     * @param draw win/draw
     */
    private void Win2 (boolean draw) {
        if (draw) Text.setText(getString(R.string.draw));
        else Text.setText(String.format("The Winner is: %s", colors[gameTurn % 2]));
        winner = true;
    }

    /**
     * Place comparator that is used for checking wins
     *
     * @param a1 cell 1's row position
     * @param a2 cell 1's column position
     * @param b1 cell 2's row position
     * @param b2 cell 2's column position
     * @param c1 cell 3's row position
     * @param c2 cell 3's column position
     * @param d1 cell 4's row position
     * @param d2 cell 4's column position
     * @return if the 4 cells are the same value.
     */
    private boolean placeComparator (int a1, int a2, int b1, int b2, int c1, int c2, int d1, int d2) {
        int first = indexArr[BOARD_SIZE - a1][a2], second = indexArr[BOARD_SIZE - b1][b2], third = indexArr[BOARD_SIZE - c1][c2], fourth = indexArr[BOARD_SIZE - d1][d2];
        return (first != 0 && first == second && first == third && first == fourth);
    }
    //endregion

    public void resetGame (View view) {
        Init();
    }

    public void goSettings (View view) {
        startActivity(new Intent(MainActivity.this, Settings.class));
    }

    //region MoveGenerator region
    @SuppressLint("StaticFieldLeak")
    private class MoveGenerator extends AsyncTask<Void, Void, Void> {
        private int row, bestCol;

        /// Instantiates MoveGenerator with dummy row and column values.
        public MoveGenerator () {
            row = -1;
            bestCol = -1;
        }

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
        }

        /// Decides the best move for CPU to play.
        @Override
        protected Void doInBackground (Void... voids) {
            long start = System.nanoTime();

            bestCol = cpu.generateMove(indexArr, gameTurn % 2 + 1);

            Log.d("Calculation time: ", (System.nanoTime() - start) / 1000000 + "[ms]");

            row = lowestRow(bestCol, indexArr, BOARD_SIZE);

            // Only allow CPU to play a move after a move is generated.
            // This check was included because onPostExecute seems to update the UI multiple times sometimes.
            runOnUiThread(() -> {
                if (generatingMove) {
                    if (row != -1 && bestCol != -1) placePiece(row, bestCol);

                    row = -1;
                    bestCol = -1;

                    generatingMove = false;
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute (Void aVoid) {
            super.onPostExecute(aVoid);
            generatingMove = false;
        }
    }
    //endregion

}
