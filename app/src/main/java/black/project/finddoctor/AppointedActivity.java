package black.project.finddoctor;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppointedActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointed);


        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navBar);
        bottomNavigationView.setSelectedItemId(R.id.appointed);

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
                else{

                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AppointedActivity.this,HomeActivity.class));
    }
}