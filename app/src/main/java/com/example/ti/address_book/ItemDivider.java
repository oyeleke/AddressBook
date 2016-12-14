package com.example.ti.address_book;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ti on 30/07/2016.
 */
public class ItemDivider extends RecyclerView.ItemDecoration {
    private final Drawable divider;

    public ItemDivider(Context context){
        int attrs[] = {android.R.attr.listDivider};
        divider= context.obtainStyledAttributes(attrs).getDrawable(0);
        // to load built in Adroid list item divider
    }
    // tod raw list items divider on the recycler view
    @Override
    public  void onDrawOver(Canvas c , RecyclerView parent, RecyclerView.State state){
        super.onDrawOver(c,parent,state);

        // calculating left and right x-coordinates for all dividers

        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();

        // to draw a line on every item but the last one

        for(int i =0 ;i<parent.getChildCount()-1; ++i){
            View item = parent.getChildAt(i); // to get the ith item

            // to calculate top and bottom y-coordinates forcurrent divider
            int top = item.getBottom() + ((RecyclerView.LayoutParams)item.getLayoutParams()).bottomMargin;
            int bottom = top + divider.getIntrinsicHeight();

            // to draw the divider with the calculated bound

            divider.setBounds(left,top,right,bottom);
            divider.draw(c);
        }
    }
}
