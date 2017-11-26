package com.example.karthik.anonymousdoubts.Chat;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.karthik.anonymousdoubts.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;



public class MessageListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final String TAG = "MessageListAdapter";

    private Context mContext;
    private List<ChatMessage> mMessageList;

    public MessageListAdapter(Context context, List<ChatMessage> messageList) {
        mContext = context;
        mMessageList = messageList;
    }


    @Override
    public int getItemCount() {
        return mMessageList.size();
    }


    @Override
    public int getItemViewType(int position) {
        ChatMessage message = mMessageList.get(position);

        if (message.getMessageUser().equals(FirebaseAuth.getInstance()
                .getCurrentUser().getEmail().split("@")[0]))
        {

            return VIEW_TYPE_MESSAGE_SENT;
        } else {

            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    public void updateMessageList(List<ChatMessage> newlist) {
        mMessageList = newlist;
        this.notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }else{
            Log.w(TAG,"Undefined View Type");
        }

        return null;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message =  mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }



    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView messageText, timeText, nameText;


        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);


        }


        void bind(ChatMessage message) {
            messageText.setText(message.getMessageText());



            timeText.setText(message.getMessageTime());

            if(!message.isAnon()){
                nameText.setText(message.getMessageUser());
            }else{
                nameText.setText(mContext.getResources().getString(R.string.AnonMode));
            }


        }
    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {

        TextView messageText, timeText;


        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);

        }

        void bind(ChatMessage message) {
            messageText.setText(message.getMessageText());

            timeText.setText(message.getMessageTime());

        }
    }
}

