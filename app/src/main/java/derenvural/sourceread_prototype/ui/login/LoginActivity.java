package derenvural.sourceread_prototype.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class LoginActivity extends SourceReadActivity {
    private LoginViewModel loginViewModel;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Check database connection
        loginViewModel = new LoginViewModel();

        // Configure Google sign-in options:
        // -request the user's ID and basic profile
        // -(ID and basic profile are included in DEFAULT_SIGN_IN)
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for already signed-in google account
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            main_redirect("login", null);
        }

        //get components
        final EditText usernameEditText = findViewById(R.id.username);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final SignInButton googleLoginButton = findViewById(R.id.google_login);
        final Button registerButton = findViewById(R.id.register);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        // Observe login form
        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                registerButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        // Observe login result
        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {
                    updateUiWithUser();

                    //Complete and destroy login activity once successful
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
        });

        // Check for change in input text
        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString(), LoginActivity.this);
                }
                return false;
            }
        });

        // Button click events
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), LoginActivity.this);
            }
        });
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);

                // Attempt google sign-in
                googleSignIn();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.register(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString(), LoginActivity.this);
            }
        });
    }

    private void updateUiWithUser() {
        // Display login message
        Toast.makeText(this, "Authentication successful!", Toast.LENGTH_SHORT).show();

        // Create bundle with serialised object
        LoggedInUser user_data = loginViewModel.getLoginUser();
        Bundle bundle = new Bundle();
        bundle.putString("activity", "login");
        user_data.saveInstanceState(bundle);

        // Start next activity and close login activity
        main_redirect("login", bundle);
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        // Display error message
        Toast.makeText(this, getString(errorString), Toast.LENGTH_SHORT).show();
    }

    // Google sign-in info
    int RC_SIGN_IN = 999;
    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Get result
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                loginViewModel.firebaseAuthWithGoogle(account, this);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));
            }
        }
    }

    // Email verification
    public void checkIfEmailVerified() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user.isEmailVerified()) {
            LoggedInUser new_user = new LoggedInUser(FirebaseAuth.getInstance().getCurrentUser());
            LoggedInUserView new_user_view = new LoggedInUserView(new_user);
            loginViewModel.setLoginUser(new_user);
            loginViewModel.setLoginResult(new LoginResult(new_user_view));
        } else {
            // email is not verified, so just prompt the message to the user and restart this activity.
            // NOTE: don't forget to log out the user.
            FirebaseAuth.getInstance().signOut();

            // If sign in fails, display a message to the user.
            loginViewModel.setLoginResult(new LoginResult(R.string.email_verification_needed));

            //restart login activity
            login_redirect();

        }
    }
    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // email sent
                    // Logout and finish activity
                    FirebaseAuth.getInstance().signOut();
                    login_redirect();

                    // notify user
                    loginViewModel.setLoginResult(new LoginResult(R.string.email_register));
                } else {
                    // If sign in fails, display a message to the user.
                    loginViewModel.setLoginResult(new LoginResult(R.string.login_failed));

                    //restart login activity
                    login_redirect();
                }
            }
        });
    }
}
