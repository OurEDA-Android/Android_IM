package quejianming.com.im;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by Administrator on 2016/2/11 0011.
 */
public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener  mListener;

    public interface OnItemClickListener{
        public void onItemClick(View view,int position);
    }

    GestureDetector gestureDetector;

    public RecyclerItemClickListener(Context context,OnItemClickListener listener) {
        mListener = listener;
        gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        View childView = rv.findChildViewUnder(e.getX(),e.getY());
        if(childView != null && mListener != null && gestureDetector.onTouchEvent(e)){
            mListener.onItemClick(childView,rv.getChildPosition(childView));
            return true;
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }
}
