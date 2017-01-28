package com.ar.sgt.mastersorpresas.model;

import android.graphics.Bitmap;

import com.ar.sgt.mastersorpresas.model.utils.BitmapConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.sql.Blob;

import org.greenrobot.greendao.annotation.Generated;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Promo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long key;

	private String url;

	private String image;

    private byte[] bitmap;

	private String text;

	private String points;

	private String percentage;

	@Generated(hash = 647784266)
	public Promo(Long key, String url, String image, byte[] bitmap, String text,
									String points, String percentage) {
					this.key = key;
					this.url = url;
					this.image = image;
					this.bitmap = bitmap;
					this.text = text;
					this.points = points;
					this.percentage = percentage;
	}

	@Generated(hash = 363330543)
	public Promo() {
	}

	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public void setBitmap(byte[] bitmap) {
		this.bitmap = bitmap;
	}

	public byte[] getBitmap() {
		return bitmap;
	}

	@Override
    public boolean equals(Object obj) {
        if (Promo.class.isAssignableFrom(obj.getClass()) && obj != null) {
            return getKey().equals(((Promo) obj).getKey());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 647784266;
    }

    @Override
	public String toString() {
		StringBuilder b = new StringBuilder("Promo [");
		b.append("text=").append(getText()).append(";");
		b.append("url=").append(getUrl()).append(";");
		b.append("image=").append(getImage()).append(";");
		b.append("points=").append(getPoints()).append(";");
		b.append("percentage=").append(getPercentage()).append("]");
		return b.toString();
	}
	
}
