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

import java.util.ArrayList;

import alast.hm.Activities.HomeActivity;
import alast.hm.Activities.ProductDetailsActivity;
import alast.hm.Animation.CircleAnimationUtil;
import alast.hm.Data.CartSingleton;
import alast.hm.Model.Product;
import alast.hm.R;
import alast.hm.ViewHolders.ProductViewHolder;

public class SearchAdapter extends RecyclerView.Adapter<ProductViewHolder> {
    private ArrayList<Product> products;

    public SearchAdapter(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_layout, parent, false);
        return new ProductViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductViewHolder productViewHolder, final int i) {
        final Product p =products.get(i);
        productViewHolder.txtProductDescription.setText(p.getDescription());
        productViewHolder.txtProductName.setText(p.getName());
        productViewHolder.txtProductPrice.setText(p.getPrice()+"/-");
        Picasso.get().load(p.getImage()).fit().centerCrop().into(productViewHolder.imageView);

        if (CartSingleton.cartNotInCart(p.getId())) {
            productViewHolder.cartIcon.setImageResource(R.drawable.cart_plus);
            productViewHolder.cartIcon.setColorFilter(Color.parseColor("#000000"));;
        }

        else{
            productViewHolder.cartIcon.setImageResource(R.drawable.cart);
            productViewHolder.cartIcon.setColorFilter(ContextCompat.getColor(productViewHolder.itemView.getContext(), R.color.colorPrimaryDark));;
        }

        productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(productViewHolder.itemView.getContext(), ProductDetailsActivity.class);
                intent.putExtra("pid",  p.getId());
                intent.putExtra("from","home");
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
                    HomeActivity.updateCartItems();
                    HomeActivity.bubbleLayout.clearAnimation();
                    HomeActivity.bubbleLayout.setVisibility(View.INVISIBLE);
                    notifyItemChanged(i);
                }

            }
        });
    }

    private void animation(ImageView fromView, final Activity context) {
        new CircleAnimationUtil().attachActivity(context).setTargetView(fromView).setMoveDuration(500).setDestView(HomeActivity.cartIcon).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                HomeActivity.cartIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_anim));
                HomeActivity.bubbleLayout.setVisibility(View.VISIBLE);
                HomeActivity.updateCartItems();
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
                HomeActivity.bubbleLayout.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        HomeActivity.bubbleLayout.startAnimation(bubbleAnim);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
