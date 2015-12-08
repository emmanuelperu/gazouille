package fr.manumehdi.gazouille.data.manager;

import android.app.Application;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import fr.manumehdi.gazouille.data.model.Gazouillis;
import fr.manumehdi.gazouille.data.model.Message;
import fr.manumehdi.gazouille.utils.StringsUtils;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

/**
 *
 */
public class GazouilleManager {


    private static final String TWEETS_PATH = "tweets";
    private static final String HASH_TAGS_PATH = "hashtags";


    private Firebase gazouillisBase;
    private Firebase hashTagBase;

    private Firebase firebase;
    private Application application;

    @Inject
    protected UserManager userManager;


    public GazouilleManager(Application application,
                            Firebase firebase) {
        this.application = application;
        this.firebase = firebase;

        this.gazouillisBase = firebase.child(TWEETS_PATH);
        this.hashTagBase = firebase.child(HASH_TAGS_PATH);
    }


    /**
     *
     */
    public Observable<Message> postMessage(String msg, String uploadedImgUrl) {
        List<String> hashTags = StringsUtils.getHashTags(msg);
        final Message message = new Message();
        message.setContent(msg);
        message.setHashTags(hashTags);
        message.setUploadedImageUrl(uploadedImgUrl);
        message.setUser(userManager.getUser());

        return Observable.create(new Observable.OnSubscribe<PushTransaction<List<String>>>() {
            @Override
            public void call(final Subscriber<? super PushTransaction<List<String>>> subscriber) {
                gazouillisBase.push().setValue(message, new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                if (firebaseError != null) {
                                    subscriber.onError(firebaseError.toException());
                                } else {
                                    subscriber.onNext(new PushTransaction<List<String>>(hashTagBase, message.getHashTags()));
                                }
                                subscriber.onCompleted();
                            }
                        }
                );
            }
        }).doOnNext(new Action1<PushTransaction<List<String>>>() {
            @Override
            public void call(PushTransaction<List<String>> listPushTransaction) {
                // sauvegarder les hashtags
            }
        }).flatMap(new Func1<PushTransaction<List<String>>, Observable<Message>>() {
            @Override
            public Observable<Message> call(PushTransaction<List<String>> listPushTransaction) {

                return Observable.just(message);
            }
        });
    }

    private Observable<DataSnapshot> createObservableOnData(final Firebase base, final boolean autoClose) {
        return Observable.create(new Observable.OnSubscribe<DataSnapshot>() {
            @Override
            public void call(final Subscriber<? super DataSnapshot> subscriber) {
                base.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        subscriber.onNext(dataSnapshot);
                        if (autoClose) {
                            subscriber.onCompleted();
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        subscriber.onError(firebaseError.toException());
                    }
                });
            }
        });
    }

    public Observable<List<Gazouillis>> getMessages(final boolean autoClose) {
        // créer un observable sur les data de firebase
        Observable<DataSnapshot> observableOnData = this.createObservableOnData(gazouillisBase, false);
        // TODO transformer en flatmap
        final Observable<List<Gazouillis>> map = observableOnData.map(new Func1<DataSnapshot, List<Gazouillis>>() {
            @Override
            public List<Gazouillis> call(DataSnapshot dataSnapshot) {
                List<Gazouillis> gazouillis = null;
                if (dataSnapshot != null) {
                    gazouillis = new ArrayList<Gazouillis>();
                    final Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        final DataSnapshot ds = iterator.next();
                        gazouillis.add(new Gazouillis(ds.getKey(), ds.getValue(Message.class)));
                    }
                }
                return gazouillis;
            }
        });
        return map;
    }


    public Observable<List<Gazouillis>> getMessages(String hashTag) {
        return this.createObservableOnData(hashTagBase.child(hashTag.replaceAll("#", "")), true)//
                .map(new Func1<DataSnapshot, List<String>>() {
                    @Override
                    public List<String> call(DataSnapshot dataSnapshot) {
                        ArrayList<String> ids = new ArrayList<String>();
                        final Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()) {
                            final DataSnapshot next = iterator.next();
                            if (next.getValue() != null) {
                                ids.add(iterator.next().getValue().toString());
                            }
                        }
                        return ids;
                    }
                }).flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> strings) {
                        return Observable.from(strings);
                    }
                }).flatMap(new Func1<String, Observable<DataSnapshot>>() {
                    @Override
                    public Observable<DataSnapshot> call(String id) {
                        return createObservableOnData(gazouillisBase.child(id), true);
                    }
                }).map(new Func1<DataSnapshot, Gazouillis>() {
                    @Override
                    public Gazouillis call(DataSnapshot dataSnapshot) {
                        return new Gazouillis(dataSnapshot.getKey(), dataSnapshot.getValue(Message.class));
                    }
                }).toList();
    }
}

