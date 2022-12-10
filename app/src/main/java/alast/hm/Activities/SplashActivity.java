package alast.hm.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.internal.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.Arrays;
import java.util.Comparator;

import alast.hm.R;
import alast.hm.Data.DataSingleton;
import alast.hm.Model.CatListHome;
import alast.hm.Model.Category;
import alast.hm.Model.Product;
import static alast.hm.Data.DataSingleton.dest;
import static alast.hm.Data.DataSingleton.l1s;
import static alast.hm.Data.DataSingleton.l2ps;
import static alast.hm.Data.DataSingleton.l2s;
import static alast.hm.Data.DataSingleton.l2s_only;
import static alast.hm.Data.DataSingleton.products;

public class  SplashActivity extends AppCompatActivity {

    private ImageView imageView;
    private boolean order = false;
    private String order_id;
    private String type = "Pending";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        fromIntent();
        imageView = (ImageView) findViewById(R.id.user_splash);
        Picasso.get().load(R.drawable.agrinet2).fit().into(imageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (internet()) {
            data();
        }
    }

    private void fromIntent() {
        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().getString("order_id") != null){
                order = true;
                order_id = getIntent().getExtras().getString("order_id");
                type = getIntent().getExtras().getString("type");
            }
        }
    }

    private void data() {
        dest();
        FirebaseDatabase.getInstance().getReference("Agrinet/Products").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                DataSingleton.getDataObject();

                //l1 init
                int l1_count = (int) dataSnapshot.child("level1").getChildrenCount();
                l1s = new Category[l1_count];
                l2s = new Category[l1_count][];
                products = new Product[l1_count][][];

                //l1 fill
                int l1_index = 0;
                for (DataSnapshot l1ds : dataSnapshot.child("level1").getChildren()) {
                    l1s[l1_index] = l1ds.getValue(Category.class);
                    l1_index++;
                }
                sort("l1");

                //l2 init
                for(int i = 0; i < l1s.length; i++) {
                    int l2_count = (int) dataSnapshot.child("level2").child(l1s[i].getId()).getChildrenCount();
                    l2s[i] = new Category[l2_count];
                    products[i] = new Product[l2_count][];
                }

                //l2 fill
                for(int i = 0; i < l1s.length; i++) {
                    int l2_index = 0;
                    for (DataSnapshot l2ds : dataSnapshot.child("level2").child(l1s[i].getId()).getChildren()) {
                        Category l2c = l2ds.getValue(Category.class);
                        l2s[i][l2_index] = l2c;
                        l2_index++;
                    }
                }

                sort("l2");

                //p init
                for(int i = 0; i < l1s.length; i++) {
                    for (int j = 0; j < l2s[i].length; j++) {
                        int p_count = (int) dataSnapshot.child("products").child(l2s[i][j].getId()).getChildrenCount();
                        products[i][j] = new Product[p_count];
                    }
                }

                //p fill
                for(int i = 0; i < l1s.length; i++) {
                    for (int j = 0; j < l2s[i].length; j++) {
                        int p_index = 0;
                        for (DataSnapshot pds : dataSnapshot.child("products").child(l2s[i][j].getId()).getChildren()){
                            Product p = pds.getValue(Product.class);
                            products[i][j][p_index] = p;
                            p_index++;
                        }
                    }
                }

                sort("p");

                //l2s_only
                int cat_count = 0;
                for (Category[] l2 : l2s) {
                    cat_count += l2.length;
                }

                l2s_only = new Category[cat_count+1];
                l2ps = new Product[cat_count+1][];

                l2s_only[0] = new CatListHome();
                l2ps[0] = new Product[0];
                int cat_index = 1;
                for (Category[] c1 : l2s) {
                    for (Category c2 : c1) {
                        l2s_only[cat_index] = c2;
                        cat_index++;
                    }
                }

                //l2ps
                int l2_index = 1;
                for (Product[][] p1: products) {
                    for (Product[] p2: p1){
                        l2ps[l2_index] = p2;
                        l2_index++;
                    }
                }

                splash();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    private void sort(String s) {

        if (s.equals("l1")) {
            //l1 sort
            Arrays.sort(l1s, new Comparator<Category>() {
                @Override
                public int compare(Category o1, Category o2) {
                    return o1.getPosition() - o2.getPosition();
                }
            });
        }
        else if (s.equals("l2")) {
            //l2 sort
            for (Category[] l2 : l2s) {
                Arrays.sort(l2, new Comparator<Category>() {
                    @Override
                    public int compare(Category o1, Category o2) {
                        return o1.getPosition() - o2.getPosition();
                    }
                });
            }
        }
        else {
            //products sort
            for (Product[][] p : products) {
                for (Product[] p2 : p) {
                    Arrays.sort(p2, new Comparator<Product>() {
                        @Override
                        public int compare(Product o1, Product o2) {
                            return o1.getPosition() - o2.getPosition();
                        }
                    });
                }
            }
        }
    }

    private void splash() {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser == null) {
                    home();
                }
                else login();
    }

    private void home() {
        switch (checkAppStart()) {
            case NORMAL:
                startActivity(new Intent(SplashActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finishAffinity();
                break;
            case FIRST_TIME_VERSION:
                // TODO show what's new
                startActivity(new Intent(SplashActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finishAffinity();
                break;
            case FIRST_TIME:
                startActivity(new Intent(SplashActivity.this, DemoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finishAffinity();
                break;
            default:
                startActivity(new Intent(SplashActivity.this, HomeActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finishAffinity();
                break;
        }
    }

    public enum AppStart {
        FIRST_TIME, FIRST_TIME_VERSION, NORMAL;
    }

    //app version code (not the version name!) that was used on the last
    private static final String LAST_APP_VERSION = "last_app_version";

    public AppStart checkAppStart() {
        PackageInfo pInfo;
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        AppStart appStart = AppStart.NORMAL;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            int lastVersionCode = sharedPreferences
                    .getInt(LAST_APP_VERSION, -1);
            int currentVersionCode = pInfo.versionCode;
            appStart = checkAppStart(currentVersionCode, lastVersionCode);
            // Update version in preferences
            sharedPreferences.edit().putInt(LAST_APP_VERSION, currentVersionCode).commit();
        } catch (PackageManager.NameNotFoundException e) { }
        return appStart;
    }

    public AppStart checkAppStart(int currentVersionCode, int lastVersionCode) {
        if (lastVersionCode == -1) {
            return AppStart.FIRST_TIME;
        } else if (lastVersionCode < currentVersionCode) {
            return AppStart.FIRST_TIME_VERSION;
        } else if (lastVersionCode > currentVersionCode) {
            return AppStart.NORMAL;
        } else {
            return AppStart.NORMAL;
        }
    }

    private boolean internet() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        else {
            Toast.makeText(this, "Internet Not Connected", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder.setTitle("No Internet");
            alertDialogBuilder
                    .setCancelable(false)
                    .setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            recreate();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return false;
        }
    }

    private void login() {

        final String phone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Agrinet");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Users").child(phone).exists()){
                    if (order){
                        Intent intent = new Intent(SplashActivity.this, OrderDetailsActivity.class);
                        intent.putExtra("from","confirm"); //goes to home_act onbackpress
                        intent.putExtra("type", type);
                        intent.putExtra("id", order_id);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finishAffinity();
                    }
                    else {
                        home();
                    }
                }
                else{
                    FirebaseAuth.getInstance().signOut();
                    recreate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
