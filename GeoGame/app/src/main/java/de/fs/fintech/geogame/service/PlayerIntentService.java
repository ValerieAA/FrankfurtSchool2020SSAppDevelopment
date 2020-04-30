package de.fs.fintech.geogame.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import de.fs.fintech.geogame.data.InventoryItem;
import de.fs.fintech.geogame.data.InventoryType;
import de.fs.fintech.geogame.data.PortalInstallation;
import de.fs.fintech.geogame.db.GeoFirebaseStructure;
import de.fs.fintech.geogame.parcelable.HackResponseParcel;
import de.fs.fintech.geogame.parcelable.PortalInfoParcel;
import de.fs.fintech.geogame.parcelable.PortalUniqueParcel;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class PlayerIntentService extends IntentService {
    private static Logger log = LoggerFactory.getLogger(PlayerIntentService.class);
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_UNIQUE = "de.fs.fintech.geogame.service.action.UNIQUE";
    public static final String ACTION_HACK = "de.fs.fintech.geogame.service.action.HACK";
    public static final String EXTRA_PORTAL_ID = "de.fs.fintech.geogame.service.extra.PORTAL_ID";

    public static final String EXTRA_UNIQUE = "de.fs.fintech.geogame.service.extra.PORTAL_UNIQUE";
    public static final String EXTRA_HACKED_ITEMS = "de.fs.fintech.geogame.service.extra.HACKED_ITEMS";

    public static final String ACTION_BCST_HACK =
            "de.fs.fintech.geogame.intent.action.MESSAGE_PROCESSED_HACK";
    public static final String ACTION_BCST_RESPONSE =
            "de.fs.fintech.geogame.intent.action.MESSAGE_PROCESSED_UNIQUE";
    public static final String EXTRA_BCST_OK = "ok";
    private GeoFirebaseStructure db;

    public PlayerIntentService() {
        super("PlayerIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        db=new GeoFirebaseStructure(this);
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionFoo(Context context, String param1, PortalUniqueParcel param2) {
        Intent intent = new Intent(context, PlayerIntentService.class);
        intent.setAction(ACTION_UNIQUE);
        intent.putExtra(EXTRA_PORTAL_ID, param1);
        intent.putExtra(EXTRA_UNIQUE, param2);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UNIQUE.equals(action)) {
                final String portalId = intent.getStringExtra(EXTRA_PORTAL_ID);
                final PortalUniqueParcel unique = intent.getParcelableExtra(EXTRA_UNIQUE);
                handleActionUnique(portalId, unique);
            } else if (ACTION_HACK.equals(action)) {
                String mist = intent.getStringExtra("mist");
                String portalId = intent.getStringExtra(EXTRA_PORTAL_ID);
                final PortalInfoParcel portal = intent.getParcelableExtra(EXTRA_UNIQUE);
                handleActionHack(portalId);
            }
        }
    }

    private void handleActionHack(String portalId) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final String uniquesPortal= db.getPortalInfo(portalId);
        final DatabaseReference myRef = database.getReference(uniquesPortal);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PortalInfoParcel fromDb = dataSnapshot.getValue(PortalInfoParcel.class);
                if(fromDb!=null) {
                    log.info("portal loaded " + uniquesPortal );
                    //
                } else {
                    log.info("not found id " + uniquesPortal);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log.info("error: load unique  " + uniquesPortal );
            }
        });
    }

    private void handleActionHack(PortalInfoParcel portal) {
        int portalLevel = calcPortalLevel(portal.installations); // getPo
        HackResponseParcel response = new HackResponseParcel();
        ArrayList<InventoryItem> items = new ArrayList<InventoryItem>();
        double success = Math.random() * 100.;
        if (success > 30.) {
            InventoryItem item = new InventoryItem();
            item.count = (int) (success * 3.1);
            item.type = InventoryType.Type.KEY.name();
            items.add(item);
        }
        int types = (int) (Math.random() * 3.);
        for (int i = 0; i < types; i++) {
            double success2 = Math.random() * 10.;
            InventoryItem item = new InventoryItem();
            item.count = (int) (success2 * success * 3.1);
            switch (i) {
                default:
                case 0:
                   // item.type = InventoryType.Type.RESO_L1.ordinal() + portalLevel;
                    break;
            }
            item.type = InventoryType.Type.KEY.name();
            items.add(item);
        }
        response.hackedItems = items.toArray(new InventoryItem[0]);
        response.hackedItems[0].count = 1;
        response.hackedItems[0].type = InventoryType.Type.KEY.name();

        response.hackedItems[0].count = 1;
        response.hackedItems[0].type = InventoryType.Type.KEY.name();
    }

    private int calcPortalLevel(PortalInstallation installations) {
        int sum = 0;
        for (int r = 0; r < installations.resos.length; r++) {
            sum += installations.resos[r];
        }
        int plainLevel = sum / installations.resos.length;
        if (plainLevel == 0 && sum > 0) return 1;
        return plainLevel;
    }



    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUnique(String portalId, PortalUniqueParcel uq) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uniquesPortal = db.getUniquePortalId(portalId);
        final DatabaseReference myRef = database.getReference(uniquesPortal);
        myRef.setValue(uq);

        broadcastUpdate(portalId, true);
    }

    private void broadcastUpdate(String portalId, boolean ok) {
        Intent bcst = new Intent();
        bcst.setAction(ACTION_BCST_RESPONSE);
        bcst.putExtra(EXTRA_PORTAL_ID, portalId);
        bcst.putExtra(EXTRA_BCST_OK, ok);
        log.info("send broadcast ok=" + ok);
        sendBroadcast(bcst);
    }


}
