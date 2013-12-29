package nl.insomnia247.nailbiter.ortholib.util;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONStringer;
import org.json.JSONException;

import java.net.URI;
import org.apache.http.entity.StringEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.util.EntityUtils;
import org.apache.http.impl.client.*;
import org.apache.http.client.*;
import org.apache.http.HttpResponse;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.HttpEntity;

public class Client{
	public String serverURL="http://orthlib.org:1703";
	private HttpClient client=new DefaultHttpClient();
	public String debugString=null;
	private JSONObject credentials=null;
	public Client(){
			client=new DefaultHttpClient();
	}
	public boolean login(String login, String pwd)throws
		IOException,ClientProtocolException,JSONException,OrthoLibException{
			if(login==null || login.isEmpty() || pwd==null||pwd.isEmpty()){
				return false;
			}

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("grant_type","password"));
			nameValuePairs.add(new BasicNameValuePair("client_id","mobileV2"));
			nameValuePairs.add(new BasicNameValuePair("client_secret",KeyRing.getClientSecret()));
			nameValuePairs.add(new BasicNameValuePair("username",login));
			nameValuePairs.add(new BasicNameValuePair("password",pwd));

			HttpPost request=new HttpPost(serverURL+"/oauth/token");
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response=client.execute(request);

			int res=response.getStatusLine().getStatusCode();
			if(res==403){
				return false;
			}
			if(res!=200){
				throw new OrthoLibException("code="+Integer.toString(res)+" "+entityToString(response.getEntity()));
			}

			debugString=entityToString(response.getEntity());
			credentials=new JSONObject(debugString);
			return true;
		}
	public List<Book> getBooks()throws
		IOException,ClientProtocolException,JSONException,OrthoLibException{
			HttpGet request=new HttpGet(serverURL+"/api/books");
			request.addHeader("Authorization","Bearer "+credentials.optString("access_token"));
			HttpResponse response=client.execute(request);

			int res=response.getStatusLine().getStatusCode();
			List<Book> books=new ArrayList<Book>();
			if(res!=200){
				throw new OrthoLibException("bcode="+Integer.toString(res)+" "+entityToString(response.getEntity()));
			}
			//debugString=response.getEntity().getContentEncoding().getValue();
			JSONArray json_books=new JSONArray(EntityUtils.toString(response.getEntity(),"UTF-8"));
			debugString=json_books.get(0).toString();
			for(int i=0;i<json_books.length();i++){
				JSONObject json_book=json_books.optJSONObject(i);
				try{
					books.add(new Book(json_book));
				}
				catch(Exception e){
				}
			}
			return books;
		}
	public String getBook(String id)throws
		IOException,ClientProtocolException,JSONException,OrthoLibException{
			HttpGet request=new HttpGet(serverURL+"/api/book/"+id);
			if(true)throw new OrthoLibException(serverURL+"/api/book/"+id);
			request.addHeader("Authorization","Bearer "+credentials.optString("access_token"));
			HttpResponse response=client.execute(request);

			int res=response.getStatusLine().getStatusCode();
			if(res!=200){
				throw new OrthoLibException("bcode="+Integer.toString(res)+" "+entityToString(response.getEntity()));
			}

			JSONObject json_book=new JSONObject(EntityUtils.toString(response.getEntity(),"UTF-8"));
			return serverURL+"/api/books/download/"+json_book.getString("downloadURL");
	}
	/** Register new user.
	 * @return false if user exists, true if registration was successful 
	 */
	public boolean register(String login, String pwd,String email,String nativeLang)throws
		IOException,ClientProtocolException,JSONException,OrthoLibException{
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("clientid", "mobileV2"));
			nameValuePairs.add(new BasicNameValuePair("username",login));
			nameValuePairs.add(new BasicNameValuePair("password",pwd));
			nameValuePairs.add(new BasicNameValuePair("email",email));
			nameValuePairs.add(new BasicNameValuePair("nativeLanguage",nativeLang));

			HttpPost request=new HttpPost(serverURL+"/api/user/registration");
			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response=client.execute(request);
			int res=response.getStatusLine().getStatusCode();
			debugString="register "+Integer.toString(res);

			if(res==304){
				return false;
			}
			if(res!=200){
				throw new OrthoLibException("error code: "+Integer.toString(res));
			}
			return true;
		}
	private String entityToString(HttpEntity entity)throws IOException{
		InputStream inputStream = entity.getContent();

		java.lang.StringBuilder buf=new java.lang.StringBuilder();
		for(int c=inputStream.read();c!=-1;c=inputStream.read()){
			buf.append((char)c);
		}

		return buf.toString();
	}
	/*private JSONObject exchangeJSONS(JSONObject obj)throws
	IOException,ClientProtocolException,JSONException{
		String s=obj.toString();

		StringEntity entity=new StringEntity(s,"UTF-8");
		entity.setContentType("application/json");
		request.setEntity(entity);
		InputStream inputStream = client.execute(request).getEntity().getContent();

		java.lang.StringBuilder buf=new java.lang.StringBuilder();
		for(int c=inputStream.read();c!=-1;c=inputStream.read()){
			buf.append((char)c);
		}

		s=buf.toString();

		return new JSONObject(s);
	}*/
}
