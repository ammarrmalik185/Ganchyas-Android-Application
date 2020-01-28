package app.ganchyas.NonActivityClasses;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class PatchPack {

    private String title;
    private String versionNo;
    private String date;
    private String time;
    private String downloadLink;
    private ArrayList<String> pathNotes;

    public PatchPack(DataSnapshot patchSnap) {

        pathNotes = new ArrayList<>();

        title = patchSnap.child("title").getValue().toString();
        versionNo = patchSnap.child("versionNo").getValue().toString();
        date = patchSnap.child("date").getValue().toString();
        time = patchSnap.child("time").getValue().toString();
        downloadLink = patchSnap.child("apkLink").getValue().toString();

        DataSnapshot patchNotesSnap = patchSnap.child("patchNotes");
        for (DataSnapshot singlePatchNote : patchNotesSnap.getChildren()){
            pathNotes.add(singlePatchNote.getValue().toString());
        }

    }

    public String getTitle() {
        return title;
    }

    public String getVersionNo() {
        return versionNo;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public String getPathNotes() {
        String notes = "";
        int count = 1;
        for (String s: pathNotes){
            notes = notes.concat(count + "- " + s + "\n");
            count++;
        }
        return notes;
    }
}
