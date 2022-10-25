package alast.hm.Data;

import java.util.ArrayList;

import alast.hm.Model.Product;

public class CartSingleton {
    public static ArrayList<Product> cartProducts = new ArrayList<>();;
    private static CartSingleton singleton;

    private CartSingleton() {}

    public static CartSingleton getCartObject()
    {
        if (singleton==null) {
            singleton = new CartSingleton();
        }
        return singleton;
    }

    public static int cartGetTotalCost(){
        int total = 0;
        for (int i = 0; i < cartProducts.size(); i++){
            int price = cartProducts.get(i).getPrice();
            int subTotal = price*(cartProducts.get(i).getQuantity());
            total += subTotal;
        }
        return total;
    }

    public static boolean cartNotInCart(String id){
        for (int i = 0; i < cartProducts.size(); i++){
            if (cartProducts.get(i).getId().equals(id)){
                return false;
            }
        }
        return true;
    }

    public static void cartAddToCart(Product p){
        p.setQuantity(1);
        cartProducts.add(p);
    }

    public static void cartRemoveItem(String id){
        for (int i = 0; i < cartProducts.size(); i++){
            if (cartProducts.get(i).getId().equals(id)){
                cartProducts.remove(i);
            }
        }
    }

}
