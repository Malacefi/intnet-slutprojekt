package intnet17.projektet;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Emelie on 2017-03-13.
 */

public class GetRequest extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String... request){
        HttpURLConnection connection = null;
        try{
            URL requestUrl = new URL(ServerRequest.BASE_URL + request[0] + request[1]);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod(ServerRequest.GET);
            connection.setRequestProperty("Accept-Charset", "UTF-8");

            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                StringBuffer response = new StringBuffer();
                String line;
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                while((line = inputStream.readLine()) != null){
                    response.append(line);
                }
                inputStream.close();
                return response.toString();
            }else{
                return Integer.toString(ServerRequest.FAIL);
            }
        }catch (IOException e){
            System.err.println("IOException när du försökte skicka ett ServerRequest!");
            return Integer.toString(ServerRequest.FAIL);
        }finally {
            connection.disconnect();
        }
    }
}
