package com.example.troye.customnavigationdrawer;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    private Toolbar topToolBar;
    private SelectedContactAdapter objAdapter;
    // Context context = null;
    private final Context context = this;
    private ListView lv = null;
    private EditText edtSearch = null;
    private boolean birthSort = false;


    // Messages for Handler
    public static final int MSG_UPDATE_ADAPTER = 0;
    public static final int MSG_CHANGE_ITEM = 1;
    public static final int MSG_DELETE_ANIMATION_REMOVE = 2;
    public static final int MSG_CALL_ANIMATION_REMOV = 3;

    // Messages for the context menu
    public static final int MSG_REMOVE_ITEM = 10;
    public static final int MSG_RENAME_ITEM = 11;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MSG_UPDATE_ADAPTER: // ListView updating
                    objAdapter.notifyDataSetChanged();
                    break;

                case MSG_DELETE_ANIMATION_REMOVE: // Start animation removing
                    View view = (View) msg.obj;
                    view.startAnimation(getDeleteAnimation(0, (msg.arg2 == 0) ? -view.getWidth() : 2 * view.getWidth(), msg.arg1));
                    break;

                case MSG_CALL_ANIMATION_REMOV: // Start animation removing
                    view = (View) msg.obj;
                    view.startAnimation(getCallAnimation(0, (msg.arg2 == 0) ? -view.getWidth() : 2 * view.getWidth(), msg.arg1));
                    break;
            }
        }
    };

    public Handler getHandler() {
        return handler;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        topToolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolBar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        topToolBar.setLogo(R.drawable.logo);
        topToolBar.setLogoDescription(getResources().getString(R.string.logo_desc));

        edtSearch = (EditText) findViewById(R.id.input_search2);

        edtSearch.setVisibility(View.GONE);

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

                String text = edtSearch.getText().toString()
                        .toLowerCase(Locale.getDefault());
                objAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        addContactsInList();
    }

    private void addContactsInList() {

        try {

            Cursor phones = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);

            try {
                ContactsListClass.displayList.clear();
            } catch (Exception e) {

            }

            while (phones.moveToNext()) {
                String phoneName = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String phoneImage = phones
                        .getString(phones
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

                SelectedContactObject cp = new SelectedContactObject();

                cp.setName(phoneName);
                cp.setNumber(phoneNumber);
                cp.setImage(phoneImage);

                ContactsListClass.displayList.add(cp);
            }
            phones.close();

            Collections.sort(ContactsListClass.displayList,
                    new Comparator<SelectedContactObject>() {
                        @Override
                        public int compare(SelectedContactObject lhs,
                                           SelectedContactObject rhs) {
                            return lhs.getName().compareTo(
                                    rhs.getName());
                        }
                    });

            objAdapter = new SelectedContactAdapter(MainActivity.this,
                    ContactsListClass.displayList);

            lv = (ListView) findViewById(R.id.contactList2);

            lv.setAdapter(objAdapter);

            final SwipeDetector swipeDetector = new SwipeDetector();
            lv.setOnTouchListener(swipeDetector);


            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    if (position == 0 || position == ContactsListClass.displayList.size())
                        return;
                    Message msg = new Message();
                    msg.arg1 = position;// If was detected swipe we delete an item
                    if (swipeDetector.swipeDetected()) {
                        if (swipeDetector.getAction() == SwipeDetector.Action.LR) {
                            msg.what = MSG_DELETE_ANIMATION_REMOVE;
                            msg.arg2 = swipeDetector.getAction() == SwipeDetector.Action.LR ? 1 : 0;
                            msg.obj = view;
                        }

                        if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                            msg.what = MSG_CALL_ANIMATION_REMOV;
                            msg.arg2 = swipeDetector.getAction() == SwipeDetector.Action.LR ? 1 : 0;
                            msg.obj = view;
                        }
                    }
                    // Otherwise, select an item
                    else
                        msg.what = MSG_CHANGE_ITEM;
                    handler.sendMessage(msg);

                }
            });

            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

/*                    SelectedContactAdapter adapter = (SelectedContactAdapter) lv.getAdapter();
                    adapter.deleteItem(position);
                    adapter.notifyDataSetChanged();*/
                    removeItem(position);

                    return true;
                }
            });

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    private void removeItem(int index) {
        ContactsListClass.displayList.remove(index);
        objAdapter.notifyDataSetChanged();
    }

    private Animation getDeleteAnimation(float fromX, float toX, int position) {
        Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
        animation.setStartOffset(100);
        animation.setDuration(800);
        animation.setAnimationListener(new DeleteAnimationListenter(position));
        animation.setInterpolator(AnimationUtils.loadInterpolator(this,
                android.R.anim.bounce_interpolator));
        return animation;
    }

    private Animation getCallAnimation(float fromX, float toX, int position) {
        Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
        animation.setStartOffset(100);
        animation.setDuration(800);
        animation.setAnimationListener(new CallAnimationListenter(position));
        animation.setInterpolator(AnimationUtils.loadInterpolator(this,
                android.R.anim.anticipate_overshoot_interpolator));
        return animation;
    }

    /**
     * Listenter used to remove an item after the animation has finished remove
     */
    public class DeleteAnimationListenter implements Animation.AnimationListener {
        private int position;

        public DeleteAnimationListenter(int position) {
            this.position = position;
        }

        @Override
        public void onAnimationEnd(Animation arg0) {

            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
            builder1.setMessage("Do you want to delete ?");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            removeItem(position);
                            dialog.cancel();
                        }
                    });
            builder1.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {


        }

        @Override
        public void onAnimationStart(Animation animation) {

        }
    }


    public class CallAnimationListenter implements Animation.AnimationListener {
        private int position;

        public CallAnimationListenter(int position) {
            this.position = position;
        }

        @Override
        public void onAnimationEnd(Animation arg0) {
            SelectedContactObject sob = (SelectedContactObject) lv.getItemAtPosition(position);
            String hotline = sob.getNumber().trim();
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + hotline));
            startActivity(callIntent);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {


        }

        @Override
        public void onAnimationStart(Animation animation) {

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_addContacts) {

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.option_alert_dialog);
            dialog.setTitle("Add new Contacts");
            ImageView image = (ImageView) dialog.findViewById(R.id.optImage);
            image.setImageResource(R.drawable.ic_action_add);
            TextView text = (TextView) dialog.findViewById(R.id.dialogTitle);
            text.setText("Chose any option");

            Button btnFromPhone = (Button) dialog.findViewById(R.id.dialogBtnFromPhone);
            Button btnAddNew = (Button) dialog.findViewById(R.id.dialogBtnAddNew);

            btnFromPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this, AddPhoneContact.class);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });

            btnAddNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this, AddNewContact.class);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });

            dialog.show();

            return true;
        }

        if (id == R.id.action_search) {

            if (birthSort) {
                edtSearch.setVisibility(View.VISIBLE);
                birthSort = false;
            } else {
                edtSearch.setVisibility(View.GONE);
                birthSort = true;
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
