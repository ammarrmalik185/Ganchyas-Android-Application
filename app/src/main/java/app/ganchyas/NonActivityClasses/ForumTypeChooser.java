package app.ganchyas.NonActivityClasses;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import app.ganchyas.R;
import app.ganchyas.WriteNewForumImageActivity;
import app.ganchyas.WriteNewForumTextActivity;
import app.ganchyas.WriteNewForumVideoActivity;

/**
 * @author Paradox;
 */

public class ForumTypeChooser extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_forum_type, container, false);

        FloatingActionButton textButton = view.findViewById(R.id.fabText);
        FloatingActionButton imageButton = view.findViewById(R.id.fabImage);
        FloatingActionButton videoButton = view.findViewById(R.id.fabVideo);
        FloatingActionButton fileButton = view.findViewById(R.id.fabFile);

        textButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WriteNewForumTextActivity.class);
                startActivity(intent);
                dismiss();
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WriteNewForumImageActivity.class);
                startActivity(intent);
                dismiss();
            }
        });
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WriteNewForumVideoActivity.class);
                startActivity(intent);
                dismiss();
            }
        });
        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "feature not available yet", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
