package fr.manumehdi.gazouille.data.di;

import dagger.Component;
import fr.manumehdi.gazouille.GazouilleApplication;
import fr.manumehdi.gazouille.ui.AddTweetDialogFragment;
import fr.manumehdi.gazouille.ui.CreateAccountActivity;
import fr.manumehdi.gazouille.ui.EditProfilActivity;
import fr.manumehdi.gazouille.ui.LoginActivity;
import fr.manumehdi.gazouille.ui.ScrollingActivity;
import fr.manumehdi.gazouille.ui.SplashActivity;

/**
 * Created by mehdi on 21/11/2015.
 */

@DataScope
@Component(
        dependencies = {AppComponent.class},
        modules = {DataModule.class}
)
public interface DataComponent {

    void inject(GazouilleApplication application);

    void inject(LoginActivity activity);

    void inject(CreateAccountActivity activity);

    void inject(EditProfilActivity activity);

    void inject(SplashActivity activity);

    void inject(ScrollingActivity activity);

    void inject(AddTweetDialogFragment fragment);

}