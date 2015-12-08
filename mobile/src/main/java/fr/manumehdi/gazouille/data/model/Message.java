package fr.manumehdi.gazouille.data.model;

/**
 * created by EmmanuelPeru on 25/11/2015
 */

import java.util.List;

public class Message {
    private String content;
    private String photo;
    private String uploadedImageUrl;
    private User user;

    private List<String> hashTags;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUploadedImageUrl() {
        return uploadedImageUrl;
    }

    public void setUploadedImageUrl(String uploadedImageUrl) {
        this.uploadedImageUrl = uploadedImageUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getHashTags() {
        return hashTags;
    }

    public void setHashTags(List<String> hashTags) {
        this.hashTags = hashTags;
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", photo='" + photo + '\'' +
                ", uploadedImageUrl='" + uploadedImageUrl + '\'' +
                ", user=" + user +
                ", hashTags=" + hashTags +
                '}';
    }
}
