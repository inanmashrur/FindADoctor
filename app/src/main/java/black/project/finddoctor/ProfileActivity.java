package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private TextView name, email, mobile;
    private Button buttonChangeP, buttonBack;
    private ImageView pic;

    private Dialog changePassDialog;

    private FirebaseUser user;
    private FirebaseFirestore db;

    private boolean online=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db =  FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        changePassDialog = new Dialog(ProfileActivity.this);

        name = (TextView)findViewById(R.id.TV_P_name);
        email = (TextView)findViewById(R.id.TV_P_email);
        mobile = (TextView)findViewById(R.id.TV_P_mobile);
        pic = (ImageView)findViewById(R.id.IV_P_Pic);
        buttonChangeP = (Button) findViewById(R.id.B_P_changePass);
        buttonBack = (Button) findViewById(R.id.B_P_back);

        buttonChangeP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialogCP();
            }
        });

        if(user!=null){
            getUserInfo();
        }

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void openDialogCP(){
        final EditText pass, c_pass;
        final Button change, exit;

        changePassDialog.setContentView(R.layout.window_password);

        pass = (EditText) changePassDialog.findViewById(R.id.ET_WPC_newPass);
        c_pass = (EditText) changePassDialog.findViewById(R.id.ET_WPC_C_newPass);

        exit = (Button) changePassDialog.findViewById(R.id.B_WPC_back);
        change = (Button) changePassDialog.findViewById(R.id.B_WPC_confirm);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePassDialog.dismiss();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String p1 , p2;
                p1 = pass.getText().toString();
                p2 = c_pass.getText().toString();

                if( p1.isEmpty() | p2.isEmpty()) Toast.makeText(ProfileActivity.this, "Please fill-up both fields!",Toast.LENGTH_SHORT).show();
                else if(!p1.equals(p2))Toast.makeText(ProfileActivity.this, "Password don't match!",Toast.LENGTH_SHORT).show();
                else{
                    user.updatePassword(p1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if( task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this, "Password is changed successfully!!",Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d("UpdatePass: ","failure");
                            }
                        }
                    });
                    changePassDialog.dismiss();
                }
            }
        });
        changePassDialog.show();
    }

    private void getUserInfo(){
        db.collection("Users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            name.setText(documentSnapshot.get("userName").toString());
                            email.setText(documentSnapshot.get("email").toString());
                            mobile.setText(documentSnapshot.get("mobile").toString());
                        }else{
                            Log.d("Getting U_info: ","failure");
                        }
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
}