package com.ar.sgt.mastersorpresas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ar.sgt.mastersorpresas.gcm.RegistrationIntentService;
import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.model.PromoDao;
import com.ar.sgt.mastersorpresas.task.AsyncTaskStatusListener;
import com.ar.sgt.mastersorpresas.task.DataUpdateTask;
import com.ar.sgt.mastersorpresas.utils.NotificationMngr;
import com.ar.sgt.mastersorpresas.view.PromoEventListener;
import com.ar.sgt.mastersorpresas.view.PromoListViewAdapter;

import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity implements PromoEventListener {

    private RecyclerView mRecycleView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private PromoListViewAdapter adapter;

    private ImageUpdateReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecycleView = (RecyclerView) findViewById(R.id.promoList);
        mRecycleView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecycleView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        if (((App) getApplication()).getDaoSession().getPromoDao().count() == 0) {
            mSwipeRefreshLayout.setRefreshing(true);
            refreshData();
        }

    }

    private void refreshData() {
        DataUpdateTask task = new DataUpdateTask(getApplication());
        task.setStatusListener(new AsyncTaskStatusListener() {
            @Override
            public void preExecute() {
                // do nothing
            }
            @Override
            public void postExecute(Object result) {
                mSwipeRefreshLayout.setRefreshing(false);
                resumeAdapter();
            }
        });
        task.execute();
    }

    @Override
    protected void onResume() {
        mReceiver = new ImageUpdateReceiver(this);
        registerReceiver(mReceiver, new IntentFilter(ImageUpdateReceiver.ACTION));

        resumeAdapter();
        NotificationMngr.hideNotification(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

    private void resumeAdapter() {
        PromoDao promoDao = ((App) getApplication()).getDaoSession().getPromoDao();

        adapter = new PromoListViewAdapter(getApplication(), this, promoDao.loadAll());
        mRecycleView.setAdapter(adapter);
        mRecycleView.invalidate();
        mRecycleView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onOpenEvent(Promo promo) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(promo.getUrl()));
        startActivity(browserIntent);
    }

    @Override
    public void onShareEvent(Promo promo) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, promo.getUrl());
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class ImageUpdateReceiver extends BroadcastReceiver {

        public static final String ACTION = "com.ar.sgt.mastersorpresas.MainActivity.ImageUpdateReceiver";

        public static final String EXTRA_POSITION = "POSITION";

        private WeakReference<MainActivity> mActivity;

        public ImageUpdateReceiver(final MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ImageUpdateReceiver", "Enter");
            MainActivity activity = mActivity.get();
            if (activity != null) {
                Log.d("ImageUpdateReceiver", "Update Image");
                activity.mRecycleView.getAdapter().notifyItemChanged(intent.getIntExtra(EXTRA_POSITION, 0));
            }
        }
    }

}
