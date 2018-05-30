package com.cmcc.cmvideo.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yyw on 2018/5/29.
 * Describe:
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected List<T> dataList;
    protected Context context;

    protected OnItemClickListener onItemClickListener;
    protected OnItemLongClickListener onItemLongClickListener;
    protected OnItemChildClickListener onItemChildClickListener;
    protected OnItemChildLongClickListener onItemChildLongClickListener;

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            onBindHoder(holder, getItem(position), position);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    public BaseRecyclerAdapter(Context ctx) {
        context = ctx;
        dataList = new ArrayList<>();
    }

    public List<T> getData() {
        return dataList;
    }

    public boolean bindData(List<T> datas, boolean isRefresh) {
        if (datas == null) {
            return false;
        }
        if (isRefresh) {
            dataList.clear();
        }
        boolean result = dataList.addAll(datas);
        notifyDataSetChanged();
        return result;
    }

    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    public T getItem(int position) {
        if (position < 0 || position >= dataList.size()) {
            return null;
        }
        return dataList.get(position);
    }

    public boolean addItem(int position, T t) {
        if (t == null) {
            return false;
        }
        if (position < 0 || position > dataList.size()) {
            return false;
        }
        if (dataList.contains(t)) {
            return false;
        }
        dataList.add(position, t);
        notifyItemInserted(position);
        return true;
    }

    public boolean addItem(T t) {
        if (t == null) {
            return false;
        }
        if (dataList.contains(t)) {
            return false;
        }
        boolean result = dataList.add(t);
        notifyItemInserted(dataList.size() - 1);
        return result;
    }

    public boolean addItems(int pos, List<? extends T> datas) {
        if (datas == null) {
            return false;
        }
        if (datas.contains(datas)) {
            return false;
        }
        dataList.addAll(pos, datas);
        notifyItemRangeInserted(pos, datas.size());
        return true;
    }

    public boolean addItems(List<? extends T> datas) {
        if (datas == null) {
            return false;
        }
        if (datas.contains(datas)) {
            return false;
        }
        dataList.addAll(datas);
        notifyItemRangeInserted(getItemCount() - datas.size() >= 0 ? getItemCount() - datas.size()
                : 0, datas.size());
        return true;
    }

    public boolean removeItem(int position) {
        if (position < 0 || position >= dataList.size()) {
            return false;
        }
        dataList.remove(position);
        notifyItemRemoved(position);
        return true;
    }

    public boolean removeItem(T t) {
        if (t == null) {
            return false;
        }
        int index = dataList.indexOf(t);
        if (index >= 0) {
            dataList.remove(index);
            notifyItemRemoved(index);
            return true;
        }
        return false;
    }

    public boolean updateItem(int position) {
        if (position < 0 || position >= dataList.size()) {
            return false;
        }
        notifyItemChanged(position);
        return true;
    }

    public boolean updateItem(T t) {
        if (t == null) {
            return false;
        }
        int index = dataList.indexOf(t);
        if (index >= 0) {
            dataList.set(index, t);
            notifyItemChanged(index);
            return true;
        }
        return false;
    }

    public boolean updateItem(int position, T t) {
        if (position < 0 || position >= dataList.size()) {
            return false;
        }
        if (t == null) {
            return false;
        }
        dataList.set(position, t);
        notifyItemChanged(position);
        return true;
    }

    public void updateItems(List<T> datas) {
        if (datas == null) {
            return;
        }
        for (T t : datas) {
            if (t == null) {
                return;
            }
            int index = dataList.indexOf(t);
            if (index >= 0) {
                dataList.set(index, t);
                notifyItemChanged(index);
            }
        }
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {
        return onItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        onItemLongClickListener = listener;
    }

    public OnItemChildLongClickListener getOnItemChildLongClickListener() {
        return onItemChildLongClickListener;
    }

    public void setOnItemChildLongClickListener(OnItemChildLongClickListener listener) {
        this.onItemChildLongClickListener = listener;
    }

    public OnItemChildClickListener getOnItemChildClickListener() {
        return onItemChildClickListener;
    }

    public void setOnItemChildClickListener(OnItemChildClickListener listener) {
        this.onItemChildClickListener = listener;
    }

    public abstract void onBindHoder(RecyclerView.ViewHolder holder, T t, int position);

    public interface OnItemClickListener {
        /**
         * View点击回调
         *
         * @param adapter
         * @param holder
         * @param view
         * @param position
         */
        void onItemClick(BaseRecyclerAdapter adapter, RecyclerView.ViewHolder holder,
                         View view, int position);
    }

    public interface OnItemLongClickListener {
        /**
         * View长按回调
         *
         * @param adapter
         * @param holder
         * @param view
         * @param position
         */
        void onItemLongClick(BaseRecyclerAdapter adapter, RecyclerView.ViewHolder holder,
                             View view, int position);
    }

    public interface OnItemChildClickListener {
        /**
         * 子View点击回调
         *
         * @param adapter
         * @param holder
         * @param view
         * @param position
         */
        void onItemChildClick(BaseRecyclerAdapter adapter, RecyclerView.ViewHolder holder,
                              View view, int position);
    }

    public interface OnItemChildLongClickListener {
        /**
         * 子View长按回调
         *
         * @param adapter
         * @param holder
         * @param view
         * @param position
         */
        void onItemChildLongClick(BaseRecyclerAdapter adapter, RecyclerView.ViewHolder holder,
                                  View view, int position);
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {

        private SparseArray<View> holder = null;

        @Override
        public boolean onLongClick(View v) {
            if (onItemLongClickListener != null && v.getId() == this.itemView.getId()) {
                onItemLongClickListener.onItemLongClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
                return true;
            } else if (onItemChildLongClickListener != null && v.getId() != this.itemView.getId()) {
                onItemChildLongClickListener.onItemChildLongClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
                return true;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null && v.getId() == this.itemView.getId()) {
                onItemClickListener.onItemClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
            } else if (onItemChildClickListener != null && v.getId() != this.itemView.getId()) {
                onItemChildClickListener.onItemChildClick(BaseRecyclerAdapter.this, this, v, getAdapterPosition());
            }
        }

        public BaseViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public <T extends View> T obtainView(int id) {
            if (null == holder) {
                holder = new SparseArray<>();
            }
            View view = holder.get(id);
            if (null != view) {
                return (T) view;
            }
            view = itemView.findViewById(id);
            if (null == view) {
                return null;
            }
            holder.put(id, view);
            return (T) view;
        }


        public <T> T obtainView(int id, Class<T> viewClazz) {
            View view = obtainView(id);
            if (null == view) {
                return null;
            }
            return (T) view;
        }


        public boolean bindChildClick(int id) {
            View view = obtainView(id);
            if (view == null) {
                return false;
            }
            view.setOnClickListener(this);
            return true;
        }

        public boolean bindChildClick(View v) {
            if (v == null) {
                return false;
            }
            if (obtainView(v.getId()) == null) {
                return false;
            }
            v.setOnClickListener(this);
            return true;
        }

        public boolean bindChildLongClick(int id) {
            View view = obtainView(id);
            if (view == null) {
                return false;
            }
            view.setOnLongClickListener(this);
            return true;
        }

        public boolean bindChildLongClick(View v) {
            if (v == null) {
                return false;
            }
            if (obtainView(v.getId()) == null) {
                return false;
            }
            v.setOnLongClickListener(this);
            return true;
        }
    }
}
