package com.neelbedekar.android.quickshare;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.desmond.squarecamera.CameraActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.neelbedekar.android.quickshare.data.QuickshareContract;

import java.io.ByteArrayOutputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddContactActivityFragment extends Fragment {


    private ImageButton addImage;
    private static final int REQUEST_CAMERA = 0;
    private boolean taken;
    private TextView previewTextView;
    private ImageView previewImage;
    private TextView imageText;
    private byte[] image;
    public static double latitude;
    public static double longitude;

    public AddContactActivityFragment() {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("taken", taken);
        outState.putByteArray("image", image);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("LOG_TAG", "is changing");
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_add_contact, container, false);
        final FloatingActionButton fabCancel = (FloatingActionButton) rootView.findViewById(R.id.fab_cancel);
        final FloatingActionButton fabAccept = (FloatingActionButton) rootView.findViewById(R.id.fab_accept);
        imageText = (TextView) rootView.findViewById(R.id.add_image_textview);
        addImage = (ImageButton) rootView.findViewById(R.id.camera_image_button);
        previewImage = (ImageView) rootView.findViewById(R.id.image_preview_imageview);
        previewTextView = (TextView) rootView.findViewById(R.id.image_preview_textview);
        if (savedInstanceState == null) {
            taken = false;
        }
        else {
            taken = savedInstanceState.getBoolean("taken");
            image = savedInstanceState.getByteArray("image");
        }
        if (!taken) {
            image = null;
        }
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        final EditText phoneText = (EditText) rootView.findViewById(R.id.phone_field_edittext);
        phoneText.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        fabAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                Intent intent = new Intent();
                intent.setAction(getString(R.string.location_receiver));
                getActivity().sendBroadcast(intent);
                EditText nameText = (EditText) rootView.findViewById(R.id.name_field_edittext);
                EditText emailText = (EditText) rootView.findViewById(R.id.email_field_edittext);
                EditText addressText = (EditText) rootView.findViewById(R.id.address_field_edittext);
                EditText facebookText = (EditText) rootView.findViewById(R.id.facebook_field_edittext);
                EditText linkedinText = (EditText) rootView.findViewById(R.id.linkedin_field_edittext);
                EditText twitterText = (EditText) rootView.findViewById(R.id.twitter_field_edittext);
                EditText snapchatText = (EditText) rootView.findViewById(R.id.snapchat_field_edittext);
                values.put(QuickshareContract.ContactEntry.NAME, nameText.getText().toString());
                values.put(QuickshareContract.ContactEntry.PHONE, phoneText.getText().toString());
                values.put(QuickshareContract.ContactEntry.EMAIL, emailText.getText().toString());
                values.put(QuickshareContract.ContactEntry.ADDRESS, addressText.getText().toString());
                values.put(QuickshareContract.ContactEntry.FACEBOOK, facebookText.getText().toString());
                values.put(QuickshareContract.ContactEntry.LINKEDIN, linkedinText.getText().toString());
                values.put(QuickshareContract.ContactEntry.TWITTER, twitterText.getText().toString());
                values.put(QuickshareContract.ContactEntry.SNAPCHAT, snapchatText.getText().toString());
                values.put(QuickshareContract.ContactEntry.LAT, Double.toString(latitude));
                values.put(QuickshareContract.ContactEntry.LONG, Double.toString(longitude));
                values.put(QuickshareContract.ContactEntry.IMAGE, image);
                getActivity().getContentResolver().insert(QuickshareContract.ContactEntry.CONTENT_URI, values);
                getActivity().finish();
            }
        });
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startCustomCameraIntent = new Intent(getActivity(), CameraActivity.class);
                startActivityForResult(startCustomCameraIntent, REQUEST_CAMERA);
            }
        });
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int diff = rootView.getRootView().getHeight() - rootView.getHeight();
                if (diff > 200) {
                    fabAccept.setVisibility(View.INVISIBLE);
                    fabCancel.setVisibility(View.INVISIBLE);
                    addImage.setVisibility(View.INVISIBLE);
                    imageText.setVisibility(View.INVISIBLE);
                    previewImage.setVisibility(View.INVISIBLE);
                    previewTextView.setVisibility(View.INVISIBLE);
                } else {
                    fabAccept.setVisibility(View.VISIBLE);
                    fabCancel.setVisibility(View.VISIBLE);
                    addImage.setVisibility(View.VISIBLE);
                    imageText.setVisibility(View.VISIBLE);
                    previewImage.setVisibility(View.VISIBLE);
                    if (taken) {
                        previewTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_CAMERA) {
            Uri takenPhotoUri = data.getData();
            Bitmap takenImage = BitmapFactory.decodeFile(takenPhotoUri.getPath());
            taken = true;
            imageText.setText(getString(R.string.change_image_string));
            previewTextView.setVisibility(View.VISIBLE);
            previewImage.setImageBitmap(takenImage);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            takenImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
            image = stream.toByteArray();
        }
    }
}
