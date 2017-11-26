package com.example.karthik.anonymousdoubts.CourseHomePage;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.karthik.anonymousdoubts.CourseCreationAndDiscovery.CourseDiscovery;
import com.example.karthik.anonymousdoubts.CourseHomePage.Course_AboutMeFragment;
import com.example.karthik.anonymousdoubts.CourseHomePage.Course_LectureFragment;
import com.example.karthik.anonymousdoubts.CourseHomePage.Course_StudentsFragment;
import com.example.karthik.anonymousdoubts.R;

import java.util.ArrayList;
import java.util.List;

public class CourseHomepageActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;


    @Override
    public void onBackPressed() {

        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            Intent i = new Intent(this, CourseDiscovery.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();

        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_course_homepage);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Edited here

        Intent intent = getIntent();
        String courseId = intent.getStringExtra("courseUId");
        Bundle bundle = new Bundle();
        bundle.putString("courseUId",courseId);
        Fragment students_list = new Course_StudentsFragment();
        Fragment about_fragment = new Course_AboutMeFragment();
        Fragment course_lecture = new Course_LectureFragment();
        about_fragment.setArguments(bundle);
        students_list.setArguments(bundle);
        course_lecture.setArguments(bundle);

        adapter.addFragment(course_lecture, "Weeks");
        adapter.addFragment(about_fragment, "About");
        adapter.addFragment(students_list, "Students");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }


    }

}
