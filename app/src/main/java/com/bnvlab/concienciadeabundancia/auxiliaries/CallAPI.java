package com.bnvlab.concienciadeabundancia.auxiliaries;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Marcos on 16/05/2017.
 */

public class CallAPI extends AsyncTask<String, String, String> {
    Context context;
    public CallAPI(Context context) {
        //set context variables if required
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... params) {

        String urlString = params[0]; // URL to call

        String resultToDisplay = "";

        InputStream in = null;
        try {

            URL url = new URL(urlString);

            HttpsURLConnection httpURLConnection = (HttpsURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST"); // here you are telling that it is a POST request, which can be changed into "PUT", "GET", "DELETE" etc.
            httpURLConnection.setRequestProperty("Authorization","key=AAAAJoiF3uY:APA91bFcQXSRcnKoPBiUk8MmBaRw_EQO55ekb_9WQzGDsia78SaPy3mgDAxwct3EjVY0GEXrs_4Z8qthZQrpqIv76fv9T-vLfQSf7Q-yzgqhnafwe562VV3--8Gg0dJQmyIVEW-BymC8");
            httpURLConnection.setRequestProperty("Content-Type", "application/json"); // here you are setting the `Content-Type` for the data you are sending which is `application/json`
//            in = new BufferedInputStream(httpURLConnection.getInputStream());
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
//            httpURLConnection.connect();
            String urlParameters = "{ \"notification\": {\"title\": \"Portugal vs. Denmark\",\"text\": \"5 to 1\"},\"to\" : \"/topics/notifications\"}";
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();

            int responseCode = httpURLConnection.getResponseCode();

            String output = "Request URL " + url;
            output += System.getProperty("line.separator") + "Request Parameters " + urlParameters;
            output += System.getProperty("line.separator") + "Response Code " + responseCode;

            BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
            String line = "";
            StringBuilder responseOutput = new StringBuilder();

            while ((line = br.readLine()) != null){
                responseOutput.append(line);
            }
            br.close();

            output += System.getProperty("line.separator") + responseOutput.toString();

//            Toast.makeText(context, output, Toast.LENGTH_LONG).show();

        } catch (final Exception e) {
        }
        return resultToDisplay;
    }


    @Override
    protected void onPostExecute(String result) {
        //Update the UI
    }

}
