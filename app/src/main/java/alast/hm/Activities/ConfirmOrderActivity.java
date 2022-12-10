package alast.hm.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import alast.hm.R;
import alast.hm.Data.CartSingleton;
import alast.hm.Prevalent.Prevalent;

import static alast.hm.Data.CartSingleton.cartProducts;

public class ConfirmOrderActivity extends AppCompatActivity {

    private static final String TAG = ConfirmOrderActivity.class.getSimpleName();
    //not placed stuff
    private ConstraintLayout not_placed_layout;
    private TextView phone_view, name_view, cost_view, add_txt;
    private TextView confirm_time;
    private Button confirm_btn;
    private String delivery_time = "40";

    private ConstraintLayout not_constraint;
    private TextView not_textview;
    private ImageView not_imageview;

    //placed stuff
    private ConstraintLayout placed_layout;
    private TextView placed_id, placed_name, placed_phn, placed_bill, placed_track_btn;
    private ImageView placed_home_icon;

    private String id_str;
    private ProgressDialog loadingBar;
    private String lati, longi, address;

    private boolean placed;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);
        fromIntent();
        backBtn();
        assignViews();
        listeners();
    }

    private void fromIntent() {
        lati = getIntent().getStringExtra("lati");
        longi = getIntent().getStringExtra("longi");
        address = getIntent().getStringExtra("address");
    }

    private void assignViews() {
        placed = false;
        not_placed_layout = (ConstraintLayout) findViewById(R.id.confirm_not_placed);
        phone_view = (TextView) findViewById(R.id.cphn_phn);
        name_view = (TextView) findViewById(R.id.cname_name);
        cost_view = (TextView) findViewById(R.id.cbill_bill);
        confirm_btn = (Button) findViewById(R.id.confirm_order_btn);
        add_txt = (TextView) findViewById(R.id.cadd_add);
        confirm_time = (TextView) findViewById(R.id.confirm_time);
        not_constraint = (ConstraintLayout) findViewById(R.id.cnfrm_not_cons);
        not_textview = (TextView) findViewById(R.id.cnfrm_not_txt);
        not_imageview = (ImageView) findViewById(R.id.cnfrm_not_cls);
        fetchDeliveryTime();

        placed_layout = (ConstraintLayout) findViewById(R.id.confirm_placed);
        placed_id = (TextView) findViewById(R.id.placed_id);
        placed_name = (TextView) findViewById(R.id.placed_name);
        placed_phn = (TextView) findViewById(R.id.placed_phn);
        placed_bill = (TextView) findViewById(R.id.placed_cost);
        placed_track_btn = (TextView) findViewById(R.id.placed_track_btn);
        placed_home_icon = (ImageView) findViewById(R.id.placed_home_icn);

        loadingBar = new ProgressDialog(this);

    }

    private void fetchDeliveryTime() {
        FirebaseDatabase.getInstance().getReference("Agrinet/Texts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("delivery_time").exists()){
                    delivery_time = dataSnapshot.child("delivery_time").getValue().toString();
                    setDeliveryTime();
                }
                if (dataSnapshot.child("confirm_text").child("visibility").getValue().equals("1")){
                    //0000 - 2359
                    int from = Integer.parseInt(dataSnapshot.child("confirm_text").child("from").getValue().toString());
                    int to = Integer.parseInt(dataSnapshot.child("confirm_text").child("to").getValue().toString());
                    String text = dataSnapshot.child("confirm_text").child("text").getValue().toString();
                    not_textview.setText(text);
                    Date date = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    int t = c.get(Calendar.HOUR_OF_DAY) * 100 + c.get(Calendar.MINUTE);
                    if (!(to > from && t >= from && t <= to || to < from && (t >= from || t <= to))) {
                        not_constraint.setVisibility(View.VISIBLE);
                        not_constraint.bringToFront();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setDeliveryTime() {
        confirm_time.setText("Within "+delivery_time+" minutes");
    }

    private void fillViews() {

        cost_view.setText("Rs. "+CartSingleton.cartGetTotalCost());
        if (CartSingleton.cartGetTotalCost() <200){
            cost_view.setText("Rs. "+(CartSingleton.cartGetTotalCost() + 50));
        }
        name_view.setText(Prevalent.getCurrentOnlineUser().getName());
        phone_view.setText("0"+Prevalent.getCurrentOnlineUser().getPhone().substring(3,13));
        add_txt.setText(address);
        setDeliveryTime();

        placed_name.setText(Prevalent.getCurrentOnlineUser().getName());
        placed_phn.setText("0"+Prevalent.getCurrentOnlineUser().getPhone().substring(3,13));
        placed_bill.setText("Rs. "+CartSingleton.cartGetTotalCost());
        if (CartSingleton.cartGetTotalCost() <200){
            placed_bill.setText("Rs. "+(CartSingleton.cartGetTotalCost() + 50));
        }
        placed_id.setText("ORDER ID ");
    }

    private void listeners() {

        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CartSingleton.cartGetTotalCost() > 0) {
                    placeOrder();
                }
                else Toast.makeText(ConfirmOrderActivity.this, "No Items in Cart", Toast.LENGTH_SHORT).show();
            }
        });

        placed_track_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmOrderActivity.this, OrderDetailsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("type","Pending");
                intent.putExtra("id",id_str);
                intent.putExtra("from","confirm");
                startActivity(intent);
                finish();
            }
        });

        placed_home_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        not_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                not_constraint.setVisibility(View.GONE);
            }
        });

    }

    private void placeOrder() {
        generateId();
    }

    @Override
    protected void onStart() {
        fillViews();
        super.onStart();
    }

    private void generateId() {
        loadingBar.setTitle("Place Order");
        loadingBar.setMessage("processing");
        loadingBar.show();

        DatabaseReference idRef = FirebaseDatabase.getInstance().getReference().child("Agrinet").child("Ids").child("Orders");
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String count_strr = dataSnapshot.getValue().toString();
                    id_str = (Integer.parseInt(count_strr)+1)+"";
                }
                else id_str = "1";
                //requestcurrentTime();
                place();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    } //calls place();

    private void place() {
        HashMap<String, Object> ordermap = new HashMap<>();
        if (CartSingleton.cartGetTotalCost() <200){
            ordermap.put("bill", (CartSingleton.cartGetTotalCost()+50)+"");
            ordermap.put("charges", "yes");
        }
        else{
            ordermap.put("bill", CartSingleton.cartGetTotalCost()+"");
            ordermap.put("charges", "no");
        }
        ordermap.put("cart", cartProducts);
        ordermap.put("id", id_str);
        ordermap.put("placed_time", ServerValue.TIMESTAMP);
        ordermap.put("user_phone", Prevalent.getCurrentOnlineUser().getPhone());
        ordermap.put("user_name", Prevalent.getCurrentOnlineUser().getName());
        ordermap.put("lati", lati);
        ordermap.put("longi", longi);
        ordermap.put("address", address);
        ordermap.put("status", "Pending");

        final DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Agrinet").child("Orders").child("Pending");
        orderRef.child(id_str).setValue(ordermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ConfirmOrderActivity.this, "SUCCESS", Toast.LENGTH_LONG).show();
                    orderPlacedViews(id_str);
                    updateIds();
                    cartProducts.clear();
                    loadingBar.dismiss();

                } else {
                    Toast.makeText(ConfirmOrderActivity.this, "Place Order Error: " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                    recreate();
                }
            }
        });
    } //calls placedviews, updateids

    private void orderPlacedViews(String id){
        fillViews(); //to refresh!
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setTitle("OneApp");
        }
        placed_id.setText("ORDER ID "+id);
        not_placed_layout.setVisibility(View.INVISIBLE);
        placed_layout.setVisibility(View.VISIBLE);
        placed = true;
    }

    private void updateIds() {
        DatabaseReference idRef = FirebaseDatabase.getInstance().getReference().child("Agrinet").child("Ids").child("Orders");
        idRef.setValue(id_str);
    }

    private void backBtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Order Confirmation");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (placed){
            Intent intent = new Intent(ConfirmOrderActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    }


}