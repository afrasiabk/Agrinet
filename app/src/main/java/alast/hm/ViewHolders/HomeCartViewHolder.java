package alast.hm.ViewHolders;


import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import alast.hm.R;

public class HomeCartViewHolder extends RecyclerView.ViewHolder  {
    public TextView txtProductName, txtProductQuantity, txtProductSubTotal;
    public ImageView increment, decrement, cross, image;

    public HomeCartViewHolder(View itemView)
    {
        super(itemView);

        image = itemView.findViewById(R.id.cart_prod_img);
        txtProductName = itemView.findViewById(R.id.cart_product_name);
        txtProductQuantity = itemView.findViewById(R.id.cart_product_quantity);
        txtProductSubTotal = itemView.findViewById(R.id.cart_product_subTotal);

        increment = (ImageView) itemView.findViewById(R.id.increment_cart) ;
        decrement = (ImageView) itemView.findViewById(R.id.decrement_cart) ;
        cross = (ImageView) itemView.findViewById(R.id.cancel_cart);
    }

}