package mic82.ebusiness.hw2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.app.ProgressDialog;
import android.widget.AdapterView;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import org.json.*;

public class MainActivity extends AppCompatActivity {

    private Button m_new= null;
    private Button m_exit= null;
    private ListView tmp=null;
    private ListAdapter la = null;
    private List<Data> m_data = null;
    private Context m_context = null;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<Data> listToSave = new ArrayList<Data>(m_data);
        outState.putSerializable("mylist", listToSave);
    }

    @Override
    protected void onRestoreInstanceState (Bundle inState)
    {
        super.onSaveInstanceState(inState);
        if(inState!=null&&inState.getSerializable("mylist")!=null) {
            ArrayList<Data> al = (ArrayList<Data>) inState.getSerializable("mylist");
            m_data = new LinkedList<Data>(al);
            la = new ListAdapter((LinkedList<Data>) m_data, m_context);
            tmp.setAdapter(la);
            la.requestUpdate();
            //Toast.makeText(MainActivity.this, "restore called", Toast.LENGTH_SHORT).show();
        }
        else
        {
            AlertDialog al = null;
            AlertDialog.Builder alb = new AlertDialog.Builder(this);
            alb.setTitle("Cannot retrieve");
            alb.setMessage("our data");
            alb.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            al = alb.create();
            al.show();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        m_context = MainActivity.this;
        m_new=(Button) findViewById(R.id.button);
        m_exit=(Button) findViewById(R.id.button2);
        tmp = (ListView) findViewById(R.id.listView);
        tmp.setEmptyView((View)findViewById(R.id.list_empty_view));
        tmp.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "You Clicked on " + m_data.get(position).getTitle(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra("obj",m_data.get(position));
                startActivityForResult(intent, 10110);
            }
        });
        m_exit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });
        m_new.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivityForResult(intent, 10101);
            }
        });
        if(savedInstanceState!=null&&savedInstanceState.getSerializable("mylist")!=null) {
            ArrayList<Data> al=(ArrayList<Data>)savedInstanceState.getSerializable("mylist");
            m_data=new LinkedList<Data>(al);
            la=new ListAdapter((LinkedList<Data>) m_data, m_context);
            tmp.setAdapter(la);
            la.requestUpdate();

        }
        else
        {
            m_data = new LinkedList<Data>();
            la = new ListAdapter((LinkedList<Data>) m_data, m_context);
            tmp.setAdapter(la);
            JSONRetriever jp = new JSONRetriever();
            jp.execute();
        }

    }

    private String MakeDataJSON(Data x)
    {
        StringBuilder sb=new StringBuilder();
        sb.append("{\"title\":\"");
        sb.append(x.getTitle());
        sb.append("\",\"content\":\"");
        sb.append(x.getContent());
        sb.append("\",\"x\":\"");
        sb.append(x.getX());
        sb.append("\",\"y\":\"");
        sb.append(x.getY());
        sb.append("\",\"date\":\"");
        sb.append(x.getDate());
        sb.append("\",\"time\":\"");
        sb.append(x.getTime());
        sb.append("\"}");
        return sb.toString();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 10101) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Post Submitted", Toast.LENGTH_SHORT).show();
                Data dt = (Data) intent.getSerializableExtra("obj");
                la.add(dt);
/*              not working on school router, so deprecated this part
                try {

                    String objstr = MakeDataJSON(dt);
                    URL url = new URL("http://192.168.1.1:8080/PostNew");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setUseCaches(false);
                    OutputStream out = conn.getOutputStream();
                    out.write(objstr.getBytes());
                    out.flush();

                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("post request failed");
                    }
                    Toast.makeText(MainActivity.this, "Post Complete", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
            }
            else
            {
                Toast.makeText(MainActivity.this, "Post Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == 10102) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Click another post to view", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //----------------This class retrieves data from web and add to list------------------
/*
Basic idea came from https://www.learn2crack.com/2013/11/listview-from-json-example.html
 */
    private class JSONRetriever extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TextView title = (TextView)findViewById(R.id.txt_title);
            TextView content = (TextView)findViewById(R.id.txt_content);
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Retrieving Data ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            }
        
        @Override
        protected JSONObject doInBackground(String... args) {
            // Retrieve JSON from URL Async
            String objstr=null;
            try {
                /****** Please Read ******
                    Originally I wrote a servlet but it only works in private network at home.
                    The router at Pitt does not allow one machine to ping the other through it.
                    So here I use a emulated json instead.
                */
                //URL url=new URL("http://192.168.1.1:8080/GetList");
                URL url = new URL("http://ndsorrowchi.github.io/publicfiles/testjson.html");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                if (conn.getResponseCode() != 200) {
                    throw new RuntimeException("request url failed");
                }
                StringBuffer sb = new StringBuffer();
                InputStream inStream = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                inStream.close();
                objstr=sb.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject json = null;
            try {
                json = new JSONObject(objstr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }
        @Override
        protected void onPostExecute(JSONObject jsonobj) {
            pDialog.dismiss();

            Data x=null;
            try {//default location LatLng(40.422633, -79.979600)
                //check every thing to be null or not, otherwise use default
                JSONArray arr=jsonobj.getJSONArray("postlist");
                JSONObject json;
                for(int i=0;i<arr.length();i++) {
                    json=arr.getJSONObject(i);
                    x = new Data();
                    x.setTitle(json.getString("title") == null ? "no data" : json.getString("title"));
                    x.setContent(json.getString("content") == null ? "no data" : json.getString("content"));
                    x.setX(json.getString("x") == null ? "-79.979600" : json.getString("x"));
                    x.setY(json.getString("y") == null ? "40.422633" : json.getString("y"));
                    x.setDate(json.getString("date") == null ? "2016/02/19" : json.getString("date"));
                    x.setTime(json.getString("time") == null ? "22:19" : json.getString("time"));
                    la.add(x);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
