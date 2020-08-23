package com.app.rightbulb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import com.tuya.smart.android.user.api.IResetPasswordCallback;
import com.tuya.smart.android.user.api.IValidateCallback;
import com.tuya.smart.home.sdk.TuyaHomeSdk;

import javax.xml.validation.Validator;

public class ForgetPasswordActivity extends AppCompatActivity {

    LinearLayout login_input_view,register_input_view_email,register_input_view_password;
    RelativeLayout register_input_view;
    TextView register,registerText,login_text;
    Button login_button;
    boolean isEmail;
    EditText email_register,country_code_register,otp_regitser,password_register;
    String email,password,otp,countryCode,error_msg="Don't Leave Empty";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        TuyaHomeSdk.init(getApplication());
        TuyaHomeSdk.setDebugMode(true);
        register_input_view_email= findViewById(R.id.register_input_view_email);
        register_input_view_password= findViewById(R.id.register_input_view_password);
        registerText=findViewById(R.id.register_text);
        login_button=findViewById(R.id.login_button_fp);
        login_text=findViewById(R.id.login_text);
        email_register=findViewById(R.id.email_fp);
        password_register=findViewById(R.id.password_fp);
        otp_regitser=findViewById(R.id.fp_otp);
        country_code_register=findViewById(R.id.country_code_fp);
        login_button.setOnClickListener(new View.OnClickListener() {
            boolean isEmailValid(CharSequence email) {
                isEmail =  Patterns.EMAIL_ADDRESS.matcher(email).matches();
                return Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
            @Override
            public void onClick(View v) {
                if(login_button.getText().toString().equals("Next")){
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
                    email=email_register.getText().toString().trim();
                    countryCode = country_code_register.getText().toString().trim();
                    if(!isEmailValid(email)){
                        TuyaHomeSdk.getUserInstance().getValidateCode(countryCode, email, new IValidateCallback() {
                            @Override
                            public void onSuccess() {
                                sendCode();
                            }

                            @Override
                            public void onError(String code, String error) {
                                Log.d("Validator Error", "onError: "+ error);
                                Toast.makeText(ForgetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        TuyaHomeSdk.getUserInstance().getValidateCode(countryCode, email, new IValidateCallback() {
                            @Override
                            public void onSuccess() {
                                sendCode();
                            }

                            @Override
                            public void onError(String code, String error) {
                                Log.d("Validator Error", "onError: "+ error);
                                Toast.makeText(ForgetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                }
                else{
                    password = password_register.getText().toString().trim();
                    otp = otp_regitser.getText().toString().trim();
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
                        if(isEmailValid(email))
                        {
                            TuyaHomeSdk.getUserInstance().resetEmailPassword(countryCode, email, otp, password, new IResetPasswordCallback() {
                                @Override
                                public void onSuccess() {
                                    startActivity(new Intent(ForgetPasswordActivity.this,LogInActivity.class));
                                    finish();
                                }

                                @Override
                                public void onError(String code, String error) {
                                    Toast.makeText(ForgetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            TuyaHomeSdk.getUserInstance().resetPhonePassword(countryCode, email, otp, password, new IResetPasswordCallback() {
                                @Override
                                public void onSuccess() {
                                    startActivity(new Intent(ForgetPasswordActivity.this,LogInActivity.class));
                                    finish();
                                }

                                @Override
                                public void onError(String code, String error) {
                                    Toast.makeText(ForgetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    private void sendCode() {


        Animation fadeout = AnimationUtils.loadAnimation(ForgetPasswordActivity.this,R.anim.fade_out);
        Animation fadein = AnimationUtils.loadAnimation(ForgetPasswordActivity.this,R.anim.fade_in);
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
                login_button.setText("Set Password");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}