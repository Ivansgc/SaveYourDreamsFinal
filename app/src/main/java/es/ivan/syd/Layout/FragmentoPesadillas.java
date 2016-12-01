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
import es.ivan.syd.Clases.Pesadilla;
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


public class FragmentoPesadillas extends Fragment {

    public static FragmentoPesadillas active = null;
    private RecyclerView lista2;
    private EditText editBuscar;
    private ImageButton botonBuscar;
    private TextView noExisten;
    private List<Pesadilla> pesadillas = new ArrayList<>();
    private List<Pesadilla> pesadillasTodas = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pesadillas, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        lista2 = (RecyclerView) view.findViewById(R.id.lista_pesadilla);
        noExisten = (TextView) view.findViewById(R.id.noExistenPesadillas);

        editBuscar = (EditText) view.findViewById(R.id.editBuscar);
        botonBuscar = (ImageButton) view.findViewById(R.id.botonBuscar);

        botonBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String busqueda = editBuscar.getText().toString();
                pesadillas.clear();
                if(busqueda != null && !busqueda.isEmpty()){
                    ArrayList<Pesadilla> pesadillasEncontradas = new ArrayList<Pesadilla>();
                    for(Pesadilla pesadilla : pesadillasTodas){
                        if(pesadilla.getTitulo().contains(busqueda))
                            pesadillasEncontradas.add(pesadilla);
                    }
                    if(!pesadillasEncontradas.isEmpty())
                        pesadillas.addAll(pesadillasEncontradas);
                }else
                    pesadillas.addAll(pesadillasTodas);
                ((PesadillaAdapter)lista2.getAdapter()).notifyDataSetChanged();
            }
        });

        refrescarPesadillas();
        active = this;
    }

    public void refrescarPesadillas(){
        int size = pesadillas.size();
        if(size > 0) {
            pesadillas.clear();
            ((PesadillaAdapter) lista2.getAdapter()).notifyItemRangeRemoved(0, size);
        }

        pesadillasTodas.clear();

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setTitle("Loading");
        dialog.setMessage("Loading pesadillas...");
        dialog.setCancelable(false);
        dialog.setIndeterminate(true);
        dialog.show();

        MainActivity.databaseReference.child("pesadillas").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long pesadillaSize = dataSnapshot.getChildrenCount();

                        if(pesadillaSize == 0){
                            lista2.setVisibility(View.GONE);
                            noExisten.setVisibility(View.VISIBLE);
                        }else{
                            lista2.setVisibility(View.VISIBLE);
                            noExisten.setVisibility(View.GONE);
                            for(DataSnapshot pesadillaSnapshot : dataSnapshot.getChildren()){
                                Pesadilla pesadilla = pesadillaSnapshot.getValue(Pesadilla.class);
                                pesadilla.setId(pesadillaSnapshot.getKey());
                                pesadillas.add(pesadilla);
                            }

                            pesadillasTodas.addAll(pesadillas);

                            PesadillaAdapter adapter = new PesadillaAdapter(pesadillas, new RecyclerViewItemSelectedListener<Pesadilla>() {
                                @Override
                                public void onItemSelected(Pesadilla item) {
                                    Intent intent = new Intent(getActivity(), CrearModificarPesadillaActivity.class);
                                    intent.putExtra("pesadilla", item);
                                    startActivity(intent);
                                }

                                @Override
                                public void onItemLongSelected(final Pesadilla item) {
                                    new AlertDialog.Builder(getContext())
                                        .setCancelable(true)
                                        .setTitle("Delete pesadilla?")
                                        .setMessage("Are you sure?")
                                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                MainActivity.databaseReference.child("pesadillas/"+item.getId()).removeValue();
                                                active.refrescarPesadillas();
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .create().show();
                                }
                            });
                            lista2.setLayoutManager(new LinearLayoutManager(getContext()));
                            lista2.setHasFixedSize(true);
                            lista2.setAdapter(adapter);
                        }

                        dialog.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(FragmentoPesadillas.class.getSimpleName(), "onCancelledPesadillaList", databaseError.toException());
                        Toast.makeText(FragmentoPesadillas.this.getContext(), "Cannot retrieve pesadilla list", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
        );
    }
}