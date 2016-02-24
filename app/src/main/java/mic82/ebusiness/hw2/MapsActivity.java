package mic82.ebusiness.hw2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.LinkedList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Data loc=null;
    private Button back=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        back=(Button)findViewById(R.id.map_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        if (savedInstanceState != null && savedInstanceState.getSerializable("dataobj") != null) {
            loc=(Data)savedInstanceState.getSerializable("dataobj");
            //Toast.makeText(MainActivity.this, "restore called", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = getIntent();
            loc = (Data) intent.getSerializableExtra("obj");
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("dataobj", loc);
    }
    protected void onRestoreInstanceState (Bundle inState) {
        super.onSaveInstanceState(inState);
        if (inState != null && inState.getSerializable("dataobj") != null) {
            loc=(Data)inState.getSerializable("dataobj");
            //Toast.makeText(MainActivity.this, "restore called", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context mContext=MapsActivity.this;
                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        if(loc!=null) {
            // Add a marker and move the camera
            //Toast.makeText(MapsActivity.this, "not null", Toast.LENGTH_SHORT).show();
            StringBuilder sb=new StringBuilder();
            //---- extract data -----
            sb.append("On ");
            sb.append(loc.getDate());
            sb.append(' ');
            sb.append(loc.getTime());
            sb.append(":\n");
            sb.append(loc.getContent());
            LatLng myloc = new LatLng(Double.valueOf(loc.getY()),Double.valueOf(loc.getX()));
            mMap.addMarker(new MarkerOptions().position(myloc).title(loc.getTitle()).snippet(sb.toString()));
            CameraUpdate zoom = CameraUpdateFactory.newLatLngZoom(myloc, 16.0f);
            mMap.animateCamera(zoom);
        }
    }
}
