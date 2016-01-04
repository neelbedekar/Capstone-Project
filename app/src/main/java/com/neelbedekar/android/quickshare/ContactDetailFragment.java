package com.neelbedekar.android.quickshare;


import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.neelbedekar.android.quickshare.data.QuickshareContract;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static String ID_KEY = "ID";
    private String id_string;
    private View rootView;
    private final int LOADER_ID = 20;
    private double latitude;
    private double longitude;
    private ShareActionProvider shareActionProvider;

    public ContactDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_contact_detail, container, false);
        hideKeyboard(getActivity(), rootView.getWindowToken());
        Bundle args = getArguments();
        if (args != null) {
            id_string = args.getString(ID_KEY);
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
        final ImageView deleteContactImageView = (ImageView) rootView.findViewById(R.id.delete_button);
        deleteContactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact();
            }
        });
        TextView deleteTextView = (TextView) rootView.findViewById(R.id.delete_text);
        deleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact();
            }
        });
        Button mapButton = (Button) rootView.findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact_detail, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    public void deleteContact() {
        Uri uri = QuickshareContract.ContactEntry.buildContactUri(Long.parseLong(id_string));
        getActivity().getContentResolver().delete(uri, null, null);
        getActivity().getContentResolver().notifyChange(uri, null);
        Bundle args = new Bundle();
        args.putBoolean(id_string, true);
        getLoaderManager().restartLoader(LOADER_ID, args, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (args != null) {
            if (!MainActivity.isTwoPane) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
            else {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(this).commit();
            }
            return null;
        }
        return new CursorLoader(
                getActivity(),
                QuickshareContract.ContactEntry.buildContactUri(Long.parseLong(id_string)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader != null) {
            if(shareActionProvider!=null) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.NAME)) + " : " +
                                data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.PHONE)));
                shareActionProvider.setShareIntent(shareIntent);

            }
            TextView nameTextView = (TextView) rootView.findViewById(R.id.detail_name);
            ImageView contact_imageView = (ImageView) rootView.findViewById(R.id.detail_image);
            TextView phoneTextView = (TextView) rootView.findViewById(R.id.detail_phone_text);
            TextView emailTextView = (TextView) rootView.findViewById(R.id.detail_email_text);
            TextView addressTextView = (TextView) rootView.findViewById(R.id.detail_address_text);
            TextView facebookTextView = (TextView) rootView.findViewById(R.id.detail_facebook_text);
            TextView linkedinTextView = (TextView) rootView.findViewById(R.id.detail_linkedin_text);
            TextView twitterTextView = (TextView) rootView.findViewById(R.id.detail_twitter_text);
            TextView snapchatTextView = (TextView) rootView.findViewById(R.id.detail_snapchat_text);
            nameTextView.setText(data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.NAME)));
            phoneTextView.setText(data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.PHONE)));
            emailTextView.setText(data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.EMAIL)));
            addressTextView.setText(data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.ADDRESS)));
            facebookTextView.setText(data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.FACEBOOK)));
            linkedinTextView.setText(data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.LINKEDIN)));
            twitterTextView.setText(data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.TWITTER)));
            snapchatTextView.setText(data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.SNAPCHAT)));
            latitude = Double.parseDouble(data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.LAT)));
            longitude = Double.parseDouble(data.getString(data.getColumnIndex(QuickshareContract.ContactEntry.LONG)));
            byte[] image = data.getBlob(data.getColumnIndex(QuickshareContract.ContactEntry.IMAGE));
            if (image != null) {
                contact_imageView.setImageBitmap(BitmapFactory.decodeByteArray(image, 0, image.length));
            } else {
                contact_imageView.setImageBitmap(BitmapFactory.decodeResource(getContext().getResources(),
                        R.drawable.no_image));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void hideKeyboard(Activity activity, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

}
