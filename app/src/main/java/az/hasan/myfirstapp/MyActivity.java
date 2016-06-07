package az.hasan.myfirstapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

public class MyActivity extends AppCompatActivity {

    /** called when "Send New Text Message" button is pressed
     *  Gets text from box labelled "enter_new_text"**/
    public void onClickSendMessage(View view) {
        String message;
        String url = "https://10.0.2.2:8000";
        int len = 0;
        EditText newTextMessage;

        //Intent intent = new Intent(this, DisplayMessageActivity.class);
        newTextMessage = (EditText) findViewById(R.id.enter_new_text);
        message = newTextMessage.getText().toString();
        System.out.println(message);

        ConnectivityManager myConnections =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo myNetworkInfo = myConnections.getActiveNetworkInfo();

        if(myNetworkInfo != null && myNetworkInfo.isConnected()){
            SendData mySendData = new SendData();
            mySendData.execute(message, url);
        }else{
            System.out.println("Not Connected to Network");
        }
    }

    private class SendData extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection myHttpConn = null;
            int len = 0;
            OutputStreamWriter outWriter = null;
            String message = params[0];
            String url = params[1];
            URL myURL = null;


            //define URL and HttpConnection, open connection to URL
            try {
                myURL = new URL(url);
                myHttpConn = (HttpURLConnection) myURL.openConnection();

                len = message.length();
                System.out.println("msg len: " + len + "to send to URL: " + myURL);

                myHttpConn.setDoOutput(true);
                myHttpConn.setRequestMethod("POST");
                myHttpConn.setFixedLengthStreamingMode(len);
                System.out.println("set fixed length streaming mode");

                outWriter = new OutputStreamWriter(myHttpConn.getOutputStream());
                outWriter.write(message);
                outWriter.flush();
                outWriter.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (myHttpConn != null) {
                    myHttpConn.disconnect();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void v){
            System.out.println("Entered post execution");
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
