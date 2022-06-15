package milan.kapetanovic.memorygame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    PlayerDBHelper playerDB;
    HttpHelper httpHelp;
    Handler handler;

    private Button b1;
    private Button b2;
    private Button b3;
    private Button b4;
    private Button b5;
    private Button b6;
    private Button b7;
    private Button b8;
    private Button b9;
    private Button b10;
    private Button b11;
    private Button b12;
    private Button b13;
    private Button b14;
    private Button b15;
    private Button b16;

    private ImageView im1;
    private ImageView im2;
    private ImageView im3;
    private ImageView im4;
    private ImageView im5;
    private ImageView im6;
    private ImageView im7;
    private ImageView im8;
    private ImageView im9;
    private ImageView im10;
    private ImageView im11;
    private ImageView im12;
    private ImageView im13;
    private ImageView im14;
    private ImageView im15;
    private ImageView im16;

    private Button start;
    private Button statistics;

    private int cnt = 0;
    private int pairCounter = 0;

    private Button buttonOne, buttonTwo;
    private ImageView imageOne, imageTwo;

    private String provera = "";
    private int[] pogodjeni = new int[8];
    private int broj = 0;

    private int user_points = 0;

    private boolean previous = false;
    private boolean pair = false;
    private boolean first = true;
    private boolean changed = false;

    private final int[] imageArray = new int[]
            {
                    R.drawable.butters_stotch,
                    R.drawable.eric_cartman,
                    R.drawable.kenny_mccormick,
                    R.drawable.kyle_broflovski,
                    R.drawable.mr_mackey,
                    R.drawable.randy_marsh,
                    R.drawable.stan_marsh,
                    R.drawable.timmy,
            };
    private final String[] tagArray = new String[]
            {
                    "tag1",
                    "tag2",
                    "tag3",
                    "tag4",
                    "tag5",
                    "tag6",
                    "tag7",
                    "tag8",
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        playerDB = new PlayerDBHelper(GameActivity.this);
        httpHelp = new HttpHelper();
        handler = new Handler();
        JNIExample jni = new JNIExample();

        b1 = findViewById(R.id.button1);
        b2 = findViewById(R.id.button2);
        b3 = findViewById(R.id.button3);
        b4 = findViewById(R.id.button4);
        b5 = findViewById(R.id.button5);
        b6 = findViewById(R.id.button6);
        b7 = findViewById(R.id.button7);
        b8 = findViewById(R.id.button8);
        b9 = findViewById(R.id.button9);
        b10 = findViewById(R.id.button10);
        b11 = findViewById(R.id.button11);
        b12 = findViewById(R.id.button12);
        b13 = findViewById(R.id.button13);
        b14 = findViewById(R.id.button14);
        b15 = findViewById(R.id.button15);
        b16 = findViewById(R.id.button16);

        im1 = findViewById(R.id.image1);
        im2 = findViewById(R.id.image2);
        im3 = findViewById(R.id.image3);
        im4 = findViewById(R.id.image4);
        im5 = findViewById(R.id.image5);
        im6 = findViewById(R.id.image6);
        im7 = findViewById(R.id.image7);
        im8 = findViewById(R.id.image8);
        im9 = findViewById(R.id.image9);
        im10 = findViewById(R.id.image10);
        im11 = findViewById(R.id.image11);
        im12 = findViewById(R.id.image12);
        im13 = findViewById(R.id.image13);
        im14 = findViewById(R.id.image14);
        im15 = findViewById(R.id.image15);
        im16 = findViewById(R.id.image16);

        start = findViewById(R.id.start_dugme);
        statistics = findViewById(R.id.statistics);

        ImageView[] images = {im1, im2, im3, im4, im5, im6, im7, im8, im9, im10, im11, im12, im13, im14, im15, im16};
        Button[] buttons = {b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12, b13, b14, b15, b16};

        for (Button button : buttons)
        {
            button.setOnClickListener(this);
        }
        start.setOnClickListener(this);
        statistics.setOnClickListener(this);

        if(first)
        {
            for(Button button : buttons)
            {
                button.setEnabled(false);
                button.setClickable(false);
            }

            for(ImageView image : images)
            {
                image.setVisibility(View.INVISIBLE);
            }
        }

        randomizeImages();

        start.setBackgroundColor(Color.RED);
        statistics.setBackgroundColor(Color.YELLOW);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(first)
                {
                    for(Button button : buttons)
                    {
                        button.setEnabled(true);
                        button.setClickable(true);
                    }

                    for(ImageView image : images)
                    {
                        image.setVisibility(View.VISIBLE);
                    }
                    start.setText(getString(R.string.restart));
                    first = false;
                    cnt = 0;
                    changed = false;
                }
                else
                {
                    if(pairCounter < 8)
                    {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("username", getUser());
                                    jsonObject.put("email", getEmail());
                                    jsonObject.put("score", -1);

                                    final boolean res = httpHelp.postJSONObjectFromURL(HttpHelper.URL_GAMES, jsonObject);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!res)
                                            {
                                                Toast.makeText(GameActivity.this, "Error, couldnt save game.", Toast.LENGTH_LONG).show();
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
                        playerDB.insertPlayer(getUser(), getEmail(), 0);
                    }
                    randomizeImages();
                    for(Button button : buttons)
                    {
                        button.setVisibility(View.VISIBLE);
                        button.setEnabled(true);
                        button.setClickable(true);
                    }

                    user_points = 0;
                    changed = true;
                    cnt = 0;
                    pair = false;
                    pairCounter = 0;

                }
            }
        });

        statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GameActivity.this, StatisticsActivity.class);
                i.putExtra("username", getIntent().getStringExtra("username"));
                i.setAction("fromGameActivity");
                startActivity(i);
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.button1:
                checkPrevious(); // Just to hold image until next one is clicked
                b1.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById(R.id.image1);
                    buttonOne = findViewById(R.id.button1);
                } else if (cnt == 1) {
                    imageTwo = findViewById (R.id.image1);
                    buttonTwo = findViewById (R.id.button1);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs(); // Image check
                break;
            case R.id.button2:
                checkPrevious();
                b2.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image2);
                    buttonOne = findViewById (R.id.button2);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image2);
                    buttonTwo = findViewById (R.id.button2);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button3:
                checkPrevious();
                b3.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image3);
                    buttonOne = findViewById (R.id.button3);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image3);
                    buttonTwo = findViewById (R.id.button3);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button4:
                checkPrevious();
                b4.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image4);
                    buttonOne = findViewById (R.id.button4);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image4);
                    buttonTwo = findViewById (R.id.button4);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button5:
                checkPrevious();
                b5.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image5);
                    buttonOne = findViewById (R.id.button5);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image5);
                    buttonTwo = findViewById (R.id.button5);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button6:
                checkPrevious();
                b6.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image6);
                    buttonOne = findViewById (R.id.button6);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image6);
                    buttonTwo = findViewById (R.id.button6);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button7:
                checkPrevious();
                b7.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image7);
                    buttonOne = findViewById (R.id.button7);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image7);
                    buttonTwo = findViewById (R.id.button7);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button8:
                checkPrevious();
                b8.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image8);
                    buttonOne = findViewById (R.id.button8);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image8);
                    buttonTwo = findViewById (R.id.button8);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button9:
                checkPrevious();
                b9.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image9);
                    buttonOne = findViewById (R.id.button9);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image9);
                    buttonTwo = findViewById (R.id.button9);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button10:
                checkPrevious();
                b10.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image10);
                    buttonOne = findViewById (R.id.button10);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image10);
                    buttonTwo = findViewById (R.id.button10);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button11:
                checkPrevious();
                b11.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image11);
                    buttonOne = findViewById (R.id.button11);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image11);
                    buttonTwo = findViewById (R.id.button11);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button12:
                checkPrevious();
                b12.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image12);
                    buttonOne = findViewById (R.id.button12);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image12);
                    buttonTwo = findViewById (R.id.button12);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button13:
                checkPrevious();
                b13.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image13);
                    buttonOne = findViewById (R.id.button13);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image13);
                    buttonTwo = findViewById (R.id.button13);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button14:
                checkPrevious();
                b14.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image14);
                    buttonOne = findViewById (R.id.button14);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image14);
                    buttonTwo = findViewById (R.id.button14);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button15:
                checkPrevious();
                b15.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image15);
                    buttonOne = findViewById (R.id.button15);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image15);
                    buttonTwo = findViewById (R.id.button15);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            case R.id.button16:
                checkPrevious();
                b16.setVisibility (View.INVISIBLE);
                if (cnt == 0) {
                    imageOne = findViewById (R.id.image16);
                    buttonOne = findViewById (R.id.button16);
                } else if (cnt == 1){
                    imageTwo = findViewById (R.id.image16);
                    buttonTwo = findViewById (R.id.button16);
                }
                if(cnt == 1)
                    pair = true;
                cnt++;
                checkPairs();
                break;
            default:
                break;
        }
    }

    public void randomizeImages()
    {
        ArrayList<Integer> intArray = new ArrayList<>();
        int imageCounter = 0;
        int circleCount = 1;
        int totalDifferentImages = 8;

        for(int i = 0; i <= circleCount; i++)
        {
            for(int j = 0; j < totalDifferentImages; j++)
            {
                intArray.add(j);
            }
        }

        Collections.shuffle(intArray);

        for(ImageView image : Arrays.asList(im1, im2, im3, im4, im5, im6, im7, im8, im9, im10, im11, im12, im13, im14, im15, im16))
        {
            image.setImageResource(imageArray[intArray.get(imageCounter)]);
            image.setTag(tagArray[intArray.get(imageCounter)]);
            imageCounter++;
        }
    }

    public void checkPairs()
    {
        JNIExample jni = new JNIExample();
        playerDB = new PlayerDBHelper(GameActivity.this);
        if(pair)
        {
            if(imageOne.getTag().equals(imageTwo.getTag()))
            {
                for(Button button : Arrays.asList(buttonOne, buttonTwo))
                {
                    button.setVisibility(View.INVISIBLE);
                    button.setEnabled(false);
                    button.setClickable(false);
                }
                user_points += 5;
                pairCounter++;
            }
            else
            {

                previous = true;
                if(user_points != 0)
                {
                    user_points--;
                }
            }
            if(pairCounter == 8)
            {

                double procenat = jni.racunajProcenat(user_points);
                Toast.makeText(this, "Procenat bodova je "+ procenat, Toast.LENGTH_LONG).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("username", getUser());
                            jsonObject.put("email", getEmail());
                            jsonObject.put("score", user_points);
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }).start();
                playerDB.insertPlayer(getUser(), getEmail(), user_points);
            }
            pair = false;
            cnt = 0;
        }
    }

    public void checkPrevious()
    {
        if(previous)
        {
            buttonOne.setVisibility(View.VISIBLE);
            buttonTwo.setVisibility(View.VISIBLE);
            previous = false;
        }
    }

    public String getUser()
    {
        return getIntent().getStringExtra("username");
    }

    public String getEmail()
    {
        return getIntent().getStringExtra("username").toLowerCase() + "@gmail.com";
    }
}
