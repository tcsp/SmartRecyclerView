package ch.comic.tcsp.smartrecyclerview.demo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ch.comic.tcsp.smartrecyclerview.demo.R;

/**
 * Created by songping on 15/12/15.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private List<String> mData;

    public CustomAdapter(Context context) {
        this.mContext = context;
        inflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
    }

    @Override
    public CustomAdapter.CustomHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CustomHolder(inflater.inflate(R.layout.item_custom_adapter, null));
    }

    @Override
    public void onBindViewHolder(CustomAdapter.CustomHolder holder, int position) {
        String message = mData.get(position);
        holder.tvMain.setText(message);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void append(List<String> list) {
        mData.addAll(list);
    }

    public void append(String data) {
        mData.add(data);
    }

    public void clear() {
        mData.clear();
    }

    public static class CustomHolder extends RecyclerView.ViewHolder {
        ImageView ivMain;
        TextView tvMain;

        public CustomHolder(View view) {
            super(view);
            ivMain = (ImageView) view.findViewById(R.id.iv_main);
            tvMain = (TextView) view.findViewById(R.id.tv_main);
        }
    }
}
