package es.ivan.syd.Layout;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import es.ivan.syd.Clases.Dream;
import es.ivan.syd.R;
import es.ivan.syd.Otros.RecyclerViewItemSelectedListener;
import es.ivan.syd.adapters.DreamAdapter;
import es.ivan.syd.adapters.PesadillaAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gonzalo on 09/11/2016.
 */

public class FragmentoDream extends Fragment {

    public static FragmentoDream active = null;
    private RecyclerView lista;
    private TextView noExisten;
    private EditText editBuscar;
    private ImageButton botonBuscar;
    private List<Dream> dreams = new ArrayList<>();
    private List<Dream> dreamsTodos = new ArrayList<>();

    public FragmentoDream() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dream, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lista = (RecyclerView) view.findViewById(R.id.lista_dream);
        noExisten = (TextView) view.findViewById(R.id.noExistenDreams);
        editBuscar = (EditText) view.findViewById(R.id.editBuscar);
        botonBuscar = (ImageButton) view.findViewById(R.id.botonBuscar);

        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String busqueda = editBuscar.getText().toString();
                dreams.clear();
                if(busqueda != null && !busqueda.isEmpty()){
                    ArrayList<Dream> dreamsEncontrados = new ArrayList<Dream>();
                    for(Dream dream : dreamsTodos){
                        if(dream.getTitulo().contains(busqueda))
                            dreamsEncontrados.add(dream);
                    }
                    if(!dreamsEncontrados.isEmpty())
                        dreams.addAll(dreamsEncontrados);
                }else{
                    dreams.addAll(dreamsTodos);
                }
                ((DreamAdapter)lista.getAdapter()).notifyDataSetChanged();
            }
        });

        refrescarDreams();
        active = this;
    }

    public void refrescarDreams(){
        int size = dreams.size();
        if(size > 0) {
            dreams.clear();
            ((DreamAdapter) lista.getAdapter()).notifyItemRangeRemoved(0, size);
        }

        dreamsTodos.clear();

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading");
        dialog.setMessage("Loading dreams...");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();

        MainActivity.databaseReference.child("dreams").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long dreamSize = dataSnapshot.getChildrenCount();

                        if(dreamSize == 0){
                            lista.setVisibility(View.GONE);
                            noExisten.setVisibility(View.VISIBLE);
                        }else{
                            lista.setVisibility(View.VISIBLE);
                            noExisten.setVisibility(View.GONE);
                            for(DataSnapshot dreamSnapshot : dataSnapshot.getChildren()){
                                Dream dream = dreamSnapshot.getValue(Dream.class);
                                dream.setId(dreamSnapshot.getKey());
                                dreams.add(dream);
                            }

                            dreamsTodos.addAll(dreams);

                            DreamAdapter adapter = new DreamAdapter(dreams, new RecyclerViewItemSelectedListener<Dream>() {
                                @Override
                                public void onItemSelected(Dream item) {
                                    Intent intent = new Intent(getActivity(), CrearModificarDreamActivity.class);
                                    intent.putExtra("dream", item);
                                    startActivity(intent);
                                }

                                @Override
                                public void onItemLongSelected(final Dream item) {
                                    new AlertDialog.Builder(getContext())
                                        .setCancelable(true)
                                        .setTitle("Delete dream?")
                                        .setMessage("Are you sure?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                MainActivity.databaseReference.child("dreams/"+item.getId()).removeValue();
                                                active.refrescarDreams();
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .create().show();
                                }
                            });
                            lista.setLayoutManager(new LinearLayoutManager(getContext()));
                            lista.setHasFixedSize(true);
                            lista.setAdapter(adapter);
                        }
                        dialog.dismiss();


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(FragmentoDream.class.getSimpleName(), "onCancelledDreamList", databaseError.toException());
                        Toast.makeText(FragmentoDream.this.getContext(), "Cannot retrieve dream list", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
        );
    }
}