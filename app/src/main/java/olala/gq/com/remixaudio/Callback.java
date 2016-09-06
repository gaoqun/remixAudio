package olala.gq.com.remixaudio;

/**
 * Created by gaoqun on 2016/9/6.
 */
public interface Callback<T,F,V> {

    void result(T t,F f,V v);
}
