package alast.hm.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import alast.hm.Model.CatListHome;
import alast.hm.R;
import alast.hm.ViewHolders.HomeProdL2Vh;

import static alast.hm.Data.DataSingleton.l2s_only;

public class HomeProdL2Adapter extends RecyclerView.Adapter<HomeProdL2Vh> {
    private HomeProdProdAdapter[] adapters;
    private RecyclerView.RecycledViewPool recycledViewPoolP;

    public HomeProdL2Adapter() {
        adapters = new HomeProdProdAdapter[l2s_only.length];
        recycledViewPoolP = new RecyclerView.RecycledViewPool();
    }

    @NonNull
    @Override
    public HomeProdL2Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.home_prod_l2_layout, parent, false);
        return new HomeProdL2Vh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeProdL2Vh holder, final int position) {
        if (holder.getItemViewType() == 0) {
            holder.title.setVisibility(View.GONE);
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setHasFixedSize(true);
            holder.recyclerView.setAdapter(new HomeCatAdapter());
        } else {

            holder.title.setText(l2s_only[position].getName());
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerView.setHasFixedSize(true);
            if (adapters[position] == null){
                adapters[position] = new HomeProdProdAdapter(position);
            }
            holder.recyclerView.setAdapter(adapters[position]);
            holder.recyclerView.setRecycledViewPool(recycledViewPoolP);
        }
    }

    public void cart_notify(){
        for (HomeProdProdAdapter a : adapters){
            if (a != null) a.notifyDataSetChanged();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (l2s_only[position] instanceof CatListHome){
            return 0;
        }
        return 1;
    }

    @Override
    public int getItemCount() {
        return l2s_only.length;
    }
}
