package eparon.connectfour;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;


public class SettingsFragment extends PreferenceFragmentCompat {

    private final String githubURL = "https://github.com/Electric1447/ConnectFour";

    @Override
    public void onCreatePreferences (Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        ListPreference mBoardSizePreference = getPreferenceManager().findPreference("board_size");
        assert mBoardSizePreference != null;
        mBoardSizePreference.setSummary(String.format("%s %s", getString(R.string.prefs_board_size), mBoardSizePreference.getValue()));
        mBoardSizePreference.setOnPreferenceChangeListener((preference, newValue) -> {
            mBoardSizePreference.setSummary(String.format("%s %s", getString(R.string.prefs_board_size), newValue));
            return true;
        });

        Preference mAboutPreference = getPreferenceManager().findPreference("about");
        assert mAboutPreference != null;
        mAboutPreference.setTitle(String.format("%s %s%s", getString(R.string.app_name), getString(R.string.prefs_about_version_suffix), BuildConfig.VERSION_NAME));
        mAboutPreference.setSummary(R.string.prefs_about_summery);

        Preference mGithubPreference = getPreferenceManager().findPreference("github");
        assert mGithubPreference != null;
        mGithubPreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(githubURL)));
            return true;
        });
    }

}