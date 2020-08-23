package com.app.rightbulb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.rightbulb.Db.UserInfo;
import com.app.rightbulb.Prefs.Prefs;
import com.tuya.smart.android.user.api.ILoginCallback;
import com.tuya.smart.android.user.api.IRegisterCallback;
import com.tuya.smart.android.user.api.IValidateCallback;
import com.tuya.smart.android.user.bean.User;
import com.tuya.smart.home.sdk.TuyaHomeSdk;
import com.tuya.smart.sdk.api.IResultCallback;

import javax.security.auth.login.LoginException;

public class LogInActivity extends AppCompatActivity {

    LinearLayout login_input_view,register_input_view_email,register_input_view_password;
    RelativeLayout register_input_view;
    TextView register,registerText,login_text,forgetPassword;
    Button login_button;
    boolean isEmail;
    EditText email_login,password_login,country_code_login,email_register,country_code_register,otp_regitser,password_register;
    String emailLogin,passwordLogin,otp,countryCodeLogin,error_msg="Don't Leave Empty";
    private static Context context;
    public static Context getAppContext() {
        return context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

//        if(Prefs.isLogin(LogInActivity.this))
//        {
//            String userID = Prefs.getUserID(LogInActivity.this);
//            UserInfo userInfo = new UserInfo(LogInActivity.this);
////            Cursor rs = userInfo.getData(userID);
//            Log.d("userInfo", "onCreate: "+userID);
//            Intent intent =new Intent(LogInActivity.this,HomeCreation_Activity.class);
//            intent.putExtra("userID",userID);
//            startActivity(intent);
//            finish();
//        }
//
//        else {
        SharedPreferences mPreferences;
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_log_in);
        TuyaHomeSdk.init(getApplication());
        TuyaHomeSdk.setDebugMode(true);
        login_input_view= findViewById(R.id.login_input_view);
        register_input_view =findViewById(R.id.register_input_view);
        register_input_view_email= findViewById(R.id.register_input_view_email);
        register_input_view_password= findViewById(R.id.register_input_view_password);
        register=findViewById(R.id.register);
        registerText=findViewById(R.id.register_text);
        login_button=findViewById(R.id.login_button);
        login_text=findViewById(R.id.login_text);
        email_login=findViewById(R.id.email_login);
        email_register=findViewById(R.id.email_register);
        password_login=findViewById(R.id.password_login);
        password_register=findViewById(R.id.password_register);
        otp_regitser=findViewById(R.id.otp);
        forgetPassword=findViewById(R.id.forgetPassword);
        country_code_login=findViewById(R.id.country_code_login);
        country_code_register=findViewById(R.id.country_code_register);
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this,ForgetPasswordActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startRegisterAnimation();

            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            boolean isEmailValid(CharSequence email) {
                isEmail =  Patterns.EMAIL_ADDRESS.matcher(email).matches();
                return Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
            @Override
            public void onClick(View v) {
                    if(login_button.getText().toString().equals("Login")){
//                        Toast.makeText(LogInActivity.this, ""+login_button.getText().toString(), Toast.LENGTH_SHORT).show();
                    if(email_login.getText().toString().isEmpty()){
                        email_login.requestFocus();
                        email_login.setError(error_msg);
                    }
                    else if(country_code_login.getText().toString().isEmpty())
                    {
                        country_code_login.requestFocus();
                        country_code_login.setError(error_msg);
                    }
                    else if (password_login.getText().toString().isEmpty())
                    {
                        password_login.requestFocus();
                        password_login.setError(error_msg);
                    }
                    else {
                        emailLogin=email_login.getText().toString().trim();
                        passwordLogin=password_login.getText().toString();
                        countryCodeLogin=country_code_login.getText().toString();
                        isEmailValid(emailLogin);
                        logIn(countryCodeLogin,passwordLogin,emailLogin);
                    }
                    }
               else if(login_button.getText().toString().equals("Next")){
//                        Toast.makeText(LogInActivity.this, ""+login_button.getText().toString(), Toast.LENGTH_SHORT).show();
                        if(email_register.getText().toString().isEmpty()){
                            email_register.requestFocus();
                            email_register.setError(error_msg);

                        }
                        else if(country_code_register.getText().toString().isEmpty())
                        {
                            country_code_register.requestFocus();
                            country_code_register.setError(error_msg);

                        }
                        else{

                            countryCodeLogin=country_code_register.getText().toString().trim();
                            emailLogin=email_register.getText().toString().trim();
                            if(!isEmailValid(emailLogin)){
                                TuyaHomeSdk.getUserInstance().getValidateCode(countryCodeLogin,emailLogin, new IValidateCallback() {
                                    @Override
                                    public void onSuccess() {
                                        sendCode();
                                    }

                                    @Override
                                    public void onError(String code, String error) {
                                        Log.d("Error", "onError: "+error);


                                    }
                                });}
                            else{
                                TuyaHomeSdk.getUserInstance().getRegisterEmailValidateCode(countryCodeLogin, emailLogin, new IResultCallback() {
                                    @Override
                                    public void onError(String code, String error) {
                                        Log.d("email_regitser_error", "onError: "+error);
                                    }

                                    @Override
                                    public void onSuccess() {
                                            sendCode();
                                    }
                                });
                            }

                        }
                }
               else if(login_button.getText().toString().equals("Register")){
//                        Toast.makeText(LogInActivity.this, ""+login_button.getText().toString(), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(LogInActivity.this, "Hello "+login_button.getText().toString(), Toast.LENGTH_SHORT).show();
                        if(password_register.getText().toString().isEmpty()){
                            password_register.requestFocus();
                            password_register.setError(error_msg);

                        }
                        else if(otp_regitser.getText().toString().isEmpty())
                        {
                            otp_regitser.requestFocus();
                            otp_regitser.setError(error_msg);

                        }
                        else{
                            passwordLogin=password_register.getText().toString().trim();
                            otp=otp_regitser.getText().toString().trim();
                            if(!isEmailValid(emailLogin)){
                            TuyaHomeSdk.getUserInstance().registerAccountWithPhone(countryCodeLogin, emailLogin, passwordLogin, otp, new IRegisterCallback() {
                                @Override
                                public void onSuccess(User user) {
                                    email_register.setText("");
                                    country_code_register.setText("");
                                    startRegisterAnimation();

                                }

                                @Override
                                public void onError(String code, String error) {
                                    Log.d("register_error", "onError: "+error);
                                }
                            });}
                            else{
                                TuyaHomeSdk.getUserInstance().registerAccountWithEmail(countryCodeLogin, emailLogin, passwordLogin, otp, new IRegisterCallback() {
                                    @Override
                                    public void onSuccess(User user) {

                                        startRegisterAnimation();
                                    }

                                    @Override
                                    public void onError(String code, String error) {
                                        Log.d("register_error", "onError: "+error);
                                    }
                                });
                            }

                        }
               }
            }
        });
    }
