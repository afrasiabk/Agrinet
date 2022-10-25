package alast.hm.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daasuu.bl.BubbleLayout;

import alast.hm.R;
import alast.hm.Adapters.SubCatAdapter;
import alast.hm.Data.DataSingleton;

import static alast.hm.Data.CartSingleton.cartProducts;

public class SubCategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private int l1_index;

    public static ImageView cartIcon;
    public static BubbleLayout bubbleLayout;
    private static TextView cart_items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_categories);
        l1_index = getIntent().getIntExtra("l1_index",0);
        backBtn();
        assignViews();
        data();
    }

    private void assignViews() {
        cartIcon = (ImageView) findViewById(R.id.sub_cats_cart_img);
        cart_items = (TextView) findViewById(R.id.sub_cats_cart_txt);
        bubbleLayout = (BubbleLayout) findViewById(R.id.sub_cats_cart_pop);

        recyclerView = (RecyclerView) findViewById(R.id.sub_cats_rv);
        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubCategoriesActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
    }

    private void data() {
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        adapter = new SubCatAdapter(l1_index);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bubbleLayout.clearAnimation();
        bubbleLayout.setVisibility(View.INVISIBLE);
        updateCartItems();
        adapter.notifyDataSetChanged();
    }

    private void backBtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(DataSingleton.l1s[l1_index].getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
    }

    public static void updateCartItems(){
        cart_items.setText(cartProducts.size()+"");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
