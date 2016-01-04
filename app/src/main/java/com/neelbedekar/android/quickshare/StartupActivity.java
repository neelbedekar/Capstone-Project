package com.neelbedekar.android.quickshare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.neelbedekar.android.quickshare.backend.myApi.MyApi;

import java.io.IOException;

public class StartupActivity extends AppCompatActivity {


    private boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final EditText usernameText = (EditText) findViewById(R.id.username_edit_text);
        ImageButton verifyButton = (ImageButton) findViewById(R.id.verify_button);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new EndpointsAsyncTask().execute();

                SharedPreferences prefs = getSharedPreferences(MainActivity.SHARED_PREFS, 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(MainActivity.PREFS_USERNAME_KEY, usernameText.getText().toString());
                editor.commit();
                finish();
            }
        });
    }

    class EndpointsAsyncTask extends AsyncTask<Void, Void, Void> {
        private MyApi myApiService = null;
        private Context context;

        @Override
        protected Void doInBackground(Void... params) {
            if(myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator
                        // - turn off compression when running against local devappserver
                        .setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            isValid = true;
        }
    }
}
