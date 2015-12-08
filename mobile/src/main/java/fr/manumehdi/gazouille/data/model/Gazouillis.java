package fr.manumehdi.gazouille.data.model;

/**
 * Represents a message on Gazouillis platform.
 * created by EmmanuelPeru on 25/11/2015
 */
public class Gazouillis {
    private String id;
    private Message message;

    public Gazouillis() {
        super();
    }

    public Gazouillis(String id, Message message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}