package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import black.project.finddoctor.Adapter.DoctorAdapter;
import black.project.finddoctor.model.Doctor;

public class SearchActivity extends AppCompatActivity {

    private EditText searchData;
    private Button buttonBack;
    private RecyclerView doctorRecView;
    private ArrayList<Doctor> mData = new ArrayList<>();
    private ArrayList<Doctor> backUpData = new ArrayList<>();
    private DoctorAdapter doctorAdapter;

    private RecyclerView.LayoutManager layoutManager;

    private String type;

    //private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        type =  getIntent().getStringExtra("type");

        if( type.equals("all")){
            prepareData(type,""); //loads all data
        }else if(type.equals("loc")){
            String what =  getIntent().getStringExtra("loc");
            prepareData(type,what); //loads data according to specific location
        }else if(type.equals("spec")){
            String what =  getIntent().getStringExtra("spec");
            prepareData(type,what); //loads data according to specific speciality
        }else if (type.equals("map")){
            mData.clear();
            mData = (ArrayList<Doctor>) getIntent().getSerializableExtra("doctors");
            Log.d("size",String.valueOf(mData.size()));
            backupData(mData);
        }

        searchData = (EditText) findViewById(R.id.ET_search);
        buttonBack = (Button) findViewById(R.id.B_S_back);

        populateRV();   //populates recyclerView

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        searchData.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String filterData = searchData.getText().toString();
                filterDoctor(filterData);
            }
        });

    }

    private void backupData(ArrayList<Doctor> data){
        backUpData.clear();
        for( int i =0; i<data.size(); i++)backUpData.add(data.get(i));
    }

    private void prepareData(String type, String what){
        mData.clear();
        backUpData.clear();

        CollectionReference docRef= db.collection("Doctors2");

        if( type.equals("all")){
            docRef.orderBy("docName", Query.Direction.ASCENDING).get().
                    addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot dSS : queryDocumentSnapshots) {
                                Doctor doctor = dSS.toObject(Doctor.class);
                                String id = dSS.getId();
                                mData.add(new Doctor(id,doctor.getPicURL(), doctor.getDocName(), doctor.getSpeciality(), doctor.getFee()));
                                backUpData.add(new Doctor(id,doctor.getPicURL(), doctor.getDocName(), doctor.getSpeciality(), doctor.getFee()));
                            }
                            doctorAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SearchActivity.this,"All search Error 999!",Toast.LENGTH_SHORT).show();
                }
            });
        }else if(type.equals("loc")){

            docRef.orderBy("docName", Query.Direction.ASCENDING).get().
                    addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot dSS : queryDocumentSnapshots) {
                                Doctor doctor = dSS.toObject(Doctor.class);
                                String id = dSS.getId();
                                if (doctor.getLocation().equals(what)) {
                                    mData.add(new Doctor(id, doctor.getPicURL(), doctor.getDocName(), doctor.getSpeciality(), doctor.getFee()));
                                    backUpData.add(new Doctor(id, doctor.getPicURL(), doctor.getDocName(), doctor.getSpeciality(), doctor.getFee()));
                                }
                            }
                            doctorAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SearchActivity.this,"Location search Error 999!",Toast.LENGTH_SHORT).show();
                }
            });

        }else if(type.equals("spec")){

            docRef.orderBy("docName", Query.Direction.ASCENDING).get().
                    addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot dSS : queryDocumentSnapshots) {
                                Doctor doctor = dSS.toObject(Doctor.class);
                                String id = dSS.getId();
                                if( doctor.getSpeciality().equals(what)) {
                                    mData.add(new Doctor(id, doctor.getPicURL(), doctor.getDocName(), doctor.getSpeciality(), doctor.getFee()));
                                    backUpData.add(new Doctor(id, doctor.getPicURL(), doctor.getDocName(), doctor.getSpeciality(), doctor.getFee()));
                                }
                            }
                            doctorAdapter.notifyDataSetChanged();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SearchActivity.this,"Speciality search Error 999!",Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            Toast.makeText(SearchActivity.this,"Search Error 999!!",Toast.LENGTH_SHORT).show();
        }
    }

    private void populateRV(){
        doctorRecView = findViewById(R.id.RV_search);
        layoutManager = new LinearLayoutManager(this);
        doctorRecView.setLayoutManager(layoutManager);

        doctorAdapter = new DoctorAdapter(this,mData);
        doctorRecView.setAdapter(doctorAdapter);
    }

    private void filterDoctor(String filterText){
        mData.clear();
        filterText = filterText.toLowerCase();

        for( int i=0; i<backUpData.size(); i++ ){

            if(backUpData.get(i).getDocName().toLowerCase().contains(filterText)
                    | backUpData.get(i).getSpeciality().toLowerCase().contains(filterText)){
                mData.add(backUpData.get(i));
            }
        }
        doctorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if( type.equals("map") )startActivity(new Intent(SearchActivity.this, FullMapActivity.class));
        else super.onBackPressed();
        finish();
    }
}
/*

*/