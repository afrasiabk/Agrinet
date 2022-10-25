package alast.hm.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import alast.hm.Model.Product;
import alast.hm.R;
import alast.hm.ViewHolders.OrderedProductsVh;

public class OrderedProductsAdapter extends RecyclerView.Adapter <OrderedProductsVh> {

    private ArrayList<Product> products;

    public OrderedProductsAdapter(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public OrderedProductsVh onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View orderView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ordered_products_layout,viewGroup,false);
        return new OrderedProductsVh(orderView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderedProductsVh holder, int i) {

        holder.name.setText(products.get(i).getName());
        holder.price.setText("Price: Rs "+products.get(i).getPrice());
        holder.id.setText("Pid: "+products.get(i).getId());
        holder.quantity.setText("Items: "+products.get(i).getQuantity());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
