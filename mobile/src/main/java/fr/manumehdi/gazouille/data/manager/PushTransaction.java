package fr.manumehdi.gazouille.data.manager;

import com.firebase.client.Firebase;

/**
 * created by EmmanuelPeru on 27/11/2015
 */
public class PushTransaction<T> {

    private Firebase firebase;
    private T object;

    public PushTransaction(Firebase firebase, T t){
        this.object = t;
        this.firebase = firebase;
    }

    public Firebase getFirebase() {
        return firebase;
    }

    public void setFirebase(Firebase firebase) {
        this.firebase = firebase;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "PushTransaction{" +
                "firebase=" + firebase +
                ", object=" + object +
                '}';
    }
}
