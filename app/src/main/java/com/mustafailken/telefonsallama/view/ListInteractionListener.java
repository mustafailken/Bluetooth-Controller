
package com.mustafailken.telefonsallama.view;


public interface ListInteractionListener<T> {


    void onItemClick(T item);
//nesneye tıkladığında çağrılır

    void startLoading();
//yükleme başladığında çağrılır.

    void endLoading(boolean partialResults);
//yükleme bittiğinde çağrılır

    void endLoadingWithDialog(boolean error, T element);
//İletişim kutusu kapatma için
}
