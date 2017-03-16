package com.mistapp.mistandroid;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.mistapp.mistandroid.model.Event;
import com.mistapp.mistandroid.model.Teammate;

import java.util.ArrayList;

/**
 * Created by aadil on 1/22/17.
 *
 * This class makes it simple to add and remove from the cache
 *
 **/


public class CacheHandler {

    private static CacheHandler cacheHandler;

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Context context;

    private CacheHandler(Context context, SharedPreferences sharedPref, SharedPreferences.Editor editor){
        this.sharedPref = context.getSharedPreferences(context.getString(R.string.app_package_name), Context.MODE_PRIVATE);
        this.editor = sharedPref.edit();
        this.context = context;
    }


    public static CacheHandler getInstance(Context context, SharedPreferences sharedPref, SharedPreferences.Editor editor){
        if (cacheHandler == null) {
            cacheHandler = new CacheHandler(context, sharedPref, editor);
        }
        return cacheHandler;
    }

    public void cacheUserUid(String uid){
        editor.putString(context.getString(R.string.user_uid_key), uid);
    }

    public String getUserUid(){
        return sharedPref.getString(context.getString(R.string.notifications), "");
    }

    public void cacheUser(Object currentUser){
        Gson gson = new Gson();
        String json = gson.toJson(currentUser);
        editor.putString(context.getString(R.string.current_user_key), json);
    }

    public String getUserJson(){
        return sharedPref.getString(context.getString(R.string.current_user_key), null);
    }

    public void cacheUserType(String userType){
        editor.putString(context.getString(R.string.current_user_type), userType);
    }

    public String getUserType(){
        return sharedPref.getString(context.getString(R.string.current_user_type), "");

    }


    public void cacheNotifications(String jsonArray){
        editor.putString(context.getString(R.string.notifications), jsonArray);
    }

    public String getNotificationsJson(){
        return sharedPref.getString(context.getString(R.string.notifications), "");
    }

    public void cacheNumUnreadNotifications(int unread){
        editor.putInt(context.getString(R.string.num_unread_notifications), unread);
    }

    public int getNumUnreadNotifications(int default_value){
        return sharedPref.getInt(context.getString(R.string.num_unread_notifications), default_value);
    }

    public void cacheAllUserFields(String uid, Object currentUser, String  userType){
        cacheUserUid(uid);
        cacheUser(currentUser);
        cacheUserType(userType);
        editor.commit();
    }

    public void cacheAllNotificationFields(String jsonArray, int unread){
        cacheNotifications(jsonArray);
        cacheNumUnreadNotifications(unread);
    }


    public void cacheEvents(ArrayList<Event> events){
        Gson gson = new Gson();
        String jsonArray = gson.toJson(events);
        editor.putString(context.getString(R.string.events), jsonArray);
    }

    public String getCachedEventsJson(){
        return sharedPref.getString(context.getString(R.string.events), "");
    }

    public void removeCachedEvents(){
        editor.remove(context.getString(R.string.events));
        editor.commit();
    }

    public void cacheTeammates(ArrayList<Teammate> teammates){
        Gson gson = new Gson();
        String jsonArray = gson.toJson(teammates);
        editor.putString(context.getString(R.string.teammates), jsonArray);
    }

    public void removeCachedTeammates(){
        editor.remove(context.getString(R.string.teammates));
        editor.commit();
    }

    public String getCachedTeammatesJson(){
        return sharedPref.getString(context.getString(R.string.teammates), "");
    }

    public void removeCachedUserFields(){
        editor.remove(context.getString(R.string.user_uid_key));
        editor.remove(context.getString(R.string.current_user_key));
        editor.remove(context.getString(R.string.current_user_type));
        editor.commit();
    }

    public void removeCachedNotificationFields(){
        editor.remove(context.getString(R.string.num_unread_notifications));
        editor.remove(context.getString(R.string.notifications));
        editor.commit();
    }

    public boolean cacheContains(String key){
        return sharedPref.contains(key);
    }

    public void commitToCache(){
        editor.commit();
    }

}
