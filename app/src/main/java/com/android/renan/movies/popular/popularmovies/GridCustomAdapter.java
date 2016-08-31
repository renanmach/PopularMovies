package com.android.renan.movies.popular.popularmovies;

import android.app.ActionBar;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class GridCustomAdapter extends ArrayAdapter {
    private Context mContext;
    private String[] mImages;
    private LayoutInflater inflater;

    public GridCustomAdapter(Context context, String[] images) {
        super(context, R.layout.listview_item_image, images);
        mContext = context;
        mImages = images;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if(view == null){
            imageView = (ImageView) inflater.inflate(R.layout.listview_item_image, viewGroup, false);
        }
        else{
            imageView = (ImageView) view;
        }

        Picasso.with(mContext)
                .load(mImages[i])
                .fit()
                .into(imageView);

        return imageView;
    }
}
