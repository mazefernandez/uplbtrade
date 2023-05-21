package com.mazefernandez.uplbtrade.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mazefernandez.uplbtrade.R;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class MessageActivity extends AppCompatActivity {
    ListView chatHistory;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> messages = new ArrayList<>();
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        reference = FirebaseDatabase.getInstance().getReference();

        TextView chatEmail = findViewById(R.id.chat_email);
        ImageButton send = findViewById(R.id.send_button);
        EditText chatInput = findViewById(R.id.input);
        chatHistory = findViewById(R.id.chat_history);

        /* SharedPref to get customer email */
        SharedPreferences pref = this.getSharedPreferences("uplbtrade", MODE_PRIVATE);
        String customerEmail = pref.getString("customer_email", "-1");

        /* Get message info from messages activity */
        Intent intent = getIntent();
        Bundle userInfo = intent.getBundleExtra("MESSAGE");
        String chatterEmail = userInfo.getString("EMAIL");
        if (!userInfo.getString("TIME").isEmpty()) {
            Long time = Long.valueOf(userInfo.getString("TIME"));
        }
        chatEmail.setText(chatterEmail);


        send.setOnClickListener(view -> {
            if (chatInput.getText().toString().isEmpty()) {
                Toast.makeText(MessageActivity.this,"Write a message!", Toast.LENGTH_SHORT).show();
            }
            /* create a message when sent */
            else {
                Map<String, Object> chatData = new HashMap<>();
                chatData.put("sender", customerEmail);
                chatData.put("receiver", chatterEmail);
                chatData.put("message", chatInput.getText().toString());
                chatData.put("time", new Date().getTime());

                reference.child("chats").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int count;
                        if (snapshot.exists()) {
                            count = (int) snapshot.getChildrenCount() + 1;
                        }
                        else {
                            count = 1;
                        }
                        reference.child("chats").child(String.valueOf(count)).setValue(chatData).addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                chatInput.setText("");
                            }
                        }).addOnFailureListener(e -> System.out.println("Error in sending message" + e.getMessage()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        reference.child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    messages.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        /* check if the sender and receiver are either the user or the person they are chatting */
                        if ((Objects.equals(dataSnapshot.child("sender").getValue(), customerEmail) || (Objects.equals(dataSnapshot.child("receiver").getValue(), customerEmail))
                        & (Objects.equals(dataSnapshot.child("sender").getValue(), chatterEmail) || Objects.equals(dataSnapshot.child("sender").getValue(), chatterEmail))))
                        {
                            String message = Objects.requireNonNull(dataSnapshot.child("message").getValue()).toString();
                            /* check if the sender is not the user */
                            if (Objects.requireNonNull(dataSnapshot.child("sender").getValue()).toString().equals(chatterEmail)) {
                                message = "> " + message;
                            }
                            messages.add(message);
                        }
                    }
                    arrayAdapter = new ArrayAdapter<>(MessageActivity.this, android.R.layout.simple_list_item_1, messages);
                    chatHistory.setAdapter(arrayAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public void onRestart()
    {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}