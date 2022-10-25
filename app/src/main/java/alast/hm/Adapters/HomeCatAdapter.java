package alast.hm.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;

import alast.hm.Activities.SubCategoriesActivity;
import alast.hm.Data.DataSingleton;
import alast.hm.Model.Category;
import alast.hm.R;
import alast.hm.ViewHolders.HomeCatVh;


public class HomeCatAdapter extends RecyclerView.Adapter<HomeCatVh> {

    public HomeCatAdapter() {

    }

    @NonNull
    @Override
    public HomeCatVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_cat_layout, parent, false);
        return new HomeCatVh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeCatVh holder, final int position) {
        final Category c = DataSingleton.l1s[position];
        holder.cat_name.setText(c.getName());
        Picasso.get().load(c.getIcon()).into(holder.cat_icon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.itemView.getContext(), SubCategoriesActivity.class);
                intent.putExtra("l1_index",position);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return DataSingleton.l2s.length;
    }
}
