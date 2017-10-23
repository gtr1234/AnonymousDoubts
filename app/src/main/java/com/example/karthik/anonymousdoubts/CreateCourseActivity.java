package com.example.karthik.anonymousdoubts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateCourseActivity extends AppCompatActivity {

    private static final String TAG = "CreateCourseActivity";
    private Toolbar toolbar;
    private EditText _courseName,_courseCode,_courseDescription;
    private Button createCourseBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        initToolBar();

        _courseName = (EditText) findViewById(R.id.input_course_name);
        _courseCode = (EditText) findViewById(R.id.input_course_code);
        _courseDescription = (EditText) findViewById(R.id.input_about_course);
        createCourseBtn = (Button) findViewById(R.id.btn_create_course);

        createCourseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String courseName = _courseName.getText().toString();
                String courseCode = _courseCode.getText().toString();
                String courseDescription = _courseDescription.getText().toString();

                onCourseCreationSuccess();
            }
        });


    }

    public void onCourseCreationSuccess() {
        createCourseBtn.setEnabled(true);
        setResult(RESULT_OK, null);
        Log.i(TAG,"finished course creation");
        finish();
    }
    public void onCourseCreationFail() {
        setResult(RESULT_CANCELED, null);
        Log.i(TAG,"finished course failed");
        finish();
    }

    public void initToolBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Course Creation");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCourseCreationFail();
            }
        });

    }
}
