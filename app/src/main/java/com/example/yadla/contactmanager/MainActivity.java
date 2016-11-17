package com.example.yadla.contactmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int EDIT = 0, DELETE = 1;

    EditText etname;
    EditText etphonenumber;
    EditText etemail;
    EditText etaddress;
    Button btnContact;
    ImageView contactImage;
    private TextInputLayout name_layout, phone_number_layout, Email_layout, address_layout;
    //    Uri imageUri = Uri.parse("@mipmap/ic_no_user.png");
    Uri imageUri = Uri.parse("android.resource://com.example.yadla.contactmanager/mipmap/ic_no_user.png");

    DatabaseHandler dbHandler;
    int longClickedItemIndex;
    ArrayAdapter<Contact> contactAdapter;

    List<Contact> Contacts = new ArrayList<Contact>();
    ListView contactListView;

    Contact contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Vishwa's Contact Manager", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        InitUI();
    }

    private void InitUI() {
        etname = (EditText) findViewById(R.id.etname);
        etphonenumber = (EditText) findViewById(R.id.etphonenumber);
        etemail = (EditText) findViewById(R.id.etemail);
        etaddress = (EditText) findViewById(R.id.etaddress);
        name_layout = (TextInputLayout) findViewById(R.id.name_layout);
        phone_number_layout = (TextInputLayout) findViewById(R.id.phone_number_layout);
        Email_layout = (TextInputLayout) findViewById(R.id.Email_layout);
        address_layout = (TextInputLayout) findViewById(R.id.address_layout);
        contactListView = (ListView) findViewById(R.id.contactList);
        contactImage = (ImageView) findViewById(R.id.contactImage);
        contactImage.setOnClickListener(this);
        dbHandler = new DatabaseHandler(getApplicationContext());

        registerForContextMenu(contactListView);

        contactListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                longClickedItemIndex = position;
                return false;
            }
        });

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("Creator");
        tabSpec.setContent(R.id.tab1);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("List");
        tabSpec.setContent(R.id.tab2);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        btnContact = (Button) findViewById(R.id.btnContact);
        btnContact.setOnClickListener(this);

        etname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                btnContact.setEnabled(String.valueOf(etname.getText()).trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        ListCount();
    }

    public void ListCount() {
        if (dbHandler.getContactsCount() != 0)
            Contacts.addAll(dbHandler.getAllContacts());
        populateList();
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        menu.setHeaderIcon(R.mipmap.ic_pencil);
        menu.setHeaderTitle("Contact Options");
        menu.add(Menu.NONE, EDIT, menu.NONE, "Edit Contact");
        menu.add(Menu.NONE, DELETE, menu.NONE, "Delete Contact");
    }

    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case EDIT:
                break;
            case DELETE:
                dbHandler.deleteContact(Contacts.get(longClickedItemIndex));
                Contacts.remove(longClickedItemIndex);
                contactAdapter.notifyDataSetChanged();
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnContact: {
                submitForm();
                break;
            }
            case R.id.contactImage: {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Contact Image"), 1);
                break;
            }
        }
    }

    private void submitForm() {
        if (!validateName()) {
            return;
        } else if (!validatePhone()) {
            return;
        } else if (!validateEmail()) {
            return;
        } else if (!validateAddress()) {
            return;
        } else {
            Contact contact = new Contact(dbHandler.getContactsCount(), String.valueOf(etname.getText()), String.valueOf(etphonenumber.getText()), String.valueOf(etemail.getText()), String.valueOf(etaddress.getText()), imageUri);
            if (!contactExists(contact)) {
                dbHandler.createContact(contact);
                Contacts.add(contact);
                contactAdapter.notifyDataSetChanged();

                Toast.makeText(getApplicationContext(), String.valueOf(etname.getText() ) + " has been added to your contacts!", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(getApplicationContext(), String.valueOf(etname.getText()) + " already exits. Please use a different name.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean contactExists(Contact contact) {
        String name = contact.get_name();
        int contactCount = Contacts.size();

        for (int i = 0; i < contactCount; i++) {
            if (name.compareToIgnoreCase(Contacts.get(i).get_name()) == 0)
                return true;
        }
        return false;
    }

    private boolean validateName() {
        if (String.valueOf(etname.getText()).trim().isEmpty()) {
            name_layout.setError(getString(R.string.err_msg_name));
            requestFocus(etname);
            return false;
        } else {
            name_layout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validatePhone() {
        if (etphonenumber.getText().toString().trim().isEmpty()) {
            phone_number_layout.setError(getString(R.string.err_msg_phone));
//            Toast.makeText(getApplicationContext(),"Enter Phone Number",Toast.LENGTH_SHORT).show();
            requestFocus(etphonenumber);
            return false;
        } else {
            phone_number_layout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateEmail() {
        String email = etemail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            Email_layout.setError(getString(R.string.err_msg_email));
            requestFocus(etemail);
            return false;
        } else {
            Email_layout.setErrorEnabled(false);
        }
        return true;
    }

    private boolean validateAddress() {
        if (etaddress.getText().toString().trim().isEmpty()) {
            address_layout.setError(getString(R.string.err_msg_address));
            requestFocus(etaddress);
            return false;
        } else {
            address_layout.setErrorEnabled(false);
        }
        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int reqCode, int resCode, Intent data) {
        if (resCode == RESULT_OK) {
            if (reqCode == 1) {
                imageUri = data.getData();
                contactImage.setImageURI(data.getData());
            }
        }
    }

    private void populateList() {
        contactAdapter = new ContactListAdapter();
        contactListView.setAdapter(contactAdapter);
    }

    public class ContactListAdapter extends ArrayAdapter<Contact> {
        public ContactListAdapter() {
            super(MainActivity.this, R.layout.listview_item, Contacts);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.listview_item, parent, false);

            Contact currentContact = Contacts.get(position);

            TextView tvcontactname = (TextView) convertView.findViewById(R.id.tvcontactname);
            tvcontactname.setText(currentContact.get_name());

            TextView tvphone = (TextView) convertView.findViewById(R.id.tvphone);
            tvphone.setText(currentContact.get_phone());

            TextView tvemail = (TextView) convertView.findViewById(R.id.tvemail);
            tvemail.setText(currentContact.get_email());

            TextView tvaddress = (TextView) convertView.findViewById(R.id.tvaddress);
            tvaddress.setText(currentContact.get_address());

            ImageView ivcontactimage = (ImageView) convertView.findViewById(R.id.ivcontactimage);
            ivcontactimage.setImageURI(Uri.parse(String.valueOf(currentContact.get_imageUri())));

            return convertView;
        }
    }
}