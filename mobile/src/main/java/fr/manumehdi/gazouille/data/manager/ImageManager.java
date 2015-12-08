package fr.manumehdi.gazouille.data.manager;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.firebase.client.Firebase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.UUID;

import fr.manumehdi.gazouille.BuildConfig;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by mehdi on 14/11/2015.
 */

public class ImageManager {

    private Cloudinary cloudinary;
    private Application application;
    private Firebase firebase;


    public ImageManager(Application application,
                        Firebase firebase) {
        this.application = application;
        this.firebase = firebase;
    }

    public ImageManager() {
        HashMap cloudinaryConfig = new HashMap<String, String>();
        cloudinaryConfig.put("cloud_name", BuildConfig.CLOUDINARY_CLOUD_NAME);
        cloudinaryConfig.put("api_key", BuildConfig.CLOUDINARY_API_KEY);
        cloudinaryConfig.put("api_secret", BuildConfig.CLOUDINARY_API_SECRET);

        cloudinary = new Cloudinary(cloudinaryConfig);
    }

    private Observable<String> uploadImageInternal(final String filePath) {
        return Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {
                        try {
                            if (filePath != null) {
                                // FIXME generer meilleur uiid
                                String generatedImageName = UUID.randomUUID().toString();
                                cloudinary.uploader().upload(compressImage(filePath), ObjectUtils.asMap("public_id", generatedImageName));
                                String urlServeur = cloudinary.url().generate(generatedImageName);
                                sub.onNext(urlServeur);
                                sub.onCompleted();
                            }
                        } catch (Exception e) {
                            sub.onError(e);
                        }
                    }
                }
        );
    }

    public Observable<String> uploadImageFile(String file) {
        return uploadImageInternal(file);
    }


    private File compressImage(String filePath) throws Exception {
        OutputStream os;
        try {
            String compressedFilePath = filePath.replace(".jpg", "_compressed.jpg");
            File file = new File(compressedFilePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            os = new FileOutputStream(file);

            Bitmap bitmap = BitmapFactory.decodeFile(filePath);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
            return file;
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Error compressing bitmap", e);
        }
        return null;
    }

}


