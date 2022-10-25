package alast.hm.ViewHolders;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import alast.hm.R;

public class OrderedProductsVh extends RecyclerView.ViewHolder
{
    public TextView name, id, price, quantity;

    public OrderedProductsVh(View itemView)
    {
        super(itemView);

        name = itemView.findViewById(R.id.ordered_prod_name);
        id = itemView.findViewById(R.id.ordered_prod_id);
        price = itemView.findViewById(R.id.ordered_prod_price);
        quantity = itemView.findViewById(R.id.ordered_prod_quan);
    }
}