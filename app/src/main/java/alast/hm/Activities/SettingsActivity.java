package alast.hm.Activities;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import alast.hm.R;
import alast.hm.Prevalent.Prevalent;

import static alast.hm.Activities.HomeActivity.updateName;

public class SettingsActivity extends AppCompatActivity {

    private TextView name_txt;
    private TextView name_edit;
    private ImageView name_img;
    private Button update_btn;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        backBtn();
        assignViews();
        listeners();
        displayUserInfo();
    }

    private void listeners() {

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });
        name_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name_txt.setVisibility(View.INVISIBLE);
                name_edit.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateInfo() {
        String name = name_edit.getText().toString();

        if (TextUtils.isEmpty(name)) {
            name_edit.setError("Can't be empty");
            return;
        }

        loadingBar.setTitle("Updating Information");
        loadingBar.setMessage("Please wait..");
        loadingBar.show();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", name_edit.getText().toString());
        ref.child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(SettingsActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    Prevalent.getCurrentOnlineUser().setName(name_edit.getText().toString());
                    displayUserInfo();
                    updateName();//in home
                    recreate();
                }
                else Toast.makeText(SettingsActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        loadingBar.dismiss();
    }

    private void displayUserInfo() {
        String name = Prevalent.getCurrentOnlineUser().getName();
        name_txt.setText(name);
        name_edit.setText(name);
    }

    private void assignViews() {
        name_txt = (TextView) findViewById(R.id.settings_display_name);
        TextView phn_txt = (TextView) findViewById(R.id.settings_display_phn);
        phn_txt.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        name_edit = (EditText) findViewById(R.id.settings_edit_name);
        name_img = (ImageView) findViewById(R.id.setting_icon_name);
        update_btn = (Button) findViewById(R.id.settings_update_btn);
        loadingBar = new ProgressDialog(this);
    }

    private void backBtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Profile");
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

