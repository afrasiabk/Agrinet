package alast.hm.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import alast.hm.Adapters.CartAdapter;
import alast.hm.Data.CartSingleton;
import alast.hm.R;

import static alast.hm.Data.CartSingleton.cartGetTotalCost;
import static alast.hm.Data.CartSingleton.cartProducts;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recView;
    private RecyclerView.Adapter recAdapter;
    private Button placeOrderBtn;
    private TextView total_price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        assignViews();
        backBtn();
        setData();
        listeners();
    }

    private void setData() {
        recView.setLayoutManager(new LinearLayoutManager(this));
        recAdapter = new CartAdapter(cartProducts);
        recView.setAdapter(recAdapter);
    }

    private void assignViews() {
        recView = (RecyclerView) findViewById(R.id.cart_list);
        placeOrderBtn = (Button) findViewById(R.id.btn_placeorder);
        total_price = (TextView) findViewById(R.id.total_price);
    }

    private void listeners() {
        placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CartSingleton.cartGetTotalCost() <= 0) {
                    Toast.makeText(CartActivity.this, "No Items in Cart", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (CartSingleton.cartGetTotalCost() < 250){
                    new AlertDialog.Builder(CartActivity.this)
                            .setTitle("Delivery Charges")
                            .setMessage("Free delivery may only be availed for bill more than Rs.200. Otherwise Rs.50 will be charged for home delivery. Continue?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (FirebaseAuth.getInstance().getCurrentUser() == null){
                                        Intent intent = new Intent(CartActivity.this, PhoneVerifyActivity.class);
                                        intent.putExtra("from","cart");
                                        startActivity(intent);
                                        return;
                                    }
                                    Intent intent = new Intent(CartActivity.this, MapsActivity.class);
                                    startActivity(intent);
                                }
                            })
                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                if (FirebaseAuth.getInstance().getCurrentUser() == null){
                    Intent intent = new Intent(CartActivity.this, PhoneVerifyActivity.class);
                    intent.putExtra("from","cart");
                    startActivity(intent);
                    return;
                }
                Intent intent = new Intent(CartActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        recAdapter.notifyDataSetChanged();
        total_price.setText("Total Rs."+ cartGetTotalCost());
    }

    private void backBtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Cart");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setElevation(0);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
