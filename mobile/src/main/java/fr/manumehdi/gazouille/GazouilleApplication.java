package fr.manumehdi.gazouille;

import android.app.Application;

import com.firebase.client.Firebase;

import fr.manumehdi.gazouille.data.di.AppComponent;
import fr.manumehdi.gazouille.data.di.AppModule;
import fr.manumehdi.gazouille.data.di.DaggerAppComponent;
import fr.manumehdi.gazouille.data.di.DaggerDataComponent;
import fr.manumehdi.gazouille.data.di.DataComponent;
import fr.manumehdi.gazouille.data.di.DataModule;

/**
 * Created by mehdi on 16/11/2015.
 */

public class GazouilleApplication extends Application {

    public static AppComponent appComponent;
    public static DataComponent dataComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

        Firebase firebase = new Firebase("https://ineat-twitter.firebaseIO.com");
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        dataComponent = DaggerDataComponent.builder()
                .appComponent(appComponent)
                .dataModule(new DataModule())
                .build();
    }
}