package alast.hm.Adapters;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import alast.hm.Activities.ProductDetailsActivity;
import alast.hm.Activities.SubCategoriesActivity;
import alast.hm.Animation.CircleAnimationUtil;
import alast.hm.Data.CartSingleton;
import alast.hm.Data.DataSingleton;
import alast.hm.Model.Product;
import alast.hm.R;
import alast.hm.ViewHolders.HomeProdProdVh;

public class SubCatProdAdapter extends RecyclerView.Adapter<HomeProdProdVh> {
    private int l1_pos;
    private int l2_pos;

    public SubCatProdAdapter(int l1_pos, int l2_pos) {
        this.l1_pos = l1_pos;
        this.l2_pos = l2_pos;
    }

    @NonNull
    @Override
    public HomeProdProdVh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.home_prod_prod_layout, parent, false);
        return new HomeProdProdVh(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final HomeProdProdVh productViewHolder, final int i) {
        final Product p = DataSingleton.products[l1_pos][l2_pos][i];
        productViewHolder.txtProductDesc.setText(p.getDescription());
        productViewHolder.txtProductName.setText(p.getName());
        productViewHolder.txtProductPrice.setText(p.getPrice()+"/-");
        Picasso.get().load(p.getImage()).fit().centerCrop().into(productViewHolder.imageView);

        if (CartSingleton.cartNotInCart(p.getId())) {
            productViewHolder.cartImg.setImageResource(R.drawable.cart_plus);
            productViewHolder.cartImg.setColorFilter(Color.parseColor("#000000"));;
        }

        else{
            productViewHolder.cartImg.setImageResource(R.drawable.cart);
            productViewHolder.cartImg.setColorFilter(ContextCompat.getColor(productViewHolder.itemView.getContext(), R.color.colorPrimaryDark));;
        }

        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(productViewHolder.itemView.getContext(), ProductDetailsActivity.class);
                intent.putExtra("pid",  p.getId());
                productViewHolder.itemView.getContext().startActivity(intent);
            }
        });

        productViewHolder.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CartSingleton.cartNotInCart(p.getId())) {
                    notifyItemChanged(i);
                    animation(productViewHolder.imageView, (Activity) productViewHolder.imageView.getContext());
                    CartSingleton.cartAddToCart(p);
                }
                else{
                    CartSingleton.cartRemoveItem(p.getId());
                    SubCategoriesActivity.updateCartItems();
                    SubCategoriesActivity.bubbleLayout.clearAnimation();
                    SubCategoriesActivity.bubbleLayout.setVisibility(View.INVISIBLE);
                    notifyItemChanged(i);
                }

            }
        });
    }

    private void animation(ImageView fromView, final Activity context) {
        new CircleAnimationUtil().attachActivity(context).setTargetView(fromView).setMoveDuration(500).setDestView(SubCategoriesActivity.cartIcon).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                SubCategoriesActivity.cartIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_anim));
                SubCategoriesActivity.bubbleLayout.setVisibility(View.VISIBLE);
                SubCategoriesActivity.updateCartItems();
                bubbleAnim(context);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();

    }

    private void bubbleAnim(final Activity context) {
        Animation bubbleAnim = AnimationUtils.loadAnimation(context, R.anim.bubble_fade);
        bubbleAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SubCategoriesActivity.bubbleLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        SubCategoriesActivity.bubbleLayout.startAnimation(bubbleAnim);
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
        return DataSingleton.products[l1_pos][l2_pos].length;
    }
}
