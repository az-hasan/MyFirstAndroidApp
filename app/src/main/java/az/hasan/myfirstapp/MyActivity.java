package az.hasan.myfirstapp;

import android.content.Context;
import android.content.pm.ApplicationInfo;
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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

public class MyActivity extends AppCompatActivity {

    private EditText newTextMessage;

    /** called when "Send New Text Message" button is pressed.
     *  Gets text from box labelled "enter_new_text".
     *
     *  Checks for device connectivity. Calls thread (SendData) to send installed
     *  application names if there is connectivity.
     *  **/
    public void onClickSendMessage(View view) {
        String message = "";
        // String url = "http://10.0.2.2:8000";
        String url = "https://cse-os.qu.edu.qa/challenge";

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
            newTextMessage.setText("Not Connected to Network");
        }
    }

    /* Implements background thread class to send information about installed applications
    * to the specified URL
    * */
    private class SendData extends AsyncTask<String, Void, String>{

        /** Called by onClickSendMessage. Takes String url, opens a connection to URL "url",
         * sends a list of all applications installed on the phone using POST
         *
         * @param params - can be accessed by param[0], param[1]
         * @param[0] - String "message" passed to method by onClickSendMessage
         * @param[1] - String "url" to send message to, passed to method by onClickSendMessage
         * @return  - If list was successfully sent, String "success" is returned.
         *      Otherwise Exceptions (if any) will be returned.
         *      This return value is used by "onPostExecute"
         * **/

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection myHttpsConn = null;
            OutputStreamWriter outWriter = null;
            String myAppNames = "";
            String message = params[0];
            String url = params[1];
            URL myURL = null;

            //get list of all applications installed on phone & concatenate names to single string
            List<ApplicationInfo> myApps = getPackageManager().getInstalledApplications(0);
            ApplicationInfo singleApp;

            if( myApps.size() > 0){
                for( int i = 0; i < myApps.size(); i++ ){
                    singleApp = myApps.get(i);
                    myAppNames += singleApp.loadLabel(getPackageManager()).toString() + "\n";
                }
            }

            //define URL and HttpConnection, open connection to URL, send list, close connection
            try {
                myURL = new URL(url);
                myHttpsConn = (HttpURLConnection) myURL.openConnection();

                System.out.println("to send to URL: " + myURL);

                myHttpsConn.setDoOutput(true);
                myHttpsConn.setRequestMethod("POST");
                myHttpsConn.setFixedLengthStreamingMode(myAppNames.length());

                outWriter = new OutputStreamWriter(myHttpsConn.getOutputStream());
                outWriter.write(myAppNames);
                outWriter.flush();
                outWriter.close();
                myHttpsConn.disconnect();

                return "success";
            }

            catch (MalformedURLException e) {
                e.printStackTrace();
                return "Malformed URL Exception";
            } catch (ProtocolException e) {
                e.printStackTrace();
                return "Protocol Exception";
            } catch (SSLHandshakeException e){
                e.printStackTrace();
                return ("SSLHandshake Exception");
            } catch (IOException e) {
                e.printStackTrace();
                return "IO Exception";
            } finally {
                if (myHttpsConn != null) {
                    myHttpsConn.disconnect();
                }
            }
        }


        /*
        * sets text field to String returns by doInBackground
        * */
        @Override
        protected void onPostExecute(String result){
            newTextMessage.setText(result);
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
