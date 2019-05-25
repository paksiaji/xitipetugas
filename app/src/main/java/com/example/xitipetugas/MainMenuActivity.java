package com.example.xitipetugas;

import android.Manifest;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainMenuActivity extends AppCompatActivity implements LocationListener, HomeFragment.OnFragmentInteractionListener, ProfileFragment.OnFragmentInteractionListener, GantiPasswordFragment.OnFragmentInteractionListener {
    LocationManager locationManager;
    public static int laporanCount = 0;
    double latitude, longitude;
    ProgressDialog dialog;
    boolean firebaseListenerAdded = false;
    boolean firstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(this);
        dialog.setMessage("loading...");
        dialog.show();
        getLocation();
        setContentView(R.layout.activity_main_menu);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment(), "HomeFragment").commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = null;
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (f instanceof HomeFragment) {
            HomeFragment homeFragment = (HomeFragment) f;
            drawer = homeFragment.drawer;
            Toast.makeText(this, "HomeFragment", Toast.LENGTH_SHORT).show();
        } else if (f instanceof ProfileFragment) {
            ProfileFragment profileFragment = (ProfileFragment) f;
            drawer = profileFragment.drawer;
            Toast.makeText(this, "ProfileFragment", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Ganti Password", Toast.LENGTH_SHORT).show();
        }

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    //Fragment Stuff
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void addFirebaseListener() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Posts");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(firstLoad == true){
                    firstLoad = false;
                }else{
                    loadLaporan();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (longitude == 0.0 && latitude == 0.0) {
            dialog.dismiss();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d("PostLoad", "ulala");
            if(firebaseListenerAdded == false){
                addFirebaseListener();
                firebaseListenerAdded = true;
            }
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e("sss","Oops Not Granted");
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1, 1, this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadLaporan(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
        Call<List<LaporanModel>> loadLaporan = retrofitInterfaces.loadPost(String.valueOf(latitude),String.valueOf(longitude));
        loadLaporan.enqueue(new Callback<List<LaporanModel>>() {
            @Override
            public void onResponse(Call<List<LaporanModel>> call, Response<List<LaporanModel>> response) {
                List<LaporanModel> list = response.body();
                Toast.makeText(MainMenuActivity.this, String.valueOf(list.size()), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<List<LaporanModel>> call, Throwable t) {
                Log.e("MainMenuActivity", t.toString());
            }
        });
    }
}
