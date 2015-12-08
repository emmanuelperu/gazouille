package fr.manumehdi.gazouille;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import fr.eperu.gazouille.R;


public class MyDisplayActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        mTextView = (TextView) findViewById(R.id.text);
    }
}