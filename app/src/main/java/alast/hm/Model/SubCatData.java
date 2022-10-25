package alast.hm.Model;

import java.util.ArrayList;

public class SubCatData {
    private Category category;
    private ArrayList<Product> products;

    public SubCatData() {}

    public SubCatData(Category category, ArrayList<Product> products) {
        this.category = category;
        this.products = products;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}
