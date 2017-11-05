package com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.karthik.anonymousdoubts.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateCourseActivity extends AppCompatActivity {

    private static final String TAG = "CreateCourseActivity";
    private Toolbar toolbar;
    private EditText _courseName,_courseCode,_courseDescription, _startDate, _endDate;
    private CheckBox _mon,_tue,_wed,_thu,_fri,_sat,_sun;
    private Button createCourseBtn;

    Calendar startDateCalendar;
    Calendar endDateCalendar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userIdEndPoint;
    private DatabaseReference courseEndPoint;
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
        courseEndPoint = mDatabase.child("institution").child(institution).child("courses");

        final String teacherName = getIntent().getStringExtra("teacherName");

        _courseName = (EditText) findViewById(R.id.input_course_name);
        _courseCode = (EditText) findViewById(R.id.input_course_code);
        _courseDescription = (EditText) findViewById(R.id.input_about_course);
        createCourseBtn = (Button) findViewById(R.id.btn_create_course);
        _startDate = (EditText) findViewById(R.id.inCourseStartDate);
        _endDate = (EditText) findViewById(R.id.inCourseEndDate);
        _mon = (CheckBox) findViewById(R.id.monCheckBox);
        _tue = (CheckBox) findViewById(R.id.tueCheckBox);
        _wed = (CheckBox) findViewById(R.id.wedCheckBox);
        _thu = (CheckBox) findViewById(R.id.thuCheckBox);
        _fri = (CheckBox) findViewById(R.id.friCheckBox);
        _sat = (CheckBox) findViewById(R.id.satCheckBox);
        _sun = (CheckBox) findViewById(R.id.sunCheckBox);

        startDateCalendar = Calendar.getInstance();
        endDateCalendar = (Calendar) startDateCalendar.clone();

        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                startDateCalendar.set(Calendar.YEAR, year);
                startDateCalendar.set(Calendar.MONTH, month);
                startDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(1);
            }
        };

        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(2);
            }
        };

        _startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateCourseActivity.this, startDateListener, startDateCalendar
                        .get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH),
                        startDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        _endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(CreateCourseActivity.this, endDateListener, startDateCalendar
                        .get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH),
                        startDateCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


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

                    CourseData courseData = new CourseData(courseDescription, null, null);
                    courseEndPoint.child(uId).setValue(courseData);

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

        if(startDateCalendar.after(endDateCalendar)){
            new AlertDialog.Builder(CreateCourseActivity.this)
                    .setTitle("Error")
                    .setMessage("Please Enter Valid End Date.")
                    .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            // do nothing - it will just close when clicked
                        }
                    }).show();
            valid = false;
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

    private void updateLabel(int state) {
        String myFormat = "dd/MM/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

        if(state == 1)
            _startDate.setText(sdf.format(startDateCalendar.getTime()));
        else if(state == 2)
            _endDate.setText(sdf.format(endDateCalendar.getTime()));
    }

}
