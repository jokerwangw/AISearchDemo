package com.cmcc.cmvideo.search.aiui.bean;

public class NavigationBean<T> {
    public T data;
    public int navigationFrom;

    public NavigationBean(){

    }

    public NavigationBean(T bean){
        data = bean;
        navigationFrom = 0;
    }

    public NavigationBean(T bean,int navFrom){
        data = bean;
        navigationFrom = navFrom;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getNavigationFrom() {
        return navigationFrom;
    }

    public void setNavigationFrom(int navigationFrom) {
        this.navigationFrom = navigationFrom;
    }
}
