package net.nemanjakovacevic.recyclerviewswipetodelete.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.nemanjakovacevic.recyclerviewswipetodelete.R;
import net.nemanjakovacevic.recyclerviewswipetodelete.base.BaseRecycleViewSwipedDelete;

import java.util.List;

/**
 * Created by jcuentas on 15/09/16.
 */
public class AdapterSwipedDelete extends BaseRecycleViewSwipedDelete<String, AdapterSwipedDelete
        .ViewHolder> {

    private OnClickListener mOnClickListener;

    public AdapterSwipedDelete(List<String> objects, RecyclerView recyclerView) {
        super(objects, recyclerView);
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        mOnClickListener = onClickListener;
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_view_delete, parent, false));
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewholder = (ViewHolder) holder;
        super.onBindViewHolder(viewholder, position);
    }

    @Override public void swiperNormal(ViewHolder viewHolder, int position, String item) {
        viewHolder.titleTextView.setText(item);
    }

    @Override public void onSwipedLeft(ViewHolder viewHolder, final int position, final String item) {
        //super.onSwipedLeft(viewHolder, position, item);
        BaseViewHolder holder;
        if (viewHolder instanceof BaseViewHolder) {
            holder = (BaseViewHolder) viewHolder;
            // we need to show the "undo" state of the row
            holder.itemView.setBackgroundColor(Color.RED);
            holder.llaContainer.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mOnClickListener.remove(item);
                    onSwipedRemove(item);
                }
            });
            holder.undobutton.setVisibility(View.VISIBLE);
            holder.undobutton.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    itemPendingRemovalList.remove(item);
                    notifyItemChanged(position);

                }
            });
        }
    }

    public class ViewHolder extends BaseRecycleViewSwipedDelete.BaseViewHolder {
        TextView titleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleTextView = (TextView) itemView.findViewById(R.id.title_text_view);
        }
    }

    public interface OnClickListener {
        void remove(Object object);
    }
}


