package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private Button logingButton, signupButton, forgetButton, guestButton;
    private EditText editText_email, editText_pass;

    private ProgressDialog progressDialog;
    private Dialog forgetDialog;

    private FirebaseAuth mAuth;
    private Dialog confirmDialog;

    private boolean online = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logingButton = (Button) findViewById(R.id.B_login);
        signupButton = (Button) findViewById(R.id.B_signup);
        forgetButton = (Button) findViewById(R.id.B_forget_pass);
        guestButton = (Button) findViewById(R.id.B_guest);

        editText_email = (EditText) findViewById(R.id.ET_email);
        editText_pass = (EditText) findViewById(R.id.ET_pass);

        forgetDialog = new Dialog(LoginActivity.this);
        confirmDialog = new Dialog(LoginActivity.this);

        mAuth = FirebaseAuth.getInstance();

        isOnline();

        logingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isOnline();
                if(!online){
                    Toast.makeText(LoginActivity.this, "You're Offline!",Toast.LENGTH_SHORT).show();
                    return;
                }

                String email = editText_email.getText().toString();
                String pass = editText_pass.getText().toString();
                if( email.isEmpty() | pass.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Please fill-up every field!", Toast.LENGTH_SHORT).show();
                }
                else {
                    viewProBar();
                    login(email, pass);
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        forgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetPassWindow();
            }
        });

        guestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
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

    private void login(String email, String pass){
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void forgetPassWindow(){
        final TextView close, sendMsg;
        final Button submit;
        final EditText emailBox;

        forgetDialog.setContentView(R.layout.window_forgetpass);
        close = (TextView) forgetDialog.findViewById(R.id.TV_close);
        sendMsg = (TextView) forgetDialog.findViewById(R.id.TV_FP);
        submit = (Button) forgetDialog.findViewById(R.id.B_F_submit);
        emailBox = (EditText) forgetDialog.findViewById(R.id.ET_F_email);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                forgetDialog.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailBox.getText().toString();

                if( !email.isEmpty() ) {

                    mAuth.sendPasswordResetEmail(email)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    emailBox.setText("");
                                    sendMsg.setTextColor(getResources().getColor(R.color.green));
                                    sendMsg.setText("A password reset email was sent to " + email + ". Check your email for resetting your password.");

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    sendMsg.setTextColor(getResources().getColor(R.color.red));
                                    sendMsg.setText("Please make sure you entered a valid email!");
                                }
                            });
                }else Toast.makeText(LoginActivity.this, "Please enter your email first!", Toast.LENGTH_SHORT).show();
            }
        });
        forgetDialog.show();
    }

    @Override
    public void onBackPressed() {
        getConfirmation();
    }

    private void viewProBar(){
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialog_progressbar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
    private void getConfirmation(){
        final Button yes, no;
        final TextView textViewMsg;

        confirmDialog.setContentView(R.layout.dialog_confirmation);

        textViewMsg = (TextView) confirmDialog.findViewById(R.id.TV_C_msg);
        textViewMsg.setText("Are you sure you want to exit?");

        yes = (Button) confirmDialog.findViewById(R.id.B_C_yes);
        no = (Button) confirmDialog.findViewById(R.id.B_C_no);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog.dismiss();
                Intent mainActivity = new Intent(Intent.ACTION_MAIN);
                mainActivity.addCategory(Intent.CATEGORY_HOME);
                mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainActivity);
                finish();
                System.exit(0);
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