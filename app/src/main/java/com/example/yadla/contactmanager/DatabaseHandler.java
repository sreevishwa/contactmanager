package com.example.yadla.contactmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ACER on 11/12/2016.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "ContactManager.db";
    private static final String TABEL_CONTACTS = "contacts";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_IMAGEURI = "imageUri";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + TABEL_CONTACTS + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NAME + " TEXT," + KEY_PHONE + " TEXT,"
                + KEY_EMAIL + " TEXT," + KEY_ADDRESS + " TEXT, " + KEY_IMAGEURI + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXITST " + TABEL_CONTACTS);
        onCreate(sqLiteDatabase);

    }

    public void createContact(Contact contact) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_NAME, contact.get_name());
        values.put(KEY_PHONE, contact.get_phone());
        values.put(KEY_EMAIL, contact.get_email());
        values.put(KEY_ADDRESS, contact.get_address());
        values.put(KEY_IMAGEURI, contact.get_imageUri().toString());

        sqLiteDatabase.insert(TABEL_CONTACTS, null, values);
        sqLiteDatabase.close();
    }

    public Contact getContact(int id) {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(TABEL_CONTACTS, new String[]{KEY_ID, KEY_NAME, KEY_PHONE, KEY_EMAIL, KEY_ADDRESS, KEY_IMAGEURI},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Contact contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5)));
        sqLiteDatabase.close();
        cursor.close();
        return contact;
    }

    public void deleteContact(Contact contact) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.delete(TABEL_CONTACTS, KEY_ID + "=?", new String[]{String.valueOf(contact.get_id())});
        sqLiteDatabase.close();
    }

    public int getContactsCount(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABEL_CONTACTS, null);

        int count = cursor.getCount();
        sqLiteDatabase.close();
        cursor.close();

        return count;
    }

   public int updateContact(Contact contact){
       SQLiteDatabase sqLiteDatabase = getWritableDatabase();

       ContentValues values = new ContentValues();

       values.put(KEY_NAME, contact.get_name());
       values.put(KEY_PHONE, contact.get_phone());
       values.put(KEY_EMAIL, contact.get_email());
       values.put(KEY_ADDRESS, contact.get_address());
       values.put(KEY_IMAGEURI, contact.get_imageUri().toString());

       int rowsAffected = sqLiteDatabase.update(TABEL_CONTACTS,values,KEY_ID + "=?", new String[]{String.valueOf(contact.get_id())});
        sqLiteDatabase.close();
       return rowsAffected;
   }

    public List<Contact> getAllContacts(){
        List<Contact> contacts = new ArrayList<Contact>();

        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + TABEL_CONTACTS, null);

        if (cursor.moveToFirst()){
            do {
                 contacts.add(new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), Uri.parse(cursor.getString(5))));

            }while (cursor.moveToNext());
        }
        cursor.close();
        sqLiteDatabase.close();
        return contacts;
    }
}