package com.ar.sgt.mastersorpresas.view;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ar.sgt.mastersorpresas.App;
import com.ar.sgt.mastersorpresas.MainActivity;
import com.ar.sgt.mastersorpresas.R;
import com.ar.sgt.mastersorpresas.model.Promo;
import com.ar.sgt.mastersorpresas.model.utils.BitmapConverter;
import com.ar.sgt.mastersorpresas.task.AsyncTaskStatusListener;
import com.ar.sgt.mastersorpresas.task.ImageDownloadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Gabriel on 27/01/2017.
 */
public class PromoListViewAdapter extends RecyclerView.Adapter<PromoListViewAdapter.PromoViewHolder> {

    private static final String TAG = "PromoListViewAdapter";

    private final List<Promo> mPromoList;

    private PromoEventListener mEventListener;

    private App mApplication;

    private BitmapConverter mBitmapConverter = new BitmapConverter();

    public PromoListViewAdapter(Application application, PromoEventListener eventListener, List<Promo> promos) {
        this.mPromoList = promos;
        this.mEventListener = eventListener;
        this.mApplication = (App) application;
    }

    @Override
    public PromoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        PromoViewHolder pvh = new PromoViewHolder(v);
        return pvh;
    }

    public App getApplication() {
        return mApplication;
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

        Log.d(TAG, "" + promo.getBitmap());
        if (promo.getBitmap() != null) {
            //holder.mImage.setImageDrawable(Drawable.createFromStream(new ByteArrayInputStream(promo.getBitmap()), "bitmap"));
            //holder.mImage.refreshDrawableState();
            holder.mImage.setImageBitmap(mBitmapConverter.byteArrayToBitmap(promo.getBitmap()));
        } else if (!TextUtils.isEmpty(promo.getImage())) {
            /*ImageDownloadTask downloadTask = new ImageDownloadTask(new AsyncTaskStatusListener<Bitmap>() {
                @Override
                public void preExecute() {
                }
                @Override
                public void postExecute(Bitmap result) {
                    if (result != null) {
                        promo.setBitmap(mBitmapConverter.bitmapToByteArray(result));
                        getApplication().getDaoSession().getPromoDao().update(promo);
                        Log.d(TAG, "" + promo.getBitmap());
                        mPromoList.set(position, promo);
                        //holder.mImage.setImageDrawable(Drawable.createFromStream(new ByteArrayInputStream(promo.getBitmap()), "bitmap"));
                        //holder.mImage.setImageDrawable(null);
                        holder.mImage.setImageBitmap(result);
                    } else {
                        holder.mImage.setImageDrawable(ContextCompat.getDrawable(getApplication().getApplicationContext(), R.drawable.ic_mastercard_logo));
                    }
                    Intent i = new Intent(MainActivity.ImageUpdateReceiver.ACTION);
                    i.putExtra(MainActivity.ImageUpdateReceiver.EXTRA_POSITION, position);
                    getApplication().sendBroadcast(i);
                }
            });
            try {
                downloadTask.execute(new URL(promo.getImage()));
            } catch (MalformedURLException e) {
                Log.e(TAG, promo.getImage(), e);
                downloadTask = null;
            }*/
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
                            getApplication().getDaoSession().getPromoDao().update(promo);
                            mPromoList.set(position, promo);
                            Intent i = new Intent(MainActivity.ImageUpdateReceiver.ACTION);
                            i.putExtra(MainActivity.ImageUpdateReceiver.EXTRA_POSITION, position);
                            getApplication().sendBroadcast(i);
                        }
                        @Override
                        public void onError() {
                        }
                    });
                    /*.into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            holder.mImage.setImageDrawable(null);
                            holder.mImage.setImageBitmap(bitmap);
                            holder.mImage.refreshDrawableState();
                            promo.setBitmap(bitmap);
                            getApplication().getDaoSession().getPromoDao().update(promo);
                            mPromoList.set(position, promo);
                            Intent i = new Intent(MainActivity.ImageUpdateReceiver.ACTION);
                            i.putExtra(MainActivity.ImageUpdateReceiver.EXTRA_POSITION, position);
                            getApplication().sendBroadcast(i);
                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            holder.mImage.setImageDrawable(errorDrawable);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            holder.mImage.setImageDrawable(placeHolderDrawable);
                        }
                    });*/
        }


        /*
        if (promo.getBitmap() != null) {

        } else {
            ImageDownloadTask downloadTask = new ImageDownloadTask(new AsyncTaskStatusListener<Bitmap>() {
                @Override
                public void preExecute() {
                }
                @Override
                public void postExecute(Bitmap result) {
                    promo.setBitmap(result);
                    getApplication().getDaoSession().getPromoDao().update(promo);
                    holder.mImage.setImageBitmap(promo.getBitmap());
                    holder.mImage.refreshDrawableState();
                }
            });
            try {
                downloadTask.execute(new URL(promo.getUrl()));
            } catch (MalformedURLException e) {
                Log.e(TAG, promo.getUrl(), e);
                downloadTask = null;
            }
        }*/

        holder.openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Open: " + promo.getText());
                mEventListener.onOpenEvent(promo);
            }
        });
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Share: " + promo.getText());
                mEventListener.onShareEvent(promo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.mPromoList.size();
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
        private Button openButton;
        private Button shareButton;

        public PromoViewHolder(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.promoText);
            mPoints = (TextView) itemView.findViewById(R.id.promoPoints);
            mPercentage = (TextView) itemView.findViewById(R.id.promoPercentage);
            mImage = (ImageView) itemView.findViewById(R.id.promoImage);
            openButton = (Button) itemView.findViewById(R.id.buttonOpen);
            shareButton = (Button) itemView.findViewById(R.id.buttonShare);
        }

    }

}
