package alast.hm.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.daasuu.bl.BubbleLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.View;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.ArrayList;

import alast.hm.R;
import alast.hm.Adapters.HomeProdL2Adapter;
import alast.hm.Adapters.SearchAdapter;
import alast.hm.Model.Product;
import alast.hm.Model.Users;
import alast.hm.Prevalent.Prevalent;
import static alast.hm.Data.CartSingleton.cartProducts;
import static alast.hm.Data.CartSingleton.getCartObject;
import static alast.hm.Data.DataSingleton.getDataObject;
import static alast.hm.Data.DataSingleton.products;
import static alast.hm.Data.PolygonSingleton.getLocObject;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static TextView userNameTextView;
    private static NavigationView navigationView;
    private SearchView searchView;
    public static ImageView cartIcon;
    public static BubbleLayout bubbleLayout;
    private static TextView cart_items;
    private DrawerLayout drawer;

    private int backPress;
    private RecyclerView l1_rv;
    private LinearLayoutManager l1_lm;
    private HomeProdL2Adapter l1_adapter;
    private ConstraintLayout cat_btn;

    private RecyclerView search_rv;
    private LinearLayoutManager search_lm;
    private SearchAdapter search_adapter;
    private ArrayList<Product> search_products;

    private ConstraintLayout not_constraint;
    private TextView not_textview;
    private ImageView not_imageview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        assignViews();
        inits();
        login_data();
        listeners();
        getCartObject();
        getLocObject();
        snack();
    }


    private void assignViews() {
        navigationView = findViewById(R.id.user_nav_view);
        View headerView = navigationView.getHeaderView(0);
        userNameTextView = headerView.findViewById(R.id.user_nav_name);

        Toolbar toolbar = findViewById(R.id.user_home_toolbar);
        setSupportActionBar(toolbar);

        cartIcon = (ImageView) findViewById(R.id.home_float_cart);
        cart_items = (TextView) findViewById(R.id.home_cart_txt);
        bubbleLayout = (BubbleLayout) findViewById(R.id.home_cart_pop);

        drawer = findViewById(R.id.user_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        searchView = (SearchView) findViewById(R.id.user_home_search);
        View v = searchView.findViewById(R.id.search_plate);
        v.setBackgroundColor(Color.TRANSPARENT);

        l1_rv = (RecyclerView) findViewById(R.id.home_sub_rv);
        search_rv = (RecyclerView) findViewById(R.id.home_search_rv);
        cat_btn = (ConstraintLayout) findViewById(R.id.home_cat_btn);

        not_constraint = (ConstraintLayout) findViewById(R.id.home_not_cons);
        not_textview = (TextView) findViewById(R.id.home_not_txt);
        not_imageview = (ImageView) findViewById(R.id.home_not_cls);
    }

    private void inits() {
        l1_lm = new LinearLayoutManager(this);
        l1_lm.setOrientation(LinearLayoutManager.VERTICAL);
        l1_rv.setLayoutManager(l1_lm);
        l1_rv.setHasFixedSize(true);
        l1_adapter = new HomeProdL2Adapter();
        l1_rv.setAdapter(l1_adapter);

        search_lm = new LinearLayoutManager(this);
        search_lm.setOrientation(LinearLayoutManager.VERTICAL);
        search_rv.setLayoutManager(search_lm);
        search_products = new ArrayList<>();
        search_adapter = new SearchAdapter(search_products);
        search_rv.setAdapter(search_adapter);

        //home_text();

    }

    private void home_text() {
        FirebaseDatabase.getInstance().getReference("Agrinet/Texts").child("home_text").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("visibility").getValue().equals("1")){
                    String text = dataSnapshot.child("text").getValue().toString();
                    not_constraint.setVisibility(View.VISIBLE);
                    not_textview.setText(text);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void listeners() {

        NavigationView navigationView = findViewById(R.id.user_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        cartIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        bubbleLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                callSearch();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                callSearch();
                return false;
            }
        });

        search_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                searchView.clearFocus();
            }
        });


        l1_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                search_hide();
            }
        });

        cat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                l1_rv.scrollToPosition(0);
            }
        });

        not_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                not_constraint.setVisibility(View.GONE);
            }
        });
    }

    private void callSearch() {
        search_products.clear();
        search_adapter.notifyDataSetChanged();
        String q = searchView.getQuery().toString();
        if (q.equals("") || q.equals(" ") || q.equals("  ")){
            search_rv.setVisibility(View.GONE);
            return;
        }
        search_rv.setVisibility(View.VISIBLE);
        search_rv.bringToFront();
        String s = q.toLowerCase();

        for (Product[][] p1 : products) {
            for (Product[] p2 : p1) {
                for (Product p : p2) {
                    if (search_products.size() >= 10) {
                        search_adapter.notifyDataSetChanged();
                        return;
                    }
                    if (p.getName().toLowerCase().contains(s)) {
                        search_products.add(0, p);
                    } else if (p.getDescription().toLowerCase().contains(s) || p.getLabels().toLowerCase().contains(s)) {
                        search_products.add(p);
                    }
                }
            }
        }
        String[] sList = s.split(" ");

        for (Product[][] p1 : products) {
            for (Product[] p2 : p1) {
                for (Product p : p2) {
                    if (search_products.size() >= 10) {
                        search_adapter.notifyDataSetChanged();
                        return;
                    }

                    for (String sts : sList) {
                        if (p.getName().toLowerCase().contains(sts)||p.getDescription().toLowerCase().contains(sts) || p.getLabels().toLowerCase().contains(sts)) {
                            if (!search_products.contains(p)) {
                                search_products.add(p);
                                break;
                            }
                        }
                    }
                }
            }
        }
        search_adapter.notifyDataSetChanged();
    }

    public static void login_data() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);

            updateName(); //sets empty
            Prevalent.setCurrentOnlineUser(null);
        }

        else {
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);

            final String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Agrinet").child("Users").child(phone);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Prevalent.setCurrentOnlineUser(dataSnapshot.getValue(Users.class));
                        updateName();
                        updateToken();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    public static void updateName(){
        if (Prevalent.getCurrentOnlineUser()!=null){
            userNameTextView.setText(Prevalent.getCurrentOnlineUser().getName());
        }
        else{
            userNameTextView.setText("");
        }
    }

    private static void updateToken(){
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Agrinet/Users").child(phone);
            String token = FirebaseInstanceId.getInstance().getToken();
            ref.child("token").setValue(token);
        }
    }

    public static void updateCartItems(){
        cart_items.setText(cartProducts.size()+"");
    }

    private void snack() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            ConstraintLayout constraintLayout = findViewById(R.id.main_layout_home);
            Snackbar snackbar = Snackbar
                    .make(constraintLayout, "You are not Logged in", Snackbar.LENGTH_LONG)
                    .setAction("Login", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(HomeActivity.this, PhoneVerifyActivity.class);
                            startActivity(intent);
                        }
                    });

            snackbar.setActionTextColor(Color.parseColor("#7ac144"));
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
    }

    private void search_hide(){
        search_rv.setVisibility(View.GONE);
        searchView.setQuery("",false);
        searchView.clearFocus();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Home
        if (id == R.id.nav_home) {
            close_drawer();
        }

        //cart
        else if (id == R.id.nav_cart) {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        }

        //pending orders
        else if (id == R.id.nav_pending) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
                intent.putExtra("type","Pending");
                startActivity(intent);
            }

            else{
                snack();
                close_drawer();
            }
        }

        //received orders
        else if (id == R.id.nav_recieved) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
                intent.putExtra("type","Delivered");
                startActivity(intent);
            }

            else {
                snack();
                close_drawer();
            }
        }


        else if (id == R.id.nav_settings) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
            }

            else {
                snack();
                close_drawer();
            }

        }

        else if (id == R.id.nav_complain) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                Intent intent = new Intent(HomeActivity.this, ComplaintActivity.class);
                startActivity(intent);
            }

            else {
                snack();
                close_drawer();
            }

        }

        else if (id == R.id.nav_logout) {
            if (FirebaseAuth.getInstance().getCurrentUser() != null){
                FirebaseAuth.getInstance().signOut();
                Prevalent.setCurrentOnlineUser(null);
                Intent intent = new Intent(HomeActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish(); //imp
            }
            else{
                snack();
                close_drawer();
            }
        }

        else if (id == R.id.nav_login) {
            Intent intent = new Intent(HomeActivity.this, PhoneVerifyActivity.class);
            startActivity(intent);
        }

        search_hide();
        close_drawer();
        return true;
    }

    private void close_drawer() {
        drawer.closeDrawer(GravityCompat.START);
        backPress = 0;
    }

    @Override
    protected void onStart() {
        super.onStart();
        bubbleLayout.clearAnimation();
        bubbleLayout.setVisibility(View.INVISIBLE);
        backPress = 0;
        l1_adapter.cart_notify();
        updateCartItems();
        search_adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if (search_rv.getVisibility() == View.VISIBLE){
            search_hide();
            return;
        }

        backPress +=1;
        if (backPress<3){
            if(backPress==2) {
                Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onBackPressed();
        finishAffinity();
    }
}
