package eparon.connectfour;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    public static final int BOARD_SIZE = 9;

    int gameTurn = 0;
    boolean winner = false;

    FrameLayout[][] fl = new FrameLayout[BOARD_SIZE][BOARD_SIZE];
    ImageButton[][] ib = new ImageButton[BOARD_SIZE][BOARD_SIZE];
    ImageView[][] iv = new ImageView[BOARD_SIZE][BOARD_SIZE];
    int[][] indexArr = new int[BOARD_SIZE][BOARD_SIZE];

    TextView Text;
    ImageView currentPlayer;

    int[] soldiers = new int[] {R.drawable.soldier_white_border, R.drawable.soldier_red_border};
    int[] cpColors = new int[] {R.drawable.soldier_red, R.drawable.soldier_white};
    String[] colors = new String[] {"Red", "White"};

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Text = findViewById(R.id.text);
        currentPlayer = findViewById(R.id.currentPlayer);

        Init();
        DrawBoard();

        ((TextView)findViewById(R.id.version)).setText(String.format("%s v%s\nCreated by Itai Levin.", getString(R.string.app_name), BuildConfig.VERSION_NAME));
    }

    /**
     * This method initializes the game.
     */
    private void Init () {
        gameTurn = 0;
        winner = false;
        Text.setText(String.format("%s's turn", colors[1]));
        currentPlayer.setImageResource(cpColors[1]);

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
    }

    /**
     * DoTurn method.
     *
     * @param colInt the column the user clicked on
     */
    private void DoTurn (int colInt) {
        if (winner) return;

        for (int i = 0; i < BOARD_SIZE; i++)
            if (indexArr[BOARD_SIZE - 1 - i][colInt] == 0) {

                indexArr[BOARD_SIZE - 1 - i][colInt] = gameTurn % 2 + 1;
                ib[BOARD_SIZE - 1 - i][colInt].setImageResource(soldiers[gameTurn % 2]);

                Text.setText(String.format("%s's turn", colors[gameTurn % 2]));
                currentPlayer.setImageResource(cpColors[gameTurn % 2]);

                gameTurn++;
                CheckWin();
                break;
            }
    }

    /**
     * Check win method.
     */
    private void CheckWin () {

        // Horizontal & Vertical Wins
        for (int i = 0; i < BOARD_SIZE; i++)
            for (int j = 0; j < BOARD_SIZE - 3; j++)
                if (placeComparator(1 + i, j, 1 + i, j + 1, 1 + i, j + 2, 1 + i, j + 3)) { // Horizontal
                    DrawLines(1, i, j);
                    Win2(false);
                    return;
                } else if (placeComparator(1 + j, i, 2 + j, i, 3 + j, i, 4 + j, i)) { // Vertical
                    DrawLines(2, i, j);
                    Win2(false);
                    return;
                }

        // Diagonal Line Wins
        for (int i = 0; i < BOARD_SIZE - 3; i++)
            for (int j = 0; j < BOARD_SIZE - 3; j++)
                if (placeComparator(1 + i, j, 2 + i, j + 1, 3 + i, j + 2, 4 + i, j + 3)) { // Ascending
                    DrawLines(3, i, j);
                    Win2(false);
                    return;
                } else if (placeComparator(4 + i, j, 3 + i, j + 1, 2 + i, j + 2, 1 + i, j + 3)) { // Descending
                    DrawLines(4, i, j);
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
        if (draw) Text.setText("Draw!");
        else Text.setText(String.format("The Winner is: %s", colors[gameTurn % 2]));
        winner = true;
    }

    private boolean placeComparator (int a1, int a2, int b1, int b2, int c1, int c2, int d1, int d2) {
        int first = indexArr[BOARD_SIZE - a1][a2], second = indexArr[BOARD_SIZE - b1][b2], third = indexArr[BOARD_SIZE - c1][c2], fourth = indexArr[BOARD_SIZE - d1][d2];
        return (first != 0 && first == second && first == third && first == fourth);
    }

    public void resetGame (View view) {
        Init();
    }

    //region Lines region
    private void setLine (int r, int c, int resID) {
        iv[r][c].setImageResource(resID);
    }

    private void DrawLines (int type, int i, int j) {
        switch (type) {
            case 1: // Horizontal
                setLine(BOARD_SIZE - 1 - i, j    , R.drawable.line_horizontal_left);
                setLine(BOARD_SIZE - 1 - i, j + 1, R.drawable.line_horizontal_middle);
                setLine(BOARD_SIZE - 1 - i, j + 2, R.drawable.line_horizontal_middle);
                setLine(BOARD_SIZE - 1 - i, j + 3, R.drawable.line_horizontal_right);
                break;
            case 2: // Vertical
                setLine(BOARD_SIZE - 1 - j, i, R.drawable.line_vertical_bottom);
                setLine(BOARD_SIZE - 2 - j, i, R.drawable.line_vertical_middle);
                setLine(BOARD_SIZE - 3 - j, i, R.drawable.line_vertical_middle);
                setLine(BOARD_SIZE - 4 - j, i, R.drawable.line_vertical_top);
                break;
            case 3: // Ascending Diagonal
                setLine(BOARD_SIZE - 1 - i, j    , R.drawable.diagonalline_bottomleft);
                setLine(BOARD_SIZE - 2 - i, j + 1, R.drawable.diagonalline_ascending);
                setLine(BOARD_SIZE - 3 - i, j + 2, R.drawable.diagonalline_ascending);
                setLine(BOARD_SIZE - 4 - i, j + 3, R.drawable.diagonalline_topright);

                setLine(BOARD_SIZE - 2 - i, j    , R.drawable.diagonalline_ascending_leftover_top);
                setLine(BOARD_SIZE - 3 - i, j + 1, R.drawable.diagonalline_ascending_leftover_top);
                setLine(BOARD_SIZE - 4 - i, j + 2, R.drawable.diagonalline_ascending_leftover_top);
                setLine(BOARD_SIZE - 1 - i, j + 1, R.drawable.diagonalline_ascending_leftover_bottom);
                setLine(BOARD_SIZE - 2 - i, j + 2, R.drawable.diagonalline_ascending_leftover_bottom);
                setLine(BOARD_SIZE - 3 - i, j + 3, R.drawable.diagonalline_ascending_leftover_bottom);
                break;
            case 4: // Descending Diagonal
                setLine(BOARD_SIZE - 4 - i, j    , R.drawable.diagonalline_topleft);
                setLine(BOARD_SIZE - 3 - i, j + 1, R.drawable.diagonalline_descending);
                setLine(BOARD_SIZE - 2 - i, j + 2, R.drawable.diagonalline_descending);
                setLine(BOARD_SIZE - 1 - i, j + 3, R.drawable.diagonalline_bottomright);

                setLine(BOARD_SIZE - 4 - i, j + 1, R.drawable.diagonalline_descending_leftover_top);
                setLine(BOARD_SIZE - 3 - i, j + 2, R.drawable.diagonalline_descending_leftover_top);
                setLine(BOARD_SIZE - 2 - i, j + 3, R.drawable.diagonalline_descending_leftover_top);
                setLine(BOARD_SIZE - 3 - i, j    , R.drawable.diagonalline_descending_leftover_bottom);
                setLine(BOARD_SIZE - 2 - i, j + 1, R.drawable.diagonalline_descending_leftover_bottom);
                setLine(BOARD_SIZE - 1 - i, j + 2, R.drawable.diagonalline_descending_leftover_bottom);
                break;
        }
    }
    //endregion

}
