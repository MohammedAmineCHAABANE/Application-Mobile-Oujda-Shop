package com.example.oujdashop.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.oujdashop.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        new Handler().postDelayed(()->{
            SharedPreferences sharedPreferences = getSharedPreferences("utilisateur",MODE_PRIVATE);
            boolean DejaConnecter = sharedPreferences.getBoolean("DejaConnecter",false);

            Intent intent ;
            if (DejaConnecter){
                intent = new Intent(this,MainActivity.class);
            }else {
                intent = new Intent(this,LoginActivity.class);
            }
            startActivity(intent);
            finish();
        },3000);
    }

}