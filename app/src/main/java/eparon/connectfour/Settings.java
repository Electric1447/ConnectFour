package eparon.connectfour;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    public String PREFS_C4 = "C4PrefsFile";
    SharedPreferences prefs;

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

        PLAYER_VS_CPU = prefs.getBoolean("mode", PLAYER_VS_CPU);

        modeCB = findViewById(R.id.cbMode);
        modeCB.setChecked(PLAYER_VS_CPU);

        ((TextView)findViewById(R.id.version)).setText(String.format("%s v%s\nCreated by Itai Levin.", getString(R.string.app_name), BuildConfig.VERSION_NAME));
    }

    public void changeMode (View view) {
        modeCB.setChecked(!modeCB.isChecked());
        PLAYER_VS_CPU = modeCB.isChecked();
    }

    public void Save () {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("mode", PLAYER_VS_CPU);
        editor.apply();
        startActivity(new Intent(Settings.this, MainActivity.class));
    }

    public void goBack (View view) {
        Save();
    }

}
