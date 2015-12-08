package fr.manumehdi.gazouille.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import javax.inject.Inject;

import fr.manumehdi.gazouille.R;
import fr.manumehdi.gazouille.data.di.DataComponent;
import fr.manumehdi.gazouille.data.manager.UserManager;
import fr.manumehdi.gazouille.data.model.User;

public class EditProfilActivity extends BaseActivity {

    TextView emailTextView;
    TextView nameTextView;
    TextView biographyTextView;
    TextView websiteTextView;

    @Inject
    UserManager userManager;

    @Override
    public void initComponent(DataComponent dataComponent) {
        dataComponent.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profil);
        android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView emailTextView = (TextView) findViewById(R.id.edit_profil_email);
        TextView nameTextView = (TextView) findViewById(R.id.edit_profil_name);
        TextView biographyTextView = (TextView) findViewById(R.id.edit_profil_biography);
        TextView websiteTextView = (TextView) findViewById(R.id.edit_profil_web_site);

        User user = userManager.getUser();
        emailTextView.setText(user.getName());
        nameTextView.setText(user.getNickname());
        biographyTextView.setText(user.getBiography());
        websiteTextView.setText(user.getWebsite());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

}
