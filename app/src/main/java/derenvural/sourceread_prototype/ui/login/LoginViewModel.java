package derenvural.sourceread_prototype.ui.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Log;
import android.util.Patterns;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import derenvural.sourceread_prototype.R;
import derenvural.sourceread_prototype.SourceReadActivity;
import derenvural.sourceread_prototype.data.login.LoggedInUser;

public class LoginViewModel extends ViewModel {
    // fields
    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<LoginFormState>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<LoginResult>();
    private MutableLiveData<LoggedInUser> login_user = new MutableLiveData<LoggedInUser>();

    // Gets
    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }
    LoggedInUser getLoginUser() { return login_user.getValue(); }
    LiveData<LoginResult> getLoginResult() { return loginResult; }

    // Sets
    void setLoginUser(LoggedInUser login_user) { this.login_user.setValue(login_user); }
    void setLoginResult(LoginResult loginResult) { this.loginResult.setValue(loginResult); }

    public void login(String email, String password, final LoginActivity cur_context) {
        // Attempt login
        cur_context.getAuth().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(cur_context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    cur_context.checkIfEmailVerified();
                } else {
                    // If sign in fails, display a message to the user.
                    loginResult.setValue(new LoginResult(R.string.login_failed));
                }
            }
        });
    }

    public void register(String email, String password, final LoginActivity cur_context) {
        // Attempt registration
        cur_context.getAuth().createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(cur_context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Set login result (when registered, user also logged in)
                    final FirebaseUser user = cur_context.getAuth().getCurrentUser();

                    // Create user document in DB collection
                    cur_context.getDatabase().create_user(user.getUid(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> registerTask) {
                            if (registerTask.isSuccessful()) {
                                cur_context.sendVerificationEmail();
                            }else{
                                Log.d("LOGIN", "user '"+user.getUid()+"' database object creation unsuccessful!");
                            }
                        }
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    loginResult.setValue(new LoginResult(R.string.register_failed));
                }
            }
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@") && username.split("@").length == 2 && username.split("@")[1].contains(".") && username.split("@")[1].split(".").length == 2) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    // Google sign-in
    public void firebaseAuthWithGoogle(GoogleSignInAccount acct, final SourceReadActivity cur_context) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        cur_context.getAuth().signInWithCredential(credential)
                .addOnCompleteListener(cur_context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success,
                            final FirebaseUser user = cur_context.getAuth().getCurrentUser();

                            // Create user document in DB collection
                            cur_context.getDatabase().create_user(user.getUid(), new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> registerTask) {
                                    if (registerTask.isSuccessful()) {
                                        // Update UI
                                        LoggedInUser new_user = new LoggedInUser(user);
                                        LoggedInUserView new_user_view = new LoggedInUserView(new_user);
                                        login_user.setValue(new_user);
                                        loginResult.setValue(new LoginResult(new_user_view));
                                    }else{
                                        Log.d("LOGIN", "user '"+user.getUid()+"' database object creation unsuccessful!");
                                    }
                                }
                            });
                        } else {
                            // Update user interface
                            loginResult.setValue(new LoginResult(R.string.login_failed));
                        }
                    }
                });
    }
}
