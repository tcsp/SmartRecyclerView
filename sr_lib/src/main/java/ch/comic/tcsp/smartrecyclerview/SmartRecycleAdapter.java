package ch.comic.tcsp.smartrecyclerview;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by tcsp on 15/12/13.
 */
public class SmartRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RecyclerView.Adapter mAdapter;

    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFooterViews = new ArrayList<>();

    private static final int TYPE_HEADER_VIEW = Integer.MIN_VALUE;
    private static final int TYPE_FOOTER_VIEW = Integer.MIN_VALUE / 2;

    public SmartRecycleAdapter(ArrayList<View> headerViews, ArrayList<View> footViews, RecyclerView.Adapter adapter) {
        this.mAdapter = adapter;
        this.mHeaderViews = headerViews;
        this.mFooterViews = footViews;
    }

    public int getHeaderViewsCount() {
        return mHeaderViews.size();
    }

    public int getFooterViewsCount() {
        return mFooterViews.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int headerViewsCount = getHeaderViewsCount();
        int footerViewsCount = getFooterViewsCount();
        if (viewType < TYPE_HEADER_VIEW + headerViewsCount) {
            return new HeaderFooterHolder(mHeaderViews.get(viewType - TYPE_HEADER_VIEW));
        } else if (viewType >= TYPE_FOOTER_VIEW && viewType < TYPE_FOOTER_VIEW + footerViewsCount) {
            return new HeaderFooterHolder(mFooterViews.get(viewType - TYPE_FOOTER_VIEW));
        } else {
            return mAdapter.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        int headerViewsCount = getHeaderViewsCount();
        int footerViewsCount = getFooterViewsCount();
        if (viewType < TYPE_HEADER_VIEW + headerViewsCount) {
            if (getInnerItemCount() == 0) {
                holder.itemView.setVisibility(View.GONE);
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
            }
        } else if (viewType >= TYPE_FOOTER_VIEW && viewType < TYPE_FOOTER_VIEW + footerViewsCount) {
            if (getInnerItemCount() == 0) {
                holder.itemView.setVisibility(View.GONE);
            } else {
                holder.itemView.setVisibility(View.VISIBLE);
            }
        } else {
            mAdapter.onBindViewHolder(holder, position - headerViewsCount);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int innerCount = getInnerItemCount();
        int headerViewsCount = getHeaderViewsCount();
        if (position < headerViewsCount) {
            return TYPE_HEADER_VIEW + position;
        } else if (headerViewsCount <= position && position < headerViewsCount + innerCount) {
            int innerItemViewType = mAdapter.getItemViewType(position - headerViewsCount);
            if (innerItemViewType < 0) {
                throw new IllegalArgumentException("your adapter's return value of getViewTypeCount() must >= 0");
            }
            return innerItemViewType;
        } else {
            return TYPE_FOOTER_VIEW + position - headerViewsCount - innerCount;
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderViewsCount() + getFooterViewsCount() + getInnerItemCount();
    }

    private int getInnerItemCount() {
        if (mAdapter == null) {
            return 0;
        } else {
            return mAdapter.getItemCount();
        }
    }

    /**
     * 当前位置是否为headerview
     *
     * @param position
     * @return
     */
    public boolean isHeaderPosition(int position) {
        return position < getHeaderViewsCount();
    }

    /**
     * 当前位置是否为footerview
     *
     * @param position
     * @return
     */
    public boolean isFooterPosition(int position) {
        return position >= getHeaderViewsCount() + getInnerItemCount();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        final RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            final GridLayoutManager.SpanSizeLookup lookup = ((GridLayoutManager) manager).getSpanSizeLookup();
            GridLayoutManager.SpanSizeLookup newLookup = new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (isHeaderPosition(position) || isFooterPosition(position)) {
                        return ((GridLayoutManager) manager).getSpanCount();
                    }
                    if (getHeaderViewsCount() > 0) {
                        return lookup.getSpanSize(position - getHeaderViewsCount());
                    } else {
                        return lookup.getSpanSize(position);
                    }
                }
            };
            ((GridLayoutManager) manager).setSpanSizeLookup(newLookup);
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
        if (lp != null
                && lp instanceof StaggeredGridLayoutManager.LayoutParams
                && (isHeaderPosition(holder.getLayoutPosition()) || isFooterPosition(holder.getLayoutPosition()))) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }

    private static class HeaderFooterHolder extends RecyclerView.ViewHolder {

        public HeaderFooterHolder(View itemView) {
            super(itemView);
        }
    }
}
