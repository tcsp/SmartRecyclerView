package ch.comic.tcsp.smartrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;

/**
 * Created by tcsp on 15/12/13.
 */
public class UltraPtrRecycleView extends FrameLayout {

    public PtrFrameLayout mPtrFrameLayout;
    public SmartRecycleView mRecyclerView;
    private PtrClassicDefaultHeader mPtrClassicHeader;
    private OnRefreshListener refreshListener;

    public UltraPtrRecycleView(Context context) {
        super(context);
        initViews();
    }

    public UltraPtrRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public UltraPtrRecycleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    protected void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.ultra_ptr_recycleview, this);
        initCustomSwipeToRefresh();
        mRecyclerView = (SmartRecycleView) view.findViewById(R.id.sr_list);
        mRecyclerView.setConvertEmptyView(this);
    }

    /**
     * init the layout view for pull to refresh
     */
    private void initCustomSwipeToRefresh() {
        mPtrFrameLayout = (PtrFrameLayout) findViewById(R.id.ptr_layout);
        //阻尼系数，默认:1.7f，越大，感觉下拉时越吃力
        mPtrFrameLayout.setResistance(1.7f);
        //触发刷新时移动的位置比例，默认，1.2f，移动达到头部高度 1.2 倍时可触发刷新操作。
        mPtrFrameLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        //回弹延时，默认200ms，回弹到刷新高度所用时间。
        mPtrFrameLayout.setDurationToClose(100);
        //头部回弹时间，默认1000ms。
        mPtrFrameLayout.setDurationToCloseHeader(500);
        //刷新是否保持头部，默认值true。
        mPtrFrameLayout.setPullToRefresh(false);
        //下拉刷新 / 释放刷新，默认为释放刷新。
        mPtrFrameLayout.setKeepHeaderWhenRefresh(true);

        mPtrClassicHeader = new PtrClassicDefaultHeader(getContext());
        mPtrFrameLayout.setHeaderView(mPtrClassicHeader);
        mPtrFrameLayout.addPtrUIHandler(mPtrClassicHeader);

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if (refreshListener != null) {
                    refreshListener.onRefresh(mRecyclerView);
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
    }

    public final SmartRecycleView getRefreshableView() {
        return mRecyclerView;
    }

    public interface OnRefreshListener {
        void onRefresh(SmartRecycleView recycleView);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.refreshListener = listener;
    }

    public void onRefreshComplete() {
        mPtrFrameLayout.refreshComplete();
    }
}
