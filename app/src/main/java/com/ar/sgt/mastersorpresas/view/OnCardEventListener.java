package com.ar.sgt.mastersorpresas.view;

import com.ar.sgt.mastersorpresas.model.Promo;

/**
 * Created by Gabriel on 27/01/2017.
 */

public interface OnCardEventListener<T> {

    void onCardEvent(EventType type, T item, Object...args);

}
