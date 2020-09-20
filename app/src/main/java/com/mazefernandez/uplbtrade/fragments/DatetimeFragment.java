package com.mazefernandez.uplbtrade.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mazefernandez.uplbtrade.R;

import java.util.Calendar;

public class DatetimeFragment extends Fragment implements View.OnClickListener{
    private EditText date, time;
    private Button dateButton, timeButton;
    private TextView itemName, customerName, price, offer;

    public DatetimeFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datetime, container, false);

        itemName = view.findViewById(R.id.item_name);
        customerName = view.findViewById(R.id.customer_name);
        price = view.findViewById(R.id.price);
        offer = view.findViewById(R.id.offer);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            itemName.setText(bundle.getString("item"));
            customerName.setText(getArguments().getString("customer"));
            price.setText(getArguments().getString("price"));
            offer.setText(getArguments().getString("offer"));
        }

        date = view.findViewById(R.id.date);
        time = view.findViewById(R.id.time);
        dateButton = view.findViewById(R.id.date_button);
        timeButton = view.findViewById(R.id.time_button);

        dateButton.setOnClickListener(this);
        timeButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        final Calendar calendar = Calendar.getInstance();
        if(v == dateButton) {
            //get date
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), (view, year1, month1, dayOfMonth) -> date.setText(String.format("%d-%d-%d", dayOfMonth, (month1 +1), year1)), year, month, day);
            datePickerDialog.show();
        }
        else if (v == timeButton) {
            //get time
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), (view, hourOfDay, minute1) -> time.setText(String.format("%d:%d", hourOfDay, minute1)), hour, minute, true);
            timePickerDialog.show();
        }
    }

    public String getDateTime() {
        String timeStr = time.getText().toString();
        timeStr = timeStr.concat(":00");
        String datetime = date.getText().toString();
        datetime = datetime.concat(" ");
        datetime = datetime.concat(timeStr);

        return datetime;
    }
}
