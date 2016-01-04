package com.neelbedekar.android.quickshare;

import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.neelbedekar.android.quickshare.data.QuickshareContract;

/**
 * Created by Milind Bedekar on 12/31/2015.
 */
public class ContactListAdapter extends CursorAdapter {

    public ContactListAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public static class ViewHolder {
        private TextView nameTextView;
        private TextView phoneTextView;
        private ImageView contactImageView;
        public ViewHolder(View view) {
            nameTextView = (TextView) view.findViewById(R.id.list_item_name_textview);
            phoneTextView = (TextView) view.findViewById(R.id.list_item_phone_textview);
            contactImageView = (ImageView) view.findViewById(R.id.list_item_imageview);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_contact, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        String name = cursor.getString(cursor.getColumnIndex(QuickshareContract.ContactEntry.NAME));
        String phone = cursor.getString(cursor.getColumnIndex(QuickshareContract.ContactEntry.PHONE));
        byte [] image = cursor.getBlob(cursor.getColumnIndex(QuickshareContract.ContactEntry.IMAGE));
        viewHolder.nameTextView.setText(name);
        viewHolder.phoneTextView.setText(phone);
        if (image != null) {
            viewHolder.contactImageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
        }
        else {
            viewHolder.contactImageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.no_image));
        }
    }
}
