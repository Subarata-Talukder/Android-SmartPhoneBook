package com.example.troye.customnavigationdrawer;

import android.content.DialogInterface;
import android.content.Intent;
import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Troye on 6/24/2015.
 */
public class AddNewContact extends AppCompatActivity {

    Spinner spinner;
    ImageView contactimage;
    private int imagerequest = 1;
    String imageDecode;


    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_contact);

        spinner = (Spinner) findViewById(R.id.groupselect);
        contactimage = (ImageView) findViewById(R.id.profile_image);
        Spinnermethod();
        contactimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();

            }
        });
    }

    public void selectImage() {

        final CharSequence[] items = {"Take Photo", "From Gallery",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewContact.this);
        builder.setTitle("Add Photo!");
        builder.setCancelable(true);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }

                } else if (items[item].equals("From Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alert11 = builder.create();
        alert11.show();
    }

    //spinner
    public void Spinnermethod() {
        String[] spinnertext = getResources().getStringArray(R.array.spinnertext);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < spinnertext.length; i++) {
            list.add(spinnertext[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        spinner.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

            contactimage.setImageBitmap(bitmap);
        }

        if (requestCode == SELECT_FILE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                contactimage.setRotation(Float.parseFloat("0"));
                contactimage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /*try {
            if (requestCode == imagerequest && null!=data && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String[] filepath = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, filepath, null, null, null);
                cursor.moveToFirst();
                int colomindex = cursor.getColumnIndex(filepath[0]);
                imageDecode = cursor.getString(colomindex);
                cursor.close();
                contactimage.setImageBitmap(BitmapFactory.decodeFile(imageDecode));
            } else {
                Toast.makeText(getApplicationContext(), "having problem", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "something problem", Toast.LENGTH_SHORT).show();
        }*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addContacts) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
