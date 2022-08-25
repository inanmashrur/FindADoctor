package black.project.finddoctor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    private boolean loggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if( user!=null ){
            loggedIn = true;
        }

        textView = (TextView) findViewById(R.id.TV_loading);
        progressBar = (ProgressBar) findViewById(R.id.PB_loading);
        //Handler handler = new Handler();
        progressBar.setMax(100);

        progressAnimation();

    }

    private void progressAnimation(){
        ProgressBarAnimation animation = new ProgressBarAnimation(this, progressBar, textView, 0f,100f, loggedIn);
        animation.setDuration(1500);
        progressBar.setAnimation(animation);
    }
}