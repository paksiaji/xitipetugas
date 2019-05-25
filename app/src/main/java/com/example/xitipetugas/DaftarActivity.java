package com.example.xitipetugas;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DaftarActivity extends AppCompatActivity {
    TextView txtMasuk;
    CircleImageView imgProfile;
    Button btnDaftar;
    EditText txtEmail, txtPassword, txtTglLahir, txtNamaDepan, txtNamaBelakang, txtKonfirmasiPassword;
    RadioButton rbLaki, rbPerempuan;
    ImageView imgCalendar;

    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;
    UploadResultModel resultModel;
    Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
        myCalendar = Calendar.getInstance();
        myCalendar.setTime(new Date());

        txtMasuk = findViewById(R.id.txtMasuk);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtTglLahir = findViewById(R.id.txtTglLahir);
        txtNamaBelakang = findViewById(R.id.txtNamaBelakang);
        txtNamaDepan = findViewById(R.id.txtNamaDepan);
        txtKonfirmasiPassword = findViewById(R.id.txtTglLahir);
        imgProfile = findViewById(R.id.imgProfile);
        rbLaki = findViewById(R.id.rbLaki);
        rbPerempuan = findViewById(R.id.rbPerempuan);
        imgCalendar = findViewById(R.id.imgCalendar);
        btnDaftar = findViewById(R.id.btnDaftar);

        updateLabel();
        imgProfile.setTag(R.id.imgProfile,"Unattached");

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    if(imgProfile.getTag(R.id.imgProfile).equals("Attached")){
                        UploadPhoto();
                    }else{
                        daftarAkun();
                    }
                }
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenCameraIntent();
            }
        });

        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(DaftarActivity.this,new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR,year);
                        myCalendar.set(Calendar.MONTH,month);
                        myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        updateLabel();
                    }
                },myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public void OnClick(View view) {
        int id = view.getId();
        Intent intent;
        switch (id){
            case R.id.txtMasuk :
                intent = new Intent(DaftarActivity.this,MasukActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }

    private void updateLabel(){
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        txtTglLahir.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean validateForm(){
        if(!txtNamaDepan.getText().toString().isEmpty()){
            if(!txtNamaBelakang.getText().toString().isEmpty()){
                if(!txtEmail.getText().toString().isEmpty()){
                    if(!txtPassword.getText().toString().isEmpty()){
                        if(!txtTglLahir.getText().toString().isEmpty()){
                            if(!txtKonfirmasiPassword.getText().toString().isEmpty()){
                                return true;
                            }else{
                                txtKonfirmasiPassword.setError("Wajib Diisi");
                                return false;
                            }
                        }else{
                            txtTglLahir.setError("Wajib Diisi");
                            return false;
                        }
                    }else {
                        txtPassword.setError("Wajib Diisi");
                        return false;
                    }
                }else{
                    txtEmail.setError("Wajib Diisi");
                    return false;
                }
            }else{
                txtNamaBelakang.setError("Wajib Diisi");
                return false;
            }
        }else{
            txtNamaDepan.setError("Wajib Diisi");
            return false;
        }
    }

    private void OpenCameraIntent(){
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getPackageManager()) != null){
            File photoFile = createImageFile();
            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(this, "com.example.xitipetugas.provider",photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(pictureIntent,REQUEST_CAPTURE_IMAGE);

            }else{
                Toast.makeText(this, "Gagal Membuka Kamera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_"+timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        try {
            File imageFile = File.createTempFile(
                    fileName,
                    ".jpg",
                    storageDir
            );

            imageFilePath = imageFile.getAbsolutePath();
            return imageFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == REQUEST_CAPTURE_IMAGE){
            Glide.with(this).load(imageFilePath).into(imgProfile);
            imgProfile.setTag(R.id.imgProfile,"Attached");
        }else{
            Glide.with(this).load(R.drawable.empty_profile).into(imgProfile);
            imgProfile.setTag(R.id.imgProfile,"Unattached");
        }
    }

    private void daftarAkun(){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Menyimpan Data...");
        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(txtEmail.getText().toString(),txtPassword.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                dialog.dismiss();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(Constants.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);

                Call<CommonResultModel> call = retrofitInterfaces.registerData(
                        authResult.getUser().getUid(),
                        txtTglLahir.getText().toString(),
                        txtNamaDepan.getText().toString(),
                        txtNamaBelakang.getText().toString(),
                        imgProfile.getTag(R.id.imgProfile).equals("Attached") == true ? resultModel.getFile_name() : "",
                        rbLaki.isChecked() == true ? "Male" : "Female",
                        "petugas"
                );

                call.enqueue(new Callback<CommonResultModel>() {
                    @Override
                    public void onResponse(Call<CommonResultModel> call, Response<CommonResultModel> response) {
                        CommonResultModel model = response.body();
                        if(model.getStatus().equals("Berhasil Meyimpan Data!")){
                            Toast.makeText(DaftarActivity.this, "Berhasil Mendaftar, Silahkan Login!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(DaftarActivity.this,MasukActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Log.e("DaftarActivity", "onResponse: "+model.getStatus() );
                            Toast.makeText(DaftarActivity.this, "Gagal Mendaftar, Silahkan Coba Lagi!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<CommonResultModel> call, Throwable t) {
                        Log.e("DaftarActivity", "onFailure: "+t.toString() );
                        Toast.makeText(DaftarActivity.this, "Gagal Mendaftar, Silahkan Coba Lagi!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("DaftarActivity", "onFailure: "+e.toString() );
                dialog.dismiss();
                Toast.makeText(DaftarActivity.this, "Kesalahan Dalam Mendaftar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void UploadPhoto(){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Mengupload Foto....");
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
        File imageFile = new File(imageFilePath);
        RequestBody body = RequestBody.create(MediaType.parse("image/*"),imageFile);
        MultipartBody.Part bodyPart = MultipartBody.Part.createFormData("image",imageFile.getName(),body);

        Call<UploadResultModel> uploadImage = retrofitInterfaces.uploadImage(bodyPart);
        uploadImage.enqueue(new Callback<UploadResultModel>() {
            @Override
            public void onResponse(Call<UploadResultModel> call, Response<UploadResultModel> response) {
                dialog.dismiss();
                UploadResultModel model = response.body();
                if(model.getStatus().equals("Berhasil")){
                    resultModel = model;
                    daftarAkun();
                    Toast.makeText(DaftarActivity.this, "Berhasil Mengupload Foto", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(DaftarActivity.this, "Gagal Mengupload Foto", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UploadResultModel> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(DaftarActivity.this, "Kesalahan Dalam Mendaftar", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
