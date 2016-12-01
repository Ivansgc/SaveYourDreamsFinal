package es.ivan.syd.Layout;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.Date;

import es.ivan.syd.Clases.Pesadilla;
import es.ivan.syd.R;

public class CrearModificarPesadillaActivity extends AppCompatActivity {

    private EditText editDescripcion;
    private EditText editTitulo;
    private EditText editDia;
    private Spinner spinnerNivel;
    private Pesadilla pesadilla = null;

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            editDia.setText(Pesadilla.dateFormat.format(myCalendar.getTime()));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_modificar_pesadilla);

        editDescripcion = (EditText) findViewById(R.id.descripcionC);
        editTitulo = (EditText) findViewById(R.id.tituloC);
        editDia = (EditText) findViewById(R.id.horaC);
        spinnerNivel = (Spinner) findViewById(R.id.nivel);
        spinnerNivel.setSelection(0);

        if(getIntent() != null && getIntent().getSerializableExtra("pesadilla") != null){
            pesadilla = (Pesadilla) getIntent().getSerializableExtra("pesadilla");
            editDescripcion.setText(pesadilla.getDescripcion());
            editTitulo.setText(pesadilla.getTitulo());
            editDia.setText(Pesadilla.dateFormat.format(pesadilla.getDia()));
            spinnerNivel.setSelection(pesadilla.getIconoId());
            myCalendar.setTime(pesadilla.getDia());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_crear);

        editDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(CrearModificarPesadillaActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String titulo = editTitulo.getText().toString();
                    String descripcion = editDescripcion.getText().toString();
                    Date dia = Pesadilla.dateFormat.parse(editDia.getText().toString());
                    int nivel = spinnerNivel.getSelectedItemPosition();

                    if(titulo.isEmpty()){
                        Toast.makeText(CrearModificarPesadillaActivity.this, "Title cannot be empty", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(descripcion.isEmpty()){
                        Toast.makeText(CrearModificarPesadillaActivity.this, "Description cannot be empty", Toast.LENGTH_LONG).show();
                        return;
                    }

                    boolean isNew = pesadilla == null;
                    if(isNew)
                        pesadilla = new Pesadilla();
                    pesadilla.setIconoId(nivel);
                    pesadilla.setTitulo(titulo);
                    pesadilla.setDescripcion(descripcion);
                    pesadilla.setDia(dia);

                    DatabaseReference dreamReference = isNew ? MainActivity.databaseReference.child("pesadillas").push()
                            : MainActivity.databaseReference.child("pesadillas/" + pesadilla.getId());

                    dreamReference.setValue(pesadilla);

                    Toast.makeText(CrearModificarPesadillaActivity.this, "Pesadilla saved correctly", Toast.LENGTH_LONG).show();
                    FragmentoPesadillas.active.refrescarPesadillas();
                    finish();
                }catch(Exception e){
                    Toast.makeText(CrearModificarPesadillaActivity.this, "Invalid date", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
