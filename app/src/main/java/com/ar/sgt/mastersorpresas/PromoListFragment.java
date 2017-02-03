package com.ar.sgt.mastersorpresas;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ar.sgt.mastersorpresas.model.DaoSession;
import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.model.PromoDao;
import com.ar.sgt.mastersorpresas.model.Reminder;
import com.ar.sgt.mastersorpresas.model.ReminderDao;
import com.ar.sgt.mastersorpresas.task.AsyncTaskStatusListener;
import com.ar.sgt.mastersorpresas.task.DataUpdateTask;
import com.ar.sgt.mastersorpresas.utils.AlarmUtils;
import com.ar.sgt.mastersorpresas.utils.ReminderUtils;
import com.ar.sgt.mastersorpresas.view.OnCardEventListener;
import com.ar.sgt.mastersorpresas.view.EventType;
import com.ar.sgt.mastersorpresas.view.OnFragmentEventListener;
import com.ar.sgt.mastersorpresas.view.PromoListViewAdapter;

import java.text.ParseException;
import java.util.List;

public class PromoListFragment extends Fragment implements OnCardEventListener<Promo> {

    private static final String TAG = "PromoListFragment";

    private RecyclerView mRecycleView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private PromoListViewAdapter adapter;

    private App application;

    private OnFragmentEventListener onFragmentEventListener;


    public PromoListFragment() {
        // Required empty public constructor
    }

    public App getApplication() {
        return application;
    }

    public static PromoListFragment newInstance(Application app) {
        PromoListFragment fragment = new PromoListFragment();
        fragment.application = (App) app;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            onFragmentEventListener = (OnFragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentEventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_promo_list, container, false);

        mRecycleView = (RecyclerView) view.findViewById(R.id.promoList);
        mRecycleView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(mLayoutManager);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });

        return view;
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
                if (result == null) {
                    Toast.makeText(getContext(), getString(R.string.retrieve_error), Toast.LENGTH_LONG).show();;
                } else {
                    resumeAdapter();
                }
            }
        });
        task.execute();
    }

    public void resumeAdapter() {
        PromoDao promoDao = ((App) getApplication()).getDaoSession().getPromoDao();
        List<Promo> promos = promoDao.loadAll();
        adapter = new PromoListViewAdapter(getApplication(), this, promos);
        mRecycleView.setAdapter(adapter);
        mRecycleView.invalidate();
        mRecycleView.getAdapter().notifyDataSetChanged();

        if (promos.isEmpty()) {
            mSwipeRefreshLayout.setRefreshing(true);
            refreshData();
        }
    }

    @Override
    public void onResume() {
        resumeAdapter();
        super.onResume();
    }

    private void createReminder(final Promo promo) {

        if (Boolean.TRUE.equals(promo.getScheduled())) return;

        DaoSession daoSession = ((App) getApplication()).getDaoSession();

        PromoDao promoDao = daoSession.getPromoDao();
        ReminderDao reminderDao = daoSession.getReminderDao();

        Reminder reminder = new Reminder();
        try {
            reminder.setTitle(promo.getTitle());
            reminder.setDateFrom(ReminderUtils.parseDate(promo.getDateFrom()));
            reminder.setDateTo(ReminderUtils.parseDate(promo.getDateTo()));
            reminder.setParentKey(promo.getKey());
            reminder.setPercentage(promo.getPercentage());
            reminder.setNextSchedule(ReminderUtils.getNextSchedule(reminder));
            reminderDao.save(reminder);

            AlarmUtils.scheduleAlarm(getContext(), reminder);

            promo.setScheduled(Boolean.TRUE);
            promoDao.save(promo);

            Snackbar.make(getView(), getString(R.string.reminder_set, reminder.getTitle()), Snackbar.LENGTH_LONG).show();

        } catch (ParseException e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (SQLiteConstraintException sq) {
            Log.e(TAG, sq.getMessage(), sq);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && mRecycleView != null) resumeAdapter();
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onCardEvent(EventType type, Promo promo, Object...args) {
        Log.d(TAG, "Handle event: " + type + " -> " + promo);
        switch (type) {
            case OPEN:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(promo.getUrl()));
                startActivity(browserIntent);
                break;
            case SHARE:
                ShareCompat.IntentBuilder.from(getActivity()).setType("text/plain").setText(promo.getUrl()).startChooser();
                break;
            case UPDATE:
                mRecycleView.getAdapter().notifyItemChanged((int) args[0]);
                break;
            case NOTIFICATION:
                createReminder(promo);
                resumeAdapter();
                this.onFragmentEventListener.onFragmentEvent(getId());
                break;
        }
    }

    public void setOnFragmentEventListener(OnFragmentEventListener onFragmentEventListener) {
        this.onFragmentEventListener = onFragmentEventListener;
    }

}
