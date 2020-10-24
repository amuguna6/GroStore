package com.angela.grostore;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.LoginStatusCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Facebook

    private LoginButton fbloginButton;

    // widgets
    private EditText mEmail, mPassword;
    private ProgressBar mProgressBar;
    private TextView register;
    private TextView resetPassword;
    private TextView resendEmailVerification;
    private Button signIn;

    private CallbackManager callbackManager;
    private static final String EMAIL = "fb_email";

    public MainActivity() {
    }

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);

        //Facebook login
        callbackManager = CallbackManager.Factory.create ();
        //facebook API login
        fbloginButton = findViewById (R.id.fblogin_button);
        fbloginButton.setReadPermissions (Arrays.asList (EMAIL));

        //login
        mEmail = findViewById (R.id.email);
        mPassword = findViewById (R.id.password);
        mProgressBar = findViewById (R.id.progressBar);

        setupFirebaseAuth ();


        signIn = (Button) findViewById (R.id.email_sign_in_button);
        signIn.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick( View view ) {

                //check if the fields are filled out
                if (!isEmpty (mEmail.getText ().toString ())
                        && !isEmpty (mPassword.getText ().toString ())) {
                    Log.d (TAG,"onClick: attempting to authenticate.");

                    showDialog ();

                    FirebaseAuth.getInstance ().signInWithEmailAndPassword (mEmail.getText ().toString (),
                            mPassword.getText ().toString ())
                            .addOnCompleteListener (new OnCompleteListener< AuthResult > () {
                                @Override
                                public void onComplete( @NonNull Task< AuthResult > task ) {

                                    hideDialog ();

                                }
                            }).addOnFailureListener (new OnFailureListener () {
                        @Override
                        public void onFailure( @NonNull Exception e ) {
                            Toast.makeText (MainActivity.this,"Authentication Failed",Toast.LENGTH_SHORT).show ();
                            hideDialog ();
                        }
                    });
                } else {
                    Toast.makeText (MainActivity.this,"You didn't fill in all the fields.",Toast.LENGTH_SHORT).show ();
                }
            }
        });
        register = (TextView) findViewById (R.id.link_register);
        register.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick( View view ) {
                Intent intent = new Intent (MainActivity.this,RegisterAccount.class);
                startActivity (intent);
            }
        });

        resetPassword = (TextView) findViewById (R.id.forgot_password);
        resetPassword.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick( View v ) {

            }
        });

       // resendEmailVerification = (TextView) findViewById (R.id.resend_verification_email);
      //  resendEmailVerification.setOnClickListener (new View.OnClickListener () {
        //    @Override
        //    public void onClick( View v ) {
        //        DialogResendVerification dialog = new DialogResendVerification ();
       //         dialog.show (getSupportFragmentManager (),"dialog_resend_email_verification");
         //       Intent intent = new Intent (MainActivity.this,DialogResendVerification.class);
         //       startActivity (intent);
          //  }
       // });

        hideSoftKeyboard ();

    }

    /**
     * Return true if the @param is null
     *
     * @param string
     * @return
     */
    private boolean isEmpty( String string ) {
        return string.equals ("");
    }


    private void showDialog() {
        mProgressBar.setVisibility (View.VISIBLE);

    }

    private void hideDialog() {
        if (mProgressBar.getVisibility () == View.VISIBLE) {
            mProgressBar.setVisibility (View.INVISIBLE);
        }
    }

    private void hideSoftKeyboard() {
        this.getWindow ().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
        ----------------------------- Firebase setup ---------------------------------
     */
    private void setupFirebaseAuth() {
        Log.d (TAG,"setupFirebaseAuth: started.");

        mAuthListener = new FirebaseAuth.AuthStateListener () {
            @Override
            public void onAuthStateChanged( @NonNull FirebaseAuth firebaseAuth ) {
                FirebaseUser user = firebaseAuth.getCurrentUser ();
                if (user != null) {

                    //check if email is verified
                    if (user.isEmailVerified ()) {
                        Log.d (TAG,"onAuthStateChanged:signed_in:" + user.getUid ());
                        Toast.makeText (MainActivity.this,"Authenticated with: " + user.getEmail (),Toast.LENGTH_SHORT).show ();

                        Intent intent = new Intent (MainActivity.this,SignedInActivity.class);
                        startActivity (intent);
                        finish ();

                    } else {
                        Toast.makeText (MainActivity.this,"Email is not Verified\nCheck your Inbox",Toast.LENGTH_SHORT).show ();
                        FirebaseAuth.getInstance ().signOut ();
                    }

                } else {
                    // User is signed out
                    Log.d (TAG,"onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart ();
        FirebaseAuth.getInstance ().addAuthStateListener (mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop ();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance ().removeAuthStateListener (mAuthListener);


            // Callback registration
            fbloginButton.registerCallback (callbackManager,new FacebookCallback< LoginResult > () {
                @Override
                public void onSuccess( LoginResult loginResult ) {
                    // App code
                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError( FacebookException exception ) {
                    // App code
                }
            });

            callbackManager = CallbackManager.Factory.create ();


            LoginManager.getInstance ().logInWithReadPermissions (this,Arrays.asList ("public_profile"));
            LoginManager.getInstance ().registerCallback (callbackManager,
                    new FacebookCallback< LoginResult > () {
                        @Override
                        public void onSuccess( LoginResult loginResult ) {
                            // App code
                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError( FacebookException exception ) {
                            // App code
                        }


                    });


            LoginManager.getInstance ().retrieveLoginStatus (this,new LoginStatusCallback () {
                @Override
                public void onCompleted( AccessToken accessToken ) {
                    // User was previously logged in, can log them in directly here.
                    // If this callback is called, a popup notification appears that says
                    // "Logged in as <User Name>"
                }

                @Override
                public void onFailure() {
                    // No access token could be retrieved for the user
                }

                @Override
                public void onError( Exception exception ) {
                    // An error occurred
                }


            });
        }
    }

}

