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

    private String title;

    private String dateFrom;

    private String dateTo;

    private Boolean hasStock;

	private Boolean scheduled;

	@Generated(hash = 660444528)
	public Promo(Long key, String url, String image, byte[] bitmap, String text,
									String points, String percentage, String title, String dateFrom,
									String dateTo, Boolean hasStock, Boolean scheduled) {
					this.key = key;
					this.url = url;
					this.image = image;
					this.bitmap = bitmap;
					this.text = text;
					this.points = points;
					this.percentage = percentage;
					this.title = title;
					this.dateFrom = dateFrom;
					this.dateTo = dateTo;
					this.hasStock = hasStock;
					this.scheduled = scheduled;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public Boolean getHasStock() {
        return hasStock;
    }

    public void setHasStock(Boolean hasStock) {
        this.hasStock = hasStock;
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

	public Boolean getScheduled() {
		return scheduled;
	}

	public void setScheduled(Boolean scheduled) {
		this.scheduled = scheduled;
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
        b.append("key=").append(getKey()).append(";");
        b.append("title=").append(title).append(";");
        b.append("text=").append(text).append(";");
        b.append("dateFrom=").append(dateFrom).append(";");
        b.append("dateTo=").append(dateTo).append(";");
        b.append("url=").append(url).append(";");
        b.append("image=").append(image).append(";");
        b.append("points=").append(points).append(";");
        b.append("hasStock=").append(hasStock).append(";");
        b.append("percentage=").append(percentage).append("]");
		return b.toString();
	}
	
}
