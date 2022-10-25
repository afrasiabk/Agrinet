package alast.hm.Activities;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import alast.hm.R;
import alast.hm.Adapters.OrderedProductsAdapter;
import alast.hm.Model.Orders;
import alast.hm.Model.Product;

public class OrderDetailsActivity extends AppCompatActivity {

    private String order_id, type;
    private DatabaseReference orderRef;
    private DatabaseReference cartRef;
    private TextView address_txt, id_txt, bill_txt, placed_txt, status_txt, ordered_label;

    private RecyclerView recyclerView;
    private ArrayList<Product> products;
    private LinearLayoutManager layoutManager;
    private OrderedProductsAdapter adapter;
    private ProgressBar progressBar;
    private boolean from_confirm = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        fromIntent();
        backBtn();
        assignViews();
        setData();
    }

    private void fromIntent() {
        if (getIntent().hasExtra("from")){
            if (getIntent().getStringExtra("from").equals("confirm")){
                from_confirm = true;
            }
        }
        order_id = getIntent().getStringExtra("id");
        type = getIntent().getStringExtra("type");
    }

    private void assignViews() {
        address_txt = (TextView) findViewById(R.id.orders_details_address);
        id_txt = (TextView) findViewById(R.id.orders_details_id);
        bill_txt = (TextView) findViewById(R.id.orders_details_bill);
        placed_txt = (TextView) findViewById(R.id.orders_details_time);
        status_txt = (TextView) findViewById(R.id.orders_delivery_status);
        recyclerView  = (RecyclerView) findViewById(R.id.orders_details_rv);
        progressBar = (ProgressBar) findViewById(R.id.orders_details_pb);
        ordered_label =(TextView) findViewById(R.id.order_det_olabel);
    }

    private void setData() {
        orderRef = FirebaseDatabase.getInstance().getReference("Orders").child(type).child(order_id);
        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Orders o = dataSnapshot.getValue(Orders.class);
                address_txt.setText(o.getAddress());
                id_txt.setText("Order Id: "+o.getId());
                if (o.getCharges().equals("yes")){
                    bill_txt.setText("Rs "+o.getBill()+" (Rs.50 delivery charges)");
                }
                else{
                    bill_txt.setText("Rs "+o.getBill());
                }
                DateFormat formatter = new SimpleDateFormat("hh:mm a  dd/MM/yy", Locale.getDefault());
                formatter.setTimeZone(TimeZone.getTimeZone("GMT+5"));
                if (type.equals("Delivered")) {
                    placed_txt.setVisibility(View.GONE);
                    ordered_label.setVisibility(View.GONE);
                    String del_time = "Delivered (" + formatter.format(new Date(o.getDelivered_time()))+")";
                    status_txt.setText(del_time);
                }
                else  {
                    placed_txt.setText(formatter.format(new Date(o.getPlaced_time())));
                    status_txt.setText(o.getStatus());
                }
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        products = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new OrderedProductsAdapter(products);
        recyclerView.setAdapter(adapter);

        cartRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(type).child(order_id).child("cart");
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot p : dataSnapshot.getChildren()){
                    products.add(p.getValue(Product.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        if (from_confirm){
            Intent intent = new Intent(OrderDetailsActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        else {
            super.onBackPressed();
        }
    }

    private void backBtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Order Details");
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
