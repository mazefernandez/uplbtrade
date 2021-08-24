package com.mazefernandez.uplbtrade.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mazefernandez.uplbtrade.R;

/* Finalize meetup details */

public class ConfirmMeetUpFragment extends Fragment implements View.OnClickListener {
    private Button confirm;

    public ConfirmMeetUpFragment() {

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_meet_up, container, false);

        /* Meetup views */
        TextView buyer = view.findViewById(R.id.buyer);
        TextView item = view.findViewById(R.id.item);
        TextView price = view.findViewById(R.id.price);
        TextView offer = view.findViewById(R.id.offer);
        TextView date = view.findViewById(R.id.date);
        TextView time = view.findViewById(R.id.time);
        TextView venue = view.findViewById(R.id.venue);

        /* Get Offer Data */
        if (getArguments() != null) {
            Bundle confirmBundle = getArguments();
            Bundle bundle = confirmBundle.getBundle("offer");

            assert bundle != null;
            item.setText(bundle.getString("item"));
            buyer.setText(bundle.getString("customer"));
            price.setText(bundle.getString("price"));
            offer.setText(bundle.getString("offer"));
            date.setText(confirmBundle.getString("date"));
            time.setText(confirmBundle.getString("time"));
            venue.setText(confirmBundle.getString("venue"));
        }

        confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
     if (v == confirm) {
            // TODO: Add Retrofit POST Transaction Call
            // TODO: Divert to Profile Activity
        }
    }
}
