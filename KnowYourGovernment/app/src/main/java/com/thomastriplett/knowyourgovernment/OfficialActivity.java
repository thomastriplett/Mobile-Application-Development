package com.thomastriplett.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.text.util.Linkify;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.HashMap;

/**
 * Created by Thomas on 4/2/2018.
 */

public class OfficialActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView location;
    private TextView office;
    private TextView name;
    private TextView party;
    private TextView address;
    private TextView phone;
    private TextView email;
    private TextView website;
    private ImageView officialPic;
    private ImageView youtube;
    private ImageView google;
    private ImageView twitter;
    private ImageView facebook;

    private Official official;

    private String photoLink;
    private String googlePlusLink;
    private String facebookLink;
    private String twitterLink;
    private String youtubeLink;

    private final String TAG = "OfficialActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        layout = findViewById(R.id.layout);
        location = findViewById(R.id.location_view);
        office = findViewById(R.id.office_view);
        name = findViewById(R.id.name_view);
        party = findViewById(R.id.party_view);
        address = findViewById(R.id.address_intent);
        phone = findViewById(R.id.phone_intent);
        email = findViewById(R.id.email_intent);
        website = findViewById(R.id.website_intent);
        officialPic = findViewById(R.id.official_pic);
        youtube = findViewById(R.id.youtube_button);
        google = findViewById(R.id.google_button);
        twitter = findViewById(R.id.twitter_button);
        facebook = findViewById(R.id.facebook_button);

        youtube.setVisibility(View.INVISIBLE);
        google.setVisibility(View.INVISIBLE);
        twitter.setVisibility(View.INVISIBLE);
        facebook.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        if (intent.hasExtra("Location")) {
            String loc = intent.getStringExtra("Location");
            location.setText(loc);
        }
        if (intent.hasExtra("Official")){
            official = intent.getParcelableExtra("Official");
            office.setText(official.getOffice());
            name.setText(official.getName());
            party.setText(official.getParty());

            if(official.getParty().contains("Democrat")){
                layout.setBackgroundColor(-13388315); // blue
            }
            else if(official.getParty().contains("Republican")){
                layout.setBackgroundColor(-48060); // red
            }
            else{
                layout.setBackgroundColor(-7500398); // dark gray
            }

            address.setText(official.getAddress());
            phone.setText(official.getPhone());
            email.setText(official.getEmail());
            website.setText(official.getUrl());

            photoLink = official.getPhotoURL();
            //Log.d(TAG,"The link is: "+photoLink);
            if (photoLink != null) {
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        // Here we try https if the http image attempt failed
                        final String changedUrl = photoLink.replace("http:", "https:");
                        picasso.load(changedUrl)
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(officialPic);
                    }
                }).build();

                picasso.load(photoLink)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(officialPic);
            } else {
                Picasso.with(this).load(photoLink)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.missingimage)
                        .into(officialPic);
            }

            HashMap<String,String> chan = official.getChannels();
            if(chan.containsKey("GooglePlus")){
                googlePlusLink = chan.get("GooglePlus");
                google.setVisibility(View.VISIBLE);
            }
            if(chan.containsKey("Facebook")){
                facebookLink = chan.get("Facebook");
                facebook.setVisibility(View.VISIBLE);
            }
            if(chan.containsKey("Twitter")){
                twitterLink = chan.get("Twitter");
                twitter.setVisibility(View.VISIBLE);
            }
            if(chan.containsKey("YouTube")){
                youtubeLink = chan.get("YouTube");
                youtube.setVisibility(View.VISIBLE);
            }
        }

        Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
        Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
        Linkify.addLinks(website, Linkify.WEB_URLS);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
                    NavUtils.navigateUpTo(this, upIntent);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openPhotoActivity(View v) {
        // photoLink = the official's photo url
        if (photoLink == null){
            return;
        }

        Intent intent = new Intent(this, PhotoActivity.class);
        intent.putExtra("Location", location.getText());
        intent.putExtra("Official", official);
        startActivity(intent);
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        // twitterLink = the official's twitter id from download
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + twitterLink));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + twitterLink));
        }
        startActivity(intent);
    }

    public void facebookClicked(View v) {
        // facebookLink = the official's facebook id from download
        String FACEBOOK_URL = "https://www.facebook.com/" + facebookLink;
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + facebookLink;
            }
        } catch (PackageManager.NameNotFoundException  e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void googlePlusClicked(View v) {
        // googlePlusLink = the official's google plus id from download
        String name = googlePlusLink;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void youTubeClicked(View v) {
        // youtubeLink = the official's youtube id from download
        String name = youtubeLink;
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch(ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }
}
