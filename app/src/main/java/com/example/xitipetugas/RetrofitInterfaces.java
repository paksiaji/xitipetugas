package com.example.xitipetugas;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitInterfaces {
    @FormUrlEncoded
    @POST("register_user.php")
    Call<CommonResultModel> registerData(
            @Field("userId") String userId,
            @Field("birthDate") String birthDate,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("profilePic") String profilePic,
            @Field("gender") String gender,
            @Field("role") String role
    );

    @Multipart
    @POST("upload_image.php")
    Call<UploadResultModel> uploadImage(
            @Part MultipartBody.Part photo
            );

    @FormUrlEncoded
    @POST("load_user_data.php")
    Call<UserModel> loadUserData(
            @Field("userId") String userId
    );

    @FormUrlEncoded
    @POST("update_profile.php")
    Call<CommonResultModel> updateUserProfile(
            @Field("userId") String userId,
            @Field("birthDate") String birthDate,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("profilePic") String profilePic,
            @Field("gender") String gender
    );

    @FormUrlEncoded
    @POST("load_post_home.php")
    Call<List<LaporanModel>> loadPost(
            @Field("userLatitude") String userLatitude,
            @Field("userLongitude") String userLongitude
    );

    @FormUrlEncoded
    @POST("load_detail_post.php")
    Call<LaporanModel> loadDetailPost(
      @Field("postId") String postId
    );
}
