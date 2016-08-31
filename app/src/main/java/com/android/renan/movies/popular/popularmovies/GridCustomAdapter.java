package com.android.renan.movies.popular.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class GridCustomAdapter extends ArrayAdapter {
    private Context mContext;
    private List<String> mImages;
    private LayoutInflater inflater;

    public GridCustomAdapter(Context context, List<String> images) {
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
                .load(mImages.get(i))
                .fit()
                .into(imageView);

        return imageView;
    }
}
