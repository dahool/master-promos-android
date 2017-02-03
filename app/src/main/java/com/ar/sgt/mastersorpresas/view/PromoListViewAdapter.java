package com.ar.sgt.mastersorpresas.view;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.model.utils.BitmapConverter;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by Gabriel on 27/01/2017.
 */
public class PromoListViewAdapter extends RecyclerView.Adapter<PromoListViewAdapter.PromoViewHolder> {

    private static final String TAG = "PromoListViewAdapter";

    private final List<Promo> mPromoList;

    private OnCardEventListener mEventListener;

    private App mApplication;

    private BitmapConverter mBitmapConverter = new BitmapConverter();

    public PromoListViewAdapter(Application application, OnCardEventListener eventListener, List<Promo> promos) {
        this.mPromoList = promos;
        this.mEventListener = eventListener;
        this.mApplication = (App) application;
    }

    @Override
    public PromoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.promo_card, parent, false);
        PromoViewHolder pvh = new PromoViewHolder(v);
        return pvh;
    }

    public App getApplication() {
        return mApplication;
    }

    private void inflateMenu(final Promo promo, final PromoViewHolder holder, final int position) {
        if (!Boolean.TRUE.equals(promo.getScheduled()) && promo.getDateTo() != null && promo.getDateFrom() != null) {
            holder.toolbar.inflateMenu(R.menu.cardmenu);
            holder.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    mEventListener.onCardEvent(EventType.NOTIFICATION, promo, position);
                    return true;
                }
            });
        }
    }
    @Override
    public void onBindViewHolder(final PromoViewHolder holder, final int position) {
        final Promo promo = this.mPromoList.get(position);

        if (TextUtils.isEmpty(promo.getPoints())) {
            holder.mPoints.setVisibility(View.GONE);
        } else {
            holder.mPoints.setText(promo.getPoints());
        }
        if (TextUtils.isEmpty(promo.getPercentage())) {
            holder.mPercentage.setVisibility(View.GONE);
        } else {
            holder.mPercentage.setText(promo.getPercentage());
        }

        holder.mText.setText(promo.getText());

        holder.toolbar.setTitle(promo.getTitle());

        if (Boolean.TRUE.equals(promo.getHasStock())) {
            holder.mDates.setText(getApplication().getString(R.string.promo_date_text, promo.getDateFrom(), promo.getDateTo()));
            inflateMenu(promo, holder, position);
        } else if (promo.getHasStock() != null) {
            holder.mDates.setText(getApplication().getString(R.string.promo_outofstock));
            inflateMenu(promo, holder, position);
        } else {
            holder.mDates.setText("");
        }

        Log.d(TAG, "" + promo.getBitmap());
        if (promo.getBitmap() != null) {
            holder.mImage.setImageBitmap(mBitmapConverter.byteArrayToBitmap(promo.getBitmap()));
        } else if (!TextUtils.isEmpty(promo.getImage())) {
            Picasso.with(getApplication().getApplicationContext())
                    .load(promo.getImage())
                    .placeholder(R.drawable.no_image)
                    .error(R.drawable.ic_mastercard_logo)
                    .into(holder.mImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            Log.d(TAG, "Loaded " + promo.getImage());
                            Bitmap bitmap = ((BitmapDrawable) holder.mImage.getDrawable()).getBitmap();
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] bitmapdata = stream.toByteArray();
                            promo.setBitmap(bitmapdata);
                            getApplication().getDaoSession().getPromoDao().save(promo);
                            mPromoList.set(position, promo);
                            mEventListener.onCardEvent(EventType.UPDATE, promo, position);
                        }
                        @Override
                        public void onError() {
                        }
                    });
        }
        holder.openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Open: " + promo.getTitle());
                mEventListener.onCardEvent(EventType.OPEN, promo);
            }
        });
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Share: " + promo.getTitle());
                mEventListener.onCardEvent(EventType.SHARE, promo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mPromoList.size();
    }

    public void updateItem(Promo item, int position) {
        mPromoList.set(position, item);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PromoViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mText;
        private TextView mPoints;
        private TextView mPercentage;
        private TextView mDates;
        private Button openButton;
        private Button shareButton;
        private Toolbar toolbar;

        public PromoViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.promoText);
            mPoints = (TextView) itemView.findViewById(R.id.promoPoints);
            mPercentage = (TextView) itemView.findViewById(R.id.promoPercentage);
            mImage = (ImageView) itemView.findViewById(R.id.promoImage);
            mDates = (TextView) itemView.findViewById(R.id.promoDates);
            openButton = (Button) itemView.findViewById(R.id.buttonOpen);
            shareButton = (Button) itemView.findViewById(R.id.buttonShare);
            toolbar = (Toolbar) itemView.findViewById(R.id.cardToolbar);
        }

    }

}
