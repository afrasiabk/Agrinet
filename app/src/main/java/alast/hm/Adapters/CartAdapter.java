package alast.hm.Adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import alast.hm.Data.CartSingleton;
import alast.hm.Model.Product;
import alast.hm.Activities.ProductDetailsActivity;
import alast.hm.R;
import alast.hm.ViewHolders.HomeCartViewHolder;

import static alast.hm.Data.CartSingleton.cartGetTotalCost;

public class CartAdapter extends RecyclerView.Adapter<HomeCartViewHolder> {

    private ArrayList<Product> products;
    private TextView totalPrice;

    public CartAdapter(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public HomeCartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = (View) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cart_items_layout, viewGroup, false);
        this.totalPrice = (TextView) ((Activity)viewGroup.getContext()).findViewById(R.id.total_price);
        totalPrice.setText("Total Rs."+ cartGetTotalCost());
        return new HomeCartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeCartViewHolder cartViewHolder, final int position) {

        final int i = cartViewHolder.getAdapterPosition();
        final String name, subTotal;
        final int price, quantity;
        name = products.get(i).getName();
        price = products.get(i).getPrice();
        quantity = products.get(i).getQuantity();
        subTotal = (price * quantity)+"";
        cartViewHolder.txtProductName.setText(name);
        cartViewHolder.txtProductQuantity.setText(quantity+"");
        cartViewHolder.txtProductSubTotal.setText(subTotal+"/-");
        Picasso.get().load(products.get(i).getImage()).into(cartViewHolder.image);

        cartViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(cartViewHolder.itemView.getContext(), ProductDetailsActivity.class);
                intent.putExtra("pid",products.get(i).getId());
                intent.putExtra("from","home");
                cartViewHolder.itemView.getContext().startActivity(intent);
            }
        });
        cartViewHolder.decrement.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (quantity>1) {
                    products.get(i).setQuantity(quantity-1);
                    totalPrice.setText("Total Rs."+ cartGetTotalCost());
                    notifyItemChanged(i);
                }
            }
        });
        cartViewHolder.increment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (quantity< 20 ) {
                    products.get(i).setQuantity(quantity+1);
                    totalPrice.setText("Total Rs."+ cartGetTotalCost());
                    notifyItemChanged(i);
                }
                else Toast.makeText(cartViewHolder.itemView.getContext(), "may only add upto 20 items in a single order", Toast.LENGTH_LONG).show();

            }
        });

        cartViewHolder.cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = products.get(i).getId();
                CartSingleton.cartRemoveItem(id);
                totalPrice.setText("Total Rs."+ cartGetTotalCost());
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

}
