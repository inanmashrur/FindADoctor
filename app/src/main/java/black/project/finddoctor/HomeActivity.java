package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import black.project.finddoctor.Adapter.TopDoctorAdapter;
import black.project.finddoctor.model.Doctor;

public class HomeActivity extends AppCompatActivity {

    private Button searchB;
    private BottomNavigationView bottomNavigationView;

    private CardView cardLoc, cardSpec;

    private RecyclerView doctorRecView;
    private List<Doctor> mData = new ArrayList<>();
    private TopDoctorAdapter topDoctorAdapter;
    private RecyclerView.LayoutManager layoutManager;
    
    private Dialog menuDialog;
    private Dialog confirmDialog;

    private String confirmationType="";

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        searchB = (Button) findViewById(R.id.B_search);
        searchB = (Button) findViewById(R.id.B_search);

        cardLoc = (CardView)findViewById(R.id.CV_locations);
        cardSpec = (CardView)findViewById(R.id.CV_specialist);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        
        menuDialog = new Dialog(HomeActivity.this);
        confirmDialog = new Dialog(HomeActivity.this);

        prepareData();  //loads data
        populateRV();   //populates recyclerView

       searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(HomeActivity.this, SearchActivity.class);
                i.putExtra("type", "all");
                startActivity(i);
            }
        });

       cardLoc.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(HomeActivity.this,LocationSearchActivity.class));
           }
       });

       cardSpec.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(HomeActivity.this,SpecialitySearchActivity.class));
           }
       });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if( item.getItemId()==R.id.home){
                    return true;
                }
                else if(item.getItemId()==R.id.menu){
                    showMenu();
                }
                else if(user!=null){
                    if( item.getItemId()==R.id.home){
                        return true;
                    }
                    if(item.getItemId()==R.id.fav){
                        startActivity(new Intent(HomeActivity.this,FavActivity.class ));
                    }
                    else if(item.getItemId()==R.id.appointed){
                        startActivity(new Intent(HomeActivity.this,AppointedActivity.class ));
                    }
                }
                else Toast.makeText(HomeActivity.this,"You must login first!",Toast.LENGTH_SHORT).show();

                return false;
            }
        });
    }
    
    private void showMenu(){
        final TextView close;
        final Button sign, profile, help, about, exit;

        menuDialog.setContentView(R.layout.window_menu);

        close = (TextView) menuDialog.findViewById(R.id.TV_M_close);
        sign = (Button) menuDialog.findViewById(R.id.B_M_Sign);
        profile = (Button) menuDialog.findViewById(R.id.B_M_Profile);
        help = (Button) menuDialog.findViewById(R.id.B_M_Help);
        about = (Button) menuDialog.findViewById(R.id.B_M_About);
        exit = (Button) menuDialog.findViewById(R.id.B_M_exit);

        if( user!= null)sign.setText("Signout");

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDialog.dismiss();
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( user!=null ){
                    confirmationType="signout";
                    getConfirmation("Are you sure you want to signout?");
                }else{
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmationType="exit";
                getConfirmation("Are you sure you want to exit?");
            }
        });
        menuDialog.show();
    }
    
    @Override
    public void onBackPressed() {
        Intent mainActivity = new Intent(Intent.ACTION_MAIN);
        mainActivity.addCategory(Intent.CATEGORY_HOME);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivity);
    }

    private void prepareData(){
        mData.clear();

        //DB Data
        CollectionReference docRef= db.collection("Doctors2");

        docRef.orderBy("visited", Query.Direction.ASCENDING).limit(5).get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot dSS : queryDocumentSnapshots) {
                            Doctor doctor = dSS.toObject(Doctor.class);
                            String id = dSS.getId();

                            mData.add(new Doctor(id,doctor.getPicURL(), doctor.getDocName(), doctor.getSpeciality(), doctor.getFee()));
                        }
                        topDoctorAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void populateRV(){
        doctorRecView = findViewById(R.id.RV_top_doctors);
        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        doctorRecView.setLayoutManager(layoutManager);

        topDoctorAdapter = new TopDoctorAdapter(this,mData);
        doctorRecView.setAdapter(topDoctorAdapter);
    }

    private void getConfirmation(String msg){

        final Button yes, no;
        final TextView textViewMsg;

        confirmDialog.setContentView(R.layout.dialog_confirmation);

        textViewMsg = (TextView) confirmDialog.findViewById(R.id.TV_C_msg);
        textViewMsg.setText(msg);

        yes = (Button) confirmDialog.findViewById(R.id.B_C_yes);
        no = (Button) confirmDialog.findViewById(R.id.B_C_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(confirmationType.equals("signout")){
                    mAuth.signOut();
                    confirmDialog.dismiss();
                    menuDialog.dismiss();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }else if(confirmationType.equals("exit")){
                    confirmDialog.dismiss();
                    Intent mainActivity = new Intent(Intent.ACTION_MAIN);
                    mainActivity.addCategory(Intent.CATEGORY_HOME);
                    mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mainActivity);
                    System.exit(0);
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.dismiss();
            }
        });
        confirmDialog.show();
    }
}
