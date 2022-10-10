package com.example.Swapp;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import Swapp.R;

public class arrayAdapter extends ArrayAdapter<cards> {

    Context context;

    public arrayAdapter(Context context, int resourceId, List<cards> items) {
        super(context, resourceId, items);
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        cards card_item = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        ImageView item_Image = (ImageView) convertView.findViewById(R.id.itemImage);
//        TextView item_Name = (TextView) convertView.findViewById(R.id.itemName);
//        TextView item_Poster = (TextView) convertView.findViewById(R.id.posterName);
//        TextView item_Location = (TextView) convertView.findViewById(R.id.itemLocation);
//        TextView item_Pref = (TextView) convertView.findViewById(R.id.itemPref);

        Glide.with(getContext()).load(card_item.getImage_Url()).into(item_Image);
//        item_Name.setText(card_item.getItem_Name());
//        item_Poster.setText(card_item.getPoster_Name());
//        item_Location.setText(card_item.getItem_Location());
//        item_Pref.setText(card_item.getItem_Preferred());

        return convertView;
    }
}
