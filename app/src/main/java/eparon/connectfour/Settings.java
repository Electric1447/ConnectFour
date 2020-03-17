package eparon.connectfour;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class Settings extends AppCompatActivity {

    public String PREFS_C4 = "C4PrefsFile";
    SharedPreferences prefs;

    int BOARD_SIZE = 9;
    EditText boardSize;

    boolean PLAYER_VS_CPU = false;
    CheckBox modeCB;

    @Override
    public void onBackPressed () {
        Save();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(PREFS_C4, Context.MODE_PRIVATE);

        BOARD_SIZE = prefs.getInt("board_size", BOARD_SIZE);
        PLAYER_VS_CPU = prefs.getBoolean("mode", PLAYER_VS_CPU);

        boardSize = findViewById(R.id.boardSizeText);
        boardSize.setHint(String.format(Locale.getDefault(), "%02d", BOARD_SIZE));

        modeCB = findViewById(R.id.cbMode);
        modeCB.setChecked(PLAYER_VS_CPU);

        ((TextView)findViewById(R.id.version)).setText(String.format("%s v%s\nCreated by Itai Levin.", getString(R.string.app_name), BuildConfig.VERSION_NAME));
    }


    public void changeMode (View view) {
        modeCB.setChecked(!modeCB.isChecked());
        PLAYER_VS_CPU = modeCB.isChecked();
    }

    public void Save () {
        if (!boardSize.getText().toString().equals("")) BOARD_SIZE = Integer.parseInt(boardSize.getText().toString());

        if (BOARD_SIZE < 7 || BOARD_SIZE > 11) {
            Toast.makeText(this, R.string.err_board_size, Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("board_size", BOARD_SIZE);
            editor.putBoolean("mode", PLAYER_VS_CPU);
            editor.apply();
            startActivity(new Intent(Settings.this, MainActivity.class));
        }
    }

    public void goBack (View view) {
        Save();
    }

}
