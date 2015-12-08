package fr.manumehdi.gazouille.data.manager;

import android.app.Application;
import android.content.Context;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;

import fr.manumehdi.gazouille.data.model.User;

/**
 * Created by mehdi on 13/11/2015.
 */

public class UserManager {

    private static final String USER_STORAGE = "user_storage";
    private static final String IS_CONNECT_PREFERENCE_KEY = "is_connect";
    private static final String USERNAME_PREFERENCE_KEY = "username";
    private static final String ID_USER_PREFERENCE_KEY = "id_user";

    private Application application;
    private Firebase firebase;
    private boolean isConnected;
    private String username;
    private String userId;
    private User user;

    public UserManager(Application application,
                       Firebase firebase) {
        this.application = application;
        this.firebase = firebase;
    }


    public Boolean isConnected() {
        return this.application != null && //
                this.application.getSharedPreferences(USER_STORAGE, Context.MODE_PRIVATE).getBoolean(IS_CONNECT_PREFERENCE_KEY, false);
    }

    public void setConnected(boolean value) {
        application.getSharedPreferences(USER_STORAGE, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(IS_CONNECT_PREFERENCE_KEY, value)
                .apply();
    }

    public String getUsername() {
        return application.getSharedPreferences(USER_STORAGE, Context.MODE_PRIVATE).getString(USERNAME_PREFERENCE_KEY, null);
    }


    public void setUsername(String value) {
        application.getSharedPreferences(USER_STORAGE, Context.MODE_PRIVATE)
                .edit()
                .putString(USERNAME_PREFERENCE_KEY, value)
                .apply();
    }

    public String getUserId() {
        return this.application.getSharedPreferences(USER_STORAGE, Context.MODE_PRIVATE).
                getString(ID_USER_PREFERENCE_KEY, null);
    }

    public void setUserId(String value) {
        application.getSharedPreferences(USER_STORAGE, Context.MODE_PRIVATE)
                .edit()
                .putString(ID_USER_PREFERENCE_KEY, value)
                .apply();
    }


    public User getUser() {
        return new User(userId, username);
    }

    public void addProfil(AuthData auth) {
        Map map = new HashMap();
        map.put("provider", auth.getProvider());
        map.put("name", getName(auth));
        firebase.child("users").child(auth.getUid()).setValue(map);
    }

    public String getName(AuthData auth) {
        if ("password".equals(auth.getProvider())) {
            return auth.getProviderData().get("email").toString(); //return auth.providerData?.get("email").toString().replace("/@.*/", "")
        }
        return "provider unsupported";
    }

    public class AccountExistException extends Exception {
        public AccountExistException() {
            super("Le compte existe déjà");
        }
    }

    private void createUser(final String email, final String password) throws Exception {
        this.firebase.createUser(email, password, new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> map) {
                // FIXME verifier le contenu de la map
                AuthData authData = null; // AuthenticationManager.this.getAuth();
                if (authData != null) {
                    addProfil(authData);
                    connect(email, password);
                }
            }

            @Override
            public void onError(FirebaseError firebaseError) {
                if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                    // FIXME comportement approprié
                    //throw new AccountExistException();
                } else {
                    throw firebaseError.toException();
                }
            }
        });
    }


    public void connect(String email, String password) {
        firebase.authWithPassword(email, password, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                isConnected = true;
                username = authData.getProviderData().get("email").toString();
                userId = authData.getUid();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {

            }
        });
    }

    public void disconnect() {
        isConnected = false;
        firebase.unauth();
    }
}