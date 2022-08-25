package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import black.project.finddoctor.model.Doctor;

public class AdminActivity extends AppCompatActivity {

    private EditText editTextName, editTextpicURL, editTextSpeciality, editTextFee, editTextChamber,
            editTextLocation, editTextContact, editTextSchedule, editTextAbout, editTextId;
    private Button uploadButton;

    private String Id;

    int c = 0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        editTextAbout = (EditText) findViewById(R.id.ET_AD_about);
        editTextName = (EditText) findViewById(R.id.ET_AD_docName);
        editTextpicURL = (EditText) findViewById(R.id.ET_AD_picURL);
        editTextSchedule = (EditText) findViewById(R.id.ET_AD_schedule);
        editTextSpeciality = (EditText) findViewById(R.id.ET_AD_speciality);
        editTextFee = (EditText) findViewById(R.id.ET_AD_fee);
        editTextChamber = (EditText) findViewById(R.id.ET_AD_chamber);
        editTextContact = (EditText) findViewById(R.id.ET_AD_conNo);
        editTextLocation = (EditText) findViewById(R.id.ET_AD_loc);
        editTextId = (EditText) findViewById(R.id.ET_AD_id);

        uploadButton = (Button)findViewById(R.id.B_AD_upload);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Name, picURL, Speciality, Fee, Chamber,
                        Location, Contact, Schedule, About;

                int visited =100;
                Name = editTextName.getText().toString();
                picURL = editTextpicURL.getText().toString();
                Schedule = editTextSchedule.getText().toString();
                Speciality = editTextSpeciality.getText().toString();
                Fee = editTextFee.getText().toString();
                Chamber = editTextChamber.getText().toString();
                Location = editTextLocation.getText().toString();
                Contact = editTextContact.getText().toString();
                About = editTextAbout.getText().toString();
                Id = editTextId.getText().toString();

                if(Id.isEmpty() | Name.isEmpty()| picURL.isEmpty()| Speciality.isEmpty()| Fee.isEmpty()
                        | Chamber.isEmpty()| Location.isEmpty()| Contact.isEmpty()| Schedule.isEmpty() | About.isEmpty() ){
                    Toast.makeText(AdminActivity.this,"Please fillup every field!!",Toast.LENGTH_SHORT).show();
                }
                else{
                    List<String> sch = new ArrayList<>();
                    for( int i =0; i<Schedule.length(); i++)sch.add(String.valueOf(Schedule.charAt(i)));
                    Doctor doc = new Doctor(Name,Speciality,Fee,picURL,About,Chamber,Location,Contact,sch,visited);
                    updateData(doc);
                }

            }
        });
    }
    private void updateData(Doctor doc){

        db.collection("Doctors2").document(Id).set(doc)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        c++;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminActivity.this,"404 Error!! while adding to Doctors!",Toast.LENGTH_SHORT).show();
            }
        });

        Map<String,String> data= new HashMap<>();
        data.put("loc",doc.getLocation());

        db.collection("Locations").document(doc.getLocation()).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        c++;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminActivity.this,"404 Error!! while adding to Locations!",Toast.LENGTH_SHORT).show();
            }
        });

        data.clear();
        data.put("spec",doc.getSpeciality());

        db.collection("Speciality").document(doc.getSpeciality()).set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        c++;
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AdminActivity.this,"404 Error!! while adding to Speciality!",Toast.LENGTH_SHORT).show();
            }
        });

        if(c%3==0) {
            c=0;
            Intent i =new Intent(AdminActivity.this,SearchActivity.class);
            i.putExtra("type","all");
            startActivity(i);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AdminActivity.this, HomeActivity.class));
    }
}