package com.example.xitipetugas;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.manager.RequestManagerRetriever;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

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

public class ProfileFragment extends Fragment{
    private OnFragmentInteractionListener mListener;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    CircleImageView imgProfil;
    TextView txtGantiFoto;
    EditText txtNamaDepan, txtNamaBelakang, txtTglLahir;
    RadioButton rbLaki, rbPerempuan;
    ImageView imgCalendar;
    Button btnSimpan, btnBatal;

    Calendar myCalendar;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    String imageFilePath;
    String currentPhotoUrl;
    UploadResultModel resultModel;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imgProfil = view.findViewById(R.id.imgProfil);
        txtGantiFoto = view.findViewById(R.id.txtGantiFoto);
        txtNamaDepan = view.findViewById(R.id.txtNamaDepan);
        txtNamaBelakang = view.findViewById(R.id.txtNamaBelakang);
        txtTglLahir = view.findViewById(R.id.txtTglLahir);
        rbLaki = view.findViewById(R.id.rbLaki);
        rbPerempuan = view.findViewById(R.id.rbPerempuan);
        imgCalendar = view.findViewById(R.id.imgCalendar);
        btnSimpan = view.findViewById(R.id.btnSimpan);
        btnBatal = view.findViewById(R.id.btnBatal);

        drawer = view.findViewById(R.id.drawer_layout);
        toolbar = view.findViewById(R.id.app_toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(getActivity(), drawer ,toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        myCalendar = Calendar.getInstance();
        myCalendar.setTime(new Date());

        navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.nav_home :
                        drawer.closeDrawer(GravityCompat.START);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment(),"HomeFragment").commit();
                        break;
                    case R.id.nav_ganti_password :
                        drawer.closeDrawer(GravityCompat.START);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new GantiPasswordFragment(),"GantiPasswordFragment").commit();
                        break;
                }
                return true;
            }
        });

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment(),"HomeFragment").commit();
            }
        });

        imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
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

        imgProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
            }
        });

        txtGantiFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCameraIntent();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imgProfil.getTag(R.id.imgProfil).equals("Attached")){
                    uploadPhoto();
                }else{
                    updateData();
                }
            }
        });

        loadUserData();
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

    private void loadUserData(){
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Sedang Memuat Data...");
        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
        Call<UserModel> loadUserProfile = retrofitInterfaces.loadUserData(auth.getCurrentUser().getUid());
        loadUserProfile.enqueue(new Callback<UserModel>() {
            @Override
            public void onResponse(Call<UserModel> call, Response<UserModel> response) {
                dialog.dismiss();
                UserModel userModel = response.body();
                if(userModel != null){
                    imgProfil.setTag(R.id.imgProfil,"Unattached");
                    if(!userModel.getProfilePic().equals("")){
                        Glide.with(getActivity()).load("https://xiti.apps.bentang.id/Images/"+userModel.getProfilePic()).into(imgProfil);
                    }else{
                        imgProfil.setImageResource(R.drawable.empty_profile);
                    }
                    txtNamaDepan.setText(userModel.getFirstName());
                    txtNamaBelakang.setText(userModel.getLastName());
                    txtTglLahir.setText(userModel.getBirthDate());
                    if(userModel.getGender().equals("Male")){
                        rbLaki.setChecked(true);
                    }else{
                        rbPerempuan.setChecked(true);
                    }
                    currentPhotoUrl = userModel.getProfilePic();
                }
            }

            @Override
            public void onFailure(Call<UserModel> call, Throwable t) {
                dialog.dismiss();
                Log.e("ProfileFragment", "onFailure: "+t.toString());
            }
        });
    }

    private void updateLabel(){
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        txtTglLahir.setText(sdf.format(myCalendar.getTime()));
    }

    private void openCameraIntent(){
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(pictureIntent.resolveActivity(getActivity().getPackageManager()) != null){
            File photoFile = createImageFile();
            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(getActivity(), "com.example.xitipetugas.provider",photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(pictureIntent,REQUEST_CAPTURE_IMAGE);

            }else{
                Toast.makeText(getActivity(), "Gagal Membuka Kamera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CAPTURE_IMAGE){
            Glide.with(this).load(imageFilePath).into(imgProfil);
            imgProfil.setTag(R.id.imgProfil,"Attached");
        }else{
            Glide.with(this).load(currentPhotoUrl.equals("") == true ? currentPhotoUrl : R.drawable.empty_profile).into(imgProfil);
            imgProfil.setTag(R.id.imgProfil,"Unattached");
        }
    }

    private File createImageFile(){
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String fileName = "IMG_"+timeStamp;
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    private void uploadPhoto(){
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Mengupload Foto...");
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
        File imageFile = new File(imageFilePath);
        RequestBody body = RequestBody.create(MediaType.parse("Image/*"),imageFile);
        MultipartBody.Part bodyPart = MultipartBody.Part.createFormData("image",imageFile.getName(),body);
        Call<UploadResultModel> uploadFoto = retrofitInterfaces.uploadImage(bodyPart);
        uploadFoto.enqueue(new Callback<UploadResultModel>() {
            @Override
            public void onResponse(Call<UploadResultModel> call, Response<UploadResultModel> response) {
                dialog.dismiss();
                UploadResultModel model = response.body();
                if(model != null){
                    if(model.getStatus().equals("Berhasil")){
                        resultModel = model;
                        Toast.makeText(getActivity(), "Berhasil Mengubah Foto", Toast.LENGTH_SHORT).show();
                        updateData();
                    }else{
                        Toast.makeText(getActivity(), "Gagal Mengubah Foto", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<UploadResultModel> call, Throwable t) {
                dialog.dismiss();
                Log.e("ProfileFragment", "onFailure: "+t.toString() );
                Toast.makeText(getActivity(), "Gagal Mengubah Foto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateData(){
        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Menyimpan Perubahan...");
        dialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitInterfaces retrofitInterfaces = retrofit.create(RetrofitInterfaces.class);
        Call<CommonResultModel> updateUserData = retrofitInterfaces.updateUserProfile(
                auth.getCurrentUser().getUid(),
                txtTglLahir.getText().toString(),
                txtNamaDepan.getText().toString(),
                txtNamaBelakang.getText().toString(),
                imgProfil.getTag(R.id.imgProfil).equals("Attached") == true ? resultModel.getFile_name() : currentPhotoUrl,
                rbLaki.isChecked() == true ? "Male" : "Female"
        );

        updateUserData.enqueue(new Callback<CommonResultModel>() {
            @Override
            public void onResponse(Call<CommonResultModel> call, Response<CommonResultModel> response) {
                dialog.dismiss();
                CommonResultModel model = response.body();
                if(model != null){
                    if(model.getStatus().equals("Berhasil Mengubah Data!")){
                        Toast.makeText(getActivity(), "Berhasil Menyimpan Perubahan!", Toast.LENGTH_SHORT).show();
                        loadUserData();
                    }else{
                        Toast.makeText(getActivity(), "Gagal Menyimpan Perubahan!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<CommonResultModel> call, Throwable t) {
                dialog.dismiss();
                Log.e("ProfileFragment", "onFailure: "+t.toString());
                Toast.makeText(getActivity(), "Gagal Meyimpan Perubahan...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
