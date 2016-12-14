package com.example.ti.address_book;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;
import android.view.LayoutInflater;

import com.example.ti.address_book.data.DatabaseDescription.Contact;

/**
 * Created by ti on 30/07/2016.
 */

public class ContactsAdapter
        extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    // interface implemented by contactFragment to respond
    //when the user touches an item in the Recyclerview
    private static final String TAG = "CURSOR";

    public interface  ContactClickListener{
        void onClick(Uri contactUri);
    }
    // nested subClass of recyclerView.ViewHolder used to implement
    //the view-holder pattern in the context of the recycler view

    public class  ViewHolder extends RecyclerView.ViewHolder{
        public final TextView textView;
        private long rowId;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(android.R.id.text1);

            // attach listener to itemview
            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        // executes when the contact in the view holder is clicked
                        @Override
                        public void onClick(View v) {
                            clickListener.onClick(Contact.buildContactUri(rowId));
                        }
                    }
            );
        }
        // set the database row id for the contact in this view holder
        public void setRowId(long rowId){
            this.rowId = rowId;
        }
    }
    // contactsadapter instance variable
    private Cursor cursor = null;
    private final ContactClickListener clickListener;

    // constructor

    public ContactsAdapter(ContactClickListener clickListener){
        this.clickListener = clickListener;
    }

    // sets up new list item and its ViewHolder

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the android.R.layout.simple_list_item_1 layout
        View view = LayoutInflater.from(parent.getContext()).inflate(
                android.R.layout.simple_list_item_1,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
           if (cursor != null && cursor.getCount() >0 && cursor.moveToFirst()){
        holder.setRowId(cursor.getLong(cursor.getColumnIndex(Contact._ID)));
        if(cursor.getString(cursor.getColumnIndex(Contact.COLUMN_NAME)) == null){
            Log.d(TAG, "Cursor not empty");
        }
        holder.textView.setText(cursor.getString(cursor.getColumnIndex(
                Contact.COLUMN_NAME)));
           }
    }
    // returns the number of items that adapter binds
    @Override
    public int getItemCount(){
        return (cursor != null) ? cursor.getCount(): 0;
    }
    // swap the adapters current cursor for a new one
    public void swapCursor (Cursor cursor){
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
