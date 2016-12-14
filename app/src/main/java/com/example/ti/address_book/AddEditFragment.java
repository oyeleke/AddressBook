package com.example.ti.address_book;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.example.ti.address_book.data.DatabaseDescription.Contact;

/**
 * Created by ti on 30/07/2016.
 */
public class AddEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // defines call back method implemented by the mainActivity
    public interface AddEditFragmentListener{
        // this is called when the contact is saved
        void onAddEditCompleted(Uri contactUri);
    }


    private static final int CONTACT_lOADER = 0;
    private AddEditFragmentListener listener; // main acticity
    private Uri contactUri; // uri of selected contact
    private boolean addingNewContact = true;

    // edit text for contacts information
    private TextInputLayout nameTextInputLayout;
    private TextInputLayout phoneTextInputLayout;
    private TextInputLayout emailTextInputLayout;
    private TextInputLayout streetTextInputLayout;
    private TextInputLayout cityTextInputLayout;
    private TextInputLayout stateTextInputLayout;
    private TextInputLayout zipTextInputLayout;
    private FloatingActionButton saveContactFab;

    private CoordinatorLayout coordinatorLayout ;// used with snackbars

    // set AddEditFragmentListener when fragment is attached;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        listener = (AddEditFragmentListener)context ;
    }
    // remove edit fragment listener when the fragment is detached

    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }

    // called when the fragments view needs to be created

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        setHasOptionsMenu(true); // fragment has items to display

        // inflate the gui and get references to edit text
        View view = inflater.inflate(R.layout.fragment_add_edit , container , false);

        nameTextInputLayout = (TextInputLayout) view.findViewById(R.id.nameTextInputLayout);
        try {
            nameTextInputLayout.getEditText().addTextChangedListener(nameChangedListener);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        phoneTextInputLayout = (TextInputLayout)view.findViewById(R.id.phoneTextInputLayout);
        emailTextInputLayout = (TextInputLayout)view.findViewById(R.id.emailTextInputLayout);
        streetTextInputLayout = (TextInputLayout)view.findViewById(R.id.streetTextInputLayout);
        cityTextInputLayout = (TextInputLayout)view.findViewById(R.id.cityTextInputLayout);
        stateTextInputLayout = (TextInputLayout)view.findViewById(R.id.stateTextInputLayout);
        zipTextInputLayout= (TextInputLayout)view.findViewById(R.id.zipTextInputLayout);

        // set Floating action button's event listener
        saveContactFab =(FloatingActionButton)view.findViewById(R.id.saveFloatingActionButton);

        saveContactFab.setOnClickListener(saveContactButtonClicked);
        updateSaveButtonFAB();

        // used to display snackBars with brief messages
        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorlayout);

        Bundle arguments = getArguments(); // null if creating new contact

        if(arguments != null)
            getLoaderManager().initLoader(CONTACT_lOADER,null,this);

        return view;
    }

    // detects when the text in the name text inpput layout's edit text changes
    // to decide wether to hide or show save button Fab

    private final TextWatcher nameChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
// called when the text in the nameTextInputLayoutchanges
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateSaveButtonFAB();

        }

        @Override
        public void afterTextChanged(Editable s) {}
    };
    // shows saveButtonFAB only if the name is not empty
    private void updateSaveButtonFAB(){
        String input = nameTextInputLayout.getEditText().getText().toString();

        // if there is a name for the contact show the floating action Button

        if (input.trim().length() != 0)
            saveContactFab.show();
        else
            saveContactFab.hide();
    }

    // responds to event generated when the user saves a contact

    private final View.OnClickListener saveContactButtonClicked =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 // to  hide the virtul keyboard
                    ((InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(getView().getWindowToken(),0);
                    saveContact();
                }
            };

    // to save the contacts information into a database
    private void saveContact(){
        // create contentValue objects containing contacts key value pairs

        ContentValues contentValues = new ContentValues();
           contentValues.put(Contact.COLUMN_NAME, nameTextInputLayout.getEditText().getText().toString());
           contentValues.put(Contact.COLUMN_PHONE, phoneTextInputLayout.getEditText().getText().toString());
           contentValues.put(Contact.COLUMN_EMAIL, emailTextInputLayout.getEditText().getText().toString());
           contentValues.put(Contact.COLUMN_STREET, streetTextInputLayout.getEditText().getText().toString());
           contentValues.put(Contact.COLUMN_CITY, cityTextInputLayout.getEditText().getText().toString());
           contentValues.put(Contact.COLUMN_STATE, stateTextInputLayout.getEditText().getText().toString());
           contentValues.put(Contact.COLUMN_ZIP, zipTextInputLayout.getEditText().getText().toString());


        if(addingNewContact){
            // use Activity's content resolver to invoke insert on the AddressBookContentProvider

            Uri newContactUri = getActivity().getContentResolver().insert(Contact.CONTENT_URI, contentValues); // where insertion of a new contact takes place

            if(newContactUri != null) {
                Snackbar.make(coordinatorLayout, R.string.contact_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newContactUri);

            }
            else{
                    Snackbar.make(coordinatorLayout, R.string.contact_not_added, Snackbar.LENGTH_LONG).show();
            }
        }
        else{
            // use Activity's contentResolver to invoke update on the AddressBookContentProvider
            // this is to update

            int updatedRows = getActivity().getContentResolver().update(contactUri, contentValues,null, null);
             // updateRows indicates the specific number of the row updated
            if(updatedRows > 0 ){
                listener.onAddEditCompleted(contactUri);
                Snackbar.make(coordinatorLayout, R.string.contact_updated, Snackbar.LENGTH_LONG).show();
            }
            else{
                Snackbar.make(coordinatorLayout, R.string.contact_not_updated,Snackbar.LENGTH_LONG).show();
            }

        }
    }



    // called by LoaderManager to create a loader
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        //create an appropriate loadder cursor based on the id argument;
        //only one loader in this fragment , so the switch is unnecessary

        switch(id){
            case CONTACT_lOADER:
                return new CursorLoader(getActivity(),
                contactUri, // uri of ccontact to display,
                null ,// null projection returns all columns
                null, // null selection returns all rows
                null, // no selection arguments
                null); // no sort order

            default:
                return null;
        }
    }
    // called by loader manager when loading compiles
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //if contact exists in database display its data
        if(data != null && data.moveToFirst()){
            // get column index for each data item

            int nameIndex = data.getColumnIndex(Contact.COLUMN_NAME);
            int phoneIndex = data.getColumnIndex(Contact.COLUMN_PHONE);
            int emailIndex = data.getColumnIndex(Contact.COLUMN_EMAIL);
            int streetIndex = data.getColumnIndex(Contact.COLUMN_STREET);
            int cityIndex = data.getColumnIndex(Contact.COLUMN_CITY);
            int stateIndex = data.getColumnIndex(Contact.COLUMN_STATE);
            int zipIndex = data.getColumnIndex(Contact.COLUMN_ZIP);

            // fill edit text with the retrieved data

            nameTextInputLayout.getEditText().setText(data.getString(nameIndex));
            phoneTextInputLayout.getEditText().setText(data.getString(phoneIndex));
            emailTextInputLayout.getEditText().setText(data.getString(emailIndex));
            streetTextInputLayout.getEditText().setText(data.getString(streetIndex));
            cityTextInputLayout.getEditText().setText(data.getString(cityIndex));
            stateTextInputLayout.getEditText().setText(data.getString(stateIndex));
            zipTextInputLayout.getEditText().setText(data.getString(zipIndex));
            updateSaveButtonFAB();
        }

    }
// called by loader manager when loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
