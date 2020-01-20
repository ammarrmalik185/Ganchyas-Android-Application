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
 * @author Paradox;
 */

public class SettingsActivity extends AppCompatActivity {

    RadioGroup colorSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonMethods.getPersonalTheme(getFilesDir()));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        colorSelector = findViewById(R.id.colorSelector);
        colorSelector.check(getThemeButtonId());
    }

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
