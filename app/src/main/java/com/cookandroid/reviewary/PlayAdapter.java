package com.cookandroid.reviewary;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookandroid.reviewary.R;

import java.util.ArrayList;

public class PlayAdapter extends RecyclerView.Adapter<PlayAdapter.ViewHolder> implements OnPlayClickListener {

    ArrayList<Play> items = new ArrayList<Play>();

    OnPlayClickListener listener;

    int layoutType = 0;

    @NonNull
    @Override
    public PlayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.single_item, viewGroup, false);

        return new ViewHolder(itemView, this, layoutType);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayAdapter.ViewHolder viewHolder, int position) {
        Play item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(Play item) {
        items.add(item);
    }

    public void setItems(ArrayList<Play> items) {
        this.items = items;
    }

    public Play getItem(int position) {
        return items.get(position);
    }

    public void setOnPlayClickListener(OnPlayClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    @Override
    public void onItemLongClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemLongClick(holder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout outerLayout;

        ImageView mainImageView;
        TextView titleTextView;
        RatingBar ratingBar;
        TextView dateTextView;

        public ViewHolder(View itemView, final OnPlayClickListener listener, int layoutType) {
            super(itemView);

            outerLayout = itemView.findViewById(R.id.outerLayout);

            mainImageView = itemView.findViewById(R.id.mainImageView);

            titleTextView = itemView.findViewById(R.id.titleTextView);

            ratingBar = itemView.findViewById(R.id.ratingBar);

            dateTextView = itemView.findViewById(R.id.dateTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();

                    if(listener != null) {
                        listener.onItemLongClick(ViewHolder.this, view, position);
                    }
                    return true;
                }
            });
        }

        public void setItem(Play item) {

            titleTextView.setText(item.getName());

            String picturePath = item.getImage();
            AppConstants.println("picturePath -> " + picturePath);

            if (picturePath != null && !picturePath.equals("")) {
                mainImageView.setVisibility(View.VISIBLE);
                mainImageView.setImageURI(Uri.parse("file://" + picturePath));

            } else {
                mainImageView.setImageResource(R.drawable.noimagefound);
            }

            String ratedPoint = item.getRating();
            setRatingBar(ratedPoint);

            dateTextView.setText(item.getDate());
        }

        public void setRatingBar(String ratedPoint) {
            ratingBar.setRating(Float.parseFloat(ratedPoint));
            ratingBar.setIsIndicator(true);
        }
    }
}
