package com.example.troye.customnavigationdrawer;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Troye on 6/21/2015.
 */
public class SelectedContactAdapter extends BaseAdapter {

    Context mContext;
    LayoutInflater inflater;
    private List<SelectedContactObject> mainDataList = null;
    private ArrayList<SelectedContactObject> arraylist;

    public SelectedContactAdapter(Context context, List<SelectedContactObject> mainDataList) {

        mContext = context;
        this.mainDataList = mainDataList;
        inflater = LayoutInflater.from(mContext);
        this.arraylist = new ArrayList<SelectedContactObject>();
        this.arraylist.addAll(mainDataList);
    }

    static class ViewHolder {
        protected TextView name;
        protected TextView number;
        protected ImageView image;
        protected ImageView imgBtnPopUp;
    }

    @Override
    public int getCount() {
        return mainDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mainDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;

        if (view == null) {

            holder = new ViewHolder();

            view = inflater.inflate(R.layout.selected_contact_list, null);

            holder.name = (TextView) view.findViewById(R.id.contactname);

            holder.number = (TextView) view.findViewById(R.id.contactno);

            holder.image = (ImageView) view.findViewById(R.id.contactimage);

            holder.imgBtnPopUp = (ImageView) view.findViewById(R.id.btnPopUp);

            view.setTag(holder);
            view.setTag(R.id.contactname, holder.name);
            view.setTag(R.id.contactno, holder.number);
            view.setTag(R.id.btnPopUp, holder.imgBtnPopUp);

            holder.imgBtnPopUp.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    PopupMenu popup = new PopupMenu(mContext, holder.imgBtnPopUp);

                    popup.getMenuInflater().inflate(R.menu.menu_list_popup, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            Toast.makeText(mContext, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    });

                    popup.show();

                }
            });

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.name.setText(mainDataList.get(position).getName());
        holder.number.setText(mainDataList.get(position).getNumber());

        if (getByteContactPhoto(mainDataList.get(position).getImage()) == null) {
            holder.image.setImageResource(R.drawable.ic_dimage);
        } else {
            holder.image.setImageBitmap(getByteContactPhoto(mainDataList.get(position).getImage()));
        }

        return view;
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        mainDataList.clear();
        if (charText.length() == 0) {
            mainDataList.addAll(arraylist);
        } else {
            for (SelectedContactObject wp : arraylist) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    mainDataList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    public Bitmap getByteContactPhoto(String contactId) {

        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = mContext.getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.DATA15}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return BitmapFactory.decodeStream(new ByteArrayInputStream(data));
                }
            }
        } finally {
            cursor.close();
        }

        return null;
    }

/*
    public void deleteItem(int pos) {
        mainDataList.remove(pos);
        notifyDataSetChanged();
    }*/
}
