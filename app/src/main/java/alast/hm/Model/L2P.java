package alast.hm.Model;

public class L2P {
    private Category l2;
    private Product[] products;

    public L2P() {
    }

    public L2P(Category l2, Product[] products) {
        this.l2 = l2;
        this.products = products;
    }

    public Category getL2() {
        return l2;
    }

    public void setL2(Category l2) {
        this.l2 = l2;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }
}
