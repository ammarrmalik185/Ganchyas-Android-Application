package app.ganchyas.NonActivityClasses;

import android.content.Context;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
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

    public static boolean downloadFile(Context context, String url, File outputFile) throws Exception {
        URL u = new URL(url);
        URLConnection conn = u.openConnection();
        int contentLength = conn.getContentLength();

        DataInputStream stream = new DataInputStream(u.openStream());

        byte[] buffer = new byte[contentLength];
        stream.readFully(buffer);
        stream.close();

        DataOutputStream fos = new DataOutputStream(new FileOutputStream(outputFile));
        fos.write(buffer);
        fos.flush();
        fos.close();
        return true;
    }

}
