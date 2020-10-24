package com.angela.grostore;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class DialogResendVerification extends AppCompatActivity {

    private static final String TAG = "DialogResendVerification";

    //widgets
    private EditText mConfirmPassword, mConfirmEmail;

    //vars
    private Context mContext;

    @Nullable
    protected Activity onCreate( LayoutInflater inflater,@Nullable ViewGroup container,@Nullable Bundle savedInstanceState ) {
        super.onCreate (savedInstanceState);
        setContentView (container,false);
        Activity view = null;
        assert false;
        mConfirmPassword = (EditText) view.findViewById(R.id.confirm_password);
        mConfirmEmail = (EditText) view.findViewById(R.id.confirm_email);
        getActivity();

        TextView confirmDialog = (TextView) view.findViewById(R.id.dialogConfirm);
        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to resend verification email.");

                if(!isEmpty(mConfirmEmail.getText().toString())
                        && !isEmpty(mConfirmPassword.getText().toString())){

                    //temporarily authenticate and resend verification email
                    authenticateAndResendEmail(mConfirmEmail.getText().toString(),
                            mConfirmPassword.getText().toString());
                }else{
                    Toast.makeText(mContext, "all fields must be filled out", Toast.LENGTH_SHORT).show();
                }


            }
        });

        // Cancel button for closing the dialog
        TextView cancelDialog = (TextView) view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            private DialogFragment dialog;

            public void setDialog( DialogFragment dialog ) {
                this.dialog = dialog;
            }

            public DialogFragment getDialog() {
                DialogFragment dialog = null;
                return dialog;
            }

            @Override
            public void onClick(View v) {
                getDialog ().dismiss();
            }


        });

        return view;
    }

    private void getActivity() {
    }

    private void setContentView( ViewGroup container,boolean b ) {
    }


    /**
     * reauthenticate so we can send a verification email again
     * @param email
     * @param password
     */
    private void authenticateAndResendEmail(String email, String password) {
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener< AuthResult > () {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful ()) {
                            Log.d (TAG,"onComplete: reauthenticate success.");
                            sendVerificationEmail ();
                            FirebaseAuth.getInstance ().signOut ();
                            getDialog ().dismiss ();

                        }

                    }

                    private void sendVerificationEmail() {
                    }

                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Invalid Credentials. \nReset your password and try again", Toast.LENGTH_SHORT).show();
                        getDialog().dismiss();
                    }

                    private DialogFragment getDialog() {
                        return null;
                    }
                });
    }

    /**
     * Return true if the @param is null
     * @param string
     * @return
     */
    private boolean isEmpty(String string){
        return string.equals("");
    }


    public void show( FragmentManager supportFragmentManager,String dialog_resend_email_verification ) {
    }
}

