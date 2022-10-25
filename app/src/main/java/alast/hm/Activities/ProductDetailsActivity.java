package alast.hm.Activities;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import alast.hm.R;
import alast.hm.Animation.CircleAnimationUtil;
import alast.hm.Data.DataSingleton;
import alast.hm.Model.Product;

import static alast.hm.Data.CartSingleton.cartAddToCart;
import static alast.hm.Data.CartSingleton.cartNotInCart;
import static alast.hm.Data.CartSingleton.cartRemoveItem;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView pName, pDesc, pPrice;
    private ImageView pImage, imgCart;
    private Button cartbtn, rmvCartbtn;
    private String pid;
    private Product product;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);
        backbtn();
        fromIntent();
        assignView();
        listeners();
        setProductDetails();
    }

    private void fromIntent() {
        Intent startIntent = getIntent();
        Bundle bundle = startIntent.getExtras();
        if (bundle != null) {
            pid = getIntent().getStringExtra("pid");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!cartNotInCart(pid)){ // if inside cartIcon
            insideViews();
        }
        else if (cartNotInCart(pid)){ //not in cartIcon
            outsideViews();
        }
    }

    private void outsideViews() {
        rmvCartbtn.setVisibility(View.INVISIBLE);
        cartbtn.setVisibility(View.VISIBLE);
    }

    private void insideViews() {
        rmvCartbtn.setVisibility(View.VISIBLE);
        cartbtn.setVisibility(View.INVISIBLE);
    }

    private void assignView() {

        pName = (TextView) findViewById(R.id.product_name_details);
        pDesc = (TextView) findViewById(R.id.product_description_details);
        pPrice = (TextView) findViewById(R.id.product_price_details);
        pImage = (ImageView) findViewById(R.id.product_image_details);
        cartbtn = (Button) findViewById(R.id.add_to_cart_btn);
        rmvCartbtn = (Button) findViewById(R.id.rmv_to_cart_btn) ;
        progressBar = (ProgressBar) findViewById(R.id.product_pb_details);
        imgCart = (ImageView) findViewById(R.id.product_imgnry_cart);
    }

    private void listeners() {

        cartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtoCart();
            }
        });

        rmvCartbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFromCart();
            }
        });
    }

    private void removeFromCart() {
        if (!cartNotInCart(pid)) {
            cartRemoveItem(pid);
            outsideViews();
        }
    }

    private void addtoCart() {
        if (cartNotInCart(pid)) {
            cartAddToCart(product);
            insideViews();
            animation(pImage, this);
            new Handler().postDelayed(cart_anim,1500);
        }
    }
    private void animation(ImageView fromView, final Activity context) {
        new CircleAnimationUtil().attachActivity(context).setTargetView(fromView).setMoveDuration(500).setDestView(HomeActivity.cartIcon).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                imgCart.startAnimation(AnimationUtils.loadAnimation(context, R.anim.click_anim));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();

    }

    private Runnable cart_anim = new Runnable() {
        public void run() {
            onBackPressed();
        }
    };

    private void setProductDetails() {
        for (Product[] p1: DataSingleton.l2ps) {
            for (Product p : p1) {
                    if (p.getId().equals(pid)) {
                        product = p;
                        pName.setText(product.getName());
                        pDesc.setText(product.getDescription());
                        pPrice.setText(product.getPrice()+"/-");
                        Picasso.get().load(product.getImage()).fit().into(pImage);
                        if (getSupportActionBar()!=null) {
                            getSupportActionBar().setTitle(product.getName());
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
            }
        }
    }

    private void backbtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}
