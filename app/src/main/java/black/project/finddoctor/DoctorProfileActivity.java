package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import black.project.finddoctor.model.Doctor;

public class DoctorProfileActivity extends AppCompatActivity {

    private TextView textViewName, textViewSpeciality, textViewAbout, textViewFee, textViewChamber,
                        textViewContact, textViewSchedule;

    private ImageView proPic;

    private Button buttonAppoint, buttonFav, buttonBack, buttonMap;

    private Dialog confirmDialog, dialogA;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private DocumentReference documentReference;

    private List<String> schedule;
    private int count1=0;
    private int count2=0;
    private String doc_id;
    private Doctor doc;
    private int check=0;
    private boolean toggleFav = false;

    private boolean today=false, tomorrow=false;
    private boolean online=false;
    private double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        doc_id =  getIntent().getSerializableExtra("doctorId").toString();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if( user!=null && isOnline() ) {
            documentReference = db.collection("FavDoctors").document(user.getUid())
                    .collection("FAV").document(doc_id);
        }

        dialogA = new Dialog(DoctorProfileActivity.this);
        confirmDialog= new Dialog(DoctorProfileActivity.this);

        textViewName = (TextView)findViewById(R.id.TV_DP_name);
        textViewSpeciality = (TextView)findViewById(R.id.TV_DP_spec);
        textViewFee = (TextView)findViewById(R.id.TV_DP_fee);
        textViewAbout = (TextView)findViewById(R.id.TV_DP_about);
        textViewChamber = (TextView)findViewById(R.id.TV_DP_TV_DP_C_L);
        textViewSchedule = (TextView)findViewById(R.id.TV_DP_sche);
        textViewContact = (TextView)findViewById(R.id.TV_DP_Con);

        proPic = (ImageView)findViewById(R.id.IV_doc_pic);

        buttonAppoint = (Button)findViewById(R.id.B_DP_Appoint);
        buttonFav = (Button)findViewById(R.id.B_DP_fav);
        buttonFav = (Button)findViewById(R.id.B_DP_fav);
        buttonBack = (Button)findViewById(R.id.B_DP_back);
        buttonMap = (Button)findViewById(R.id.B_DP_loc);

        getDoctorInfo();
        getButFavColor(); //gets if the is fav or not and sets buttons' BG

        buttonAppoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOnline();
                if( !online ){
                    Toast.makeText(DoctorProfileActivity.this, "You're Offline!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(user!=null)appointDoctor();
                else Toast.makeText(DoctorProfileActivity.this,"You must login first!",Toast.LENGTH_SHORT).show();
            }
        });

        buttonFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOnline();
                if( !online ){
                    Toast.makeText(DoctorProfileActivity.this, "You're Offline!",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(user!=null){
                    if( toggleFav ){
                        toggleFav = false;
                        buttonFav.setBackground(getDrawable(R.drawable.ic_favorite_white));
                        deleteFav();
                    }else{
                        toggleFav = true;
                        buttonFav.setBackground(getDrawable(R.drawable.ic_favorite_blue));
                        addFavourite();
                    }

                }
                else Toast.makeText(DoctorProfileActivity.this,"You must login first!",Toast.LENGTH_SHORT).show();
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DoctorProfileActivity.this, MapsActivity.class);
                i.putExtra("lat",lat);
                i.putExtra("lng",lng);
                startActivity(i);
            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
                finish();
            }
        });

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

    private void getConfirmation(String day, int count){
        final Button yes, no;
        final TextView textViewMsg;

        confirmDialog.setContentView(R.layout.dialog_confirmation);

        textViewMsg = (TextView) confirmDialog.findViewById(R.id.TV_C_msg);
        textViewMsg.setText("Do you want to book an appointed on that day?");

        yes = (Button) confirmDialog.findViewById(R.id.B_C_yes);
        no = (Button) confirmDialog.findViewById(R.id.B_C_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookAppointment(day,count);
                confirmDialog.dismiss();
                dialogA.dismiss();
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

    private void getButFavColor(){

        if(user!=null){

            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Log.d("available: ",String.valueOf(task.getResult().exists()));
                    if ( task.getResult().exists() ) {
                        buttonFav.setBackground(getDrawable(R.drawable.ic_favorite_blue));
                        toggleFav = true;
                    }else{
                        buttonFav.setBackground(getDrawable(R.drawable.ic_favorite_white));
                        toggleFav = false;
                    }
                }
            });
        }else buttonFav.setBackground(getDrawable(R.drawable.ic_favorite_white));
    }

    private String getSchedule(List<String> S){
        String s="";
        for(int i=0; i<S.size(); i++){
            if(S.get(i).equals("1"))s+="SAT ";
            else if(S.get(i).equals("2"))s+="SUN ";
            else if(S.get(i).equals("3"))s+="MON ";
            else if(S.get(i).equals("4"))s+="TUE ";
            else if(S.get(i).equals("5"))s+="WED ";
            else if(S.get(i).equals("6"))s+="THU ";
            else s+="FRI ";
        }
        return s;
    }

    private void addFavourite(){
        Map<String, Object> doc2 = new HashMap<>();
        doc2.put("id", doc_id);
        doc2.put("docName", textViewName.getText().toString());
        doc2.put("speciality", textViewSpeciality.getText().toString());
        doc2.put("fee", textViewFee.getText().toString());
        doc2.put("picURL", "test");

        documentReference.set(doc2)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("AddF: ","success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("AddF: ","failure");
                    }
                });
    }

    private void deleteFav(){
        documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("DeleteF: ","success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("DeleteF: ","failure");
            }
        });
    }

    private void appointDoctor(){
        today =false;
        tomorrow =false;

        //open available slot in a dialogue window
        ProgressDialog pD = new ProgressDialog(DoctorProfileActivity.this);
        pD.show();
        pD.setContentView(R.layout.dialog_progressbar);

        //get today & current hour
        Date date = java.util.Calendar.getInstance().getTime();
        int hour = date.getHours();

        //Log.d("Time Now: ",String.valueOf(date.getHours()));

        int d = date.getDay()+2; //We took Sat as '1' But the took Sun as '0'
        if( d==8 )d=1;
        String day = String.valueOf(d);
        d++;
        if( d==8 )d=1;
        String day2 = String.valueOf(d);

        //check if today or tomorrow he sits for patients
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        Date date1 = new Date();
        String day1Date = formatter.format(date1);
        String day2Date = getNextDate(formatter.format(date1));

        for( int i=0; i<schedule.size();i++ ){
            if(schedule.get(i).equals(day) && hour<18 )today=true;
            if(schedule.get(i).equals(day2))tomorrow=true;
        }

        //Check if appointment available
        if( today ){
            count1=0;
            db.collection("Appointments").document("Doctors").collection(doc_id)
                    .document("Date").collection(day1Date).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            for(DocumentSnapshot dSS: queryDocumentSnapshots){
                                count1++;
                            }
                            if(count1>=5)today=false;
                            Log.d("count1: ",String.valueOf(count1));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("count1: ","failure");
                        }
                    });
        }
        if( tomorrow ){
            count2=0;
            db.collection("Appointments").document("Doctors").collection(doc_id)
                    .document("Date").collection(day2Date).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            for(DocumentSnapshot dSS: queryDocumentSnapshots){
                                count2++;
                            }
                            if(count2>=5)tomorrow=false;
                            Log.d("count2: ",String.valueOf(count2));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("count2: ","failure");
                        }
                    });
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                pD.dismiss();
                openAppointDialog(day1Date,day2Date);
            }
        }, 1500);


    }

    public static String getNextDate(String curDate) {
        String nextDate = "";
        try {
            Calendar today = Calendar.getInstance();
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            Date date = format.parse(curDate);
            today.setTime(date);
            today.add(Calendar.DAY_OF_YEAR, 1);
            nextDate = format.format(today.getTime());
        } catch (Exception e) {
            return nextDate;
        }
        return nextDate;
    }

    private void openAppointDialog(String day1, String day2){

        final TextView close, todayB, tomB;

        dialogA.setContentView(R.layout.dialog_appointment);

        close = (TextView) dialogA.findViewById(R.id.TV_ApDi_close);
        todayB = (TextView) dialogA.findViewById(R.id.TV_ApDi_today);
        tomB = (TextView) dialogA.findViewById(R.id.TV_ApDi_tom);

        if( today )todayB.setText(day1);
        if( tomorrow )tomB.setText(day2);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogA.dismiss();
            }
        });
        todayB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if he/she already booked an appointment for that day
                db.collection("Appointments").document("Doctors").collection(doc_id)
                        .document("Date").collection(day1).document(day1+doc_id+user.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult().exists()){
                                    Toast.makeText(DoctorProfileActivity.this, "You already booked an appointment on that day!",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DoctorProfileActivity.this,AppointedActivity.class));
                                    finish();
                                    return;
                                }
                            }
                        });
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(today)getConfirmation(day1,count1);
                    }
                }, 200);

            }
        });
        tomB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check if he/she already booked an appointment for that day
                db.collection("Appointments").document("Doctors").collection(doc_id)
                        .document("Date").collection(day2).document(day2+doc_id+user.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult().exists()){
                                    Toast.makeText(DoctorProfileActivity.this, "You already booked an appointment on that day!",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(DoctorProfileActivity.this,AppointedActivity.class));
                                    finish();
                                    return;
                                }
                            }
                        });

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(tomorrow)getConfirmation(day2,count2);
                    }
                }, 200);
            }
        });
        dialogA.show();

    }

    private void bookAppointment(String date, int count){

        String appointID = date+doc_id+user.getUid();

        Map<String, Object> dataU = new HashMap<>();

        Map<String, Object> dataD = new HashMap<>();
        dataD.put("userId",user.getUid());

        dataU.put("docId",doc_id);
        dataU.put("docName",doc.getDocName());
        dataU.put("speciality",doc.getSpeciality());
        dataU.put("picURL",doc.getPicURL());
        dataU.put("date",date);
        dataU.put("serial",(count+1));

        check=0;

        db.collection("Appointments").document("Doctors").collection(doc_id)
                .document("Date").collection(date).document(appointID).set(dataD)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        check++;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Appointment in Doc : ","failure");
                    }
                });

        db.collection("Appointments").document("Users").collection(user.getUid())
                .document(appointID).set(dataU)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        check++;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Appointment in User : ","failure");
                    }
                });

        if(check%2==0){
            Toast.makeText(DoctorProfileActivity.this,"Appointment is booked successfully!",Toast.LENGTH_SHORT).show();
            increaseDoctorsVisit();
            startActivity(new Intent(DoctorProfileActivity.this, AppointedActivity.class));
            finish();
        }
    }

    private void increaseDoctorsVisit(){
        db.collection("Doctors2").document(doc_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Map<String,Object> docTemp = task.getResult().getData();
                            long visited = (long) docTemp.get("visited");

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                docTemp.replace("visited",visited,visited+1);
                            }

                            db.collection("Doctors2").document(doc_id).set(docTemp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(!task.isSuccessful())Log.d("Visited Increment : ","failure");
                                }
                            });

                        }
                    }
                });
    }

    private void getDoctorInfo(){

        db.collection("Doctors2").document(doc_id).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if(document.exists()){
                                doc = document.toObject(Doctor.class);

                                lat = document.getDouble("lat");//
                                lng = document.getDouble("lng");//

                                textViewName.setText(doc.getDocName());
                                textViewSpeciality.setText(doc.getSpeciality());
                                textViewFee.setText(doc.getFee());
                                textViewAbout.setText(doc.getAbout());
                                textViewContact.setText(doc.getContactNo());

                                String loc = doc.getChamber()+", "+doc.getLocation();
                                textViewChamber.setText(loc);

                                schedule = doc.getSchedule();
                                String sch = getSchedule(schedule);
                                textViewSchedule.setText(sch);

                            }else{
                                Toast.makeText(DoctorProfileActivity.this,"Error No Doc!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }else{
                            Toast.makeText(DoctorProfileActivity.this,"Error404!!",Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }
}