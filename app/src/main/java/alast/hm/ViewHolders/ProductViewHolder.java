package alast.hm.ViewHolders;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import alast.hm.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    public TextView txtProductName, txtProductDescription, txtProductPrice;
    public ImageView imageView, cartIcon;
    public LinearLayout cartBtn;

    public ProductViewHolder(@NonNull View itemView)
    {
        super(itemView);

        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
        txtProductDescription = (TextView) itemView.findViewById(R.id.product_description);
        txtProductPrice =(TextView) itemView.findViewById(R.id.product_price);
        cartIcon =(ImageView) itemView.findViewById(R.id.product_cart_btn);
        cartBtn = (LinearLayout) itemView.findViewById(R.id.product_cart);
    }

}
