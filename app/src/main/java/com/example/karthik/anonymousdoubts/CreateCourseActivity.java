package com.example.karthik.anonymousdoubts;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateCourseActivity extends AppCompatActivity {

    private static final String TAG = "CreateCourseActivity";
    private Toolbar toolbar;
    private EditText _courseName,_courseCode,_courseDescription;
    private Button createCourseBtn;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userIdEndPoint;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        initToolBar();

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        String institution = firebaseUser.getEmail().split("@")[1];
        institution = institution.replace(".","");

        mDatabase =  FirebaseDatabase.getInstance().getReference();
        userIdEndPoint = mDatabase.child("institution").child(institution).child("users").child(userId);

        final String teacherName = getIntent().getStringExtra("teacherName");

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

                if(validate(courseName, courseCode, courseDescription)) {

                    final ProgressDialog progressDialog = new ProgressDialog(CreateCourseActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Creating Course...");
                    progressDialog.show();

                    CourseMetaData courseMetaData = new CourseMetaData(courseName, teacherName, courseCode);
                    String uId = userIdEndPoint.child("courseMetaDataArrayList").push().getKey();
                    courseMetaData.courseUId = uId;

                    userIdEndPoint.child("courseMetaDataArrayList").child(uId).setValue(courseMetaData);

                    progressDialog.dismiss();

                    onCourseCreationSuccess();
                }

            }
        });


    }

    public boolean validate(String courseName,String courseCode,String courseDescription) {
        boolean valid = true;

        if (courseName.isEmpty()) {
            _courseName.setError("Course name can't be empty");
            valid = false;
        } else {
            _courseName.setError(null);
        }



        if (courseCode.isEmpty()) {
            _courseCode.setError("Course code can't be empty");
            valid = false;
        } else {
            _courseCode.setError(null);
        }

        if (courseDescription.isEmpty()) {
            _courseDescription.setError("Course description can't be empty");
            valid = false;
        } else {
            _courseDescription.setError(null);
        }


        return valid;
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
