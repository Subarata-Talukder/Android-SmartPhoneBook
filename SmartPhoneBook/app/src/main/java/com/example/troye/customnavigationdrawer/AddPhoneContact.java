package com.example.troye.customnavigationdrawer;

import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class AddPhoneContact extends AppCompatActivity {

    Context context = null;
    ContactsAdapter objAdapter;
    ListView lv = null;
    EditText edtSearch = null;
    LinearLayout llContainer = null;
    Button btnOK = null;
    RelativeLayout rlPBContainer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_add_phone_contact);

        rlPBContainer = (RelativeLayout) findViewById(R.id.pbcontainer);

        edtSearch = (EditText) findViewById(R.id.input_search);
        llContainer = (LinearLayout) findViewById(R.id.data_container);
        btnOK = (Button) findViewById(R.id.ok_button);

        btnOK.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                getSelectedContacts();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2,
                                      int arg3) {
                // When user changed the Text
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

    private void getSelectedContacts() {

        StringBuffer sb = new StringBuffer();

        for (ContactObject bean : ContactsListClass.phoneList) {

            if (bean.isSelected()) {

                ContactsListClass.selectedItems.add(bean);
                if (bean.isSelected()) {
                    sb.append(bean.getName());
                    sb.append(",");
                }

            }
        }

        String s = sb.toString().trim();

        if (TextUtils.isEmpty(s)) {
            Toast.makeText(context, "Select at least one Contact",
                    Toast.LENGTH_SHORT).show();
        } else {

            new AlertDialog.Builder(this)
                    .setTitle("Add Contacts")
                    .setMessage("Are you sure you want to add selected contacts?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            for (int i = 0; i <= ContactsListClass.selectedItems.size() - 1; i++)
                                System.out.println("===================" + ContactsListClass.selectedItems.get(i).getName());

                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

        }

    }

    private void addContactsInList() {

        Thread thread = new Thread() {
            @Override
            public void run() {

                showPB();

                try {

                    Cursor phones = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, null, null, null);

                    try {
                        ContactsListClass.phoneList.clear();
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

                        ContactObject cp = new ContactObject();

                        cp.setName(phoneName);
                        cp.setNumber(phoneNumber);
                        cp.setImage(phoneImage);

                        ContactsListClass.phoneList.add(cp);

                    }
                    phones.close();

                    lv = new ListView(context);
                    lv.setLayoutParams(new LayoutParams(
                            LayoutParams.MATCH_PARENT,
                            LayoutParams.MATCH_PARENT));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            llContainer.addView(lv);
                        }
                    });

                    Collections.sort(ContactsListClass.phoneList,
                            new Comparator<ContactObject>() {
                                @Override
                                public int compare(ContactObject lhs,
                                                   ContactObject rhs) {
                                    return lhs.getName().compareTo(
                                            rhs.getName());
                                }
                            });

                    objAdapter = new ContactsAdapter(AddPhoneContact.this,
                            ContactsListClass.phoneList);

                    lv.setAdapter(objAdapter);
                    lv.setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {

                            CheckBox chk = (CheckBox) view
                                    .findViewById(R.id.contactcheck);
                            ContactObject bean = ContactsListClass.phoneList
                                    .get(position);
                            if (bean.isSelected()) {
                                bean.setSelected(false);
                                chk.setChecked(false);
                            } else {
                                bean.setSelected(true);
                                chk.setChecked(true);
                            }
                        }
                    });

                } catch (Exception e) {

                    e.printStackTrace();

                }

                hidePB();
            }
        };
        thread.start();
    }

    void showPB() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                rlPBContainer.setVisibility(View.VISIBLE);
                edtSearch.setVisibility(View.GONE);
                btnOK.setVisibility(View.GONE);
            }
        });
    }

    void hidePB() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                rlPBContainer.setVisibility(View.GONE);
                edtSearch.setVisibility(View.VISIBLE);
                btnOK.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_phone_contact, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_addContacts) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}



