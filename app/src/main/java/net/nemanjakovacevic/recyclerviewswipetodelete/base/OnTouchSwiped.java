package net.nemanjakovacevic.recyclerviewswipetodelete.base;

/**
 * Created by jcuentas on 15/09/16.
 */
interface OnTouchSwiped<V, E> {
    void onNormal(V viewHolder, int position, E item);
    void onSwipedLeft(V viewHolder, int position, E item);
}
