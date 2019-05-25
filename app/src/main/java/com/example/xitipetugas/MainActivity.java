package com.example.xitipetugas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.security.Permission;

public class MainActivity extends AppCompatActivity {
    Button btnDaftar, btnMasuk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDaftar = findViewById(R.id.btnDaftar);
        btnMasuk = findViewById(R.id.btnMasuk);
    }

    public void OnClick(View view) {
        if(checkPermission()){
            int id = view.getId();
            Intent intent = null;
            switch (id){
                case R.id.btnDaftar :
                    intent = new Intent(MainActivity.this,DaftarActivity.class);
                    break;
                case R.id.btnMasuk :
                    intent = new Intent(MainActivity.this, MasukActivity.class);
                    break;
            }
            startActivity(intent);
            finish();
        }
    }

    private void RequestPermission(){
        String[] permission = {Manifest.permission.INTERNET,Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        ActivityCompat.requestPermissions(this,permission,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 1){
            Toast.makeText(this, "Akses Berhasil Diberikan", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this,"Silahkan Berikan Akses Untuk Melanjutkan",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                ) {

            RequestPermission();
            return false;
        } else{
            return true;
        }
    }
}
