package ch.comic.tcsp.smartrecyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by tcsp on 15/12/13.
 */
public class SmartRecycleView extends RecyclerView {

    private Adapter mAdapter;
    private Adapter mWrapAdapter;
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFooterViews = new ArrayList<>();

    private View mEmptyView;
    private View mConvertEmptyView;

    private RecyclerView.OnScrollListener mLoadingMoreScrollListener;

    private int mRealFooterCounter = 0;
    private OnLoadMoreHelper mOnLoadMoreHelper;
    private View mLoadingMoreView;
    private LoadMode mLoadMode = LoadMode.AUTO;

    public SmartRecycleView(Context context) {
        super(context);
    }

    public SmartRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        mAdapter = adapter;
        mWrapAdapter = new SmartRecycleAdapter(mHeaderViews, mFooterViews, adapter);
        super.setAdapter(mWrapAdapter);
        //将数据刷新监听注册到未被装饰的adapter
        mAdapter.registerAdapterDataObserver(mDataObserver);
        if (mEmptyView != null) {
            updateEmptyStatus((mAdapter == null) || mAdapter.getItemCount() == 0);
        }
        //默认支持加载更多
        enableLoadmore();
    }

    private final RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            mWrapAdapter.notifyDataSetChanged();
            updateHelperDisplays();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeInserted(positionStart, itemCount);
            updateHelperDisplays();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount);
            updateHelperDisplays();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            mWrapAdapter.notifyItemRangeChanged(positionStart, itemCount, payload);
            updateHelperDisplays();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            mWrapAdapter.notifyItemRangeRemoved(positionStart, itemCount);
            updateHelperDisplays();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            mWrapAdapter.notifyItemMoved(fromPosition, toPosition);
            updateHelperDisplays();
        }
    };

    public void addHeaderView(View view) {
        if (view == null) {
            throw new IllegalArgumentException("header is null");
        }

        mHeaderViews.add(view);
        notifyHeaderOrFooter();
    }

    public void addFooterView(View view) {
        if (view == null) {
            throw new IllegalArgumentException("footer is null");
        }

        mFooterViews.add(view);
        notifyHeaderOrFooter();
    }

    private void addFooterView(View view, boolean isLoadingMore) {
        if (!isLoadingMore) {
            mRealFooterCounter++;
        }
        addFooterView(view);
    }

    public void removeHeaderView(View view) {
        mHeaderViews.remove(view);
        notifyHeaderOrFooter();
    }

    public void removeFooterView(View view) {
        mFooterViews.remove(view);
        notifyHeaderOrFooter();
    }

    private void removeFooterView(View view, boolean isLoadingMore) {
        if (!isLoadingMore) {
            mRealFooterCounter--;
        }
        removeFooterView(view);
    }

    public int getHeaderViewsCount() {
        return mHeaderViews.size();
    }

    public int getFooterViewsCount() {
        return mFooterViews.size();
    }

    private void notifyHeaderOrFooter() {
        if (mWrapAdapter != null) {
            mDataObserver.onChanged();
        }
    }

    /**
     * Sets the view to show if the adapter is empty
     */
    public void setEmptyView(View emptyView) {
        mEmptyView = emptyView;

        final boolean empty = ((mAdapter == null) || mAdapter.getItemCount() == 0);
        updateEmptyStatus(empty);
    }

    public void setConvertEmptyView(View view) {
        mConvertEmptyView = view;
    }

    private void updateHelperDisplays() {
        if (mAdapter == null)
            return;

        if (mEmptyView != null) {
            updateEmptyStatus((mAdapter == null) || mAdapter.getItemCount() == 0);
        }
    }

    /**
     * Update the status of the list based on the empty parameter.  If empty is true and
     * we have an empty view, display it.  In all the other cases, make sure that the listview
     * is VISIBLE and that the empty view is GONE (if it's not null).
     */
    private void updateEmptyStatus(boolean empty) {
        if (empty) {
            if (mEmptyView != null) {
                mEmptyView.setVisibility(View.VISIBLE);
                setVisibility(View.GONE);
                if (mConvertEmptyView != null) {
                    mConvertEmptyView.setVisibility(GONE);
                }
            } else {
                // If the caller just removed our empty view, make sure the list view is visible
                setVisibility(View.VISIBLE);
                if (mConvertEmptyView != null) {
                    mConvertEmptyView.setVisibility(VISIBLE);
                }
            }

        } else {
            if (mEmptyView != null) mEmptyView.setVisibility(View.GONE);
            setVisibility(View.VISIBLE);
            if (mConvertEmptyView != null) {
                mConvertEmptyView.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * Enable loading more of the recyclerview
     */
    public void enableLoadmore() {
        if (mLoadingMoreScrollListener != null) {
            removeOnScrollListener(mLoadingMoreScrollListener);
            mLoadingMoreScrollListener = null;
        }
        mLoadingMoreScrollListener = new RecyclerView.OnScrollListener() {
            private int[] lastPositions;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

                int mVisibleItemCount = layoutManager.getChildCount();
                int mTotalItemCount = layoutManager.getItemCount();
                Log.i("tcsp", "mVisibleItemCount: " + mVisibleItemCount);
                Log.i("tcsp", "mTotalItemCount: " + mTotalItemCount);
                int mFirstVisibleItem = 0;
                int lastVisibleItemPosition = 0;

                if (layoutManager instanceof GridLayoutManager || layoutManager instanceof LinearLayoutManager) {
                    lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                    mFirstVisibleItem = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                    if (lastPositions == null)
                        lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];

                    staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                    lastVisibleItemPosition = findMax(lastPositions);

                    staggeredGridLayoutManager.findFirstVisibleItemPositions(lastPositions);
                    mFirstVisibleItem = findMin(lastPositions);
                }

                Log.i("tcsp", "lastVisibleItemPosition: " + lastVisibleItemPosition);
                Log.i("tcsp", "mFirstVisibleItem: " + mFirstVisibleItem);

                if (getFooterViewsCount() == mRealFooterCounter && (mFirstVisibleItem + mVisibleItemCount) == mTotalItemCount && mFirstVisibleItem > 0) {
                    if (mOnLoadMoreHelper != null && mOnLoadMoreHelper.canLoadMore()) {
                        addFooterView(getLoadingMoreView(), true);
                        if (mLoadMode == LoadMode.AUTO) {
                            mOnLoadMoreHelper.onLoadMore();
                        }
                    }
                }
            }
        };
        addOnScrollListener(mLoadingMoreScrollListener);
    }

    public void disableLoadmore() {
        if (mLoadingMoreScrollListener != null) {
            removeOnScrollListener(mLoadingMoreScrollListener);
            mLoadingMoreScrollListener = null;
        }
    }

    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }

    private int findMin(int[] lastPositions) {
        int min = Integer.MAX_VALUE;
        for (int value : lastPositions) {
            if (value != RecyclerView.NO_POSITION && value < min)
                min = value;
        }
        return min;
    }

    public void setLoadMoreMode(LoadMode mode) {
        mLoadMode = mode;
    }

    public void onLoadMoreCompleted() {
        if (mLoadingMoreView != null) {
            removeFooterView(mLoadingMoreView, true);
            mLoadingMoreView = null;
        }
    }

    public void onLoadMoreFailed() {
        if (mLoadingMoreView != null) {
            removeFooterView(mLoadingMoreView, true);
            mLoadingMoreView = null;
            addFooterView(getLoadingMoreView(LoadMode.MANUAL), true);
        }
    }

    private View getLoadingMoreView() {
        return getLoadingMoreView(mLoadMode);
    }

    private View getLoadingMoreView(LoadMode mode) {
        if (mode == LoadMode.AUTO) {
            if (mOnLoadMoreHelper != null) {
                mLoadingMoreView = mOnLoadMoreHelper.createCustomLoadingFooter();
            }
            if (mLoadingMoreView == null) {
                mLoadingMoreView = LayoutInflater.from(getContext()).inflate(R.layout.loading_view, null);
            }
        } else {
            if (mOnLoadMoreHelper != null) {
                mLoadingMoreView = mOnLoadMoreHelper.createCustomManualLoadingFooter();
            }
            if (mLoadingMoreView == null) {
                mLoadingMoreView = LayoutInflater.from(getContext()).inflate(R.layout.manual_loading_view, null);
            }

            mLoadingMoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnLoadMoreHelper.onLoadMore();
                    removeFooterView(mLoadingMoreView, true);
                    addFooterView(getLoadingMoreView(LoadMode.AUTO), true);
                }
            });
        }

        return mLoadingMoreView;
    }

    public abstract static class OnLoadMoreHelper {
        public abstract boolean canLoadMore();

        public abstract void onLoadMore();

        public View createCustomLoadingFooter() {
            return null;
        }

        public View createCustomManualLoadingFooter() {
            return null;
        }
    }

    public static enum LoadMode {
        AUTO,
        MANUAL
    }

    public void setOnLoadMoreHelper(OnLoadMoreHelper listener) {
        mOnLoadMoreHelper = listener;
    }
}
