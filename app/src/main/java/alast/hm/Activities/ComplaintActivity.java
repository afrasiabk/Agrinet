package alast.hm.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import alast.hm.Prevalent.Prevalent;
import alast.hm.R;

public class ComplaintActivity extends AppCompatActivity {

    private Button submit_btn, close_btn;
    private EditText complain_edit;
    private TextView response_txt;
    private ProgressBar progressBar;
    private String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        backBtn();
        assignViews();
        listeners();
    }

    private void assignViews() {
        submit_btn = (Button) findViewById(R.id.user_complaint_sbmt);
        close_btn = (Button) findViewById(R.id.user_complaint_done);
        complain_edit = (EditText) findViewById(R.id.user_complaint_edit);
        response_txt = (TextView) findViewById(R.id.user_complaint_txt);
        progressBar = (ProgressBar) findViewById(R.id.user_complaint_pb);
    }

    private void listeners(){
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void submit() {
        text = complain_edit.getText().toString();
        if (text.isEmpty()){
            complain_edit.setError("empty");
            return;
        }
        generateKey();
    }

    private void generateKey() {

        submit_btn.setVisibility(View.INVISIBLE);
        complain_edit.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference idRef = FirebaseDatabase.getInstance().getReference().child("Ids").child("Complaints");
        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int id_str;
                if (dataSnapshot.exists()){
                    id_str = Integer.parseInt(dataSnapshot.getValue().toString())+1;
                }
                else id_str = 1;
                startUpload(id_str);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void startUpload(final int id) {
        HashMap<String,Object> map = new HashMap<>();
        map.put("id",id+"");
        map.put("phone",Prevalent.getCurrentOnlineUser().getPhone());
        map.put("name",Prevalent.getCurrentOnlineUser().getName());
        map.put("complaint",text);

        DatabaseReference suggest = FirebaseDatabase.getInstance().getReference("Complaints");
        suggest.child(id+"").updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    updateIds(id);
                }
                else {
                    recreate();
                    Toast.makeText(ComplaintActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateIds(int id_str) {
        DatabaseReference idRef = FirebaseDatabase.getInstance().getReference("Ids").child("Complaints");
        idRef.setValue(id_str);
        close_btn.setVisibility(View.VISIBLE);
        response_txt.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void backBtn() {
        if (getSupportActionBar()!=null) {
            getSupportActionBar().setTitle("Make A Complaint");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
