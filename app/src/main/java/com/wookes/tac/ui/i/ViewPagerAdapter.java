package com.wookes.tac.ui.i;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.wookes.tac.R;


public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private Integer[] images = {R.drawable.illustrate_capture_moments, R.drawable.illustrate_capture_everywhere, R.drawable.illustrate_capture_dance};
    private Integer[] caption = {R.string.intro_caption_one, R.string.intro_caption_two, R.string.intro_caption_three};

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.dialog_layout_intro_pager, container, false);
        ((ImageView)(view.findViewById(R.id.imageView))).setImageResource(images[position]);
        ((TextView)(view.findViewById(R.id.caption))).setText(caption[position]);
        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}