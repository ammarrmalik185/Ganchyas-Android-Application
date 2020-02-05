package app.ganchyas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import app.ganchyas.NonActivityClasses.CommonMethods;

/**
 * Allows the user to edit the app's preferences
 * @author Paradox
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * This contains the group of radio buttons that refers to the available themes
     */
    RadioGroup colorSelector;

    /**
     * Overriding onCreate to Inflate custom UI using activity_edit_info.xml
     * @param savedInstanceState contains the old state of this UI
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        colorSelector = findViewById(R.id.colorSelector);
        colorSelector.check(getThemeButtonId());
    }

    /**
     * Invoked when the apply button is pressed
     * @param view The button that was pressed
     */
    public void colorApplyButton(View view) {
        int id = colorSelector.getCheckedRadioButtonId();
        int theme = R.style.DarkTheme;
        switch (id) {
            case R.id.themeDark:
                theme = R.style.DarkTheme;
                break;
            case R.id.themeLight:
                theme = R.style.LightTheme;
                break;
            case R.id.themeBlue:
                theme = R.style.BlueTheme;
                break;
            case R.id.themeGreen:
                theme = R.style.GreenTheme;
                break;
        }

        File file = new File(getFilesDir().getAbsolutePath() + "/theme.txt");
        try {
            if (!file.exists()) file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(Integer.toString(theme));
            fileWriter.close();
            recreate();
        } catch (IOException e) {
            Toast.makeText(SettingsActivity.this, "File save error", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Gives the id of the appropriate radio button that needs to be checked
     * @return Id of a radio button in the radio group
     */
    private int getThemeButtonId(){
        int themeId = CommonMethods.getPersonalTheme(getFilesDir());
        if (themeId==R.style.DarkTheme)
            return R.id.themeDark;
        else if (themeId==R.style.LightTheme)
            return R.id.themeLight;
        else if (themeId==R.style.BlueTheme)
            return R.id.themeBlue;
        else if (themeId==R.style.GreenTheme)
            return R.id.themeGreen;
        else
            return R.id.themeDark;
    }

    /**
     * Overwriting onBackPressed to recreate the MainActivity
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
