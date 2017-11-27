package com.example.karthik.anonymousdoubts;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.karthik.anonymousdoubts.Authentication.EmailVerificationActivity;
import com.example.karthik.anonymousdoubts.Authentication.LoginActivity;
import com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery.CourseDiscovery;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView im = (ImageView) findViewById(R.id.imageView);

        setSizeLogo(im);

        Handler handler = new Handler();  // call should be state alert!!!!!!!!!!!!
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, 3000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setSizeLogo(View view){

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics( dm );

        int screenWidth = dm.widthPixels;
        screenWidth = screenWidth - (int) Math.round(0.15*screenWidth);

        view.getLayoutParams().width = screenWidth;
        view.getLayoutParams().height = screenWidth/2;

    }

}
