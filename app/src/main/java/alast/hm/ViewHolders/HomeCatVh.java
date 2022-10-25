package alast.hm.ViewHolders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import alast.hm.R;

public class HomeCatVh extends RecyclerView.ViewHolder {
    public TextView cat_name;
    public ImageView cat_icon;

    public HomeCatVh(@NonNull View itemView) {
        super(itemView);
        cat_icon = (ImageView) itemView.findViewById(R.id.home_level1_icon);
        cat_name = (TextView) itemView.findViewById(R.id.home_level1_name);
    }
}
