package alast.hm.ViewHolders;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import alast.hm.R;

public class SubCatLayoutVh extends RecyclerView.ViewHolder {
    public TextView title;
    public RecyclerView recyclerView;

    public SubCatLayoutVh(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.sub_cats_lay_lbl);
        recyclerView = itemView.findViewById(R.id.sub_cats_lay_rv);
    }
}
