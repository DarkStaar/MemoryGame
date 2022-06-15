package milan.kapetanovic.memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Button register;
    private EditText usernameET, passwordET, emailET;
    private String username, password, email;
    private HttpHelper httpHelp;
    private Handler handler;
    boolean res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        httpHelp = new HttpHelper();
        handler = new Handler();

        register = findViewById(R.id.regButt);
        usernameET = findViewById(R.id.register_username);
        passwordET = findViewById(R.id.register_password);
        emailET = findViewById(R.id.register_email);

        register.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.regButt:
                username = usernameET.getText().toString();
                password = passwordET.getText().toString();
                email = emailET.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("username", username);
                            jsonObject.put("password", password);
                            jsonObject.put("email", email);

                            res = httpHelp.postJSONObjectFromURL(HttpHelper.URL_REGISTER, jsonObject);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                        if(res)
                                        {
                                            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                        else
                                        {
                                            Toast.makeText(RegisterActivity.this, "Error!", Toast.LENGTH_LONG).show();
                                        }
                                }
                            });
                        }catch (IOException | JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
        }
    }
}
