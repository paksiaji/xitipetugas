package com.example.xitipetugas;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;
import java.util.Locale;

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.MyViewHolder> {
    Context mContext;
    List<LaporanModel> listLaporan;

    public LaporanAdapter(Context mContext, List<LaporanModel> listLaporan) {
        this.mContext = mContext;
        this.listLaporan = listLaporan;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.laporan_item_layout,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        myViewHolder.txtJudul.setText(listLaporan.get(i).getTitle());
        myViewHolder.txtDeskripsi.setText(listLaporan.get(i).getDescription());
        getCityLocation(listLaporan.get(i).getLatitude(),listLaporan.get(i).getLongitude(),myViewHolder.txtLokasi);
        Glide.with(mContext).load(listLaporan.get(i).getUrlContent()).apply(RequestOptions.bitmapTransform(new RoundedCorners(20))).into(myViewHolder.imgContent);

        myViewHolder.txtJudul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailActivityIntent(listLaporan.get(i).getPostId());
            }
        });

        myViewHolder.txtDeskripsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailActivityIntent(listLaporan.get(i).getPostId());
            }
        });

        myViewHolder.txtLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailActivityIntent(listLaporan.get(i).getPostId());
            }
        });

        myViewHolder.imgContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailActivityIntent(listLaporan.get(i).getPostId());
            }
        });
        myViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDetailActivityIntent(listLaporan.get(i).getPostId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listLaporan.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imgContent;
        TextView txtJudul, txtDeskripsi, txtLokasi;
        ConstraintLayout container;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imgContent = itemView.findViewById(R.id.imgContent);
            txtJudul = itemView.findViewById(R.id.txtJudul);
            txtDeskripsi = itemView.findViewById(R.id.txtDescripsi);
            txtLokasi = itemView.findViewById(R.id.txtLokasi);
            container = itemView.findViewById(R.id.constLayout);
        }
    }

    public void getCityLocation(double latitude,double longitude,TextView txtLocation){
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);
            String addressLine = addresses.get(0).getAddressLine(0);
            String cityName = addressLine.split(",")[2];
            txtLocation.setText(cityName);
        } catch (Exception e) {
            txtLocation.setText("-");
            e.printStackTrace();
        }
    }

    private void openDetailActivityIntent(String laporanId){
        Intent intent = new Intent(mContext,DetailLaporanActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("laporanId",laporanId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
}
