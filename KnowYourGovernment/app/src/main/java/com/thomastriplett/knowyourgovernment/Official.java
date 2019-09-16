package com.thomastriplett.knowyourgovernment;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thomas on 4/2/2018.
 */

public class Official implements Parcelable{

    private String office;
    private String name;
    private String party;
    private String address;
    private String phone;
    private String url;
    private String email;
    private String photoURL;
    private HashMap<String,String> channels;

    public Official(){
        this.office = "U.S. President";
        this.name = "Thomas Triplett";
        this.party = "Democratic";
        this.address = "789 Lawrence Ave";
        this.phone = "309-321-4561";
        this.url = "www.thomastriplett.com";
        this.email = "thomastriplett@gmail.com";
        this.photoURL = "www.thomastriplettphoto.com";
        this.channels = new HashMap<>();
    }

    public Official(String o, String n, String p, String a, String po, String u, String e, String pu, HashMap<String,String> c){
        this.office = o;
        this.name = n;
        this.party = p;
        this.address = a;
        this.phone = po;
        this.url = u;
        this.email = e;
        this.photoURL = pu;
        this.channels = c;
    }

    protected Official(Parcel in) {
        office = in.readString();
        name = in.readString();
        party = in.readString();
        address = in.readString();
        phone = in.readString();
        url = in.readString();
        email = in.readString();
        photoURL = in.readString();
        channels = new HashMap<>();
        int size = in.readInt();
        for(int i = 0; i < size; i++){
            String key = in.readString();
            String value = in.readString();
            channels.put(key,value);
        }
    }

    public static final Creator<Official> CREATOR = new Creator<Official>() {
        @Override
        public Official createFromParcel(Parcel in) {
            return new Official(in);
        }

        @Override
        public Official[] newArray(int size) {
            return new Official[size];
        }
    };

    public String getOffice() {
        return office;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return "("+party+")";
    }


    public void setOffice(String office) {
        this.office = office;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public HashMap<String, String> getChannels() {
        return channels;
    }

    public void setChannels(HashMap<String,String> channels) {
        this.channels = channels;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(office);
        parcel.writeString(name);
        parcel.writeString(party);
        parcel.writeString(address);
        parcel.writeString(phone);
        parcel.writeString(url);
        parcel.writeString(email);
        parcel.writeString(photoURL);
        parcel.writeInt(channels.size());
        for(Map.Entry<String,String> entry : channels.entrySet()){
            parcel.writeString(entry.getKey());
            parcel.writeString(entry.getValue());
        }
    }
}
