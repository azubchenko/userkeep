package com.handycraft.userkeep.model;

import android.text.TextUtils;
import android.util.Patterns;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by stanlytwiddle on 8/26/14.
 */
public class User {
    static public final String FIELD_LOGIN = "login";
    static public final String FIELD_EMAIL = "email";
    static public final String FIELD_PWD = "pwd";
    static public final String FIELD_TOKEN = "token";

    private JSONObject user;

    public User(String login, String email, String pwd, String token) {
        try {
            user = new JSONObject();
            user.put(FIELD_LOGIN, login.trim());
            user.put(FIELD_EMAIL, email.trim());
            user.put(FIELD_PWD, pwd.trim());
            user.put(FIELD_TOKEN, token.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public User(JSONObject jsonObject) {
        this.user = jsonObject;
    }

    public User(String jsonString) throws JSONException {
        this.user = new JSONObject(jsonString);
    }

    public boolean isUserEmpty() {
        return user == null || user.optString(FIELD_LOGIN, null) == null;
    }

    public boolean isLoginPwdSet() {
        return !isUserEmpty() && user.optString(FIELD_PWD) != null;
    }

    public boolean isTokenSet() {
        return !isUserEmpty() && user.optString(FIELD_TOKEN) != null;
    }

    public boolean isEmailSet() {
        return !isUserEmpty() && user.optString(FIELD_EMAIL) != null;
    }

    public boolean isValid() {
        return !isUserEmpty() && isLoginPwdSet();
    }

    public void setToken(String token) {
        try {
            user.put(FIELD_TOKEN, token.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getToken() {
        return isUserEmpty() ? null : user.optString(FIELD_TOKEN);
    }

    public JSONObject getUserJson() {
        return isUserEmpty() ? null : user;
    }

    public String getUserString() {
        return isUserEmpty() ? null : user.toString();
    }

    static public boolean isUserValid(String login, String email, String pwd, String pwd1) {
        return !TextUtils.isEmpty(login.trim()) && !TextUtils.isEmpty(email.trim()) && !TextUtils.isEmpty(pwd.trim()) && !TextUtils.isEmpty(pwd1.trim())
                && pwd.trim().equals(pwd1.trim()) && email.trim().matches(Patterns.EMAIL_ADDRESS.toString());
    }
}
