package com.thomastriplett.knowyourgovernment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by Thomas on 4/6/2018.
 */

public class PhotoActivity extends AppCompatActivity {

    private ConstraintLayout layout;
    private TextView location;
    private TextView office;
    private TextView name;
    private ImageView photo;

    private Official official;
    private String photoLink;

    private final String TAG = "PhotoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        layout = findViewById(R.id.layout);
        location = findViewById(R.id.location_view);
        office = findViewById(R.id.office_view);
        name = findViewById(R.id.name_view);
        photo = findViewById(R.id.photo_view);

        Intent intent = getIntent();
        if (intent.hasExtra("Location")) {
            String loc = intent.getStringExtra("Location");
            location.setText(loc);
        }
        if (intent.hasExtra("Official")){
            official = intent.getParcelableExtra("Official");
            office.setText(official.getOffice());
            name.setText(official.getName());

            if(official.getParty().contains("Democrat")){
                layout.setBackgroundColor(-13388315); // blue
            }
            else if(official.getParty().contains("Republican")){
                layout.setBackgroundColor(-48060); // red
            }
            else{
                layout.setBackgroundColor(-7500398); // dark gray
            }

            photoLink = official.getPhotoURL();
            Log.d(TAG,"The Link is : "+photoLink);
            if (photoLink != null) {
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        // Here we try https if the http image attempt failed
                        final String changedUrl = photoLink.replace("http:", "https:");
                        picasso.load(changedUrl)
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(photo);
                    }
                }).build();

                picasso.load(photoLink)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(photo);
            } else {
                Picasso.with(this).load(photoLink)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.missingimage)
                        .into(photo);
            }
        }
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
}
