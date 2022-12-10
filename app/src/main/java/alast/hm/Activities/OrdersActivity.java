package alast.hm.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

import alast.hm.R;
import alast.hm.Adapters.OrdersAdapter;
import alast.hm.Model.Orders;

public class OrdersActivity extends AppCompatActivity {

    private static final String TAG = OrdersActivity.class.getSimpleName();
    private String type;
    private String user_phone;
    private RecyclerView recyclerView;
    private TextView empty_label;
    private DatabaseReference ordersRef;
    private LinearLayoutManager layoutManager;
    private ArrayList<Orders> orders;
    private OrdersAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        fromIntent();
        backBtn();
        assignViews();
        setData();
    }

    private void fromIntent() {
        type = getIntent().getStringExtra("type");
    }

    private void assignViews() {
        user_phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        recyclerView = (RecyclerView) findViewById(R.id.user_orders_rv);
        empty_label = (TextView) findViewById(R.id.user_orders_empty);
        progressBar = (ProgressBar) findViewById(R.id.user_orders_pb);
    }

    private void setData() {
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        orders = new ArrayList<>();
        adapter = new OrdersAdapter(orders, type);
        recyclerView.setAdapter(adapter);

        ordersRef = FirebaseDatabase.getInstance().getReference("Agrinet/Orders").child(type);
        Query q = ordersRef.orderByChild("user_phone").equalTo(user_phone);

        q.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = (int) dataSnapshot.getChildrenCount();
                if (count == 0){
                    empty_label.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
                else {
                    for (DataSnapshot order : dataSnapshot.getChildren()) {
                        orders.add(0, order.getValue(Orders.class));
                    }
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void backBtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle(type+" Orders");
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
