package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
    private FirebaseUser user;

    private Dialog menuDialog;
    private Dialog confirmDialog;
    private Dialog aboutDialog;
    private Dialog helpDialog;

    private SwipeRefreshLayout swipeRefreshLayout;

    private String confirmationType="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navBar);
        bottomNavigationView.setSelectedItemId(R.id.fav);

        user = FirebaseAuth.getInstance().getCurrentUser();
        menuDialog = new Dialog(FavActivity.this);
        confirmDialog = new Dialog(FavActivity.this);

        buttonBack = (Button)findViewById(R.id.B_F_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Toast.makeText(FavActivity.this,"Tips: Slide down to refresh data.",Toast.LENGTH_LONG).show();

        prepareData();  //loads data
        populateRV();   //populates recyclerView

        //refreshes Data
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mData.clear();
                prepareData();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

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
                else if(item.getItemId()==R.id.menu){
                    showMenu();
                }
                return false;
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

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuDialog.dismiss();
                startActivity(new Intent(FavActivity.this, ProfileActivity.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutDialog = new Dialog(FavActivity.this);
                final Button buttonWAback;
                aboutDialog.setContentView(R.layout.window_about);

                buttonWAback = (Button) aboutDialog.findViewById(R.id.B_WA_back);

                buttonWAback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        aboutDialog.dismiss();
                    }
                });
                aboutDialog.show();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                helpDialog = new Dialog(FavActivity.this);
                final Button buttonWHback;
                helpDialog.setContentView(R.layout.window_help);

                buttonWHback = (Button) helpDialog.findViewById(R.id.B_WH_back);

                buttonWHback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        helpDialog.dismiss();
                    }
                });
                helpDialog.show();
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( user!=null ){
                    confirmationType="signout";
                    getConfirmation("Are you sure you want to signout?");
                }else{
                    startActivity(new Intent(FavActivity.this, LoginActivity.class));
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
                    FirebaseAuth.getInstance().signOut();
                    confirmDialog.dismiss();
                    menuDialog.dismiss();
                    startActivity(new Intent(FavActivity.this, LoginActivity.class));
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(FavActivity.this,HomeActivity.class));
    }

}