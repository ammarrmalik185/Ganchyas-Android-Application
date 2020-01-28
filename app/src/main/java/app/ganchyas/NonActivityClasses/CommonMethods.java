package app.ganchyas.NonActivityClasses;

import android.content.Context;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import app.ganchyas.R;

/**
 * Contains static methods that are used throughout the application
 * @author Paradox
 */
public class CommonMethods {

    /**
     * Gets the theme saved in an hidden file on the phone's memory
     * @param file The root folder assigned to the application
     * @return A reference id of the theme
     */
    public static int getPersonalTheme(File file) {
        File themeFile = new File(file.getAbsolutePath() + "/theme.txt");
        if (themeFile.exists()) {
            Scanner fileReader;
            try {
                fileReader = new Scanner(themeFile);
                return Integer.parseInt(fileReader.nextLine());
            } catch (FileNotFoundException e) {
                return R.style.DarkTheme;
            }
        } else return R.style.DarkTheme;
    }

    /**
     * Displays a Toast Message
     * @param context Context of the activity
     * @param message Toast message to be displayed
     */
    public static void toastMessage(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
