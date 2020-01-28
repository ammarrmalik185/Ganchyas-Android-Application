package app.ganchyas.NonActivityClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.zip.Inflater;

import app.ganchyas.R;

public class PatchesListAdapter extends ArrayAdapter<PatchPack> {

    public PatchesListAdapter(@NonNull Context context, @NonNull List<PatchPack> objects) {
        super(context, R.layout.design_patches, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
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
        versionNo.setText("Version no : " + currentPack.getVersionNo() + " - ");

        return patchView;
    }
}
