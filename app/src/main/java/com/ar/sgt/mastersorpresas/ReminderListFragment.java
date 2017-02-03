package com.ar.sgt.mastersorpresas;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.model.PromoDao;
import com.ar.sgt.mastersorpresas.model.Reminder;
import com.ar.sgt.mastersorpresas.model.ReminderDao;
import com.ar.sgt.mastersorpresas.utils.AlarmUtils;
import com.ar.sgt.mastersorpresas.utils.ReminderUtils;
import com.ar.sgt.mastersorpresas.view.OnCardEventListener;
import com.ar.sgt.mastersorpresas.view.EventType;
import com.ar.sgt.mastersorpresas.view.OnFragmentEventListener;
import com.ar.sgt.mastersorpresas.view.ReminderListViewAdapter;

import java.util.List;

public class ReminderListFragment extends Fragment implements OnCardEventListener<Reminder> {

    private static final String TAG = "ReminderListFragment";

    private RecyclerView mRecycleView;

    private ReminderListViewAdapter adapter;

    private OnFragmentEventListener onFragmentEventListener;

    private App application;

    public ReminderListFragment() {
        // Required empty public constructor
    }

    public App getApplication() {
        return application;
    }

    public static ReminderListFragment newInstance(Application app) {
        ReminderListFragment fragment = new ReminderListFragment();
        fragment.application = (App) app;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminder_list, container, false);

        mRecycleView = (RecyclerView) view.findViewById(R.id.reminderList);
        mRecycleView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(mLayoutManager);

        return view;
    }

    public void resumeAdapter() {
        ReminderDao itemDao = ((App) getApplication()).getDaoSession().getReminderDao();
        List<Reminder> reminderList = itemDao.queryBuilder().orderDesc(ReminderDao.Properties.DateTo).list();

        adapter = new ReminderListViewAdapter(getApplication(), this, reminderList);
        mRecycleView.setAdapter(adapter);
        mRecycleView.invalidate();
        mRecycleView.getAdapter().notifyDataSetChanged();
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
    public void onResume() {
        resumeAdapter();
        super.onResume();
    }

    public void onDeleteEvent(Reminder item) {
        ReminderDao itemDao = ((App) getApplication()).getDaoSession().getReminderDao();
        PromoDao promoDao = ((App) getApplication()).getDaoSession().getPromoDao();
        Promo promo = promoDao.load(item.getParentKey());
        if (promo != null) {
            promo.setScheduled(Boolean.FALSE);
            promoDao.save(promo);
        }
        AlarmUtils.cancelAlarm(getContext(), item);
        itemDao.delete(item);
        resumeAdapter();

        this.onFragmentEventListener.onFragmentEvent(getId());
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser && mRecycleView != null) resumeAdapter();
        super.setUserVisibleHint(isVisibleToUser);
    }

    public void onDisableEvent(Reminder item) {
        // TODO manage alarms
        ReminderDao itemDao = ((App) getApplication()).getDaoSession().getReminderDao();
        if (item.getNextSchedule() != null) {
            item.setNextSchedule(null);
            AlarmUtils.cancelAlarm(getContext(), item);
            Snackbar.make(getView(), getString(R.string.reminder_unset, item.getTitle()), Snackbar.LENGTH_SHORT).show();
        } else {
            item.setNextSchedule(ReminderUtils.getNextSchedule(item));
            AlarmUtils.scheduleAlarm(getContext(), item);
            if (item.getNextSchedule() != null) Snackbar.make(getView(), getString(R.string.reminder_set, item.getTitle()), Snackbar.LENGTH_SHORT).show();
        }
        itemDao.save(item);
        resumeAdapter();
    }

    @Override
    public void onCardEvent(EventType type, Reminder item, Object... args) {
        Log.d(TAG, "Handle event: " + type + " -> " + item);
        switch (type) {
            case NOTIFICATION:
                onDisableEvent(item);
                break;
            case DELETE:
                onDeleteEvent(item);
                break;
        }
    }

    public void setOnFragmentEventListener(OnFragmentEventListener onFragmentEventListener) {
        this.onFragmentEventListener = onFragmentEventListener;
    }

}
