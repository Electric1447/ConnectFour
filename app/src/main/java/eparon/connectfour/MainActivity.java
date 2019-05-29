package eparon.connectfour;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.GridLayout.Spec;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    private final int BOARD_SIZE = 9;

    int gameTurn = 0;
    boolean winner = false;

    GridLayout gridLayout;
    Spec[] row = new Spec[BOARD_SIZE];
    Spec[] col = new Spec[BOARD_SIZE];
    int slotParams;

    FrameLayout[][] fl = new FrameLayout[BOARD_SIZE][BOARD_SIZE];
    ImageButton[][] ib = new ImageButton[BOARD_SIZE][BOARD_SIZE];
    ImageView[][] iv = new ImageView[BOARD_SIZE][BOARD_SIZE];
    int[][] placeUsed = new int[BOARD_SIZE][BOARD_SIZE];

    TextView Text;
    ImageView currentPlayer;

    int[] soldiers = new int[]{R.drawable.soldier_white_border, R.drawable.soldier_red_border};
    int[] cpColors = new int[]{R.drawable.soldier_red, R.drawable.soldier_white};
    String[] colors = new String[]{"Red", "White"};

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView version = findViewById(R.id.version);
        version.setText(String.format("ConnectFour v%s\nCreated by Itai Levin", BuildConfig.VERSION_NAME));

        Text = findViewById(R.id.text);
        currentPlayer = findViewById(R.id.currentPlayer);

        // Getting the Screen Size and converting it into the Slots Prams;
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int)(40 * scale + 1f);
        double screenWidth = size.x - pixels;
        slotParams = (int)(screenWidth / BOARD_SIZE);

        // Initializing the GridLayout;
        for (int i = 0; i < BOARD_SIZE; i++) {
            row[i] = GridLayout.spec(i);
            col[i] = GridLayout.spec(i);
        }

        gridLayout = findViewById(R.id.gl);
        gridLayout.setColumnCount(BOARD_SIZE);
        gridLayout.setRowCount(BOARD_SIZE);

        Init(true);
    }

    /*
     *  Initialize Game
     */
    private void Init (boolean onAppStart) {

        deleteCache(getApplicationContext());

        gameTurn = 0;
        winner = false;
        Text.setText(String.format("%s's turn", colors[1]));
        currentPlayer.setImageResource(cpColors[1]);

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {

                if (onAppStart) {
                    View v = View.inflate(this, R.layout.cell, null);
                    fl[i][j] = v.findViewById(R.id.fl);
                    ib[i][j] = v.findViewById(R.id.image);
                    iv[i][j] = v.findViewById(R.id.line);

                    if (fl[i][j].getParent() != null)
                        ((ViewGroup)fl[i][j].getParent()).removeView(fl[i][j]);

                    LayoutParams lp = new LayoutParams(row[i], col[j]);
                    lp.width = slotParams;
                    lp.height = slotParams;
                    fl[i][j].setLayoutParams(lp);
                    gridLayout.addView(fl[i][j], lp);
                }

                placeUsed[i][j] = 0;
                ib[i][j].setImageResource(R.drawable.empty_border);
                iv[i][j].setImageResource(android.R.color.transparent);

                final int finalJ = j;
                ib[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View view) {
                        DoTurn(finalJ);
                    }
                });
            }
        }

        // Set the GridLayout's height the same size as it's width
        ViewGroup.LayoutParams lp = gridLayout.getLayoutParams();
        lp.height = lp.width;
        gridLayout.setLayoutParams(lp);
    }

    private void DoTurn (int colInt) {
        if (!winner)
            for (int i = 0; i < BOARD_SIZE; i++)
                if (placeUsed[BOARD_SIZE - 1 - i][colInt] == 0) {

                    placeUsed[BOARD_SIZE - 1 - i][colInt] = gameTurn % 2 + 1;
                    ib[BOARD_SIZE - 1 - i][colInt].setImageResource(soldiers[gameTurn % 2]);

                    Text.setText(String.format("%s's turn", colors[gameTurn % 2]));
                    currentPlayer.setImageResource(cpColors[gameTurn % 2]);

                    gameTurn++;
                    if (gameTurn == 81)
                        Text.setText("Draw!");

                    CheckWin();
                    break;
                }
    }

    private void CheckWin () {

        // Horizontal Win
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (winner)
                break;
            for (int j = 0; j < BOARD_SIZE - 3; j++) {
                if (placeUsed[BOARD_SIZE - 1 - i][j] != 0
                        && placeUsed[BOARD_SIZE - 1 - i][j] == placeUsed[BOARD_SIZE - 1 - i][j + 1]
                        && placeUsed[BOARD_SIZE - 1 - i][j] == placeUsed[BOARD_SIZE - 1 - i][j + 2]
                        && placeUsed[BOARD_SIZE - 1 - i][j] == placeUsed[BOARD_SIZE - 1 - i][j + 3]) {

                    setLine(BOARD_SIZE - 1 - i, j    , R.drawable.line_horizontal_left);
                    setLine(BOARD_SIZE - 1 - i, j + 1, R.drawable.line_horizontal_middle);
                    setLine(BOARD_SIZE - 1 - i, j + 2, R.drawable.line_horizontal_middle);
                    setLine(BOARD_SIZE - 1 - i, j + 3, R.drawable.line_horizontal_right);

                    Win2();
                    break;
                }
            }
        }

        // Vertical Win
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (winner)
                break;
                for (int j = 0; j < BOARD_SIZE - 3; j++) {
                    if (placeUsed[BOARD_SIZE - 1 - j][i] != 0
                            && placeUsed[BOARD_SIZE - 1 - j][i] == placeUsed[BOARD_SIZE - 2 - j][i]
                            && placeUsed[BOARD_SIZE - 1 - j][i] == placeUsed[BOARD_SIZE - 3 - j][i]
                            && placeUsed[BOARD_SIZE - 1 - j][i] == placeUsed[BOARD_SIZE - 4 - j][i]) {

                        setLine(BOARD_SIZE - 1 - j, i, R.drawable.line_vertical_bottom);
                        setLine(BOARD_SIZE - 2 - j, i, R.drawable.line_vertical_middle);
                        setLine(BOARD_SIZE - 3 - j, i, R.drawable.line_vertical_middle);
                        setLine(BOARD_SIZE - 4 - j, i, R.drawable.line_vertical_top);

                        Win2();
                        break;
                    }
                }
        }

        // Diagonal Line Wins
        for (int i = 0; i < BOARD_SIZE - 3; i++) {
            if (winner)
                break;
            for (int j = 0; j < BOARD_SIZE - 3; j++) {

                // Ascending Diagonal Line Win
                if (placeUsed[BOARD_SIZE - 1 - i][j] != 0
                        && placeUsed[BOARD_SIZE - 1 - i][j] == placeUsed[BOARD_SIZE - 2 - i][j + 1]
                        && placeUsed[BOARD_SIZE - 1 - i][j] == placeUsed[BOARD_SIZE - 3 - i][j + 2]
                        && placeUsed[BOARD_SIZE - 1 - i][j] == placeUsed[BOARD_SIZE - 4 - i][j + 3]) {

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

                    Win2();
                    break;
                }

                // Descending Diagonal Line Win
                if (placeUsed[BOARD_SIZE - 4 - i][j] != 0
                        && placeUsed[BOARD_SIZE - 4 - i][j] == placeUsed[BOARD_SIZE - 3 - i][j + 1]
                        && placeUsed[BOARD_SIZE - 4 - i][j] == placeUsed[BOARD_SIZE - 2 - i][j + 2]
                        && placeUsed[BOARD_SIZE - 4 - i][j] == placeUsed[BOARD_SIZE - 1 - i][j + 3]) {

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

                    Win2();
                    break;
                }
            }
        }
    }

    private void Win2 () {
        Text.setText(String.format("The Winner is: %s", colors[gameTurn % 2]));
        winner = true;
    }

    private void setLine (int r, int c, int resID) {
        iv[r][c].setImageResource(resID);
    }

    public void resetGame (View view) {
        Init(false);
    }

    public static void deleteCache (Context context) {
        FileUtils.deleteQuietly(context.getCacheDir());
    }

}
