package net.nemanjakovacevic.recyclerviewswipetodelete.base;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.nemanjakovacevic.recyclerviewswipetodelete.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/* Implement:
        placaAdapter = new PlacaAdapter(contractorList, rviContainer);
        placaAdapter.setOnClickListener(this);
        placaAdapter.setUndoOn(true);
        rviContainer.setLayoutManager(new LinearLayoutManager(this));
        rviContainer.setAdapter(placaAdapter);
        rviContainer.setHasFixedSize(true);
*/



/**
 * Created by jcuentas on 14/09/16.
 */
public abstract class BaseRecycleViewSwiped<E, V>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnTouchSwiped<V, E> {


    private static final int PENDING_REMOVAL_TIMEOUT = 3000; //3sec

    boolean undoOn;
    protected List<E> itemList;
    private List<E> itemPendingRemovalList;
    private Handler handler = new Handler();
    HashMap<E, Runnable> pendingRunnables = new HashMap<>();

    public BaseRecycleViewSwiped(List<E> objects, RecyclerView recyclerView) {
        itemList = objects;
        itemPendingRemovalList = new ArrayList<>();

        setUpItemTouchHelper(recyclerView);
        setUpAnimationDecoratorHelper(recyclerView);
        //Probar este error con el util
//        for (int i=1; i<= 15; i++) {
//            this.itemList.add((E)("Item " + i));
//        }
    }

    @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    public abstract void swiperNormal(V viewHolder, int position,E item);

    @Override public void onNormal(V viewHolder, int position,E item) {
        BaseViewHolder holder;
        if (viewHolder instanceof BaseViewHolder){
            holder = (BaseViewHolder) viewHolder;
            // we need to show the "normal" state
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.llaContainer.setVisibility(View.VISIBLE);
            holder.undoButton.setVisibility(View.INVISIBLE);
            holder.undoButton.setOnClickListener(null);
        }
        swiperNormal(viewHolder, position, item);
    }

    @Override public void onSwipedLeft(V  viewHolder, int position, final E item) {
        BaseViewHolder holder;
        if (viewHolder instanceof BaseViewHolder) {
            holder = (BaseViewHolder) viewHolder;
            // we need to show the "undo" state of the row
            holder.itemView.setBackgroundColor(Color.RED);
            holder.llaContainer.setVisibility(View.INVISIBLE);
            holder.undoButton.setVisibility(View.VISIBLE);
            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSwipedRemove(item);
                }
            });
        }
    }

    @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final E item = itemList.get(position);

        if (itemPendingRemovalList.contains(item)) {
            onSwipedLeft((V) holder, position, item);
        } else {
            onNormal((V)holder, position, item);
        }
    }

    @Override public int getItemCount() {
        return itemList.size();
    }

    public static class BaseViewHolder extends RecyclerView.ViewHolder {
        Button undoButton;
        View llaContainer;

        public BaseViewHolder(View itemView) {
            super(itemView);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);
            llaContainer = itemView.findViewById(R.id.llaContainer);
        }
    }

    protected void onSwipedRemove(E entidad) {
        // user wants to undo the removal, let's cancel pending task
        Runnable pendingRemovalRunnable = pendingRunnables.get(entidad);
        pendingRunnables.remove(entidad);
        if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
        itemPendingRemovalList.remove(entidad);
        // this will rebind the row in "normal" state
        notifyItemChanged(itemList.indexOf(entidad));

    }

    public void setUndoOn(boolean undoOn) {
        this.undoOn = undoOn;
    }

    public boolean isUndoOn() {
        return undoOn;
    }

    public void pendingRemoval(int position) {
        final E item = itemList.get(position);
        if (!itemPendingRemovalList.contains(item)) {
            itemPendingRemovalList.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(itemList.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        E item = itemList.get(position);
        if (itemPendingRemovalList.contains(item)) {
            itemPendingRemovalList.remove(item);
        }
        if (itemList.contains(item)) {
            itemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        E item = itemList.get(position);
        return itemPendingRemovalList.contains(item);
    }

//   public abstract class TouchSwiped<V, E>{
//       private V viewHolder;
//       private int position;
//
//       public TouchSwiped(RecyclerView.ViewHolder viewHolder, int position) {
//           this.viewHolder = (V) viewHolder;
//           this.position = position;
//           final E item = (E) itemList.get(position);
//
//           if (itemPendingRemovalList.contains(item)) {
//
//               onTouchSwiped.onSwipedLeft(this.viewHolder, position, item);
//           } else {
//               onTouchSwiped.onNormal(this.viewHolder, position, item);
//           }
//       }
//    }



    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     * :http://stackoverflow.com/questions/8280027/what-does-porterduff-mode-mean-in-android-graphics-what-does-it-do
     */
    private void setUpItemTouchHelper(final RecyclerView mRecyclerView) {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper
                .SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            Drawable xMark;
            int xMarkMargin;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                xMark = ContextCompat.getDrawable(mRecyclerView.getContext(), R.drawable.ic_clear_24dp);
                xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
                xMarkMargin = (int) mRecyclerView.getContext()
                        .getResources()
                        .getDimension(R.dimen.ic_clear_margin);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                BaseRecycleViewSwiped testAdapter = (BaseRecycleViewSwiped)recyclerView.getAdapter();
                if (testAdapter.isUndoOn() && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                BaseRecycleViewSwiped adapter = (BaseRecycleViewSwiped)mRecyclerView.getAdapter();
                if (swipeDir == ItemTouchHelper.LEFT){
                    boolean undoOn = adapter.isUndoOn();
                    if (undoOn) {
                        adapter.pendingRemoval(swipedPosition);
                    } else {
                        adapter.remove(swipedPosition);
                    }
                }

            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // draw x mark
                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = xMark.getIntrinsicWidth();
                int intrinsicHeight = xMark.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                xMark.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }


    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper(RecyclerView recyclerView) {
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }

}

