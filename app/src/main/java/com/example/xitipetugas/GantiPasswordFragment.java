package com.example.xitipetugas;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class GantiPasswordFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    EditText txtPasswordLama, txtPasswordBaru, txtKonfirmasi;
    Button btnSimpan, btnBatal;


    public GantiPasswordFragment() {
        // Required empty public constructor
    }

    public static GantiPasswordFragment newInstance(String param1, String param2) {
        GantiPasswordFragment fragment = new GantiPasswordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ganti_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtPasswordBaru = view.findViewById(R.id.txtNamaBelakang);
        txtPasswordLama = view.findViewById(R.id.txtNamaDepan);
        txtKonfirmasi = view.findViewById(R.id.txtTglLahir);
        btnBatal = view.findViewById(R.id.btnBatal);
        btnSimpan = view.findViewById(R.id.btnSimpan);

        drawer = view.findViewById(R.id.drawer_layout);
        toolbar = view.findViewById(R.id.app_toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer ,toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_profile :
                        drawer.closeDrawer(GravityCompat.START);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment(),"ProfileFragment").commit();
                        break;
                    case R.id.nav_home :
                        drawer.closeDrawer(GravityCompat.START);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment(),"HomeFragment").commit();
                        break;
                }
                return true;
            }
        });


        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData()){
                    final FirebaseAuth auth = FirebaseAuth.getInstance();
                    AuthCredential credential = EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(),txtPasswordLama.getText().toString());
                    auth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                auth.getCurrentUser().updatePassword(txtPasswordBaru.getText().toString()).addOnCompleteListener(
                                        new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(getActivity(), "Berhasil Mengubah Password!", Toast.LENGTH_SHORT).show();
                                                }else{
                                                    Toast.makeText(getActivity(), "Gagal Mengubah Password!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                );
                            }else{
                                Toast.makeText(getActivity(), "Password Lama Salah", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private boolean validateData(){
        if(!txtPasswordBaru.getText().toString().isEmpty()){
            if(!txtPasswordLama.getText().toString().isEmpty()){
                if(!txtKonfirmasi.getText().toString().isEmpty()){
                    if(txtKonfirmasi.getText().toString().equals(txtKonfirmasi.getText().toString())){
                        return true;
                    }else{
                        txtKonfirmasi.setError("Password Baru Tidak Cocok");
                        txtPasswordBaru.setError("Password Baru Tidak Cocok");
                        return false;
                    }
                }else{
                    txtKonfirmasi.setError("Wajib Diisi");
                    return false;
                }
            }else{
                txtPasswordLama.setError("Wajib Diisi");
                return false;
            }
        }else{
            txtPasswordBaru.setError("Wajib Diisi");
            return false;
        }
    }
}
