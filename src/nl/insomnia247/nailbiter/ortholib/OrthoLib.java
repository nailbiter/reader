package nl.insomnia247.nailbiter.ortholib;

import java.util.List;
import android.app.Activity;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileOutputStream;
import java.io.BufferedInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.URLConnection;
import java.net.URL;
import android.os.Bundle;
import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.view.View;
import android.widget.ListView;
import android.app.ProgressDialog;
import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import android.os.Bundle;
import android.os.Message;
import android.net.Uri;
import android.content.SharedPreferences;
import android.widget.SimpleExpandableListAdapter;
import android.widget.ExpandableListView;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

import nl.insomnia247.nailbiter.ortholib.util.Client;
import nl.insomnia247.nailbiter.ortholib.util.Book;
/*
 *) no internet

 *) bookshelf (simple)

 *) language, refresh pwd

 * paid_books
 * renew token
 */

public class OrthoLib extends Activity
{
	private List<Book> books=null;
	private void print(String what){
	    ((TextView)(((Activity)this).findViewById(R.id.textview))).setText(what);
	}
	Client client=null;
	protected static final int SIGNIN_ACTIVITY_REQUEST_CODE=1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState){
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    try{
		    client=new Client();
		    
		    SharedPreferences settings = getPreferences(MODE_PRIVATE);
		    String login=settings.getString("login","");
		    String pwd=settings.getString("pwd","");
		    
		    if((login.length()+pwd.length())==0){
			    Intent intent=new Intent(this,SigninActiv.class);
			    intent.putExtra("msg",getString(R.string.sign_in_welcome_msg));
			    startActivityForResult(intent,SIGNIN_ACTIVITY_REQUEST_CODE);
		    }else{
			    tryToLogin(login,pwd);
		    }
	    }
	    catch(Exception e){
		    print("onCreate_e "+e.getMessage());
	    }
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode==SIGNIN_ACTIVITY_REQUEST_CODE){
			try{
				String login=data.getStringExtra("login");
				String pwd=data.getStringExtra("pwd");
				if(data.getBooleanExtra("register",false)){
					if(client.register(login,pwd,data.getStringExtra("email"),data.getStringExtra("nativelang"))){
						tryToLogin(login,pwd);
					}else{
						Intent intent=new Intent(this,SigninActiv.class);
						intent.putExtra("msg",getString(R.string.sign_in_user_exists_msg));
						intent.putExtra("register",true);
						startActivityForResult(intent,SIGNIN_ACTIVITY_REQUEST_CODE);
					}
				}else{
					print("metryToLogin "+login+" "+pwd);
					tryToLogin(login,pwd);
				}
			}
			catch(Exception e){
				print("onActivityResult "+e.getMessage());
			}
		}
	}
	private void listBooks()throws Exception{
	    try{
	    	books=client.getBooks();
	    }
	    catch(Exception e){
		    print("listBooks_e "+e.getMessage());
	    }
	    print(client.debugString);
	    if(books==null){
		    return;
	    }
	    
	    String NAME = "NAME";
	    SimpleExpandableListAdapter mAdapter=null;

	    Map<String,String> categories=new HashMap<String,String>();
	    categories.put("saints",getString(R.string.saints_cat));
	    categories.put("service",getString(R.string.service_cat));
	    categories.put("beginners",getString(R.string.beginners_cat));
	    categories.put("theology",getString(R.string.theology_cat));
	    categories.put("history",getString(R.string.history_cat));
	    categories.put("icons",getString(R.string.iconography_cat));
	    categories.put("others",getString(R.string.others_cat));
	    Map<String,Integer> categories_ordering=new HashMap<String,Integer>();
	    categories_ordering.put("beginners",new Integer(0));
	    categories_ordering.put("saints",new Integer(1));
	    categories_ordering.put("service",new Integer(2));
	    categories_ordering.put("theology",new Integer(3));
	    categories_ordering.put("history",new Integer(4));
	    categories_ordering.put("icons",new Integer(5));
	    categories_ordering.put("others",new Integer(6));
	    final ArrayList<ArrayList<Book>> books_in_cats=new ArrayList<ArrayList<Book>>(categories.size());
	    for(int i=0;i<categories.size();i++){
		    books_in_cats.add(new ArrayList<Book>());
	    }
	    for(Book book:books){
		    try{
		    	books_in_cats.get(categories_ordering.get(book.category).intValue()).add(book);
		    }
		    catch(Exception e){
			    throw new Exception(book.category);
			    /*throw new Exception(book.category+" "+Integer.toString(categories_ordering.get(book.category).intValue())+" "+
					    Integer.toString(books_in_cats.size())+" "+Integer.toString(categories.size()));*/
		    }
	    }
	    final ArrayList<Integer> active_categories=new ArrayList<Integer>();
	    for(int i=0;i<books_in_cats.size();i++){
		    if(books_in_cats.get(i).size()>0){
			    active_categories.add(new Integer(i));
		    }
	    }
	    String[] group = new String[active_categories.size()];
	    String[][] child =new String[active_categories.size()][];
	    for(int i=0;i<group.length;i++){
		    List<Book> cat=books_in_cats.get(active_categories.get(i).intValue());
		    group[i]=categories.get(cat.get(0).category);
		    child[i]=new String[cat.size()];
		    for(int j=0;j<cat.size();j++){
			    child[i][j]=cat.get(j).title;
		    }
	    }


            List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
            List<List<Map<String, String>>> childData = new ArrayList<List<Map<String, String>>>();
            for (int i = 0; i < group.length; i++) {
                Map<String, String> curGroupMap = new HashMap<String, String>();
                groupData.add(curGroupMap);
                curGroupMap.put(NAME, group[i]);
    
                List<Map<String, String>> children = new ArrayList<Map<String, String>>();
                for (int j = 0; j < child[i].length; j++) {
                    Map<String, String> curChildMap = new HashMap<String, String>();
                    children.add(curChildMap);
                    curChildMap.put(NAME, child[i][j]);
                }
                childData.add(children);
            }
    
            // Set up our adapter
            mAdapter = new SimpleExpandableListAdapter(this, groupData,
                    android.R.layout.simple_expandable_list_item_1,
                    new String[] { NAME }, new int[] { android.R.id.text1 },
                    childData, android.R.layout.simple_expandable_list_item_2,
                    new String[] { NAME }, new int[] { android.R.id.text1 });
	    
	    ((ExpandableListView)(((Activity)this).findViewById(R.id.main_books_explistview)))
		    .setAdapter(mAdapter);
	    ((ExpandableListView)(((Activity)this).findViewById(R.id.main_books_explistview)))
		    .setOnChildClickListener(new ExpandableListView.OnChildClickListener(){
			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id){
				Book book=books_in_cats.get(active_categories.get(groupPosition).intValue()).get(childPosition);
				String url=null;
				try{
					url=client.getBook(book.id);
					print(url);
					doDownload(url,book.title);
				}
				catch(Exception e){
					print("onClick_e "+e.getMessage());
				}
				return true;
			}
		    });
	}

