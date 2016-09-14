package net.nemanjakovacevic.recyclerviewswipetodelete.base;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jcuentas on 14/09/16.
 */
public class BaseRecycleViewSwiped extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int PENDING_REMOVAL_TIMEOUT = 3000; //3sec

    boolean undoOn;
    private Handler handler = new Handler();

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override public int getItemCount() {
        return 0;
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(View itemView) {
            super(itemView);
        }
    }
}
