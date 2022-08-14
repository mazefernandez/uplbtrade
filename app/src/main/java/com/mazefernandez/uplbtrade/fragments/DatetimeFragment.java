package com.mazefernandez.uplbtrade.fragments;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mazefernandez.uplbtrade.R;

import java.util.Calendar;

/* Allows the user to set a time and date for the meetup */

public class DatetimeFragment extends Fragment implements View.OnClickListener{
    private EditText date, time;
    private Button dateButton, timeButton;
    private ImageView itemImg;
    private String imgString;


    public DatetimeFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datetime, container, false);

        /* datetime views */
        TextView itemName = view.findViewById(R.id.item_name);
        TextView customerName = view.findViewById(R.id.customer_name);
        TextView price = view.findViewById(R.id.price);
        TextView offer = view.findViewById(R.id.offer);
        itemImg = view.findViewById(R.id.item_img);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            itemName.setText(bundle.getString("item"));
            customerName.setText(getArguments().getString("customer"));
            price.setText(getArguments().getString("price"));
            offer.setText(getArguments().getString("offer"));
            imgString = getArguments().getString("image");
        }

        /* Firebase instances */
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        /* retrieve image from firebase */
        StorageReference ref = storageReference.child("images/"+imgString);

        final long ONE_MEGABYTE = 1024 * 1024 * 5;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            System.out.println("Successfully read image");
            itemImg.setImageBitmap(bitmap);
        }).addOnFailureListener(fail -> System.out.println("Failed to read image" + fail));


        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        dateButton = view.findViewById(R.id.date_button);
        timeButton = view.findViewById(R.id.time_button);

        dateButton.setOnClickListener(this);
        timeButton.setOnClickListener(this);

        return view;
    }

    /* allows a user to choose a date and time */
    @Override
    public void onClick(View v) {
        final Calendar calendar = Calendar.getInstance();
        if(v == dateButton) {
            //get date
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            @SuppressLint("DefaultLocale") DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view, year1, month1, dayOfMonth) -> date.setText(String.format("%d-%d-%d", year1, (month1 +1), dayOfMonth)), year, month, day);
            datePickerDialog.show();
        }
        else if (v == timeButton) {
            //get time
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            @SuppressLint("DefaultLocale") TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), (view, hourOfDay, minute1) -> time.setText(String.format("%d:%d", hourOfDay, minute1)), hour, minute, true);
            timePickerDialog.show();
        }
    }

    /* concatenates date and time */
    public String getDateTime() {
        String timeStr = time.getText().toString();
        timeStr = timeStr.concat(":00");
        String datetime = date.getText().toString();
        datetime = datetime.concat(" ");
        datetime = datetime.concat(timeStr);

        return datetime;
    }
}
