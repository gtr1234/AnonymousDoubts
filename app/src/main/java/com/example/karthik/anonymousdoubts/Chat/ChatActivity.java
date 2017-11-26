package com.example.karthik.anonymousdoubts.Chat;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;


import com.example.karthik.anonymousdoubts.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private MessageListAdapter messageListAdapter;
    private RecyclerView recyclerView;
    private List<ChatMessage> messageList;
    private boolean isPrivate;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private DatabaseReference msgEndPoint;

    private FirebaseAuth mAuth  = FirebaseAuth.getInstance();
    final FirebaseUser firebaseUser = mAuth.getCurrentUser();

    private LinearLayout ll_chatbox;
    private EditText et_chatbox;
    private Button sendButton;
    private Switch switch1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ll_chatbox = (LinearLayout) findViewById(R.id.layout_chatbox);
        et_chatbox  = (EditText) findViewById(R.id.edittext_chatbox);
        sendButton = (Button) findViewById(R.id.button_chatbox_send);
        switch1 = (Switch) findViewById(R.id.switch1);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(switch1.isEnabled()){
            setIncognitoUI();
            isPrivate = true;
        }

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    setIncognitoUI();
                    isPrivate = true;
                    Log.d(TAG,"Set Privacy to TRUE");
                }else{
                    setNormalUI();
                    isPrivate = false;
                    Log.d(TAG,"Set Privacy to FALSE");
                }
            }
        });



        messageList = new ArrayList<>();
        messageListAdapter = new MessageListAdapter(ChatActivity.this, messageList);

        recyclerView = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        recyclerView.setAdapter(messageListAdapter);


        String institution = firebaseUser.getEmail().split("@")[1];
        institution = institution.replace(".","");
        final String username = firebaseUser.getEmail().split("@")[0];
        msgEndPoint = ref.child("institution").child(institution).child("messages");

        msgEndPoint.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                messageList.add(message);
                Log.d(TAG,"Child added");

                messageListAdapter.updateMessageList(messageList);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.edittext_chatbox);

                Log.d(TAG,"Message sent with FLAG: " + isPrivate);
                msgEndPoint.push().setValue(new ChatMessage(input.getText().toString(),username,isPrivate));

                input.setText("");
            }
        });


    }

    private void setNormalUI() {
        getSupportActionBar().setBackgroundDrawable( new ColorDrawable(ContextCompat.getColor(this,R.color.primary)));
        //ll_chatbox.setBackground(new ColorDrawable(Color.WHITE));

        int colorFrom = ContextCompat.getColor(this,R.color.black);
        int colorTo = ContextCompat.getColor(this,R.color.primary);
        int chatBoxColorTo = ContextCompat.getColor(this,R.color.white);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(500); // milliseconds
        ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(),colorFrom,chatBoxColorTo);
        colorAnimation2.setDuration(500);
        final Window window = getWindow();

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int val = (int) animator.getAnimatedValue();
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(val));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(val);
                }
            }

        });

        colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ll_chatbox.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });

        colorAnimation.start();
        colorAnimation2.start();
        ll_chatbox.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
        et_chatbox.setTextColor(Color.BLACK);
        sendButton.setTextColor(Color.BLACK);
        switch1.setText("Normal");
    }

    protected void setIncognitoUI(){
        //ColorDrawable cdBlack = new ColorDrawable(Color.BLACK);
        //getSupportActionBar().setBackgroundDrawable(cdBlack);
        //ll_chatbox.setBackground(cdBlack);

        int colorFrom = ContextCompat.getColor(this,R.color.primary);
        int chatBoxColorFrom = ContextCompat.getColor(this,R.color.white);
        int colorTo = ContextCompat.getColor(this,R.color.black);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        ValueAnimator colorAnimation2 = ValueAnimator.ofObject(new ArgbEvaluator(),chatBoxColorFrom,colorTo);
        colorAnimation.setDuration(500); // milliseconds
        colorAnimation2.setDuration(500);
        final Window window = getWindow();

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                int val = (int) animator.getAnimatedValue();

                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(val));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.setStatusBarColor(val);
                }
            }

        });

        colorAnimation2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                ll_chatbox.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });

        colorAnimation.start();
        colorAnimation2.start();

        et_chatbox.setTextColor(Color.WHITE);
        sendButton.setTextColor(Color.WHITE);
        switch1.setText("Anonymous");
    }


}