package alast.hm.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import alast.hm.R;

public class HomeProdL2Vh extends RecyclerView.ViewHolder {
    public TextView title;
    public RecyclerView recyclerView;

    public HomeProdL2Vh(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.home_prod_l2_lbl);
        recyclerView = itemView.findViewById(R.id.home_prod_l2_rv);
    }
}