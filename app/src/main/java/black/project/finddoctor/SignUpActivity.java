package black.project.finddoctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import black.project.finddoctor.model.User;

public class SignUpActivity extends AppCompatActivity {

    private Button buttonSignup, buttonBack;
    private EditText editTextName, editTextEmail, editTextPass, editTextCPass, editTextMobile;

    private ProgressDialog progressDialog;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        buttonSignup = (Button)findViewById(R.id.B_SU2);
        buttonBack = (Button)findViewById(R.id.B_SU_g_back);

        editTextName = (EditText) findViewById(R.id.ET_SU_name);
        editTextEmail = (EditText) findViewById(R.id.ET_SU_email);
        editTextMobile = (EditText) findViewById(R.id.ET_SU_Mobile);
        editTextPass = (EditText) findViewById(R.id.ET_SU_pass);
        editTextCPass = (EditText) findViewById(R.id.ET_SU_C_pass);

        //mAuth = FirebaseAuth.getInstance();

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String pass, cPass, name, email, mobile;
                pass = editTextPass.getText().toString();
                cPass = editTextCPass.getText().toString();
                if(pass.equals(cPass)){

                    name = editTextName.getText().toString();
                    email = editTextEmail.getText().toString();
                    mobile = editTextMobile.getText().toString();

                    if( name.isEmpty() | email.isEmpty() | mobile.isEmpty() )Toast.makeText(SignUpActivity.this,"Please fillup every field!",
                            Toast.LENGTH_SHORT).show();
                    else {
                        User user = new User(name,email,mobile);
                        signUp(user,pass);
                    }
                }
                else Toast.makeText(SignUpActivity.this,"Password don't match!",Toast.LENGTH_SHORT).show();

            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void signUp(User userC, String pass){
        viewProBar();
        mAuth = FirebaseAuth.getInstance();
        String email = userC.getEmail();

        //Toast.makeText(SignUpActivity.this,email+" "+pass ,Toast.LENGTH_SHORT).show();
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if( task.isSuccessful()){

                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder().setDisplayName(userC.getuserName()).build();
                            user.updateProfile(profile)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("Update Profile", "User profile updated.");
                                            }
                                        }
                                    });

                            String UID = user.getUid();
                            updateUserDB(userC, UID); //Updates User DB

                            startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                        }
                        else{
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserDB(User user, String UID){
        db.collection("Users").document(UID).set(user);
    }

    private void viewProBar(){
        progressDialog = new ProgressDialog(SignUpActivity.this);
        progressDialog.show();
        progressDialog.setContentView(R.layout.dialog_progressbar);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}