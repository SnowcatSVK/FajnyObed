package com.snowcat.fajnyobed.Logic;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.snowcat.fajnyobed.R;
import com.snowcat.fajnyobed.database.FavouritesTable;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Snowcat on 27-Sep-15.
 */
public class FavouritesCursorAdapter extends CursorAdapter {
    LayoutInflater inflater;
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private DisplayImageOptions options;
    public FavouritesCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        //Log.e("ADAPTER", "constructor");
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //Log.e("ADAPTER", "new view");
        return inflater.inflate(R.layout.item_favourite, parent, false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //Log.e("ADAPTER", "Load data");
        String name = cursor.getString(cursor.getColumnIndexOrThrow(FavouritesTable.COLUMN_RESTAURANT_NAME));
        TextView nameText = (TextView) view.findViewById(R.id.favourites_list_name_textView);
        nameText.setText(name);
    }

    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 0);
                    displayedImages.add(imageUri);
                }
            }
        }
    }

    public String getRestaurantId(int position) {
        Cursor cursor = getCursor();
        int id = 0;
        if (cursor.moveToPosition(position)) {
            id = cursor.getInt(cursor.getColumnIndexOrThrow(FavouritesTable.COLUMN_RESTAURANT_ID));
        }
        return String.valueOf(id);
    }
}
