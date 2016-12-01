package es.ivan.syd.Otros;

/**
 * Created by Gonzalo on 09/11/2016.
 */

public abstract class RecyclerViewItemSelectedListener<T>{
    public abstract void onItemSelected(T item);
    public abstract void onItemLongSelected(T item);
}