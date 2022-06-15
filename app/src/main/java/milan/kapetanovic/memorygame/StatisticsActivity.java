package milan.kapetanovic.memorygame;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {
    private ListView lista;
    private StatisticsAdapter adapter;
    private ArrayList<String> ListaSvihUsera;
    private String username;
    private int points;
    private String email;
    private Integer najboljiRezultat;
    private Integer najgoriRezultat;
    private ArrayList<String> ListaKorisnikaBaza;
    private Integer[] ListaPoena;
    private Button refresh;
    HttpHelper httpHelp;
    Handler handler;
    private String user;
    private MyReceiver myReceiver;
    private PlayerDBHelper playerDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        refresh = findViewById(R.id.refresh);

        refresh.setOnClickListener(this);

        user = getIntent().getStringExtra("username");

        adapter = new StatisticsAdapter(this, user);
        playerDB = new PlayerDBHelper(StatisticsActivity.this);
        httpHelp = new HttpHelper();
        handler = new Handler();

        lista = findViewById(R.id.statistika_lista);
        lista.setAdapter(adapter);

        NotificationChannel channel = new NotificationChannel("My notification","My notification", NotificationManager.IMPORTANCE_DEFAULT);

        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);


        Intent s = new Intent(StatisticsActivity.this, GameService.class);
        startService(s);

        ListaKorisnikaBaza = playerDB.readAllPlayers();

        for (int i = 0 ; i < ListaKorisnikaBaza.size() ; i++){
            ListaPoena = playerDB.readResultForPlayer(ListaKorisnikaBaza.get(i));
            najboljiRezultat = ListaPoena[ListaPoena.length - 1];
            najgoriRezultat = ListaPoena[0];
            email = ListaKorisnikaBaza.get(i) + "@" + ListaKorisnikaBaza.get(i) + ".com";
            if(ListaKorisnikaBaza.get(i).equals("Add user")){
                email = "no email";
            }
            Element m = new Element(ListaKorisnikaBaza.get(i),email,najboljiRezultat,najgoriRezultat);
            adapter.addElement(m);
        }

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Element o = adapter.getItem(i);
                String username = o.getName();
                Intent intent = new Intent(StatisticsActivity.this, DetailsActivity.class);
                intent.putExtra("username", username);
                stopService(s);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.refresh:
                new Thread(new Runnable() {
                        @Override
                        public void run() {
                            JSONArray jsonArray = new JSONArray();
                            JSONObject player = new JSONObject();
                            playerDB = new PlayerDBHelper(StatisticsActivity.this);
                            playerDB.deleteAll();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.removeElements();
                                }
                            });
                            try {
                                jsonArray = httpHelp.getJSONArrayFromURL(HttpHelper.URL_GAMES);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            for(int i = 0; i < jsonArray.length(); i++) {
                                try {
                                    player = jsonArray.getJSONObject(i);
                                    username = player.getString("username");
                                    points = player.getInt("score");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                playerDB.insertPlayer(username, username + "@gmail.com", points);
                            }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ListaKorisnikaBaza = playerDB.readAllPlayers();
                                        for (int i = 0; i < ListaKorisnikaBaza.size(); i++) {
                                            username = ListaKorisnikaBaza.get(i);
                                            ListaPoena = playerDB.readResultForPlayer(username);
                                            int najveci = ListaPoena[0];
                                            int najmanji = ListaPoena[ListaPoena.length - 1];
                                            email = ListaKorisnikaBaza.get(i) + "@gmail.com";
                                            Element e = new Element(username, email, najveci, najmanji);
                                            adapter.addElement(e);

                                        }
                                        playerDB.close();
                                    }
                                });

                            }


                    }).start();
        }
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub

        //Register BroadcastReceiver
        //to receive event from our service
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Action");
        registerReceiver(myReceiver, intentFilter);

        //Start our own service
        Intent s = new Intent(StatisticsActivity.this,GameService.class);
        startService(s);

        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(myReceiver);
        super.onStop();
    }

    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            int msg = arg1.getIntExtra("Message", 0);
            Log.d("Tag",String.valueOf(msg));
            NotificationCompat.Builder builder = new NotificationCompat.Builder(StatisticsActivity.this,"My notification");
            builder.setContentTitle("Score is refreshed");
            builder.setContentText("Score is refreshed.");
            builder.setSmallIcon(R.drawable.ic_launcher_background);
            builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(StatisticsActivity.this);
            managerCompat.notify(1,builder.build());

            if (msg == 1) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONArray jsonArray = new JSONArray();
                        JSONObject player = new JSONObject();
                        playerDB = new PlayerDBHelper(StatisticsActivity.this);
                        playerDB.deleteAll();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.removeElements();
                            }
                        });
                        try {
                            jsonArray = httpHelp.getJSONArrayFromURL(HttpHelper.URL_GAMES);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                player = jsonArray.getJSONObject(i);
                                username = player.getString("username");
                                points = player.getInt("score");

                                playerDB.insertPlayer(username, username + "@gmail.com", points);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ListaKorisnikaBaza = playerDB.readAllPlayers();
                                    for (int i = 0; i < ListaKorisnikaBaza.size(); i++) {
                                        username = ListaKorisnikaBaza.get(i);
                                        ListaPoena = playerDB.readResultForPlayer(username);
                                        int najveci = ListaPoena[0];
                                        int najmanji = ListaPoena[ListaPoena.length - 1];
                                        Element e = new Element(username, username + "@gmail.com", najveci, najmanji);
                                        adapter.addElement(e);

                                    }

                                    playerDB.close();
                                }
                            });

                        }



                }).start();
            }
        }
    }
}

