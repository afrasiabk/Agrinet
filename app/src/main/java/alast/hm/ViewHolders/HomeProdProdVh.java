package alast.hm.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import alast.hm.R;

public class HomeProdProdVh extends RecyclerView.ViewHolder {
    public TextView txtProductName, txtProductPrice, txtProductDesc;
    public ImageView imageView, cartImg;
    public ConstraintLayout cartBtn;

    public HomeProdProdVh(@NonNull View itemView) {
        super(itemView);

        txtProductDesc = (TextView) itemView.findViewById(R.id.home_prod_prod_desc);
        imageView = (ImageView) itemView.findViewById(R.id.home_prod_prod_img);
        txtProductName = (TextView) itemView.findViewById(R.id.home_prod_prod_name);
        txtProductPrice =(TextView) itemView.findViewById(R.id.home_prod_prod_price);
        cartImg =(ImageView) itemView.findViewById(R.id.home_prod_prod_cart_img);
        cartBtn =(ConstraintLayout) itemView.findViewById(R.id.home_prod_prod_cart);
    }
}