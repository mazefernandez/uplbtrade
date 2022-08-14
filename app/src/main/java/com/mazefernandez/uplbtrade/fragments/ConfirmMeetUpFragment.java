package com.mazefernandez.uplbtrade.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.mazefernandez.uplbtrade.R;
import com.mazefernandez.uplbtrade.UPLBTrade;
import com.mazefernandez.uplbtrade.activities.HomeActivity;
import com.mazefernandez.uplbtrade.activities.ProfileActivity;
import com.mazefernandez.uplbtrade.activities.TransactionActivity;
import com.mazefernandez.uplbtrade.activities.TransactionsActivity;
import com.mazefernandez.uplbtrade.models.Transaction;

import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/* Finalize meetup details */

public class ConfirmMeetUpFragment extends Fragment implements View.OnClickListener {
    private Button confirm;
    private int itemId;
    private int offerId;
    private int sellerId;
    private int buyerId;
    String dateSql;
    String sqlTime;
    String stringVenue;

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
            itemId = bundle.getInt("item_id");
            offerId = bundle.getInt("offer_id");
            sellerId = bundle.getInt("seller_id");
            buyerId = bundle.getInt("buyer_id");
        }

        dateSql = date.getText().toString();
        sqlTime = time.getText().toString();
        stringVenue = venue.getText().toString();

        confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
     if (v == confirm) {
         Transaction transaction = new Transaction(dateSql, sqlTime, stringVenue, itemId, offerId, sellerId, buyerId);

         UPLBTrade.retrofitClient.addTransaction(new Callback<Transaction>() {
             @Override
             public void onResponse(@NonNull Call<Transaction> call, @NonNull Response<Transaction> response) {
                 System.out.println("Added Transaction");
                 System.out.println(response.body());
             }

             @Override
             public void onFailure(@NonNull Call<Transaction> call, @NonNull Throwable t) {
                 System.out.println("Failed to add transaction");
                 System.out.println(t.getMessage());
             }
         }, transaction);

         Context context = getActivity();
         CharSequence text = "Meet up confirmed!";
         int duration = Toast.LENGTH_SHORT;

         Toast toast = Toast.makeText(context, text, duration);
         toast.show();

         Intent intent = new Intent(getActivity(), TransactionsActivity.class);
         startActivity(intent);
        }
    }
}
