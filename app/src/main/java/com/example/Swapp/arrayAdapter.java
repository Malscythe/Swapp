package com.example.Swapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;

import org.w3c.dom.Text;

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

        ImageView imageView = convertView.findViewById(R.id.itemImage);
        TextView item_Name = convertView.findViewById(R.id.itemName);
        TextView item_Location = convertView.findViewById(R.id.itemLocation);
        TextView item_Description = convertView.findViewById(R.id.itemDescription);
        TextView item_Pref =  convertView.findViewById(R.id.itemPref);
        TextView item_RFT =  convertView.findViewById(R.id.itemRFT);
        TextView seeMore = convertView.findViewById(R.id.seeMore);
        TextView descriptinHeader = convertView.findViewById(R.id.itemDescriptionHeader);
        TextView posterName = convertView.findViewById(R.id.posterName);

        Glide.with(imageView.getContext()).load(card_item.getImageUrls()).into(imageView);
        item_Name.setText(card_item.getItem_Name());
        item_Location.setText(card_item.getItem_City().concat(", " + card_item.getItem_State()));
        item_Pref.setText(card_item.getItem_Preferred());
        posterName.setText(card_item.getPoster_Name());

        if (card_item.getItem_Category().equals("Groceries")) {
            item_Description.setVisibility(View.GONE);
            descriptinHeader.setText("List of items:");
        } else {
            if (card_item.getItem_Description().length() < 50) {
                item_Description.setText(card_item.getItem_Description());
                seeMore.setVisibility(View.GONE);
            } else {
                item_Description.setText(card_item.getItem_Description().substring(0, 50));
                seeMore.setVisibility(View.VISIBLE);
            }
        }


        item_RFT.setText(card_item.getItem_RFT());
        return convertView;
    }
}
