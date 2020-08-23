package com.app.rightbulb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    Thread th;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        th= new Thread()
        {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);
                    Intent intent=new Intent(MainActivity.this,LogInActivity.class);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        th.start();

    }
}
