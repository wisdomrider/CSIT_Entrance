package np.com.aawaz.csitentrance.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.TimeUnit;

import np.com.aawaz.csitentrance.R;
import np.com.aawaz.csitentrance.objects.EventSender;
import np.com.aawaz.csitentrance.objects.SPHandler;

public class PhoneNoActivity extends AppCompatActivity {


    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    TextInputEditText phone, c_code;
    TextInputEditText verify_code;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private FirebaseAuth mAuth;
    private ViewSwitcher vs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_no);

        phone = (TextInputEditText) findViewById(R.id.phone_no);
        c_code = (TextInputEditText) findViewById(R.id.c_code);
        verify_code = (TextInputEditText) findViewById(R.id.phone_no_code);
        vs = (ViewSwitcher) findViewById(R.id.phone_auth_vs);
        TextView change_phone_no = (TextView) findViewById(R.id.change_phone_no);
        final TextView code_sent_text = (TextView) findViewById(R.id.code_sent_text);

        final CardView submit = (CardView) findViewById(R.id.phone_continue_button);
        final CardView verify = (CardView) findViewById(R.id.phone_verify_button);

        final MaterialDialog dialog = new MaterialDialog.Builder(PhoneNoActivity.this)
                .content("Sending verification code")
                .progress(true, 100)
                .cancelable(false)
                .build();
        mAuth = FirebaseAuth.getInstance();


        submit.setVisibility(View.INVISIBLE);
        verify.setVisibility(View.INVISIBLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        change_phone_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vs.showPrevious();
            }
        });

        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 10)
                    submit.setVisibility(View.VISIBLE);
                else
                    submit.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        verify_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 5)
                    verify.setVisibility(View.VISIBLE);
                else
                    verify.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        phone.setText(SPHandler.getInstance().getPhoneNo().replace("+977", ""));

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                dialog.dismiss();
                e.printStackTrace();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(PhoneNoActivity.this, "Invalid mobile number. Please try again with a valid one.", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Toast.makeText(PhoneNoActivity.this, "Too many verification request sent. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                submit.setVisibility(View.VISIBLE);
                ((TextView) submit.getChildAt(0)).setText("Resend Code");
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                PhoneNoActivity.this.verificationId = verificationId;
                PhoneNoActivity.this.forceResendingToken = forceResendingToken;
                super.onCodeSent(verificationId, forceResendingToken);
                dialog.dismiss();
                vs.showNext();
                code_sent_text.setText("Code sent to " + c_code.getText().toString() + phone.getText());
                submit.setVisibility(View.INVISIBLE);
            }

        };

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (c_code.getText().toString().length() > 1) {
                    SPHandler.getInstance().setPhoneNo(phone.getText().toString());
                    if (forceResendingToken == null)
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(c_code.getText().toString() + phone.getText().toString(),
                                60,
                                TimeUnit.SECONDS,
                                PhoneNoActivity.this,
                                mCallback);
                    else
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(c_code.getText().toString() + phone.getText().toString(),
                                60,
                                TimeUnit.SECONDS,
                                PhoneNoActivity.this,
                                mCallback,
                                forceResendingToken);
                    dialog.show();
                } else {
                    Toast.makeText(PhoneNoActivity.this, "Invalid country code.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, verify_code.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }
        });
    }

    private void onVerified() {
        new EventSender().logEvent("phone_verified");
        SPHandler.getInstance().setPhoneNo(phone.getText().toString());
        startActivity(new Intent(PhoneNoActivity.this, MainActivity.class)
                .putExtra("fragment", getIntent().getStringExtra("fragment")));
        finish();
    }

    private void signInWithPhoneAuthCredential(final PhoneAuthCredential credential) {
        final MaterialDialog verifying = new MaterialDialog.Builder(this)
                .content("Verifying the code...")
                .progress(true, 100)
                .build();
        verifying.show();

        mAuth.getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            verifying.dismiss();
                            onVerified();
                        } else {
                            verifying.dismiss();
                            Toast.makeText(PhoneNoActivity.this, "Invalid verification code.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(PhoneNoActivity.this, "Phone number already associated with another account. Please try another number.", Toast.LENGTH_LONG).show();
                        }
                        e.printStackTrace();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}