private void openPdfIntent(String path) {
    try
    {
      final Intent intent = new Intent(OrthoLib.this, Second.class);
      intent.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, path);
      startActivity(intent);
    }
    catch (Exception e) 
    {
      e.printStackTrace();
    }
}
protected void doDownload(final String urlLink, final String fileName) {
	final ProgressDialog progress = new ProgressDialog(this);
	progress.setTitle("Book Loads");
	progress.setMessage("Loading...");
	progress.setCancelable(false);
	progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	progress.show();

        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                progress.dismiss();
		print(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp_books/"+fileName);

		File file = new File(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp_books/"+fileName);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/pdf");
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
		//openPdfIntent(android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmp_books/"+fileName);
            }
        };

	new Thread(new Runnable() {public void run() {
		File root = android.os.Environment.getExternalStorageDirectory();
		File dir = new File (root.getAbsolutePath() + "/tmp_books/");
		if(dir.exists()==false) {
			dir.mkdirs();
		}

	    	try {
		    URL url = new URL(urlLink);
		    URLConnection connection = url.openConnection();
		    connection.connect();
		    int fileLength = connection.getContentLength();
		    progress.setMax(fileLength);

		    // download the file
		    InputStream input = new BufferedInputStream(url.openStream());
		    OutputStream output = new FileOutputStream(dir+"/"+fileName);

		    byte data[] = new byte[1024];
		    long total = 0;
		    int count;
		    while ((count = input.read(data)) != -1) {
			total += count;
			progress.setProgress((int)total);
			output.write(data, 0, count);
		    }

		    output.flush();
		    output.close();
		    input.close();
		    handler.sendEmptyMessage(0);
		} catch (Exception e) {
		     e.printStackTrace();
		}
                }}).start();      
}
private void tryToLogin(String login,String pwd){
	try{
		if(client.login(login,pwd)){
			print("logged in "+login+" "+pwd);
			SharedPreferences.Editor preferences_editor=getPreferences(MODE_PRIVATE).edit();
			preferences_editor.putString("login",login);
			preferences_editor.putString("pwd",pwd);
			preferences_editor.commit();
			listBooks();
		}else{
			print("fail "+login+" "+pwd);
			Intent intent=new Intent(this,SigninActiv.class);
			intent.putExtra("msg",getString(R.string.incor_login_or_pwd_msg));
			startActivityForResult(intent,SIGNIN_ACTIVITY_REQUEST_CODE);
		}
	}
	catch(java.io.IOException e){
		print("tryToLogin_nete "+e.getMessage());
		//showDialog(NO_CONNECTION_DIALOG);
	}
	catch(Exception e){
		//print("tryToLogin_e "+e.getMessage()+"d:"+client.debugString);
		print("tryToLogin_e "+e.getMessage()+" "+e.getStackTrace()[0].toString()+" "
			+e.getStackTrace()[1].toString()+" "
			+e.getStackTrace()[2].toString()+" "
			+e.getStackTrace()[3].toString()
				);
	}
}
}
