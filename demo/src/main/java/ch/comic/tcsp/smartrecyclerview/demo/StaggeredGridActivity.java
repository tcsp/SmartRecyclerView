package ch.comic.tcsp.smartrecyclerview.demo;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.comic.tcsp.smartrecyclerview.SmartRecycleView;
import ch.comic.tcsp.smartrecyclerview.UltraPtrRecycleView;
import ch.comic.tcsp.smartrecyclerview.demo.adapter.CustomAdapter;

/**
 * Created by songping on 15/12/23.
 */
public class StaggeredGridActivity extends AppCompatActivity {
    private boolean mCanLoadMore = true;

    private UltraPtrRecycleView pullToRefreshRecyclerView;
    private SmartRecycleView mRecycleView;
    private CustomAdapter mAdapter;

    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

        onRefresh();
    }

    private void initView() {
        pullToRefreshRecyclerView = (UltraPtrRecycleView) findViewById(R.id.wrv_list);
        mRecycleView = pullToRefreshRecyclerView.getRefreshableView();
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(staggeredGridLayoutManager);
        mAdapter = new CustomAdapter(this);
        mRecycleView.setAdapter(mAdapter);

        tvEmpty = (TextView) findViewById(R.id.tv_empty);
        mRecycleView.setEmptyView(tvEmpty);

        //添加第一个header
        View header1 = new View(this);
        header1.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30);
        header1.setLayoutParams(layoutParams1);
        mRecycleView.addHeaderView(header1);

        pullToRefreshRecyclerView.setOnRefreshListener(new UltraPtrRecycleView.OnRefreshListener() {
            @Override
            public void onRefresh(SmartRecycleView recycleView) {
                StaggeredGridActivity.this.onRefresh();
            }
        });
        mRecycleView.setOnLoadMoreHelper(new SmartRecycleView.OnLoadMoreHelper() {
            @Override
            public boolean canLoadMore() {
                return mCanLoadMore;
            }

            @Override
            public void onLoadMore() {
                onMore();
            }
        });
    }

    //下拉刷新
    private void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {

                mAdapter.clear();
                for (int i = 0; i < 15; i++) {
                    mAdapter.append("item" + i);
                }
                mAdapter.notifyDataSetChanged();
                pullToRefreshRecyclerView.onRefreshComplete();
            }

        }, 1000);
    }

    //加载更多
    private void onMore() {
        new Handler().postDelayed(new Runnable() {
            public void run() {

                for (int i = 0; i < 15; i++) {
                    mAdapter.append("item" + i);
                }
                mAdapter.notifyDataSetChanged();
                mRecycleView.onLoadMoreCompleted();
            }

        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
