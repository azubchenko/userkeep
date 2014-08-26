package com.handycraft.userkeep.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.handycraft.userkeep.model.User;

/**
 * Created by stanlytwiddle on 8/27/14.
 */
public class UserManager {

    static final String PREF_APP = "pref_app";
    static final String PREF_USER = "pref_user";

    /**
     * @param context
     * @param user
     */
    static public void saveUser(Context context, User user) {
        try {

            DataEncryptor dataEncryptor = new DataEncryptor(context);
            String encryptedUser = dataEncryptor.encrypt(user.getUserString());

            SharedPreferences prefs = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_USER, encryptedUser);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @return
     */
    static public User loadUser(Context context) {
        User user = null;
        try {

            DataEncryptor dataEncryptor = new DataEncryptor(context);
            SharedPreferences prefs = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
            String userString = dataEncryptor.decrypt(prefs.getString(PREF_USER, null));
            user = new User(userString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * @param context
     */
    static public void resetUser(Context context) {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(PREF_USER, "");
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
