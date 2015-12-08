package fr.manumehdi.gazouille.ui;

import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

import fr.manumehdi.gazouille.R;
import fr.manumehdi.gazouille.data.di.DataComponent;
import fr.manumehdi.gazouille.data.manager.UserManager;

public class SplashActivity extends BaseActivity {

    @Inject
    protected UserManager userManager;

    @Override
    public void initComponent(DataComponent dataComponent) {
        dataComponent.inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // FIXME
            e.printStackTrace();
        }

        Intent intent;
        if (userManager != null && userManager.isConnected()) {
            intent = new Intent(this, ScrollingActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }

        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
