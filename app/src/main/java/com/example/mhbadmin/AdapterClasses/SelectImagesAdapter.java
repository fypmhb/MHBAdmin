package com.example.mhbadmin.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mhbadmin.R;

import java.util.List;


public class SelectImagesAdapter extends RecyclerView.Adapter<SelectImagesAdapter.MyViewHolder> {

    private Context context = null;
    private List lImages = null;
    private List lImagesNames = null;
    private RecyclerView rvSignUp = null;
    private ImageView ivUploadImage = null;

    public SelectImagesAdapter(Context context, List lImages, List lImagesNames, RecyclerView rvSignUp, ImageView ivUploadImage) {
        this.context = context;
        this.lImages = lImages;
        this.lImagesNames = lImagesNames;
        this.rvSignUp = rvSignUp;
        this.ivUploadImage = ivUploadImage;
    }

    @NonNull
    @Override
    public SelectImagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.select_images_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SelectImagesAdapter.MyViewHolder holder, final int position) {
        try {
            Glide.with(context).load(lImages.get(position))
                    .placeholder(R.drawable.ic_loading_image).into(holder.ivEntranceImages);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*if (position+1==lImages.size()) {
            holder.ivCross.setVisibility(View.GONE);
        }*/

        if (getItemCount() == 1) {
            lImages.remove(0);
            lImagesNames.remove(0);

            rvSignUp.setVisibility(View.GONE);
            ivUploadImage.setVisibility(View.VISIBLE);
        }

        holder.ivCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mClickListener.onItemClick(false, position);
            }
        });

        holder.ivEntranceImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onItemClick(true, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lImages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCross = null,
                ivEntranceImages = null;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCross = (ImageView) itemView.findViewById(R.id.iv_cross);
            ivEntranceImages = (ImageView) itemView.findViewById(R.id.iv_entrance_images);
        }
    }

    private ClickListener mClickListener;

    //interface to send callBack
    public interface ClickListener {
        void onItemClick(boolean flag, int position);
    }

    public void setOnClickListener(SelectImagesAdapter.ClickListener clickListener) {
        mClickListener = clickListener;
    }
}