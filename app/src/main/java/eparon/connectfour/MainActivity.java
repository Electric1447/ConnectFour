package eparon.connectfour;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridLayout.*;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    private final int BOARD_SIZE = 9;

    int gameTurn = 0;
    boolean winner = false;

    GridLayout gridLayout;
    Spec row[] = new Spec[BOARD_SIZE];
    Spec col[] = new Spec[BOARD_SIZE];
    int slotWidth;
    double slotHeight;

    ImageButton ib[][] = new ImageButton[row.length][col.length];
    int placeUsed[][] = new int[row.length][col.length];

    TextView Text;
    ImageView currentPlayer;

    int soldiers[] = new int[]{R.drawable.soldier_white_border, R.drawable.soldier_red_border};
    int cpColors[] = new int[]{R.drawable.soldier_red, R.drawable.soldier_white};
    String colors[] = new String[]{"Red", "White"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView version = findViewById(R.id.version);
        version.setText(String.format("ConnectFour v%s   By Itai Levin", BuildConfig.VERSION_NAME));

        Text = findViewById(R.id.text);
        currentPlayer = findViewById(R.id.currentPlayer);

        // Getting the Screen Size and converting it into the Slots Prams;
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (40 * scale + 1f);
        double screenWidth = size.x - pixels;
        slotWidth = (int) (screenWidth / BOARD_SIZE);
        slotHeight = (double) (slotWidth);

        // Initializing the GridLayout;
        for (int i = 0; i < row.length; i++) {
            row[i] = GridLayout.spec(i);
            col[i] = GridLayout.spec(i);
        }

        gridLayout = findViewById(R.id.gl);
        gridLayout.setColumnCount(col.length);
        gridLayout.setRowCount(row.length);

        Init();
    }

    /*
     *  Initialize Game
     */
    private void Init() {

        gameTurn = 0;
        winner = false;
        Text.setText(String.format("%s's turn", colors[1]));
        currentPlayer.setImageResource(cpColors[1]);

        for (int i = 0; i < row.length; i++) {
            for (int j = 0; j < col.length; j++) {

                placeUsed[i][j] = 0;

                View v = View.inflate(this, R.layout.border, null);
                ib[i][j] = v.findViewById(R.id.image);

                if (ib[i][j].getParent() != null)
                    ((ViewGroup) ib[i][j].getParent()).removeView(ib[i][j]);

                LayoutParams lp = new LayoutParams(row[i], col[j]);
                lp.width = slotWidth;
                lp.height = (int) (slotHeight);
                ib[i][j].setLayoutParams(lp);
                gridLayout.addView(ib[i][j], lp);

                final int finalJ = j;
                ib[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DoTurn(finalJ);
                    }
                });
            }
        }

        // Set the GridLayout's height the same size as it's width
        ViewGroup.LayoutParams lp = gridLayout.getLayoutParams();
        double temp = (double)(lp.width); // Converting to temp value to ovoid a stupid Warning
        lp.height = (int)(temp);
        gridLayout.setLayoutParams(lp);
    }

    private void DoTurn(int colInt) {
        if (!winner) {
            for (int i = 0; i < row.length; i++) {
                if (placeUsed[row.length - 1 - i][colInt] == 0) {
                    placeUsed[row.length - 1 - i][colInt] = gameTurn % 2 + 1;
                    ib[row.length - 1 - i][colInt].setImageResource(soldiers[gameTurn % 2]);
                    Text.setText(String.format("%s's turn", colors[gameTurn % 2]));
                    currentPlayer.setImageResource(cpColors[gameTurn % 2]);
                    gameTurn++;
                    if (gameTurn == 81)
                        Text.setText("Draw!");
                    CheckWin();
                    break;
                }
            }
        }
    }

    private void CheckWin() {

        // Horizontal Win
        for (int i = 0; i < row.length; i++) {
            for (int j = 0; j < col.length - 3; j++) {
                if (placeUsed[row.length - 1 - i][j] != 0
                        && placeUsed[row.length - 1 - i][j] == placeUsed[row.length - 1 - i][j + 1]
                        && placeUsed[row.length - 1 - i][j] == placeUsed[row.length - 1 - i][j + 2]
                        && placeUsed[row.length - 1 - i][j] == placeUsed[row.length - 1 - i][j + 3]) {
                    Text.setText(String.format("The Winner is: %s", colors[gameTurn % 2]));
                    winner = true;
                    break;
                }
            }
        }

        // Vertical Win
        for (int i = 0; i < col.length; i++) {
            if (!winner) {
                for (int j = 0; j < row.length - 3; j++) {
                    if (placeUsed[row.length - 1 - j][i] != 0
                            && placeUsed[row.length - 1 - j][i] == placeUsed[row.length - 2 - j][i]
                            && placeUsed[row.length - 1 - j][i] == placeUsed[row.length - 3 - j][i]
                            && placeUsed[row.length - 1 - j][i] == placeUsed[row.length - 4 - j][i]) {
                        Text.setText(String.format("The Winner is: %s", colors[gameTurn % 2]));
                        winner = true;
                        break;
                    }
                }
            }
        }

        // Diagonal Line Wins
        for (int i = 0; i < row.length - 3; i++) {
            for (int j = 0; j < col.length - 3; j++) {

                // Ascending Diagonal Line Win
                if (placeUsed[row.length - 1 - i][j] != 0
                        && placeUsed[row.length - 1 - i][j] == placeUsed[row.length - 2 - i][j + 1]
                        && placeUsed[row.length - 1 - i][j] == placeUsed[row.length - 3 - i][j + 2]
                        && placeUsed[row.length - 1 - i][j] == placeUsed[row.length - 4 - i][j + 3]) {
                    Text.setText(String.format("The Winner is: %s", colors[gameTurn % 2]));
                    winner = true;
                    break;
                }

                // Descending Diagonal Line Win
                if (placeUsed[row.length - 4 - i][j] != 0
                        && placeUsed[row.length - 4 - i][j] == placeUsed[row.length - 3 - i][j + 1]
                        && placeUsed[row.length - 4 - i][j] == placeUsed[row.length - 2 - i][j + 2]
                        && placeUsed[row.length - 4 - i][j] == placeUsed[row.length - 1 - i][j + 3]) {
                    Text.setText(String.format("The Winner is: %s", colors[gameTurn % 2]));
                    winner = true;
                    break;
                }
            }
        }
    }

    public void resetGame(View view) {
        Init();
    }

}
