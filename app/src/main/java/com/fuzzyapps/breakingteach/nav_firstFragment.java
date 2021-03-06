package com.fuzzyapps.breakingteach;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager.SystemBarConfig;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import it.sephiroth.android.library.bottomnavigation.BottomBehavior;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class nav_firstFragment extends Fragment {
    private static final String TAG = nav_firstFragment.class.getSimpleName();
    RecyclerView mRecyclerView;

    public nav_firstFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.nav_first_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //AQUI PARSEAR TODAS LAS VARIABLES Y AGREGAR SUS LISTENERS...
        mRecyclerView = (RecyclerView) view.findViewById(R.id.RecyclerView01);
    }

    @Override
    public void onActivityCreated(@Nullable final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        BaseActivity activity = (BaseActivity) getActivity();
        final SystemBarConfig config = activity.getSystemBarTint().getConfig();

        final int navigationHeight;
        final int actionbarHeight;

        if (activity.hasTranslucentNavigation()) {
            navigationHeight = config.getNavigationBarHeight();
        } else {
            navigationHeight = 0;
        }

        if (activity.hasTranslucentStatusBar()) {
            actionbarHeight = config.getActionBarHeight();
        } else {
            actionbarHeight = 0;
        }

        Log.d(TAG, "navigationHeight: " + navigationHeight);
        Log.d(TAG, "actionbarHeight: " + actionbarHeight);

        final BottomNavigation navigation = activity.getBottomNavigation();
        if (null != navigation) {
            navigation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    navigation.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    final CoordinatorLayout.LayoutParams coordinatorLayoutParams =
                            (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();

                    final CoordinatorLayout.Behavior behavior = coordinatorLayoutParams.getBehavior();
                    final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
                    if (behavior instanceof BottomBehavior) {
                        final boolean scrollable = ((BottomBehavior) behavior).isScrollable();

                        Log.d(TAG, "scrollable: " + scrollable);
                        Log.d(TAG, "bottomNagivation: " + navigation.getNavigationHeight());
                        Log.d(TAG, "finalNavigationHeight: " + navigationHeight);

                        int totalHeight;

                        if (scrollable) {
                            totalHeight = navigationHeight;
                            params.bottomMargin -= navigationHeight;
                        } else {
                            totalHeight = navigation.getNavigationHeight();
                        }

                        Log.d(TAG, "totalHeight: " + totalHeight);
                        Log.d(TAG, "bottomMargin: " + params.bottomMargin);

                        createAdater(totalHeight);
                    } else {
                        params.bottomMargin -= navigationHeight;
                        createAdater(navigationHeight);
                    }
                    mRecyclerView.requestLayout();
                }
            });
        } else {
            final ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mRecyclerView.getLayoutParams();
            params.bottomMargin -= navigationHeight;
            createAdater(navigationHeight);
        }
    }

    private void createAdater(int height) {
        Log.i(getClass().getSimpleName(), "createAdapter(" + height + ")");
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new Adapter(getActivity(), height, createData()));
    }

    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    static class TwoLinesViewHolder extends RecyclerView.ViewHolder {

        final TextView title;
        final TextView description;
        final ImageView imageView;
        final int marginBottom;

        public TwoLinesViewHolder(final View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(android.R.id.title);
            description = (TextView) itemView.findViewById(android.R.id.text1);
            imageView = (ImageView) itemView.findViewById(android.R.id.icon);
            marginBottom = ((ViewGroup.MarginLayoutParams) itemView.getLayoutParams()).bottomMargin;
        }
    }

    private class Adapter extends RecyclerView.Adapter<TwoLinesViewHolder> {
        private final Picasso picasso;
        private final int navigationHeight;
        private final Book[] data;

        public Adapter(final Context context, final int navigationHeight, final Book[] data) {
            this.navigationHeight = navigationHeight;
            this.data = data;
            this.picasso = Picasso.with(context);
        }

        @Override
        public TwoLinesViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            final View view = LayoutInflater.from(getActivity()).inflate(R.layout.simple_card_item, parent, false);
            final TwoLinesViewHolder holder = new TwoLinesViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final TwoLinesViewHolder holder, final int position) {
            if (position == getItemCount() - 1) {
                ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).bottomMargin = holder.marginBottom + navigationHeight;
            } else {
                ((ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams()).bottomMargin = holder.marginBottom;
            }

            final Book item = data[position];
            holder.title.setText(item.title);
            holder.description.setText("Por " + item.author);
            holder.imageView.setImageBitmap(null);
            //Toast.makeText(getActivity(),getBitmapFromURL(item.imageUrl).toString(),Toast.LENGTH_SHORT).show();

            //holder.imageView.setImageBitmap(getBitmapFromURL(item.imageUrl));
            picasso.cancelRequest(holder.imageView);

            picasso.load(item.imageUrl)
                    .noPlaceholder()
                    .resizeDimen(R.dimen.simple_card_image_width, R.dimen.simple_card_image_height)
                    .centerCrop()
                    .into(holder.imageView);
        }

        @Override
        public int getItemCount() {
            return data.length;
        }
    }

    private Book[] createData() {
        return new Book[]{
                new Book("Sala 1 - Matemática", "Scott Masterson", "http://i.imgur.com/dyyP2iO.jpg"),
                new Book("Sala 2 - Física", "Ali Conners", "http://i.imgur.com/da6QIlR.jpg"),
                new Book("Sala 3 - Química", "Sandra Adams", "http://i.imgur.com/YHoOJh4.jpg"),
                new Book("Sala 4 - Bioligía", "Janet Perkins", "http://i.imgur.com/3jxqrKP.jpg"),
        };
    }

    static class Book {
        final String title;
        final String author;
        final String imageUrl;

        Book(final String title, final String author, final String imageUrl) {
            this.title = title;
            this.author = author;
            this.imageUrl = imageUrl;
        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}
