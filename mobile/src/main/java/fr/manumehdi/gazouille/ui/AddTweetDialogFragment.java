package fr.manumehdi.gazouille.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import fr.manumehdi.gazouille.GazouilleApplication;
import fr.manumehdi.gazouille.R;
import fr.manumehdi.gazouille.data.manager.GazouilleManager;
import fr.manumehdi.gazouille.data.manager.ImageManager;
import fr.manumehdi.gazouille.data.model.Gazouillis;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class AddTweetDialogFragment extends DialogFragment {

    public static int REQUEST_IMAGE_CAPTURE = 1;

    @Inject
    public GazouilleManager gazouilleManager;

    @Inject
    protected ImageManager imageManager;

    private Button cancelButton;
    private Button okButton;
    private EditText messageEditText;
    private ImageView mImageView;
    private ImageButton uploadImagebutton;
    private ImageButton takePhotoButton;

    private Gazouillis currentGazouille;

    private String imageFilePath;

    private Subscription addMessageSubscription;
    private Subscription uploadImageSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GazouilleApplication.dataComponent.inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_add_tweet, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        messageEditText = (EditText) view.findViewById(R.id.add_tweet_message_edit);
        // FIXME
        // messageEditText.showKeyboard();


        cancelButton = (Button) view.findViewById(R.id.add_tweet_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        okButton = (Button) view.findViewById(R.id.add_tweet_ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTweet();
            }
        });

        uploadImagebutton = (ImageButton) view.findViewById(R.id.add_tweet_upload_image_button);
        uploadImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // nothing for the moment
            }
        });

        takePhotoButton = (ImageButton) view.findViewById(R.id.add_tweet_take_photo_button);
        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mImageView = (ImageView) view.findViewById(R.id.add_tweet_preview_image);
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String imageFilePath = createImageFilePath();
        // FIXME esternaliser vide
        Toast.makeText(this.getActivity(), imageFilePath != null ? imageFilePath : "vide", Toast.LENGTH_LONG).show();

        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    private String createImageFilePath() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        final File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        try {
            File image = java.io.File.createTempFile(imageFileName, ".jpg", getContext().getFilesDir().getAbsoluteFile());
            return image.getAbsolutePath();
        } catch (Exception e) {
            // FIXME
        }
        return null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            // FIXME test nullable
            mImageView.setImageBitmap((Bitmap) data.getExtras().get("data"));
        }
    }

    private void addTweet() {
        if (messageEditText.getText() == null || TextUtils.isEmpty(messageEditText.getText())) {
            // FIXME un petit Toast marrant du genre 'Rien à gazouiller ?'
            messageEditText.setError("Un message ne doit pas être vide");
            return;
        }
        final String msg = messageEditText.getText().toString().trim();
        messageEditText.setError(null);

        addMessageSubscription = imageManager.uploadImageFile(imageFilePath).flatMap(new Func1<String, Observable<?>>() {
            @Override
            public Observable<?> call(String imageUrl) {
                return gazouilleManager.postMessage(msg, imageUrl);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                        // FIXME Object -> String
                .subscribe(new Action1<Object>() {
                    public void call(Object msg) {
                        addMessageSubscription = null;
                        Snackbar.make(getActivity().getWindow().getDecorView(), "Tweet envoyé", Snackbar.LENGTH_LONG).show();
                        dismiss();
                    }
                }, new Action1<Throwable>() {
                    public void call(Throwable error) {
                        Toast.makeText(getActivity(), error.getMessage() != null ? error.getMessage() : "Error", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
                    }
                });
    }

    @Override
    public void onDestroyView() {
        if (addMessageSubscription != null)
            addMessageSubscription.unsubscribe();
        addMessageSubscription = null;
        super.onDestroyView();
    }

}