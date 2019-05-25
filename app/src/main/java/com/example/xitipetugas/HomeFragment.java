package com.example.xitipetugas;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment implements LocationListener {
    private OnFragmentInteractionListener mListener;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    LocationManager locationManager;
    RecyclerView rvList;
    SwipeRefreshLayout refreshLayout;

    double latitude, longitude;
    ProgressDialog dialog;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Memuat...");
        dialog.setCancelable(false);
        dialog.show();

        drawer = view.findViewById(R.id.drawer_layout);
        toolbar = view.findViewById(R.id.app_toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer ,toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        rvList = view.findViewById(R.id.rvList);
        refreshLayout = view.findViewById(R.id.refreshLayout);

        navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_profile :
                        drawer.closeDrawer(GravityCompat.START);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment(),"ProfileFragment").commit();
                        break;
                    case R.id.nav_ganti_password :
                        drawer.closeDrawer(GravityCompat.START);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new GantiPasswordFragment(),"GantiPasswordFragment").commit();
                        break;
                }
                return true;
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadPost();
            }
        });

        loadPost();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if(longitude == 0.0 && latitude == 0.0){
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            loadPost();
            Log.d("PostLoad","ulala");
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void getLocation(){
        try{
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1,1,this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    private void loadPost(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);

        Call<List<LaporanModel>> loadLaporan = retrofitInterfaces.loadPost(String.valueOf(latitude),String.valueOf(longitude));
        loadLaporan.enqueue(new Callback<List<LaporanModel>>() {
            @Override
            public void onResponse(Call<List<LaporanModel>> call, Response<List<LaporanModel>> response) {
                refreshLayout.setRefreshing(false);
                dialog.dismiss();
                List<LaporanModel> list = response.body();
                LaporanAdapter adapter = new LaporanAdapter(getActivity(),list);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                rvList.setLayoutManager(linearLayoutManager);
                rvList.setAdapter(adapter);
                MainMenuActivity activity = (MainMenuActivity)getActivity();
                activity.laporanCount = list.size();
            }

            @Override
            public void onFailure(Call<List<LaporanModel>> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }
}
