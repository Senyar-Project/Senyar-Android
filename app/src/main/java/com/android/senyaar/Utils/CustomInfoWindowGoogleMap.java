package com.android.senyaar.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.android.senyaar.Activities.SignInActivity;
import com.android.senyaar.Model.InfoWindowData;
import com.android.senyaar.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {
    private OnInfoWindowElemTouchListener infoButtonListener;
    private Context context;

    public CustomInfoWindowGoogleMap(Context ctx) {
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity) context).getLayoutInflater()
                .inflate(R.layout.custom_marker_dialog, null);

        TextView name_tv = view.findViewById(R.id.rider_name);
        TextView details_tv = view.findViewById(R.id.rider_info);

        TextView rating_tv = view.findViewById(R.id.tv_rating);
        RatingBar rating_bar = view.findViewById(R.id.ratingBar);

        TextView book_tv = view.findViewById(R.id.tv_book);
        TextView contact_tv = view.findViewById(R.id.tv_contact);

        // Setting custom OnTouchListener which deals with the pressed state
        // so it shows up
        this.infoButtonListener = new OnInfoWindowElemTouchListener(book_tv){
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                Intent intent = new Intent(context, SignInActivity.class);
                context.startActivity(intent);
            }
        };

        book_tv.setOnTouchListener(infoButtonListener);

        name_tv.setText(marker.getTitle());
        details_tv.setText(marker.getSnippet());

        InfoWindowData infoWindowData = (InfoWindowData) marker.getTag();


        name_tv.setText(infoWindowData.getRider_name());
        details_tv.setText(infoWindowData.getRider_info());
        rating_tv.setText(infoWindowData.getRating());
        rating_bar.setNumStars(Integer.parseInt(infoWindowData.getRating()));

        return view;
    }
}
