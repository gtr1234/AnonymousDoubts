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

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Weeks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class CreateCourseActivity extends AppCompatActivity {

    private static final String TAG = "CreateCourseActivity";
    private Toolbar toolbar;
    private EditText _courseName,_courseCode,_courseDescription,_coursePasscode, _startDate, _endDate;
    private CheckBox _mon,_tue,_wed,_thu,_fri,_sat,_sun;
    private Button createCourseBtn;

    Calendar startDateCalendar;
    Calendar endDateCalendar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference userIdEndPoint;
    private DatabaseReference allCoursesMetaDataEndPoint;
    private DatabaseReference allCoursesDataEndPoint;
    private DatabaseReference institutionEndPoint;
    String userId;

    private HashMap<String, Integer> daysToNum;

    int calendarSetState = 0;

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
        allCoursesMetaDataEndPoint = mDatabase.child("institution").child(institution).child("allCoursesMetaData");
        allCoursesDataEndPoint = mDatabase.child("institution").child(institution).child("allCoursesData");
        institutionEndPoint = mDatabase.child("institution").child(institution);

        final String teacherName = getIntent().getStringExtra("teacherName");

        _courseName = (EditText) findViewById(R.id.input_course_name);
        _courseCode = (EditText) findViewById(R.id.input_course_code);
        _courseDescription = (EditText) findViewById(R.id.input_about_course);
        _coursePasscode = (EditText) findViewById(R.id.input_course_passcode);
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
                calendarSetState++;
            }
        };

        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                endDateCalendar.set(Calendar.YEAR, year);
                endDateCalendar.set(Calendar.MONTH, month);
                endDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(2);
                calendarSetState++;
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
                String coursePasscode = _coursePasscode.getText().toString();

                if(validate(courseName, courseCode, courseDescription, coursePasscode)) {

                    final ProgressDialog progressDialog = new ProgressDialog(CreateCourseActivity.this,
                            R.style.AppTheme_Dark_Dialog);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Creating Course...");
                    progressDialog.show();

                    CourseMetaData courseMetaData = new CourseMetaData(courseName, teacherName, courseCode);

                    String courseuId = allCoursesMetaDataEndPoint.push().getKey();

                    courseMetaData.courseUId = courseuId;
                    courseMetaData.passcode = coursePasscode;


                    DateTime s = new DateTime(startDateCalendar.get(Calendar.YEAR), startDateCalendar.get(Calendar.MONTH)+1,
                            startDateCalendar.get(Calendar.DAY_OF_MONTH),0, 0,0,
                            0);
                    DateTime e = new DateTime(endDateCalendar.get(Calendar.YEAR), endDateCalendar.get(Calendar.MONTH)+1,
                            endDateCalendar.get(Calendar.DAY_OF_MONTH),0, 0,0,
                            0);


                    DateTime modStartDate = s.dayOfWeek().withMinimumValue();
                    DateTime modEndDate = e.dayOfWeek().withMaximumValue();

                    int weeksInBetween = Weeks.weeksBetween(modStartDate,modEndDate).getWeeks()+1;

                    Log.i(TAG,"modified Start "+modStartDate.toString()+" modified End date "+modEndDate.toString());

                    allCoursesMetaDataEndPoint.child(courseuId).setValue(courseMetaData);
                    userIdEndPoint.child("courseUIds").child(courseuId).setValue(courseuId);
                    allCoursesDataEndPoint.child(courseuId).child("courseDescription").setValue(courseDescription);


                    Log.i(TAG,"weeks = "+weeksInBetween);
                    for (int i = 0; i < weeksInBetween; i++) {
                        String weekId = institutionEndPoint.child("weeks").push().getKey();

                        String tempUid = allCoursesDataEndPoint.child(courseuId).child("weekIds").push().getKey();
                        allCoursesDataEndPoint.child(courseuId).child("weekIds").child(tempUid).setValue(weekId);

                        ArrayList<String> daysSelected = getSelectedDays();

                        String myFormat = "dd/MM/yy";
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                        DateTime weekStartDateTime = modStartDate.plusDays(i*7);
                        String weekStartDate = sdf.format(weekStartDateTime.toDate());
                        String weekEndDate = sdf.format(weekStartDateTime.plusDays(6).toDate());

                        for (int j = 0; j < daysSelected.size(); j++) {

                            String lectureDate = sdf.format(weekStartDateTime.plusDays(daysToNum.get(daysSelected.get(j))).toDate());

                            String lectureId = institutionEndPoint.child("lecturesData").push().getKey();
                            institutionEndPoint.child("weeksData").child(weekId).child("lectureIds").push().setValue(lectureId);
                            institutionEndPoint.child("weeksData").child(weekId).child("StartDate").setValue(weekStartDate);
                            institutionEndPoint.child("weeksData").child(weekId).child("EndDate").setValue(weekEndDate);

                            institutionEndPoint.child("lecturesData").child(lectureId).child("day").setValue(daysSelected.get(j));
                            institutionEndPoint.child("lecturesData").child(lectureId).child("date").setValue(lectureDate);
                        }

                    }


                    progressDialog.dismiss();

                    onCourseCreationSuccess();
                }

            }
        });


    }

    public boolean validate(String courseName,String courseCode,String courseDescription, String coursePasscode) {
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

        if(calendarSetState < 2) {
            new AlertDialog.Builder(CreateCourseActivity.this)
                    .setTitle("Error")
                    .setMessage("Please Select Valid Start Date and End Date.")
                    .setNeutralButton("Back", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            // do nothing - it will just close when clicked
                        }
                    }).show();
            valid = false;
        }
        else if(startDateCalendar.after(endDateCalendar)){
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

        if (coursePasscode.isEmpty()) {
            _coursePasscode.setError("Course passcode can't be empty");
            valid = false;
        } else {
            _coursePasscode.setError(null);
        }

        if (!_mon.isChecked() && !_tue.isChecked() && !_wed.isChecked() && !_thu.isChecked() &&
                !_fri.isChecked() && !_sat.isChecked() && !_sun.isChecked()) {
                    new AlertDialog.Builder(CreateCourseActivity.this)
                        .setTitle("Error")
                        .setMessage("Please select at-least one day in the week")
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

    private ArrayList<String> getSelectedDays(){
        ArrayList<String> daysSelected = new ArrayList<>();

        daysToNum = new HashMap<>();
        daysToNum.put("Monday",0);
        daysToNum.put("Tuesday",1);
        daysToNum.put("Wednesday",2);
        daysToNum.put("Thursday",3);
        daysToNum.put("Friday",4);
        daysToNum.put("Saturday",5);
        daysToNum.put("Sunday",6);

        if(_mon.isChecked())
            daysSelected.add("Monday");
        if(_tue.isChecked())
            daysSelected.add("Tuesday");
        if(_wed.isChecked())
            daysSelected.add("Wednesday");
        if(_thu.isChecked())
            daysSelected.add("Thursday");
        if(_fri.isChecked())
            daysSelected.add("Friday");
        if(_sat.isChecked())
            daysSelected.add("Saturday");
        if(_sun.isChecked())
            daysSelected.add("Sunday");

        return daysSelected;
    }

}
