package com.example.xitipetugas;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailLaporanActivity extends AppCompatActivity {
    ImageView imgKembali, imgContent, imgKategori, imgStatus;
    TextView txtKembali, txtJudul, txtDeskripsi, txtNama, txtLokasi, txtWaktu;
    Button btnMaps, btnUpdate;
    CircleImageView imgProfil;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_laporan);

        imgKembali = findViewById(R.id.imgKembali);
        imgContent = findViewById(R.id.imgContent);
        imgKategori = findViewById(R.id.imgKategori);
        imgStatus = findViewById(R.id.imgStatus);

        txtKembali = findViewById(R.id.txtKembali);
        txtJudul = findViewById(R.id.txtJudul);
        txtDeskripsi = findViewById(R.id.txtDeskripsi);
        txtNama = findViewById(R.id.txtName);
        txtLokasi = findViewById(R.id.txtLokasi);
        txtWaktu = findViewById(R.id.txtTime);

        btnMaps = findViewById(R.id.btnMaps);
        btnUpdate = findViewById(R.id.btnUpdate);

        imgProfil = findViewById(R.id.imgProfil);


        imgKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        txtKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadDetailPost();
    }

    private void loadDetailPost(){
        dialog = new ProgressDialog(this);
        dialog.setMessage("Memuat...");
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterfaces interfaces = retrofit.create(RetrofitInterfaces.class);
        Call<LaporanModel> loadDetailPost = interfaces.loadDetailPost(getIntent().getStringExtra("laporanId"));
        loadDetailPost.enqueue(new Callback<LaporanModel>() {
            @Override
            public void onResponse(Call<LaporanModel> call, Response<LaporanModel> response) {
                dialog.dismiss();
                LaporanModel model = response.body();

                Glide.with(DetailLaporanActivity.this).load(model.getUrlContent()).apply(RequestOptions.bitmapTransform(new RoundedCorners(50))).into(imgContent);
                determineStatus(model.getProgress(), imgStatus);
                determineCategory(model.getCategory(),imgKategori);
                countTime(Long.valueOf(model.getTimePost()),txtWaktu);
                getLocation(Double.valueOf(model.getLatitude()),Double.valueOf(model.getLongitude()),txtLokasi);
                txtJudul.setText(model.getTitle());
                txtDeskripsi.setText(model.getDescription());
                loadUserData(model.getUserId(),txtNama,imgProfil);
            }

            @Override
            public void onFailure(Call<LaporanModel> call, Throwable t) {
                dialog.dismiss();
                Log.e("DetailLaporanActivity", t.toString() );
            }
        });
    }

    private void loadUserData(String userId, final TextView txtName, final ImageView imgProfil){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterfaces interfaces = retrofit.create(RetrofitInterfaces.class);
        Call<UserModel> loadUserData = interfaces.loadUserData(userId);
        loadUserData.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                dialog.dismiss();
                UserModel model = response.body();
                txtName.setText(model.getFirstName()+" "+model.getLastName());
                if(model.getUserId().equals("")){
                    Glide.with(DetailLaporanActivity.this).load(R.drawable.empty_profile).into(imgProfil);
                }else{
                    Glide.with(DetailLaporanActivity.this).load("http://xiti.apps.bentang.id/Images/"+model.getProfilePic()).into(imgProfil);
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                dialog.dismiss();
                Log.e("DetailLaporanActivity", t.toString() );
            }
        });
    }

    private void getLocation(double latitude, double longitude, TextView txtLokasi){
        Geocoder geocoder = new Geocoder(DetailLaporanActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);
            String addressLine = addresses.get(0).getAddressLine(0);
            String cityName = addressLine.split(",")[2];
            txtLokasi.setText(cityName.trim());
        } catch (Exception e) {
            e.printStackTrace();
            txtLokasi.setText("-");
        }
    }

    private void countTime(long time, TextView txtTime){
        try {
            Date datePost = new Date(time * 1000);
            Date dateNow  = new Date();
            long interval = dateNow.getTime() - datePost.getTime();
            if(TimeUnit.SECONDS.convert(interval,TimeUnit.MILLISECONDS) >= 60){
                if(TimeUnit.MINUTES.convert(interval,TimeUnit.MILLISECONDS) >= 60){
                    if(TimeUnit.HOURS.convert(interval,TimeUnit.MILLISECONDS) >= 24){
                        txtTime.setText(String.valueOf(TimeUnit.DAYS.convert(interval,TimeUnit.MILLISECONDS))+ " Hari Yang Lalu");
                    }else{
                        txtTime.setText(String.valueOf(TimeUnit.HOURS.convert(interval,TimeUnit.MILLISECONDS))+ " Jam Yang Lalu");
                    }
                }else{
                    txtTime.setText(String.valueOf(TimeUnit.MINUTES.convert(interval,TimeUnit.MILLISECONDS))+ " Menit Yang Lalu");
                }
            }else{
                txtTime.setText(String.valueOf(TimeUnit.SECONDS.convert(interval,TimeUnit.MILLISECONDS))+ " Detik Yang Lalu");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void determineCategory(String kategori, ImageView imgKategori){
        switch (kategori){
            case "kdrt" :
                imgKategori.setImageResource(R.drawable.kdrt);
                break;
            case "pohon tumbang" :
                imgKategori.setImageResource(R.drawable.pohontumbang);
                break;
            case "jalan rusak" :
                imgKategori.setImageResource(R.drawable.jalanrusak);
                break;
            case "bencana alam" :
                imgKategori.setImageResource(R.drawable.bencanaalam);
                break;
            case "kriminalitas" :
                imgKategori.setImageResource(R.drawable.kriminalitas);
                break;
            case "kebakaran" :
                imgKategori.setImageResource(R.drawable.kebakaran);
                break;
        }
    }

    private void determineStatus(String status, ImageView imgStatus){
        switch (status){
            case "Pending" :
                imgStatus.setImageResource(R.drawable.pending);
                break;
            case "Process" :
                imgStatus.setImageResource(R.drawable.process);
                break;
            case "Finish" :
                imgStatus.setImageResource(R.drawable.finish);
                break;

        }
    }
}
