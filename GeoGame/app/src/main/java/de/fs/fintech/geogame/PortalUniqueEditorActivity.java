package de.fs.fintech.geogame;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import de.fs.fintech.geogame.data.PortalUnique;
import de.fs.fintech.geogame.db.GeoFirebaseStructure;
import de.fs.fintech.geogame.parcelable.PortalInfoParcel;
import de.fs.fintech.geogame.parcelable.PortalUniqueParcel;
import de.fs.fintech.geogame.service.PlayerIntentService;

public class PortalUniqueEditorActivity extends AppCompatBaseActivity implements View.OnClickListener {
    private static Logger log = LoggerFactory.getLogger(PortalUniqueEditorActivity.class);

    public static final String EXTRA_PORTAL_PARCEL = "portal";
    private TextView mTextDescription;
    private File portalThumbFile;
    private ImageButton mImageButton;
    private PortalInfoParcel portal;
    private String email;
    private GeoFirebaseStructure db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portal_unique_editor);

        db=new GeoFirebaseStructure(this);

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
                Intent intent=new Intent(PortalUniqueEditorActivity.this,PortalEditorActivity.class);
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


        initDirectionButtons(0,false,true);
        load();

        //...onCreate
        IntentFilter filter = new IntentFilter(PlayerIntentService.ACTION_BCST_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);
        log.info("registered broadcast");

        Button connectButton= (Button) findViewById(R.id.button_connect);
        connectButton.setOnClickListener(this);

    }

    private ResponseReceiver receiver;

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_connect:
                Intent intent=new Intent(this, PortalDetailsActivity.class);
                intent.putExtra(EXTRA_PORTAL_PARCEL,portal);
                startActivity(intent);
                return;
        }
    }

    public class ResponseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // wenn erfolgreich gespeichert können wir beenden.
            boolean ok=intent.getBooleanExtra(PlayerIntentService.EXTRA_BCST_OK,false);
            log.info("received broadcast: saved ok="+ok);
            finish();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        log.info("unregister broadcast");
        unregisterReceiver(receiver);
    }

    private void initDirectionButtons(int bitfield, boolean visited, boolean captured) {
        final CheckBox visitedCb = (CheckBox) findViewById(R.id.checkBoxVisited);
        visitedCb.setChecked(visited);
        final CheckBox capturedCb = (CheckBox) findViewById(R.id.checkBoxCaptured);
        capturedCb.setChecked(captured);

        final int[] ids={
                R.id.imageButtonN,
                R.id.imageButtonNE,
                R.id.imageButtonE,
                R.id.imageButtonSE,
                R.id.imageButtonS,
                R.id.imageButtonSW,
                R.id.imageButtonW,
                R.id.imageButtonNW
        };
        final ToggleButton[] toggleButtons=new ToggleButton[ids.length];
        final boolean[] state=new boolean[ids.length];
        CompoundButton.OnCheckedChangeListener dirDispatcher=new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton v, boolean isChecked) {
                int direction= (int) v.getTag();
                state[direction]=isChecked;
            }
        };
        for(int j=0;j<ids.length;j++) {
            ToggleButton toggle = (ToggleButton) findViewById(ids[j]);
            toggle.setOnCheckedChangeListener(dirDispatcher);
            toggle.setTag(j);
            int mask=1<<j;
            boolean checked=(bitfield & mask) != 0;
            toggle.setChecked(checked);
            toggleButtons[j]=toggle;
            state[j]=checked;
        }


        ToggleButton resoBtn= (ToggleButton) findViewById(R.id.imageButtonC);
        resoBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for(int j=0;j<ids.length;j++) {
                    toggleButtons[j].setChecked(isChecked);
                    state[j]=isChecked;
                }

            }
        });

        getBitfield(ids, state);

        Button sendBtn= (Button) findViewById(R.id.button_ok);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int bitfieldOut = getBitfield(ids, state);
                boolean visited=visitedCb.isChecked();
                boolean captured=capturedCb.isChecked();
                save(visited,captured,bitfieldOut);
            }
        });
    }

    private void load() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String uniquesPortal= db.getUniquePortalId(portal.id);
        final DatabaseReference myRef = database.getReference(uniquesPortal);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PortalUnique fromDb = dataSnapshot.getValue(PortalUnique.class);
                if(fromDb!=null) {
                    log.info("unique loaded " + uniquesPortal );
                    initDirectionButtons(fromDb.resoBits,fromDb.visited,fromDb.captured);
                } else {
                    log.info("new unique  " + uniquesPortal);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log.info("error: load unique  " + uniquesPortal );
            }
        });
    }

    private void save(final boolean visited, final boolean captured, final int bitfieldOut) {
        PortalUniqueParcel uq = new PortalUniqueParcel();
        uq.visited = visited;
        uq.captured = captured;
        uq.resoBits = bitfieldOut;

        if(true) {
            Intent i=new Intent(this,PlayerIntentService.class);
            i.setAction(PlayerIntentService.ACTION_UNIQUE);
            i.putExtra(PlayerIntentService.EXTRA_PORTAL_ID,portal.id);
            i.putExtra(PlayerIntentService.EXTRA_UNIQUE,uq);
            startService(i);
        } else {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            final String uniquesPortal= db.getUniquePortalId(portal.id);
            final DatabaseReference myRef = database.getReference(uniquesPortal);
            myRef.setValue(uq);
        }
    }


    private int getBitfield(int[] ids, boolean[] state) {
        String s="";
        int bitfieldOut=0;
        for(int j=0;j<ids.length;j++) {
            s+=j+"="+state[j]+",";
            if(state[j]) {
                bitfieldOut |= 1 << j;
            }
        }
        log.debug("bitfieldOut="+bitfieldOut+":"+s);
        return bitfieldOut;
    }

    private void setImageButton() {
        String absolutePath = portalThumbFile.getAbsolutePath();
        log.info("setting ImageButton from cache "+ absolutePath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bm=BitmapFactory.decodeFile(absolutePath, bmOptions);
        mImageButton.setImageBitmap(bm);
    }
}
