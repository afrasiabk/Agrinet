package alast.hm.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import alast.hm.R;
import alast.hm.ViewHolders.SubCatLayoutVh;

import static alast.hm.Data.DataSingleton.l2s;

public class SubCatAdapter extends RecyclerView.Adapter<SubCatLayoutVh> {

    private int l1_index;
    public SubCatAdapter(int l1_index) {
        this.l1_index = l1_index;
    }

    @NonNull
    @Override
    public SubCatLayoutVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sub_cats_layout, parent, false);
        return new SubCatLayoutVh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SubCatLayoutVh holder, final int position) {
        if (holder.recyclerView.getAdapter() == null) {
            holder.title.setText(l2s[l1_index][position].getName());
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setAdapter(new SubCatProdAdapter(l1_index, position));
            holder.recyclerView.setNestedScrollingEnabled(false);
            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setHasFixedSize(true);
        }
        else {
            holder.recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return l2s[l1_index].length;
    }
}
