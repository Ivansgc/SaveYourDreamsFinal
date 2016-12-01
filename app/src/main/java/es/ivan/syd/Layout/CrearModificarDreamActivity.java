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

import es.ivan.syd.Clases.Dream;
import es.ivan.syd.R;

public class CrearModificarDreamActivity extends AppCompatActivity {

    private EditText editDescripcion;
    private EditText editTitulo;
    private EditText editDia;
    private Spinner spinnerNivel;
    private Dream dream = null;

    Calendar myCalendar = Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            editDia.setText(Dream.dateFormat.format(myCalendar.getTime()));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_modificar_dream);

        editDescripcion = (EditText) findViewById(R.id.descripcionC);
        editTitulo = (EditText) findViewById(R.id.tituloC);
        editDia = (EditText) findViewById(R.id.horaC);
        spinnerNivel = (Spinner) findViewById(R.id.nivel);
        spinnerNivel.setSelection(0);

        if(getIntent() != null && getIntent().getSerializableExtra("dream") != null){
            dream = (Dream) getIntent().getSerializableExtra("dream");
            editDescripcion.setText(dream.getDescripcion());
            editTitulo.setText(dream.getTitulo());
            editDia.setText(Dream.dateFormat.format(dream.getDia()));
            spinnerNivel.setSelection(dream.getIconoId());
            myCalendar.setTime(dream.getDia());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_crear);

        editDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(CrearModificarDreamActivity.this, date, myCalendar
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
                    Date dia = Dream.dateFormat.parse(editDia.getText().toString());
                    int nivel = spinnerNivel.getSelectedItemPosition();

                    if(titulo.isEmpty()){
                        Toast.makeText(CrearModificarDreamActivity.this, "Title cannot be empty", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(descripcion.isEmpty()){
                        Toast.makeText(CrearModificarDreamActivity.this, "Description cannot be empty", Toast.LENGTH_LONG).show();
                        return;
                    }

                    boolean isNew = dream == null;
                    if(isNew)
                        dream = new Dream();
                    dream.setIconoId(nivel);
                    dream.setTitulo(titulo);
                    dream.setDescripcion(descripcion);
                    dream.setDia(dia);

                    DatabaseReference dreamReference = null;
                    if(isNew)
                        dreamReference = MainActivity.databaseReference.child("dreams").push();
                    else
                        dreamReference = MainActivity.databaseReference.child("dreams/" + dream.getId());

                    dreamReference.setValue(dream);

                    Toast.makeText(CrearModificarDreamActivity.this, "Dream saved correctly", Toast.LENGTH_LONG).show();
                    FragmentoDream.active.refrescarDreams();
                    finish();
                }catch(Exception e){
                    Toast.makeText(CrearModificarDreamActivity.this, "Invalid date", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


}
