package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import black.project.finddoctor.Adapter.DoctorAdapter;
import black.project.finddoctor.Adapter.FavDoctorAdapter;
import black.project.finddoctor.model.Doctor;

public class FavActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Button buttonBack;

    private RecyclerView recyclerViewFavDoc;
    private FavDoctorAdapter adapterFavDoc;
    private RecyclerView.LayoutManager layoutManager;

    private List<Doctor> mData = new ArrayList<>();
    private List<Doctor> backUpData = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navBar);
        bottomNavigationView.setSelectedItemId(R.id.fav);

        buttonBack = (Button)findViewById(R.id.B_F_back);

        prepareData();  //loads data
        populateRV();   //populates recyclerView

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if( item.getItemId()==R.id.home){
                    startActivity(new Intent(FavActivity.this,HomeActivity.class ));
                }
                else if(item.getItemId()==R.id.fav){
                    return true;
                }
                else if(item.getItemId()==R.id.appointed){
                    startActivity(new Intent(FavActivity.this,AppointedActivity.class ));
                }
                else{

                }
                return false;
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void prepareData(){
        db = FirebaseFirestore.getInstance();
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mData.clear();
        backUpData.clear();

        //DB Data
        CollectionReference favRef= db.collection("FavDoctors").document(user_id)
                .collection("FAV");

        favRef.orderBy("docName", Query.Direction.ASCENDING).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot dSS : queryDocumentSnapshots) {
                            Doctor doctor = dSS.toObject(Doctor.class);
                            String id = dSS.getId();
                            mData.add(new Doctor(id,doctor.getPicURL(), doctor.getDocName(), doctor.getSpeciality(), doctor.getFee()));
                            backUpData.add(new Doctor(id,doctor.getPicURL(), doctor.getDocName(), doctor.getSpeciality(), doctor.getFee()));
                        }
                        adapterFavDoc.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FavActivity.this,"You didn't add any doctors!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateRV(){
        recyclerViewFavDoc = findViewById(R.id.RV_fav);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewFavDoc.setLayoutManager(layoutManager);

        adapterFavDoc = new FavDoctorAdapter(this,mData);
        recyclerViewFavDoc.setAdapter(adapterFavDoc);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FavActivity.this,HomeActivity.class));
    }

}