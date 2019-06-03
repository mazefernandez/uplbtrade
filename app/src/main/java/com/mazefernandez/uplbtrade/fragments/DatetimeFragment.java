package com.mazefernandez.uplbtrade.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.mazefernandez.uplbtrade.R;

import java.util.Calendar;

public class DatetimeFragment extends Fragment implements View.OnClickListener{
    EditText date, time;
    Button dateButton, timeButton;
    TextView itemName, customerName, price;

    public DatetimeFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_datetime, container, false);

        itemName = view.findViewById(R.id.item_name);
        customerName = view.findViewById(R.id.customer_name);
        price = view.findViewById(R.id.item_price);

        if (getArguments() != null) {
            Bundle bundle = getArguments();
            itemName.setText(bundle.getString("item"));
            customerName.setText(getArguments().getString("customer"));
            price.setText(getArguments().getString("price"));
            System.out.println("HEEEEEERE" + getArguments().getString("customer"));
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

            DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    date.setText(String.format("%d-%d-%d", dayOfMonth, (month+1), year));
                }
            }, year, month, day);
            datePickerDialog.show();
        }
        else if (v == timeButton) {
            //get time
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    time.setText(String.format("%d:%d", hourOfDay, minute));
                }
            }, hour, minute, true);
            timePickerDialog.show();
        }
    }

    public void displayData() {

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
