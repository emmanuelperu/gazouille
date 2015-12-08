package fr.manumehdi.gazouille.ui;

import android.os.Bundle;

import fr.manumehdi.gazouille.R;
import fr.manumehdi.gazouille.data.di.DataComponent;

/**
 * Created by mehdi on 22/11/2015.
 */

public class CreateAccountActivity extends BaseActivity {

    @Override
    public void initComponent(DataComponent dataComponent) {
        // nothing
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

}