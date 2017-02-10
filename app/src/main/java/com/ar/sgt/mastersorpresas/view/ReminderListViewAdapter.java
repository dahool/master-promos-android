package com.ar.sgt.mastersorpresas.view;

import android.app.Application;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.Reminder;
import com.ar.sgt.mastersorpresas.model.utils.BitmapConverter;
import com.ar.sgt.mastersorpresas.utils.ReminderUtils;

import java.util.List;

/**
 * Created by Gabriel on 27/01/2017.
 */
public class ReminderListViewAdapter extends RecyclerView.Adapter<ReminderListViewAdapter.ReminderViewHolder> {

    private static final String TAG = "ReminderListViewAdapter";

    private final List<Reminder> mItemList;

    private OnCardEventListener mEventListener;

    private App mApplication;

    private BitmapConverter mBitmapConverter = new BitmapConverter();

    public ReminderListViewAdapter(Application application, OnCardEventListener eventListener, List<Reminder> promos) {
        this.mItemList = promos;
        this.mEventListener = eventListener;
        this.mApplication = (App) application;
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_card, parent, false);
        ReminderViewHolder pvh = new ReminderViewHolder(v);
        return pvh;
    }

    public App getApplication() {
        return mApplication;
    }

    @Override
    public void onBindViewHolder(final ReminderViewHolder holder, final int position) {
        final Reminder item = this.mItemList.get(position);

        if (TextUtils.isEmpty(item.getPercentage())) {
            holder.mPercentage.setVisibility(View.GONE);
        } else {
            holder.mPercentage.setText(item.getPercentage());
        }

        holder.toolbar.setTitle(item.getTitle());
        holder.mDates.setText(getApplication().getString(R.string.promo_date_text, ReminderUtils.formatDate(item.getDateFrom()), ReminderUtils.formatDate(item.getDateTo())));

        if (ReminderUtils.canBeScheduled(item)) {
            if (item.getNextSchedule() == null) {
                holder.disableButton.setText(getApplication().getString(R.string.button_enable_alarm));
                holder.disableButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_notification_on, 0, 0, 0);
            }
        } else {
            holder.disableButton.setEnabled(false);
            holder.disableButton.setAlpha(.5f);
        }

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Delete: " + item.getTitle());
                mEventListener.onCardEvent(EventType.DELETE, item);
            }
        });
        holder.disableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Disable: " + item.getTitle());
                mEventListener.onCardEvent(EventType.NOTIFICATION, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mItemList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {

        private TextView mPercentage;
        private TextView mDates;
        private Button deleteButton;
        private Button disableButton;
        private Toolbar toolbar;

        public ReminderViewHolder(View itemView) {
            super(itemView);
            mDates = (TextView) itemView.findViewById(R.id.promoDates);
            mPercentage = (TextView) itemView.findViewById(R.id.promoPercentage);
            deleteButton = (Button) itemView.findViewById(R.id.buttonDelete);
            disableButton = (Button) itemView.findViewById(R.id.buttonDisable);
            toolbar = (Toolbar) itemView.findViewById(R.id.cardToolbar);
        }

    }

}
