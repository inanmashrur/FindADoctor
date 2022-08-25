package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import black.project.finddoctor.Adapter.TopDoctorAdapter;
import black.project.finddoctor.model.Doctor;

public class HomeActivity extends AppCompatActivity{

    private Button searchB;
    private BottomNavigationView bottomNavigationView;

    private CardView cardLoc, cardSpec, cardMap;

    private RecyclerView doctorRecView;
    private ArrayList<Doctor> mData = new ArrayList<>();
    private TopDoctorAdapter topDoctorAdapter;
    private RecyclerView.LayoutManager layoutManager;
    
    private Dialog menuDialog;
    private Dialog confirmDialog;
    private Dialog aboutDialog;
    private Dialog helpDialog;
    private ImageSlider imageSlider;

    private String confirmationType="";
    private boolean online=false;

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        searchB = (Button) findViewById(R.id.B_search);
        searchB = (Button) findViewById(R.id.B_search);
        searchB.setLongClickable(true);

        cardLoc = (CardView)findViewById(R.id.CV_locations);
        cardSpec = (CardView)findViewById(R.id.CV_specialist);
        cardMap = (CardView)findViewById(R.id.CV_map);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        
        menuDialog = new Dialog(HomeActivity.this);
        confirmDialog = new Dialog(HomeActivity.this);

        prepareData();  //loads data
        populateRV();   //populates recyclerView

        if(!isOnline())Toast.makeText(HomeActivity.this, "You're Offline!",Toast.LENGTH_SHORT).show();

        cardMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOnline();
                if( online ) {
                    startActivity(new Intent(HomeActivity.this, FullMapActivity.class));
                    finish();
                }else Toast.makeText(HomeActivity.this, "You're Offline!",Toast.LENGTH_SHORT).show();
            }
        });

       searchB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOnline();
                if(online) {
                    Intent i = new Intent(HomeActivity.this, SearchActivity.class);
                    i.putExtra("type", "all");
                    startActivity(i);
                }else Toast.makeText(HomeActivity.this, "You're Offline!",Toast.LENGTH_SHORT).show();
            }
        });

       searchB.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View view) {

               if(user!=null && (user.getEmail().equals("inanmashrur@gmail.com") | user.getEmail().equals("arsami01910@gmail.com"))) {
                   startActivity(new Intent(HomeActivity.this, AdminActivity.class));
                   finish();
               }
               return false;
           }
       });

       cardLoc.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               isOnline();
               if(online) {
                   startActivity(new Intent(HomeActivity.this, LocationSearchActivity.class));
               }else Toast.makeText(HomeActivity.this, "You're Offline!",Toast.LENGTH_SHORT).show();
           }
       });

       cardSpec.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               isOnline();
               if(online) {
                   startActivity(new Intent(HomeActivity.this, SpecialitySearchActivity.class));
               }else Toast.makeText(HomeActivity.this, "You're Offline!",Toast.LENGTH_SHORT).show();
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

        //image slider
        imageSlider= findViewById(R.id.slider);

        populateSlider();

    }

    private void populateSlider(){
        List<SlideModel> slideModels= new ArrayList<>();

        db.collection("SliderData").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(DocumentSnapshot dSS: queryDocumentSnapshots){
                            String imageURL = dSS.get("imageURL").toString();
                            slideModels.add(new SlideModel(imageURL));
                        }
                        imageSlider.setImageList(slideModels,true);
                        Log.d("slider load","Success");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("slider load","failure");
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

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(user!=null) {
                    menuDialog.dismiss();
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                }else Toast.makeText(HomeActivity.this,"You must login first!",Toast.LENGTH_SHORT).show();
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutDialog = new Dialog(HomeActivity.this);
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
                helpDialog = new Dialog(HomeActivity.this);
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

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            online=true;
            return true;
        } else {
            online=false;
            return false;
        }
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

        docRef.orderBy("visited", Query.Direction.DESCENDING).limit(5).get().
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
