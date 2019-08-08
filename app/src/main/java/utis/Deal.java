package utis;

import java.io.Serializable;

public class Deal implements Serializable {
    private String id;
    private String title;
    private String description;
    private String imgUrl;
    private String imageName;
    private long price;

    public Deal(){}

    public Deal(String title, String description, String imgUrl, long price){
        this.title = title;
        this.description = description;
        this.imgUrl = imgUrl;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
