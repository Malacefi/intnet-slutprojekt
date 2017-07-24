package intnet17.projektet;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Emelie on 2017-03-10.
 */

public abstract class PostRequest extends AsyncTask<String, Integer, Integer> {

    @Override
    protected Integer doInBackground(String... request){
        HttpURLConnection connection = null;
        try{
            URL requestUrl = new URL(ServerRequest.BASE_URL + request[0]);
            connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod(ServerRequest.POST);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("charset", "utf-8");
            connection.setDoOutput(true);

            String requestBody = request[1];

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(requestBody);
            outputStream.flush();
            outputStream.close();

            int responseCode = connection.getResponseCode();

            if(responseCode == HttpURLConnection.HTTP_OK){
                BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String response = inputStream.readLine();
                int responseConverted = Integer.parseInt(response);
                inputStream.close();
                return responseConverted;
            }else{
                return ServerRequest.FAIL;
            }
        }catch (IOException e){
            System.err.println("IOException när du försökte skicka ett ServerRequest!");
            return ServerRequest.FAIL;
        }finally {
            connection.disconnect();
        }
    }
}
