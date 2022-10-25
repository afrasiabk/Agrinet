package alast.hm.Model;


public class Category  {
    private String icon;
    private String name;
    private String id;
    private int position;

    public Category() {
    }

    public Category(String icon, String name, String id, int position) {
        this.icon = icon;
        this.name = name;
        this.id = id;
        this.position = position;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
