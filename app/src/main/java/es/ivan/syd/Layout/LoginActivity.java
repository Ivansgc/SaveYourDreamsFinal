package es.ivan.syd.Layout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import org.apache.commons.validator.routines.EmailValidator;

import es.ivan.syd.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    //Elementos de la vista LoginActivity
    private Button btnSignInuser;
    private EditText edtxtEmail;
    private EditText edtxtPassword;
    private TextView txtViewSignup;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    //Elemento boton google
    private SignInButton mGoogleBtn;

    private static final int RC_SIGN_IN = 1;

    private GoogleApiClient mGoogleApiClient;

    //private Firebase mAuthGoogle

    private static final String TAG = "LOGIN_ACTIVITY";

    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Localizamos elementos de la vista del login
        edtxtEmail = (EditText) findViewById(R.id.editEmailLogin);
        edtxtPassword = (EditText) findViewById(R.id.editPassLogin);
        btnSignInuser = (Button) findViewById(R.id.buttonLogin);
        txtViewSignup = (TextView) findViewById(R.id.textLogin_registro);

        firebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        };

        if(firebaseAuth.getCurrentUser() != null){
            //profile activity here
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }

        progressDialog = new ProgressDialog(this);

        //Activamos los botones de registrar y el de logeo
        btnSignInuser.setOnClickListener(this);
        txtViewSignup.setOnClickListener(this);

        //Localizamos boton de google
        mGoogleBtn = (SignInButton) findViewById(R.id.googleBtn);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener(){

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                        Toast.makeText(LoginActivity.this, "You got an error", Toast.LENGTH_LONG).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }

    public void onStart(){
        super.onStart();

        firebaseAuth.addAuthStateListener(mAuthListener);
    }



    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });

    }

    //Metodo de logeo de usuario
    private void userLogin(){
        //Obtenemos el texto escrito en los elementos mail y pass del login
        String email = edtxtEmail.getText().toString().trim();
        String password = edtxtPassword.getText().toString().trim();

        EmailValidator validator = EmailValidator.getInstance();

        //Si el mail esta vacio o no es valido pedimos a traves de un mensaje Toast la ingresion del email correcto
        if(TextUtils.isEmpty(email) || !validator.isValid(email)){
            Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
            //stopping the function further
            return;
        }

        //Lo mismo que para el mail, para la password
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            //stopping the function further
            return;
        }
        //if validation are ok
        //we will first show a progressbar
        //configuramos el progressdialog para que mientras se realiza el proceso de registro muestre un mensaje
        //avisando de que se esta registrando usuario
        progressDialog.setMessage("Log in User...");
        progressDialog.show();

        //metodo para logearse el usuario
        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        //Si el login a sido realizado comenzamos la nueva actividad y cargamos la pantalla inicial
                        //user is succesfully registered and logged in
                        //we will start the profile activity here
                        //right now lets display a toast only
                        if (task.isSuccessful()){
                            //start the profile activity
                            finish();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                    }
                });
    }

    //Si el usuario clica en btn de login usa el metodo logear usuario
    @Override
    public void onClick(View view) {
        if(view == btnSignInuser){
            userLogin();
        }
        //Si el usuario clica en btn registrarse cargamos la pantalla de LoginActivity
        if(view == txtViewSignup){
            finish();
            startActivity(new Intent(this, RegistroActivity.class));
        }

    }
}
