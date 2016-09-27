package net.nemanjakovacevic.recyclerviewswipetodelete.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by jcuentas on 20/09/16.
 */
public class Adapter extends ArrayAdapter {
    public Adapter(Context context, int resource) {
        super(context, resource);

    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
