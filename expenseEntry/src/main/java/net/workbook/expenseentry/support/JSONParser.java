package net.workbook.expenseentry.support;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class JSONParser 
{
	private static InputStream is = null;
    private static JSONObject jObj = null;
    private static JSONArray jArr = null;
    private static String json = "";

    // Google API
    // POST
    public JSONObject getJSONFromUrl(String url) 
    { 
        // Making HTTP request
    	try
    	{
            HttpPost httpPost = new HttpPost(url);
 
            HttpResponse response = LoginClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();           
        } 
    	catch (UnsupportedEncodingException e) 
    	{
            e.printStackTrace();
        } 
    	catch (ClientProtocolException e) 
    	{
            e.printStackTrace();
        }
    	catch (IOException e) 
    	{
            e.printStackTrace();
        }
         
        try 
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) 
            {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } 
        catch (Exception e) 
        {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try 
        {
            jObj = new JSONObject(json);
        } 
        catch (JSONException e) 
        {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
        
        // return JSON String
        return jObj;
    }
    
    // PreLogin 
    // POST
    public String getResponse(String host,String url)
    {
    	// Making HTTP request
        try 
        {
            HttpHost httpHost = new HttpHost(host);
        	HttpPost httpPost = new HttpPost(url);
 
            HttpResponse httpResponse = LoginClient.execute(httpHost,httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();           
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        } 
        catch (ClientProtocolException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
         
        try 
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) 
            {
                sb.append(line);
            }
            is.close();
            json = sb.toString();
        } 
        catch (Exception e) 
        {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;
    }
    
    // ListViews
    // GET
    public JSONArray getJSONArrayFromUrl(String host,String url) throws JSONException 
    {
    	StringBuilder builder = new StringBuilder();
    	HttpHost httpHost = new HttpHost(host);
         HttpGet httpGet = new HttpGet(url);
         try {
        	 HttpResponse response = LoginClient.execute(httpHost,httpGet);
           StatusLine statusLine = response.getStatusLine();
           int statusCode = statusLine.getStatusCode();
           if (statusCode == 200) {
             HttpEntity entity = response.getEntity();
             InputStream content = entity.getContent();
             BufferedReader reader = new BufferedReader(new InputStreamReader(content));
             
             String line;
             while ((line = reader.readLine()) != null) {
               builder.append(line);
             }
           } else {
             Log.e("==>", "Failed to download file");
           }
         } catch (ClientProtocolException e) {
           e.printStackTrace();
         } catch (IOException e) {
           e.printStackTrace();
         }

     // try parse the string to a JSON object
     try 
     {
         jArr = new JSONArray( builder.toString());
         //System.out.println(""+jarray);
     } 
     catch (JSONException e) {
         Log.e("JSON Parser", "Error parsing data " + e.toString());
     }

     // return JSON String
     return jArr;

    }
    
    // Login Session
    // POST
    public JSONObject getLoginSessionFromUrl(String host,String url) 
    {
    	// Making HTTP request
        try 
        {
        	HttpHost httpHost = new HttpHost(host);
            HttpPost httpPost = new HttpPost(url);
            
            HttpResponse httpResponse = LoginClient.execute(httpHost,httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();           
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        } 
        catch (ClientProtocolException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
         
        try 
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) 
            {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } 
        catch (Exception e) 
        {
        	try 
        	{
				is.close();
			} 
        	catch (IOException e1) 
			{
				e1.printStackTrace();
			}
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try 
        {
            jObj = new JSONObject(json);
        } 
        catch (JSONException e) 
        {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        return jObj;
    }
    
    public JSONObject getJSONbyGet(String host,String url)
    {
    	// Making HTTP request
        try 
        {
        	HttpHost httpHost = new HttpHost(host);
            HttpGet httpGet = new HttpGet(url);
            
            HttpResponse httpResponse = LoginClient.execute(httpHost,httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();           
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        } 
        catch (ClientProtocolException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
         
        try 
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) 
            {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
        } 
        catch (Exception e) 
        {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
 
        // try parse the string to a JSON object
        try 
        {
            jObj = new JSONObject(json);
        } 
        catch (JSONException e) 
        {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }
 
        // return JSON String
        return jObj;
    }
    
    public String putResponse(String host,String url)
    {
    	// Making HTTP request
        try 
        {
            HttpHost httpHost = new HttpHost(host);
        	HttpPut httpPut = new HttpPut(url);
 
            HttpResponse httpResponse = LoginClient.execute(httpHost,httpPut);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();           
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        } 
        catch (ClientProtocolException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
         
        try 
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) 
            {
                sb.append(line);
            }
            is.close();
            json = sb.toString();
        } 
        catch (Exception e) 
        {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;
    }
    
    public String deleteResponse(String host,String url)
    {
    	// Making HTTP request
        try 
        {
            HttpHost httpHost = new HttpHost(host);
        	HttpDelete httpDelete = new HttpDelete(url);
 
            HttpResponse httpResponse = LoginClient.execute(httpHost,httpDelete);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();           
        } 
        catch (UnsupportedEncodingException e) 
        {
            e.printStackTrace();
        } 
        catch (ClientProtocolException e) 
        {
            e.printStackTrace();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
         
        try 
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) 
            {
                sb.append(line);
            }
            is.close();
            json = sb.toString();
        } 
        catch (Exception e) 
        {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }
        return json;
    }
    
    public String postBitmap(String host,String url, Bitmap bitmap, int quality)
    {
    	try 
    	{
	    	ByteArrayOutputStream bos = new ByteArrayOutputStream();
	        bitmap.compress(CompressFormat.PNG, quality, bos);
	        byte[] data = bos.toByteArray();
	        HttpHost httpHost = new HttpHost(host);
	        HttpPost postRequest = new HttpPost(url);
	        ByteArrayBody bab = new ByteArrayBody(data, "name.jpg");
	        
	        MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
	        reqEntity.addPart("uploaded", bab);
	        reqEntity.addPart("photoCaption", new StringBody("photo",ContentType.APPLICATION_JSON));
	        postRequest.setEntity(reqEntity.build());
	        
	        HttpResponse response = LoginClient.execute(httpHost,postRequest);
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(
	                response.getEntity().getContent(), "UTF-8"));
	        
	        String sResponse;
	        StringBuilder s = new StringBuilder();
	
	        while ((sResponse = reader.readLine()) != null) 
	        {
	            s = s.append(sResponse);
	        }
	        return s.toString();
    	} 
    	catch (Exception e) 
    	{
	        e.printStackTrace();
	        return null;
    	}
    }
}
