package eparon.connectfour.Util;

import android.widget.ImageView;

import eparon.connectfour.R;

import static eparon.connectfour.MainActivity.BOARD_SIZE;
import static eparon.connectfour.Util.ResUtil.getResID;

public class LineDrawer {

    public static final int HORIZONTAL_LINE = 1;
    public static final int VERTICAL_LINE = 2;
    public static final int ASCENDING_DIAGONAL_LINE = 3;
    public static final int DESCENDING_DIAGONAL_LINE = 4;

    private ImageView[][] iv;

    public LineDrawer (ImageView[][] iv) {
        this.iv = iv;
    }

    public void DrawLines (int type, int i, int j) {
        switch (type) {
            case HORIZONTAL_LINE:
                setLine(BOARD_SIZE - 1 - i, j    , "line_horizontal_left");
                setLine(BOARD_SIZE - 1 - i, j + 1, "line_horizontal_middle");
                setLine(BOARD_SIZE - 1 - i, j + 2, "line_horizontal_middle");
                setLine(BOARD_SIZE - 1 - i, j + 3, "line_horizontal_right");
                break;
            case VERTICAL_LINE:
                setLine(BOARD_SIZE - 1 - j, i, "line_vertical_bottom");
                setLine(BOARD_SIZE - 2 - j, i, "line_vertical_middle");
                setLine(BOARD_SIZE - 3 - j, i, "line_vertical_middle");
                setLine(BOARD_SIZE - 4 - j, i, "line_vertical_top");
                break;
            case ASCENDING_DIAGONAL_LINE:
                setLine(BOARD_SIZE - 1 - i, j    , "diagonalline_bottomleft");
                setLine(BOARD_SIZE - 2 - i, j + 1, "diagonalline_ascending");
                setLine(BOARD_SIZE - 3 - i, j + 2, "diagonalline_ascending");
                setLine(BOARD_SIZE - 4 - i, j + 3, "diagonalline_topright");

                setLine(BOARD_SIZE - 2 - i, j    , "diagonalline_ascending_leftover_top");
                setLine(BOARD_SIZE - 3 - i, j + 1, "diagonalline_ascending_leftover_top");
                setLine(BOARD_SIZE - 4 - i, j + 2, "diagonalline_ascending_leftover_top");
                setLine(BOARD_SIZE - 1 - i, j + 1, "diagonalline_ascending_leftover_bottom");
                setLine(BOARD_SIZE - 2 - i, j + 2, "diagonalline_ascending_leftover_bottom");
                setLine(BOARD_SIZE - 3 - i, j + 3, "diagonalline_ascending_leftover_bottom");
                break;
            case DESCENDING_DIAGONAL_LINE:
                setLine(BOARD_SIZE - 4 - i, j    , "diagonalline_topleft");
                setLine(BOARD_SIZE - 3 - i, j + 1, "diagonalline_descending");
                setLine(BOARD_SIZE - 2 - i, j + 2, "diagonalline_descending");
                setLine(BOARD_SIZE - 1 - i, j + 3, "diagonalline_bottomright");

                setLine(BOARD_SIZE - 3 - i, j    , "diagonalline_descending_leftover_bottom");
                setLine(BOARD_SIZE - 2 - i, j + 1, "diagonalline_descending_leftover_bottom");
                setLine(BOARD_SIZE - 1 - i, j + 2, "diagonalline_descending_leftover_bottom");
                setLine(BOARD_SIZE - 4 - i, j + 1, "diagonalline_descending_leftover_top");
                setLine(BOARD_SIZE - 3 - i, j + 2, "diagonalline_descending_leftover_top");
                setLine(BOARD_SIZE - 2 - i, j + 3, "diagonalline_descending_leftover_top");
                break;
        }
    }

    private void setLine (int r, int c, String resIDStr) {
        iv[r][c].setImageResource(getResID(resIDStr, R.drawable.class));
    }

}
