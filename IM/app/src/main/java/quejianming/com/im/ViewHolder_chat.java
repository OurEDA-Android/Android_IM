package quejianming.com.im;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/2/13 0013.
 */
public class ViewHolder_chat extends RecyclerView.ViewHolder {

    private TextView tv_title;
    private TextView tv_contain;
    public ViewHolder_chat(View root) {
        super(root);
        this.tv_title = (TextView) root.findViewById(R.id.tv_chat_user);
        this.tv_contain = (TextView) root.findViewById(R.id.tv_chat_contain);
    }

    public TextView getTv_contain() {
        return tv_contain;
    }

    public TextView getTv_title() {
        return tv_title;
    }

    public void setTv_contain(TextView tv_contain) {
        this.tv_contain = tv_contain;
    }

    public void setTv_title(TextView tv_title) {
        this.tv_title = tv_title;
    }
}
