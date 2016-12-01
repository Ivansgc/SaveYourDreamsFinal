package es.ivan.syd.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import es.ivan.syd.Clases.Dream;
import es.ivan.syd.Clases.Iconos;
import es.ivan.syd.R;
import es.ivan.syd.Otros.RecyclerViewItemSelectedListener;

import java.util.List;

/**
 * Created by Gonzalo on 09/11/2016.
 */

public class DreamAdapter extends RecyclerView.Adapter<DreamAdapter.ViewHolder> {

    private List<Dream> dreams;

    private RecyclerViewItemSelectedListener<Dream> listener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public ImageView imagen;
        public TextView textoTitulo;
        public TextView textoFecha;
        private RecyclerViewItemSelectedListener listener;

        public ViewHolder(View v, final List<Dream> data, final RecyclerViewItemSelectedListener<Dream> listener) {
            super(v);
            this.listener = listener;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null)
                        listener.onItemSelected(data.get(ViewHolder.this.getLayoutPosition()));
                }
            });

            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(listener != null)
                        listener.onItemLongSelected(data.get(ViewHolder.this.getLayoutPosition()));
                    return true;
                }
            });

            textoTitulo = (TextView) v.findViewById(R.id.titulo);
            textoFecha = (TextView) v.findViewById(R.id.fecha);
            imagen = (ImageView) v.findViewById(R.id.imagen);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DreamAdapter(List<Dream> dreams, RecyclerViewItemSelectedListener<Dream> listener) {
        this.dreams = dreams;
        this.listener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DreamAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dream, parent, false);
        ViewHolder vh = new ViewHolder(v, dreams, listener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.textoTitulo.setText(dreams.get(position).getTitulo());
        holder.textoFecha.setText(dreams.get(position).getFormattedTime());
        holder.imagen.setImageResource(Iconos.fromId(dreams.get(position).getIconoId()).getDrawableId());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dreams.size();
    }
}