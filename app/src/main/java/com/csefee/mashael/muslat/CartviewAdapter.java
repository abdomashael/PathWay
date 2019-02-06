package com.csefee.mashael.muslat;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Mashael on 20/2/18.
 */

public class CartviewAdapter extends RecyclerView.Adapter<CartviewAdapter.ViewHolder> {

    private int[] endtime, distance, price;
    private String[] startpoint, endpoint, starttime;
    private Listener listener;

    public CartviewAdapter(String[] startpoint, String[] starttime, String[] endpoint, int[] endtime, int[] distance, int[] price) {
        this.startpoint = startpoint;
        this.starttime = starttime;
        this.endpoint = endpoint;
        this.endtime = endtime;
        this.distance = distance;
        this.price = price;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public CartviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cv = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_caption, parent, false);
        return new ViewHolder(cv);
    }

    @Override
    public void onBindViewHolder(CartviewAdapter.ViewHolder holder, final int position) {
        CardView cardView = holder.cardView;

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        cardView.setBackgroundColor(color);
        cardView.setCardElevation(6);

        TextView startpointView = (TextView) cardView.findViewById(R.id.from_loc);
        startpointView.setText(String.valueOf(startpoint[position]));


        TextView endpointView = (TextView) cardView.findViewById(R.id.to_loc);
        endpointView.setText(endpoint[position]);

        TextView timeView = (TextView) cardView.findViewById(R.id.time_nu);
        timeView.setText(starttime[position]);

        TextView distanceView = (TextView) cardView.findViewById(R.id.distance_nu);
        distanceView.setText((String.valueOf(distance[position])));

        TextView priceView = (TextView) cardView.findViewById(R.id.money_nu);
        priceView.setText((String.valueOf(price[position])));


        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClick(position);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        if (startpoint != null)
            return startpoint.length;
        return 0;
    }

    public static interface Listener {
        public void onClick(int postion);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }
}
