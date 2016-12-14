package com.example.ti.address_book;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ti.address_book.data.DatabaseDescription.Contact;
/**
 * Created by ti on 30/07/2016.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    // call bck method implemented by the main activity
    public interface  DetailFragmentListener{
        void onContactDeleted(); // called when a contact is deleted

        // pass Uri of contact to the DetailFragmentListener
        void onEditContact(Uri contactUri);
    }
    private static final int CONTACT_LOADER = 0;
    private DetailFragmentListener listener; // mainActicity
    private Uri contactUri; // uri of selected contact

    // to display each of a particulars contact information
    private TextView nameTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView streetTextView;
    private TextView cityTextView;
    private TextView stateTextView;
    private TextView zipTextView;
    // set DetatilFragmentListener when fragment is attached
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
    }
    // remove detatilFragmentListener when fragment is detached
    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }
    // caleled when the DetailFragmentListener view needs to be created

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container ,Bundle savedInstanceState
    ){
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // this fragment has menu items to display
        //get bundle of arguments then extracts the contacts uri
        Bundle arguments = getArguments();

        if (arguments != null)
            contactUri = arguments.getParcelable(MainActivity.CONTACT_URI);
        // inflate DetailFragment's layout

        View view = inflater.inflate(R.layout.fragment_details,container,false);

        // get the EditTexts

        nameTextView = (TextView)view.findViewById(R.id.nametextView);
        phoneTextView = (TextView)view.findViewById(R.id.phonetextView);
        emailTextView = (TextView)view.findViewById(R.id.emailtextView);
        streetTextView = (TextView)view.findViewById(R.id.streettextView);
        cityTextView = (TextView)view.findViewById(R.id.citytextView);
        stateTextView  = (TextView)view.findViewById(R.id.statetextView);
        zipTextView = (TextView)view.findViewById(R.id.ziptextView);

        // load the contacts
        getLoaderManager().initLoader(CONTACT_LOADER,null,this);

        return view;

    }

    // display this fragments menu items
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_details_menu,menu);
    }
    // handle menu items selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch ((item.getItemId())){
            case R.id.action_edit:
                listener.onEditContact(contactUri); //pass uri to listener
                return true;
            case R.id.action_delete:
                deleteContact();
                return true;
        }
        return  super.onOptionsItemSelected(item);
    }
    //delete a contact
    private void  deleteContact(){
        // use fragment manager to display the confirmDelete DialogFragment
        confirmDelete.show(getFragmentManager(), "confirm delete");

    }

    // dialogFragment to confirm deletion of contact

    private final DialogFragment confirmDelete = new DialogFragment(){
        //  create an alert dialog and return it

        @Override
    public Dialog onCreateDialog(Bundle bundle){
            // create a new AlertDialog builder
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());

            builder.setTitle(R.string.confirm_title);
            builder.setMessage(R.string.confirm_message);

            // provide an ok button that simply discards the dialog

            builder.setPositiveButton(R.string.button_delete,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int button) {
                            // use the activity's content Ressolver to invoke delete on the AddressBookContentProovider

                            getActivity().getContentResolver().delete(contactUri, null, null);
                            listener.onContactDeleted();

                        }
                    }
            );
            // to cancel an alert dialog set the second parameter to null
            builder.setNegativeButton(R.string.button_cancel,null);
            return  builder.create(); // return the dialog
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // create an appropraite Cursorloader based on the id argument ;
        // only one loader in this fragment hence switch is unnecessary
        CursorLoader cursorLoader;
        switch (id){
            case CONTACT_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                        contactUri,
                        null, // null projection returns all columns
                        null,// null  selection returns all rows
                        null,// no selection arguments
                        Contact.COLUMN_NAME+ " COLLATE NOCASE ASC" ); // no sort order
                break;
            default:
                cursorLoader = null;
                break;
        }
        return cursorLoader;
    }
     // called by load manager when loader finishes
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // if contact exists in database display its data

        if (data != null && data.moveToFirst()){
            //get column index for each data item
            int nameIndex = data.getColumnIndex(Contact.COLUMN_NAME);
            int phoneIndex = data.getColumnIndex(Contact.COLUMN_PHONE);
            int emailIndex = data.getColumnIndex(Contact.COLUMN_EMAIL);
            int streetIndex = data.getColumnIndex(Contact.COLUMN_STREET);
            int cityIndex = data.getColumnIndex(Contact.COLUMN_CITY);
            int stateIndex = data.getColumnIndex(Contact.COLUMN_STATE);
            int zipIndex = data.getColumnIndex(Contact.COLUMN_ZIP);

            // fill each textViews with the retrieved data
            nameTextView.setText(data.getString(nameIndex));
            phoneTextView.setText(data.getString(phoneIndex));
            emailTextView.setText(data.getString(emailIndex));
            streetTextView.setText(data.getString(streetIndex));
            cityTextView.setText(data.getString(cityIndex));
            stateTextView.setText(data.getString(stateIndex));
            zipTextView.setText(data.getString(zipIndex));
        }

    }
    // called by the LoaderManager When loader is being reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