//}

    private void sendCode() {


        Animation fadeout = AnimationUtils.loadAnimation(LogInActivity.this,R.anim.fade_out);
        Animation fadein = AnimationUtils.loadAnimation(LogInActivity.this,R.anim.fade_in);
        register_input_view_email.startAnimation(fadeout);
        register_input_view_password.setVisibility(View.VISIBLE);
        register_input_view_email.setVisibility(View.INVISIBLE);
        register_input_view_password.startAnimation(fadein);
        fadein.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                login_button.setText("Register");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void logIn(String countryCode, String password, String email) {
//        Toast.makeText(this, ""+isEmail, Toast.LENGTH_SHORT).show();
        if(isEmail){
            TuyaHomeSdk.getUserInstance().loginWithEmail(countryCode, password, email, new ILoginCallback() {
                @Override
                public void onSuccess(User user) {
//                    Toast.makeText(LogInActivity.this, ""+user.getUsername(), Toast.LENGTH_SHORT).show();
                    Intent intent =new Intent(LogInActivity.this,HomeCreation_Activity.class);
                    intent.putExtra("userID",user.getUid());
                    startActivity(intent);
                }
                @Override
                public void onError(String code, String error) {
                    Log.d("Login_Error", "onError: "+error);
                    Toast.makeText(LogInActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
                TuyaHomeSdk.getUserInstance().loginWithPhonePassword(countryCode, email, password, new ILoginCallback() {
                    @Override
                    public void onSuccess(User user) {
//                        Toast.makeText(LogInActivity.this, ""+user.getUsername(), Toast.LENGTH_SHORT).show();
                        Log.d("Login Phone", "onSuccess: "+user.getUid());
                        UserInfo userInfo = new UserInfo(LogInActivity.this);
                        Boolean result = userInfo.insertContact(user.getNickName(),user.getMobile(),user.getEmail(),user.getUid(),user.getHeadPic(),user.getSid(),user.getPhoneCode());
                        if(result){
                            Prefs.setLogin(LogInActivity.this,true);
                            Prefs.setUserID(LogInActivity.this,user.getUid());
                            Intent intent =new Intent(LogInActivity.this,HomeCreation_Activity.class);
                            intent.putExtra("userID",user.getUid());
                            startActivity(intent);
                            finish();
                        }
                        else
                        {
                            Toast.makeText(LogInActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onError(String code, String error) {
                        Log.d("Login_Error", "onError: "+error);
                        Toast.makeText(LogInActivity.this, ""+error, Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    private void startRegisterAnimation() {
        if(register.getText().toString().equals("Register"))
        {
        Animation left_to_right= AnimationUtils.loadAnimation(LogInActivity.this,R.anim.left_to_right);
        Animation right_to_left= AnimationUtils.loadAnimation(LogInActivity.this,R.anim.right_to_left);
        /*left_to_right.setFillAfter(true);
        right_to_left.setFillAfter(true);*/
        register_input_view.startAnimation(left_to_right);
        register_input_view.setVisibility(View.VISIBLE);
        login_input_view.setVisibility(View.INVISIBLE);
        register_input_view_email.setVisibility(View.VISIBLE);
        login_input_view.startAnimation(right_to_left);
        left_to_right.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                register.setText("LogIn");
                registerText.setText("Already have an Account?");
                login_text.setText("Register");
                login_button.setText("Next");
            }

            @Override
            public void onAnimationEnd(Animation animation) {

//                startRegisterAnimatiom();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }
     else{
            Animation left_to_right=AnimationUtils.loadAnimation(LogInActivity.this,R.anim.left);
            Animation right_to_left=AnimationUtils.loadAnimation(LogInActivity.this,R.anim.right);
            /*left_to_right.setFillAfter(true);
            right_to_left.setFillAfter(true);*/
            register_input_view.startAnimation(left_to_right);
            register_input_view.setVisibility(View.INVISIBLE);
            login_input_view.setVisibility(View.VISIBLE);
            login_input_view.startAnimation(right_to_left);
            left_to_right.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    register.setText("Register");
                    registerText.setText("Don't have an Account?");
                    login_text.setText("logIn");
                    login_button.setText("LogIn");
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

}
