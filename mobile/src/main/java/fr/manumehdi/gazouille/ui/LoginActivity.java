package fr.manumehdi.gazouille.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.ColorMatrixColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dd.morphingbutton.MorphingButton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import fr.manumehdi.gazouille.BuildConfig;
import fr.manumehdi.gazouille.R;
import fr.manumehdi.gazouille.data.di.DataComponent;
import fr.manumehdi.gazouille.data.manager.UserManager;
import rx.Subscription;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

    @Inject
    protected UserManager userManager;

    private Subscription subscription;

    private AutoCompleteTextView emailView;
    private ImageView background;
    private EditText passwordView;

    private MorphingButton loginInButton;
    private MorphingButton.Params loginInParams;

    @Override
    public void initComponent(DataComponent dataComponent) {
        dataComponent.inject(this);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.background = (ImageView) findViewById(R.id.login_background);

        float brightness = 0.25f;
        float[] colorMatrix = new float[]{
                0.33f, 0.33f, 0.33f, 0f, brightness,
                0.33f, 0.33f, 0.33f, 0f, brightness,
                0.33f, 0.33f, 0.33f, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
        };

        ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrix);
        background.setColorFilter(colorFilter);

        loginInButton = (MorphingButton) this.findViewById(R.id.email_sign_in_button);

        loginInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });

        emailView = (AutoCompleteTextView) findViewById(R.id.email);
        initLoginButton(0);
        populateAutoComplete();

        passwordView = (EditText) findViewById(R.id.password);
        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Boolean result = Boolean.FALSE;
                if (actionId == R.id.login || actionId == EditorInfo.IME_NULL) {
                    attemptLogin();
                    result = Boolean.TRUE;
                }
                return result;
            }
        });

        TextView createAccountButton = (TextView) findViewById(R.id.email_create_account_button);
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CreateAccountActivity.class));
                finish();
            }
        });

        if (BuildConfig.DEBUG) {
            emailView.setText("toto@gmail.com");
            passwordView.setText("");
        }
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        LoaderManager loader = this.getSupportLoaderManager();
        loader.initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                String[] values = new String[1];
                values[0] = ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE;
                return null; /*CursorLoader(LoginActivity.this.getApplicationContext(),
                        Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                                ContactsContract.Contacts.Data.CONTENT_DIRECTORY), new ProfileQuery().getProjection()[0],
                        ContactsContract.Contacts.Data.MIMETYPE + " = ?", values,
                        ContactsContract.Contacts.Data.IS_PRIMARY + " DESC"):*/
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {
                /* nothing */
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                if (cursor == null) {
                    return;
                }

                List<String> emails = new ArrayList<String>();
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    emails.add(cursor.getString(ProfileQuery.ADDRESS));
                    cursor.moveToNext();
                }

                addEmailsToAutoComplete(emails);
            }

        });
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {
            Snackbar.make(emailView, R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, //
                    new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }

        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // FIXME ce equals n'a pas de sens ?
        if (Manifest.permission.READ_CONTACTS.equals(requestCode)) {
            if (grantResults != null && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    private void createAccount() {
        if (subscription != null) {
            return;
        }

        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null)
                focusView.requestFocus();
        } else {
            showProgress(true);
            // FIXME creer un observable
            /*hideKeyboard();
            subscription = userManager.createUser(email, password).subscribeOnMainThread({
                    displayGazouillis()
            }, {error ->
                    subscription = null
                    showProgress(false)

                    var errorMsg:String
            if (error in AccountExistException){
                errorMsg = "Votre compte existe déjà";
            }else{
                errorMsg = getString(R.string.error_incorrect_password);
            }

            passwordView.setError(errorMsg);
            passwordView.requestFocus();
            });*/

        }
    }


    private void attemptLogin() {
        if (subscription != null) {
            return;
        }
        // Reset errors.
        emailView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (cancel) {
            if (focusView != null)
                focusView.requestFocus();
        } else {
            showProgress(true);
            /*subscription = userManager.connect(email, password).subscribeOnMainThread({auth ->
                    displayGazouillis()
            }, {error ->
                    subscription = null;
                    showProgress(false);
                    passwordView.setError(getString(R.string.error_incorrect_password));
                    passwordView.requestFocus();
            });
            */
        }
    }

    private void displayGazouillis() {
        Intent intent = new Intent(this, ScrollingActivity.class);
        /*
        if (animated) {
            startActivityWithRevealAnimation(intent, ContextCompat.getColor(this, R.color.colorPrimary), findViewById(R.id.email_sign_in_button), {
                finish()
            })
        } else {
            startActivity(intent)
            finish()
        }*/
        startActivity(intent);
        finish();
    }

    private boolean isEmailValid(String email) {
        // FIXME regexp
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.length() > 4;
    }


    private void initLoginButton(final int duration) {
        if (this.loginInParams == null) {
            loginInButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    loginInButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    loginInParams = MorphingButton.Params.create()
                            .duration(duration)
                            .cornerRadius(loginInButton.getHeight())
                            .width(loginInButton.getWidth())
                            .height(loginInButton.getHeight())
                            .color(ContextCompat.getColor(LoginActivity.this.getApplicationContext(), R.color.colorPrimary))
                            .colorPressed(ContextCompat.getColor(LoginActivity.this.getApplicationContext(), R.color.colorAccent));
                    loginInButton.morph(loginInParams);
                }
            });
        } else {
            // FIXME methode non compatible
            //loginInParams.setDuration(duration);
            loginInButton.morph(loginInParams);
        }

    }

    private void showProgress(boolean show) {
        if (show) {
            //var dimen = resources.getDimension(R.dimen.login_height_button)
            MorphingButton.Params rounded = MorphingButton.Params.create()
                    .duration(600)
                    .cornerRadius(loginInButton.getHeight())
                    .width(loginInButton.getHeight())
                    .height(loginInButton.getHeight())
                    .color(ContextCompat.getColor(this, R.color.colorPrimary));
            loginInButton.blockTouch();
            loginInButton.morph(rounded);

            final ProgressBar progress = (ProgressBar) findViewById(R.id.email_sign_in_progress);
            progress.animate()
                    .alphaBy(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .withStartAction(new Runnable() {
                        @Override
                        public void run() {
                            progress.setVisibility(View.VISIBLE);
                        }
                    }).start();
        } else {
            initLoginButton(600);
            loginInButton.unblockTouch();
            final ProgressBar progress = (ProgressBar) findViewById(R.id.email_sign_in_progress);
            progress.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            progress.setVisibility(View.GONE);
                        }
                    }).start();
        }
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        ArrayAdapter adapter = new ArrayAdapter(getApplicationContext(),
                android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
        emailView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        if (subscription != null) {
            subscription.unsubscribe();
        }

        super.onDestroy();
    }

    // FIXME revoir cette class
    public final class ProfileQuery {
        public String[] getProjection() {
            String[] PROJECTION = new String[2];
            PROJECTION[0] = ContactsContract.CommonDataKinds.Email.ADDRESS;
            PROJECTION[1] = ContactsContract.CommonDataKinds.Email.IS_PRIMARY;
            return PROJECTION;
        }

        public static final int ADDRESS = 0;
    }

    private int REQUEST_READ_CONTACTS = 0;
}

