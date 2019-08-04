package com.example.mhbadmin.AdapterClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.mhbadmin.R;

import java.util.List;

public class ImageSwipeAdapter extends PagerAdapter {

    private List<String> image_resources = null;
    private Context context = null;

    public ImageSwipeAdapter(Context context, List<String> image_resources) {
        this.context = context;
        this.image_resources=image_resources;
    }

    @Override
    public int getCount() {
        return image_resources.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        View item_view = layoutInflater.inflate(R.layout.swipe_layout, container, false);
        ImageView imageView = item_view.findViewById(R.id.iv_swipe_image_view);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(context).load(image_resources.get(position)).into(imageView);
        container.addView(item_view);
        return item_view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((LinearLayout) object);
    }
}