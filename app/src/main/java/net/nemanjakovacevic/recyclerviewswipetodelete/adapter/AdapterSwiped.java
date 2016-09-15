package net.nemanjakovacevic.recyclerviewswipetodelete.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.nemanjakovacevic.recyclerviewswipetodelete.R;
import net.nemanjakovacevic.recyclerviewswipetodelete.base.BaseRecycleViewSwiped;

import java.util.List;

/**
 * Created by jcuentas on 15/09/16.
 */
public class AdapterSwiped extends BaseRecycleViewSwiped<String, AdapterSwiped.ViewHolder> {

    public AdapterSwiped(List<String> objects, RecyclerView recyclerView) {
        super(objects, recyclerView);
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_view, parent, false));
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewholder = (ViewHolder) holder;
        super.onBindViewHolder(viewholder, position);
    }

    @Override public void swiperNormal(ViewHolder viewHolder, int position, String item) {
        viewHolder.titleTextView.setText("HOLA!!!!!!!!!");
    }

    public class ViewHolder extends BaseRecycleViewSwiped.BaseViewHolder {
        TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
        }
    }
}


