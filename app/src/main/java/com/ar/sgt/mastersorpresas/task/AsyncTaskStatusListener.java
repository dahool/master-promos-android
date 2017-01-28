package com.ar.sgt.mastersorpresas.task;

/**
 * Created by Gabriel on 26/01/2017.
 */

public interface AsyncTaskStatusListener<T> {

    void preExecute();

    void postExecute(T result);

}
