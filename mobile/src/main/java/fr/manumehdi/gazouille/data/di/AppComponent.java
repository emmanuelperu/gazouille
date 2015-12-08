package fr.manumehdi.gazouille.data.di;

import android.app.Application;
import com.firebase.client.Firebase;
import javax.inject.Singleton;
import dagger.Component;

/**
 * Created by mehdi on 21/11/2015.
 */

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    Application application();
    Firebase firebase();

}