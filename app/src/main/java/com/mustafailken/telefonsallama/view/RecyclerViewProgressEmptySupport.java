
package com.mustafailken.telefonsallama.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;


public class RecyclerViewProgressEmptySupport extends RecyclerView {


    private View emptyView;
//Listenin boş olup olmadığını gösterecek.

    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {

        @Override
        public void onChanged() {//Liste göürünümünü ayarlama
            Adapter<?> adapter = getAdapter();
            if (adapter != null && emptyView != null) {
                if (adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    RecyclerViewProgressEmptySupport.this.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    RecyclerViewProgressEmptySupport.this.setVisibility(View.VISIBLE);
                }
            }

        }
    };


    private ProgressBar progressView;
    //Yükleme sırasında gösterilen görünüm


    public RecyclerViewProgressEmptySupport(Context context) {
        super(context);
    }


    public RecyclerViewProgressEmptySupport(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public RecyclerViewProgressEmptySupport(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }

        emptyObserver.onChanged();
    }


    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

   // Boş görünümü ayarlar.

    public void setProgressView(ProgressBar progressView) {
        this.progressView = progressView;
    }
//Set görünümünü ayarlar.

    public void startLoading() {
//boş görünümü saklar
        if (this.emptyView != null) {
            this.emptyView.setVisibility(GONE);
        }
//ilerleme barını gösterir.
        if (this.progressView != null) {
            this.progressView.setVisibility(VISIBLE);
        }
    }


    public void endLoading() {
//ilerleme barını saklar.
        if (this.progressView != null) {
            this.progressView.setVisibility(GONE);
        }

//Görünümü yenileme
        emptyObserver.onChanged();
    }
}