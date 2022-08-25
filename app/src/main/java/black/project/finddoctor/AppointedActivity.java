package black.project.finddoctor;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import black.project.finddoctor.Adapter.AppointDoctorAdapter;
import black.project.finddoctor.Adapter.FavDoctorAdapter;
import black.project.finddoctor.model.Doctor;

public class AppointedActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Button buttonBack;

    private RecyclerView recyclerViewAppDoc;
    private AppointDoctorAdapter adapterAppDoc;
    private RecyclerView.LayoutManager layoutManager;

    private List< Map<String,Object> > mData = new ArrayList<>();
    private List <Map<String,Object> > backUpData = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseUser user;

    private Dialog menuDialog;
    private Dialog confirmDialog;
    private Dialog aboutDialog;
    private Dialog helpDialog;

    private String confirmationType="";

    //private boolean undo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointed);

        db = FirebaseFirestore.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        menuDialog = new Dialog(AppointedActivity.this);
        confirmDialog = new Dialog(AppointedActivity.this);

        buttonBack = (Button)findViewById(R.id.B_AD_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navBar);
        bottomNavigationView.setSelectedItemId(R.id.appointed);

        Toast.makeText(AppointedActivity.this,"Tips: Swipe left appointment to delete it! ",Toast.LENGTH_LONG).show();

        prepareData();  //loads data
        populateRV();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if( item.getItemId()==R.id.home){
                    startActivity(new Intent(AppointedActivity.this,HomeActivity.class ));
                }
                else if(item.getItemId()==R.id.fav){
                    startActivity(new Intent(AppointedActivity.this,FavActivity.class ));
                }
                else if(item.getItemId()==R.id.appointed){
                    return true;
                }
                else if(item.getItemId()==R.id.menu){
                    showMenu();
                }
                return false;
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewAppDoc);

    }

    Map<String,Object> deletedAppointment;

    // Deletes a note from recycler view when user swipes left
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            final int position = viewHolder.getAdapterPosition();

            final TextView text;
            final Button yesB, noB;

            deletedAppointment = mData.get(position);
            mData.remove(position);
            backUpData.remove(position);
            adapterAppDoc.notifyItemRemoved(position);

            confirmDialog.setContentView(R.layout.dialog_confirmation);

            text = (TextView) confirmDialog.findViewById(R.id.TV_C_msg);
            text.setText("Once you delete, you can't take that appointment again. Are you sure to cancel/delete this appointment?");
            yesB = (Button) confirmDialog.findViewById(R.id.B_C_yes);
            noB = (Button) confirmDialog.findViewById(R.id.B_C_no);

            noB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mData.add(position,deletedAppointment);
                    backUpData.add(position,deletedAppointment);
                    adapterAppDoc.notifyItemInserted(position);
                    confirmDialog.dismiss();
                }
            });

            yesB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteAppoint(deletedAppointment.get("docId").toString(),deletedAppointment.get("date").toString());
                    confirmDialog.dismiss();
                }
            });

            confirmDialog.show();

            /*/shows undo options for deleted note
            Snackbar.make(recyclerViewAppDoc,"Your appointment with "+deletedAppointment.get("docName")
                    +" on "+deletedAppointment.get("date")+" is deleted!!", Snackbar.LENGTH_SHORT)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mData.add(position,deletedAppointment);
                            backUpData.add(position,deletedAppointment);
                            adapterAppDoc.notifyItemInserted(position);
                            undo = true;
                        }
                    }).show();


            //wait for 2seconds before deleting note from DB in case user change his mind
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if( !undo ){
                        deleteAppoint(deletedAppointment.get("docId").toString(),deletedAppointment.get("date").toString());
                    }
                }
            }, 5000);

            undo = false;*/
        }
    };

    private void deleteAppoint(String d_id, String date){
        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String appointId = date+d_id+user_id;
        db.collection("Appointments").document("Users").collection(user_id)
                .document(appointId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AppointedActivity.this,"Deleted successfully!",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Delete App: ","failure");
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AppointedActivity.this,HomeActivity.class));
    }

    private void prepareData(){
        Log.d("State","Preparing Data");

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mData.clear();
        backUpData.clear();

        //DB Data
        CollectionReference appRef= db.collection("Appointments").document("Users")
                .collection(user_id);

        appRef.orderBy("date", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d("State","Inside OnComplete");
                        if(task.isSuccessful()){
                            Log.d("State","Inside Success");
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                Map<String, Object> temp = doc.getData();
                                mData.add(temp);
                                backUpData.add(temp);
                                Log.d("AppActivity: ",temp.get("docId").toString());
                            }
                            adapterAppDoc.notifyDataSetChanged();
                        }else{
                            Toast.makeText(AppointedActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void populateRV(){
        Log.d("State","Populating Data");
        recyclerViewAppDoc = findViewById(R.id.RV_App_Doc);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewAppDoc.setLayoutManager(layoutManager);

        adapterAppDoc = new AppointDoctorAdapter(this,mData);
        recyclerViewAppDoc.setAdapter(adapterAppDoc);
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
                startActivity(new Intent(AppointedActivity.this, ProfileActivity.class));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                aboutDialog = new Dialog(AppointedActivity.this);
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
                helpDialog = new Dialog(AppointedActivity.this);
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
                    startActivity(new Intent(AppointedActivity.this, LoginActivity.class));
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
                    startActivity(new Intent(AppointedActivity.this, LoginActivity.class));
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