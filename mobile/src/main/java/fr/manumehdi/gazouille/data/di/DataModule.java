package fr.manumehdi.gazouille.data.di;

import android.app.Application;

import com.firebase.client.Firebase;

import dagger.Module;
import dagger.Provides;
import fr.manumehdi.gazouille.data.manager.GazouilleManager;
import fr.manumehdi.gazouille.data.manager.ImageManager;
import fr.manumehdi.gazouille.data.manager.UserManager;

/**
 * Created by mehdi on 21/11/2015.
 */

@Module
public class DataModule {

    @Provides
    @DataScope
    public UserManager provideUserManager(Application application, Firebase firebase) {
        return new UserManager(application, firebase);
    }

    @Provides
    @DataScope
    public GazouilleManager provideGazouilleManager(Application application, Firebase firebase) {
        return new GazouilleManager(application, firebase);
    }

    @Provides
    @DataScope
    public ImageManager provideImageManager(){
        return new ImageManager();
    }
}