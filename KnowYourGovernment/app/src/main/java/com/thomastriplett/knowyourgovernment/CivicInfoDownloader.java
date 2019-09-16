package com.thomastriplett.knowyourgovernment;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

/**
 * Created by Thomas on 4/6/2018.
 * API key = AIzaSyCjELdzRCeEwN8lcyvsFp7lINAqynr5HNg
 */

public class CivicInfoDownloader extends AsyncTask<String, Void, String> {

    private MainActivity mainActivity;
    public static boolean running = false;

    public CivicInfoDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected String doInBackground(String... strings) {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL("https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyCjELdzRCeEwN8lcyvsFp7lINAqynr5HNg&address="+strings[0]);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        return sb.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        if(s == null){
            // Create a toast indicating Civic Info Service is unavailable
            mainActivity.setOfficialList(null);
        }
        else if(s.equals("")){
            // Create a toast indicating no data is available for the specified location
            mainActivity.setOfficialList(null);
        }
        else{
            try {
                Object[] results = new Object[2];

                JSONObject jsonObj = new JSONObject(s);
                JSONObject normalizedInput = jsonObj.getJSONObject("normalizedInput");
                JSONArray offices = jsonObj.getJSONArray("offices");
                JSONArray officials = jsonObj.getJSONArray("officials");

                String city = normalizedInput.getString("city");
                String state = normalizedInput.getString("state");
                String zip = normalizedInput.getString("zip");

                results[0] = city+", "+state+" "+zip;

                ArrayList<Official> officialList= new ArrayList<Official>();
                HashMap<String, String> order = new HashMap<>();

                for (int i=0; i < offices.length();i++){
                    JSONObject c = offices.getJSONObject(i);
                    String name = c.getString("name");
                    JSONArray indices = c.getJSONArray("officialIndices");

                    for (int p=0; p < indices.length();p++){
                        order.put(Integer.toString((int)indices.get(p)), name);
                    }
                }

                for (int j=0; j < officials.length();j++){
                    JSONObject c = officials.getJSONObject(j);
                    String name = c.getString("name");

                    String addr = "No Data Provided";
                    if(c.has("address")) {
                        JSONArray address = c.getJSONArray("address");
                        JSONObject addressObj = address.getJSONObject(0);
                        String line1 = addressObj.getString("line1");
                        String line2 = "";
                        if (addressObj.has("line2")) {
                            line2 = addressObj.getString("line2");
                        }
                        String officialCity = addressObj.getString("city");
                        String officialState = addressObj.getString("state");
                        String officialZip = addressObj.getString("zip");

                        if(!line2.equals("")) {
                            addr = line1 + "\n" + line2 + "\n" + officialCity + ", " + officialState + " " + officialZip;
                        }
                        else{
                            addr = line1 + "\n" + officialCity + ", " + officialState + " " + officialZip;
                        }
                    }

                    String party = "Unknown";
                    if (c.has("party")) {
                        party = c.getString("party");
                    }
                    String phone = "No Data Provided";
                    if(c.has("phones")){
                        JSONArray phones = c.getJSONArray("phones");
                        phone = (String)phones.get(0);
                    }
                    String url = "No Data Provided";
                    if(c.has("urls")) {
                        JSONArray urls = c.getJSONArray("urls");
                        url = (String) urls.get(0);
                    }
                    String email = "No Data Provided";
                    if (c.has("emails")) {
                        JSONArray emails = c.getJSONArray("emails");
                        email = (String)emails.get(0);
                    }
                    String photoURL = null;
                    if(c.has("photoUrl")) {
                        photoURL = c.getString("photoUrl");
                    }
                    HashMap<String,String> chan = new HashMap<>();
                    if(c.has("channels")) {
                        JSONArray channels = c.getJSONArray("channels");
                        for (int k = 0; k < channels.length()-1; k++) {
                            JSONObject currentChannel = (JSONObject) channels.get(k);
                            String type = currentChannel.getString("type");
                            String id = currentChannel.getString("id");
                            chan.put(type, id);
                        }
                    }

                    String office = order.get(Integer.toString(j));
                    officialList.add(new Official(office, name, party, addr, phone, url, email, photoURL, chan));
                }

                results[1] = officialList;
                mainActivity.setOfficialList(results);
                running = false;
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
