package milan.kapetanovic.memorygame;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class StatisticsAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Element> list;
    PlayerDBHelper playerDB;
    HttpHelper httpHelper;
    Handler handler;
    private String username;

    public StatisticsAdapter(Context mContext, String user) {
        this.mContext = mContext;
        list = new ArrayList<>();
        if(username != null)
        {
            username = user;
        }
        else
        {
            username = "am";
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Element getItem(int i) {
        Element o = null;
        try
        {
            o = list.get(i);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void addElement(Element e)
    {
        list.add(e);
        notifyDataSetChanged();
    }

    public void removeElementByIndex(int i)
    {
        list.remove(i);
        notifyDataSetChanged();
    }

    public void removeElements()
    {
        for(int i = 0; i < list.size(); i++)
        {
            list.remove(i);
        }
        notifyDataSetChanged();
    }

    public void removeElementByName(String username)
    {
        ArrayList<String> usernames = playerDB.readAllPlayers();
        if(usernames.contains(username))
        {
            list.remove(username);
        }
        notifyDataSetChanged();
    }

    public ArrayList<Integer> getBestResultByUsername(String name)
    {
        ArrayList<Integer> bestScoreList = new ArrayList<Integer>();

        Integer[] bestScores = playerDB.readResultForPlayer(name);

        Integer best = bestScores[0];

        bestScoreList.add(best);

        return bestScoreList;
    }

    public ArrayList<Integer> getWorstResultByUsername(String name)
    {
        ArrayList<Integer> worstScoreList = new ArrayList<Integer>();

        Integer[] worstScores = playerDB.readResultForPlayer(name);

        int length = worstScores.length;

        Integer worst = worstScores[length - 1];

        worstScoreList.add(worst);

        return worstScoreList;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        Log.d("Adapter_TAG", "getView");
        if(view == null)
        {
            Log.d("Adapter_TAG", "getView - null");
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row, null);
            viewHolder = new ViewHolder();
            viewHolder.name = view.findViewById(R.id.username);
            viewHolder.email = view.findViewById(R.id.email);
            viewHolder.bestScore = view.findViewById(R.id.best_score);
            viewHolder.worstScore = view.findViewById(R.id.worst_score);
            viewHolder.buttonItem = view.findViewById(R.id.row_button);
            view.setTag(viewHolder);
        }
        else
        {
            viewHolder = (ViewHolder) view.getTag();
        }
        playerDB = new PlayerDBHelper(mContext);
        Element element = (Element) getItem(i);

        int a = element.getBestScore();
        int b = element.getWorstScore();
        String best = Integer.toString(a);
        String worst = Integer.toString(b);
        viewHolder.name.setText(element.getName());
        viewHolder.email.setText(element.getEmail());
        viewHolder.bestScore.setText(best);
        viewHolder.worstScore.setText(worst);

        if(username != null && username.equals(element.getName()))
            {
                viewHolder.buttonItem.setVisibility(View.VISIBLE);
                viewHolder.buttonItem.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        playerDB.delete(element.getName());
                        removeElementByIndex(i);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try
                                {
                                    final boolean res = httpHelper.httpDelete(HttpHelper.URL_GAMES_USER + username);

                                }
                                catch(JSONException e)
                                {
                                    e.printStackTrace();
                                }
                                catch(IOException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }).start();

                    }
                });
            }
            else
            {
                viewHolder.buttonItem.setVisibility(View.INVISIBLE);
            }


        return view;
    }


    static class ViewHolder
    {
        TextView name;
        TextView email;
        TextView bestScore;
        TextView worstScore;

        Button buttonItem;
    }
}
