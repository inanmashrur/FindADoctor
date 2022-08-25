package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import black.project.finddoctor.model.Doctor;

public class FullMapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //private double lat, lng;
    private List< Map<String,Object> > locations = new ArrayList<>();
    Map<String,ArrayList<Doctor>> combCham = new HashMap<>();

    private ProgressDialog progressDialog;
    private Button buttonBack;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_map);

        buttonBack = (Button)findViewById(R.id.B_FM_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        db = FirebaseFirestore.getInstance();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFull);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        progressDialog = new ProgressDialog(FullMapActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialog_progressbar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        prepareData();

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("     @@@Inside "," handler@@@@@@");
                setMarkers(mMap);
            }
        }, 1500);*/

        //move the camera
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.840253,90.422060),6.9f));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String chamber = marker.getTitle();
                Log.d("clicked: ",chamber);

                for( int i=0; i<combCham.get(chamber).size(); i++){
                    String D = combCham.get(chamber).get(i).getId()+" "+combCham.get(chamber).get(i).getDocName()+" "+combCham.get(chamber).get(i).getSpeciality();
                    Log.d("doctor",D);
                    ArrayList<Doctor> doctors = combCham.get(chamber);
                    Intent intent = new Intent(FullMapActivity.this, SearchActivity.class);
                    intent.putExtra("type","map");
                    intent.putExtra("doctors", doctors);
                    startActivity(intent);
                }

                return false;
            }
        });

    }

    private void setMarkers(GoogleMap mMap){

        for( int i=0; i<locations.size(); i++){

            String chamber = locations.get(i).get("loc").toString();
            String title = chamber;

            LatLng marker = new LatLng((double)locations.get(i).get("lat"), (double)locations.get(i).get("lng"));
            mMap.addMarker(new MarkerOptions().position(marker).title(title));
            Log.d("runs:",String.valueOf(i+1));
        }
        progressDialog.dismiss();

    }

    private void prepareData(){

        db.collection("Doctors2").orderBy("chamber").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int i=0;
                        //testing
                        String lastCham ="";

                        for (QueryDocumentSnapshot dSS : queryDocumentSnapshots) {

                            ArrayList<Doctor> docL = new ArrayList<>();//creating list
                            Doctor doc = dSS.toObject(Doctor.class);
                            doc.setId(dSS.getId());
                            docL.add(doc);

                            Map<String,Object> loc = new HashMap<>();
                            //loc.put("docId",dSS.getId());
                            loc.put("lat",dSS.get("lat"));
                            loc.put("lng",dSS.get("lng"));
                            loc.put("loc",dSS.get("chamber"));

                            if( i!=0 ){
                                if( dSS.get("chamber").equals(lastCham)){
                                    //add to group
                                    combCham.get(lastCham).add(doc);
                                    i++;
                                    continue;
                                }
                            }
                            Log.d("Added Chamber ",dSS.get("chamber").toString());
                            locations.add(loc);
                            lastCham = dSS.get("chamber").toString();
                            combCham.put(lastCham,docL);
                            i++;
                        }
                        setMarkers(mMap);
                        Log.d("Added Locations: ",String.valueOf(i));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Getting data: ","failure");
                    }
                });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FullMapActivity.this,HomeActivity.class));
        finish();
    }
}