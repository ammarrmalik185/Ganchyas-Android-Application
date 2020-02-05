package app.ganchyas.NonActivityClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import app.ganchyas.R;

public class PatchesListAdapter extends ArrayAdapter<PatchPack> {

    public PatchesListAdapter(@NonNull Context context, @NonNull List<PatchPack> objects) {
        super(context, R.layout.design_patches, objects);
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View patchView;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        patchView = inflater.inflate(R.layout.design_patches, parent, false);

        TextView title = patchView.findViewById(R.id.title);
        TextView dataTime = patchView.findViewById(R.id.dateTime);
        TextView patchNotes =  patchView.findViewById(R.id.patchNotes);
        TextView versionNo =  patchView.findViewById(R.id.versionNo);

        PatchPack currentPack = getItem(position);

        assert currentPack != null;
        title.setText(currentPack.getTitle());
        dataTime.setText(String.format("Released on : %s at %s", currentPack.getDate(), currentPack.getTime()));
        patchNotes.setText(currentPack.getPathNotes());
        versionNo.setText("Version no : " + currentPack.getVersionNo());
        patchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.dialog_confirmation);
                TextView confirmationMessage = dialog.findViewById(R.id.confirmationMessage);
                Button choice1 = dialog.findViewById(R.id.choice1);
                Button choice2 = dialog.findViewById(R.id.choice2);
                confirmationMessage.setText("Download patch " + getItem(position).getVersionNo()
                                                + " ?");
                choice1.setText("Yes");
                choice2.setText("No");
                choice2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                choice1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Thread thread = new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                installVersion(position);
//                            }
//                        });
//                        thread.start();
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getItem(position).getDownloadLink()));
                        getContext().startActivity(browserIntent);

                    }
                });
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        return patchView;
    }

    private void installVersion(int position) {
        File apkFile = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            apkFile = new File(getContext().getFilesDir().getAbsolutePath() + "installPackage.apk");
        }
        boolean downloaded = downloadFile(getItem(position).getDownloadLink(), apkFile);
        if (downloaded){
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        }
    }

    private boolean downloadFile(String url, File outputFile) {
        try {
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
        } catch(FileNotFoundException e) {
            CommonMethods.toastMessage(getContext(), "Unable to find required apk");
            return false;
        } catch (IOException e) {
            CommonMethods.toastMessage(getContext(), "Unable to find required apk");
            return false;
        }
    }
}
