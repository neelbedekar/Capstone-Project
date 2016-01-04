package com.neelbedekar.android.quickshare;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.neelbedekar.android.quickshare.Callback;

import com.neelbedekar.android.quickshare.data.QuickshareContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private ContactListAdapter contactListAdapter;
    private ListView contactList;
    private int position = ListView.INVALID_POSITION;
    private EditText searchText;

    private final int LOADER_ID = 10;

    public ContactListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_contact_list, container, false);
        final Cursor cursor = getActivity().getContentResolver().query(
                QuickshareContract.ContactEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        final ImageButton searchButton = (ImageButton) rootView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartLoader();
                hideKeyboard(getActivity(), searchButton.getWindowToken());
            }
        });
        contactListAdapter = new ContactListAdapter(getActivity(), cursor, 0);
        searchText = (EditText) rootView.findViewById(R.id.searchText);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                restartLoader();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        contactList = (ListView) rootView.findViewById(R.id.contacts_list);
        contactList.setAdapter(contactListAdapter);
        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = contactListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity()).onItemSelected(cursor.getString(
                            cursor.getColumnIndex(QuickshareContract.ContactEntry._ID)));
                    restartLoader();
                }
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String searchString = searchText.getText().toString();
        final String selection =  QuickshareContract.ContactEntry.NAME + " LIKE ? ";
        if (searchString.length() > 0) {
            searchString = "%" + searchString + "%";
            return new CursorLoader(
                    getActivity(),
                    QuickshareContract.ContactEntry.CONTENT_URI,
                    null,
                    selection,
                    new String[]{searchString},
                    null
            );
        }
        else {
            return new CursorLoader(
                    getActivity(),
                    QuickshareContract.ContactEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        contactListAdapter.swapCursor(data);
        if (position != ListView.INVALID_POSITION) {
            contactList.smoothScrollToPosition(position);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        contactListAdapter.swapCursor(null);
    }



    private void restartLoader() {
        getLoaderManager().restartLoader(LOADER_ID, null, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        restartLoader();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;
        if (context instanceof Activity) {
            a = (Activity) context;
            a.setTitle(getString(R.string.title_fragment_contacts_list));
        }
    }

    public void hideKeyboard(Activity activity, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }
}
