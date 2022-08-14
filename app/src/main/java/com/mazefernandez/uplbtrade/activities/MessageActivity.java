package com.mazefernandez.uplbtrade.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.models.Message;


public class MessageActivity extends AppCompatActivity {
    ListView chatHistory;
    FirebaseListAdapter<Message> messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ImageView chatImg = findViewById(R.id.chat_img);
        TextView chatName = findViewById(R.id.chat_name);
        ImageButton send = findViewById(R.id.send_button);
        chatHistory = findViewById(R.id.chat_history);

        Message message = (Message) getIntent().getSerializableExtra("MESSAGE");
        assert message != null;
        chatName.setText(message.getName());

        displayMessages();

        send.setOnClickListener(new View.OnClickListener() {
            EditText chatInput = findViewById(R.id.input);
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().push().setValue(new Message(message.getName(),chatInput.getText().toString(),message.getEmail()));
                chatInput.setText("");
            }
        });
    }

    private void displayMessages() {
        messageAdapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.user_row, FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {
                TextView customerName = findViewById(R.id.customer_name);
                TextView messageText = findViewById(R.id.message_text);
                TextView time = findViewById(R.id.time);

                customerName.setText(model.getName());
                messageText.setText(model.getMessage());
                time.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",model.getTime()));
            }
        };
        chatHistory.setAdapter(messageAdapter);
    }
}