package de.fs.fintech.geogame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import de.fs.fintech.geogame.parcelable.HackResponseParcel;
import de.fs.fintech.geogame.parcelable.PortalInfoParcel;
import de.fs.fintech.geogame.service.PlayerIntentService;

public class PortalDetailsActivity extends AppCompatBaseActivity implements View.OnClickListener, View.OnLongClickListener {
    private static final int REQUEST_HACK = 123;
    private static final String HACK_RESULT = "hack_result";
    private static Logger log = LoggerFactory.getLogger(PortalDetailsActivity.class);

    public static final String EXTRA_PORTAL_PARCEL = "portal";
    private TextView mTextDescription;
    private File portalThumbFile;
    private ImageButton mImageButton;
    private PortalInfoParcel portal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal_details);

        Intent callingIntent=getIntent();
        portal=callingIntent.getParcelableExtra(EXTRA_PORTAL_PARCEL);
        setTitle(portal.title);
        mTextDescription= (TextView) findViewById(R.id.textDescription);
        mTextDescription.setText(portal.description);

        mImageButton=(ImageButton) findViewById(R.id.imageButton);
        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(true) return; // Funktion deaktiviert b.a.w.
                Intent intent=new Intent(PortalDetailsActivity.this,PortalEditorActivity.class);
                intent.putExtra(PortalDetailsActivity.EXTRA_PORTAL_PARCEL,portal);
                startActivity(intent);
                // TODO PortalEditorActivity aus Parcel initialisieren und "UPDATE" senden
            }
        });

        if(portal.urlPhoto!=null) {
        /* ohne WRITE_EXTERNAL_STORAGE permissions
             * für große Files, die jeder sehen darf, ggf. auch auf seinen PC kopieren kann
             * == file dumped to /storage/emulated/0/Android/data/de.fs.fintech.geogame/files/ext_data/dump.json
             */
            portalThumbFile = new File(((Context) this).getExternalFilesDir("portal_thumbs"), portal.id + ".jpg");
            if (!portalThumbFile.exists()) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();
                // Create a reference with an initial file path and name
                StorageReference pathReference = storageRef.child(portal.urlPhoto);
                pathReference.getFile(portalThumbFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        log.info("file downloaded to cache");
                       setImageButton();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                        log.error("unable to download to cache. Caught:",exception);
                    }
                });
            } else { // file exists already
                setImageButton();
            }
        }

        int[] ids={
                R.id.button_hack,
                R.id.button_attack

        };
        for(int j=0;j<ids.length;j++) {
            Button btn = (Button) findViewById(ids[j]);
            btn.setOnClickListener(this);
            btn.setOnLongClickListener(this);
        }
    }

    private void setImageButton() {
        String absolutePath = portalThumbFile.getAbsolutePath();
        log.info("setting ImageButton from cache "+ absolutePath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bm=BitmapFactory.decodeFile(absolutePath, bmOptions);
        mImageButton.setImageBitmap(bm);
    }

    @Override
    public void onClick(View v) {
    switch (v.getId()) {
        case R.id.button_hack:
        {
            Intent i=new Intent(this,PlayerIntentService.class);
            i.setAction(PlayerIntentService.ACTION_HACK);
            i.putExtra("mist","123");
            i.putExtra(PlayerIntentService.EXTRA_PORTAL_ID,portal.id);
            i.putExtra(PlayerIntentService.EXTRA_UNIQUE,portal);
            startService(i);
            return;
        }
    }
    }


    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()) {
            case R.id.button_hack:
            {
                // TODO hier kommen die Projektarbeiten rein.
                Class<? extends Activity>[] hackActivities=new Class[]{
                        MainActivity.class // dummy
                };
                int select= (int) (Math.random()*hackActivities.length);
                Intent intent=new Intent(this,hackActivities[select]);
                intent.putExtra(EXTRA_PORTAL_PARCEL,portal);
                startActivityForResult(intent,REQUEST_HACK);
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_HACK && resultCode == RESULT_OK) {
            HackResponseParcel reponse=data.getParcelableExtra(HACK_RESULT);
            // TODO in inventory packen bzw. anzeigen, was gehackt wurde (Toast)
        }
    }
}
