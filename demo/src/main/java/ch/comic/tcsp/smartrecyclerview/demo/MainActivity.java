package ch.comic.tcsp.smartrecyclerview.demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import ch.comic.tcsp.smartrecyclerview.SmartRecycleView;
import ch.comic.tcsp.smartrecyclerview.UltraPtrRecycleView;
import ch.comic.tcsp.smartrecyclerview.demo.adapter.CustomAdapter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLinear, btnGrid, btnStagger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();
    }

    private void initView() {
        btnLinear = (Button) findViewById(R.id.btn_linear);
        btnGrid = (Button) findViewById(R.id.btn_grid);
        btnStagger = (Button) findViewById(R.id.btn_staggered);

        btnLinear.setOnClickListener(this);
        btnGrid.setOnClickListener(this);
        btnStagger.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_linear:
                intent.setClass(this, LinearLayoutActivity.class);
                break;
            case R.id.btn_grid:
                intent.setClass(this, GridLayoutActivity.class);
                break;
            case R.id.btn_staggered:
                intent.setClass(this, StaggeredGridActivity.class);
                break;
        }
        startActivity(intent);
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
