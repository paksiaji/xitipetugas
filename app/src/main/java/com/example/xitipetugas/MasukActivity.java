package com.example.xitipetugas;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MasukActivity extends AppCompatActivity {
    EditText txtEmail, txtPassword;
    TextView txtDaftar;
    Button btnMasuk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_masuk);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtDaftar = findViewById(R.id.btnDaftar);
        btnMasuk = findViewById(R.id.btnMasuk);
    }


    public void OnClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnMasuk :
                login();
                break;
            case R.id.txtDaftar :
                Intent intent = new Intent(MasukActivity.this,DaftarActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void login(){
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(txtEmail.getText().toString(),txtPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
                    Call<UserModel> loadUserData = retrofitInterfaces.loadUserData(auth.getCurrentUser().getUid());
                    loadUserData.enqueue(new Callback<UserModel>() {
                        @Override
                        public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                            UserModel model = response.body();
                            if(model.getRole().equals("petugas")){
                                Toast.makeText(MasukActivity.this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MasukActivity.this,MainMenuActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                auth.getCurrentUser().delete();
                                Toast.makeText(MasukActivity.this, "Akun Anda Tidak Terdaftar Sebagai Petugas", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserModel> call, Throwable t) {
                            auth.getCurrentUser().delete();
                            Log.e("MainActivity", "onFailure: "+t.toString() );
                            Toast.makeText(MasukActivity.this, "Login Gagal, Silahkan Coba Lagi!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Log.e("MasukActivity", "onComplete: Task Failed" );
                    Toast.makeText(MasukActivity.this, "Login Gagal, Silahkan Coba Lagi!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
