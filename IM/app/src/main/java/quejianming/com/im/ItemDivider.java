package quejianming.com.im;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2016/2/9 0009.
 */
public class ItemDivider extends RecyclerView.ItemDecoration{

    private Drawable drawable;
    public ItemDivider(Context context,int resId) {
        super();
        //在这里我们传入作为Divider的Drawable对象
        drawable = context.getResources().getDrawable(resId);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth()-parent.getPaddingRight();
        final int childCount = parent.getChildCount();

        for(int i = 0;i<childCount;i++){
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            
            //以下计算主要用来确定绘制的位置

            final int top = child.getBottom()+params.bottomMargin;
            final int bottom = top+drawable.getIntrinsicHeight();
            drawable.setBounds(left,top,right,bottom);
            drawable.draw(c);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.set(0,0,0,drawable.getIntrinsicWidth());
    }
}
