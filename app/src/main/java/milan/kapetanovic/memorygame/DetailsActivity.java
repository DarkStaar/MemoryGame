package milan.kapetanovic.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    private TextView mTextView;
    private ArrayAdapter<Integer> mListAdapter;
    private Integer[] listaRezultata;
    PlayerDBHelper playerDB;
    HttpHelper httpHelp;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        playerDB = new PlayerDBHelper(DetailsActivity.this);
        httpHelp = new HttpHelper();
        handler = new Handler();
        String username = getIntent().getStringExtra("username");

        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONArray jsonArray = new JSONArray();
                try {
                    jsonArray = httpHelp.getJSONArrayFromURL(HttpHelper.URL_GAMES_USER + username);
                    if(jsonArray == null)
                    {
                        Toast.makeText(DetailsActivity.this, "Error, no games fetched for player.", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        for(int i = 0; i < jsonArray.length(); i++)
                        {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            int score = jsonObject.getInt("score");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mListAdapter.add(score);
                                }
                            });
                        }
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }).start();

        ArrayList<String> a = getIntent().getStringArrayListExtra("bestScore");
        listaRezultata = playerDB.readResultForPlayer(username);
        mTextView = findViewById(R.id.username_details);

        mTextView.setText(username);

        mListAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);

        ListView list = findViewById(R.id.details_lista);
        list.setAdapter(mListAdapter);
        list.setEmptyView(findViewById(R.id.emptyView));
        mListAdapter.addAll(listaRezultata);
    }
}