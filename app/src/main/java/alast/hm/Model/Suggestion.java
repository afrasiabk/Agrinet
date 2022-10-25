package alast.hm.Model;

public class Suggestion {
    private String id, name, description, image;
    private boolean voted;

    public Suggestion() { }

    public Suggestion(String id, String name, String description, String image, boolean voted) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.voted = voted;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isVoted() {
        return voted;
    }

    public void setVoted(boolean voted) {
        this.voted = voted;
    }

}
