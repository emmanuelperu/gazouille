package fr.manumehdi.gazouille.data.di;

import android.app.Application;

import com.firebase.client.Firebase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import fr.manumehdi.gazouille.BuildConfig;

/**
 * Created by mehdi on 21/11/2015.
 */

@Module
public class AppModule {

    private Application application;

    public AppModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return this.application;
    }

    @Provides
    @Singleton
    Firebase provideFirebase() {
        // FIXME externaliser l'url aussi
        return new Firebase(BuildConfig.FIREBASE_URL);
    }

}