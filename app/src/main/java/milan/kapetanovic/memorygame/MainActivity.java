package milan.kapetanovic.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private Button register;
    private EditText et, et2;
    private String username, password;
    private PlayerDBHelper playerDB;
    private HttpHelper httpHelp;
    private Handler handler;
    boolean res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login = findViewById(R.id.login_button);
        et = findViewById(R.id.edit_username);
        et2 = findViewById(R.id.edit_pass);
        register = findViewById(R.id.register_button);

        playerDB = new PlayerDBHelper(MainActivity.this);
        httpHelp = new HttpHelper();
        handler = new Handler();

        login.setOnClickListener(this);
        register.setOnClickListener(this);



        //playerDB.insertPlayer("test", "test@email.com", 13);
        //Log.d("sda", "Insert");
        //playerDB.insertPlayer("test2", "test2@email.com", 23);
        //playerDB.insertPlayer("test3", "test3@email.com", 33);
        //playerDB.insertPlayer("test4", "test4@email.com", 43);

    }

    public void onClick(View view)
    {
        switch(view.getId()) {
            case R.id.login_button:
                username = et.getText().toString();
                password = et2.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("username", username);
                            jsonObject.put("password", password);

                            res = httpHelp.postJSONObjectFromURL(httpHelp.URL_LOGIN, jsonObject);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(res)
                                    {
                                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                                        intent.putExtra("username", username);
                                        startActivity(intent);
                                    }
                                    else
                                    {
                                        Toast.makeText(MainActivity.this, "Pogresan username/sifra", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();

            case R.id.register_button:
                Log.d("Main Activity", "Register Button");
                Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
            default:
                break;
        }
    }
}