package alast.hm.Data;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import alast.hm.Model.LatiLongi;

public class PolygonSingleton {

    private static DatabaseReference locsRef = FirebaseDatabase.getInstance().getReference("Agrinet/Polygon");
    public static List<LatLng> polygonArray = new ArrayList<>();;
    private static PolygonSingleton polygonSingleton;

    private PolygonSingleton() {}

    public static PolygonSingleton getLocObject() {
        if (polygonSingleton ==null) {
            polygonSingleton = new PolygonSingleton();
            fillLocsArray();
        }
        return polygonSingleton;
    }

    private static void fillLocsArray() {
        locsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot points : dataSnapshot.getChildren()) {
                    LatiLongi latiLongi = points.getValue(LatiLongi.class);
                    polygonArray.add(new LatLng(latiLongi.getLati(), latiLongi.getLongi()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
