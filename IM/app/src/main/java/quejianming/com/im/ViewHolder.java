package quejianming.com.im;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/2/9 0009.
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    private TextView tv;
    public ViewHolder(View root) {
        super(root);
        this.tv = (TextView) root.findViewById(R.id.textView);
    }
    public TextView getTv() {
        return tv;
    }
}
