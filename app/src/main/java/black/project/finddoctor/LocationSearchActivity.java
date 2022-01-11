package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import black.project.finddoctor.Adapter.FavDoctorAdapter;
import black.project.finddoctor.Adapter.LocationAdapter;
import black.project.finddoctor.model.Doctor;

public class LocationSearchActivity extends AppCompatActivity {

    private Button buttonBack;

    private RecyclerView recyclerViewLocation;
    private LocationAdapter adapterLocation;
    private RecyclerView.LayoutManager layoutManager;

    private List<String> mData = new ArrayList<>();

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);

        prepareData();  //loads data
        populateRV();   //populates recyclerView

        buttonBack = (Button)findViewById(R.id.B_LS_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }
    private void prepareData(){
        db = FirebaseFirestore.getInstance();
        mData.clear();

        //DB Data
        CollectionReference locRef= db.collection("Locations");

        locRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getId();
                        mData.add(id);
                        //Log.d(TAG, document.getId() + " => " + document.getData());
                    }

                    //Toast.makeText(LocationSearchActivity.this,i,Toast.LENGTH_SHORT).show();
                    adapterLocation.notifyDataSetChanged();
                }else{
                    Toast.makeText(LocationSearchActivity.this,"Error Getting Documents",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void populateRV(){
        recyclerViewLocation = findViewById(R.id.RV_loc_search);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewLocation.setLayoutManager(layoutManager);

        adapterLocation = new LocationAdapter(this,mData);
        recyclerViewLocation.setAdapter(adapterLocation);
    }
}