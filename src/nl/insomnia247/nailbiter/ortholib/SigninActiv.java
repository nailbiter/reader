package nl.insomnia247.nailbiter.ortholib;

import android.content.Context;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.ArrayAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.view.ViewGroup;
import android.content.DialogInterface;

import java.util.List;
import android.view.LayoutInflater;

public class SigninActiv extends Activity
{
    private String nativelang=null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
	super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_signin);
	((TextView)findViewById(R.id.signin_dialog_msg_textview)).setText(getIntent().getStringExtra("msg"));

	Spinner spinner = (Spinner) findViewById(R.id.signin_dialog_nativelang_spinner);
	ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	R.array.nativelang_array, android.R.layout.simple_spinner_item);
	adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	spinner.setAdapter(adapter);
	spinner.setOnItemSelectedListener(new NativeLangSelectionListener());
	if(getIntent().getBooleanExtra("register",false)){
		register_mode(null);
	}
    }
    public void submitCallbackLogin(View button){
	setResult(RESULT_OK,new Intent().putExtra("login",((EditText)findViewById(R.id.signin_dialog_login_edittext)).getText().toString())
					.putExtra("pwd",((EditText)findViewById(R.id.signin_dialog_pwd_edittext)).getText().toString()));
	finish();
    }
    private void submitRegister(){
	setResult(RESULT_OK,new Intent().putExtra("login",((EditText)findViewById(R.id.signin_dialog_login_edittext)).getText().toString())
					.putExtra("pwd",((EditText)findViewById(R.id.signin_dialog_pwd_edittext)).getText().toString())
					.putExtra("email",((EditText)findViewById(R.id.signin_dialog_email_edittext)).getText().toString())
					.putExtra("nativeLang",nativelang)
					.putExtra("register",true));
	finish();
    }
    public void register_mode(View button){
	    /*change GUI, 
	     * remove login button, 
	     * change callback for register, 
	     * change name for register+="!"
	     */
	    //? "reg succesful" message?
	findViewById(R.id.signin_dialog_email_edittext).setVisibility(View.VISIBLE);
	findViewById(R.id.signin_dialog_nativelang_spinner).setVisibility(View.VISIBLE);
	findViewById(R.id.signin_dialog_login_button).setVisibility(View.GONE);
	Button reg_button= (Button)findViewById(R.id.signin_dialog_register_button);
	reg_button.setText(reg_button.getText().toString()+"!");
	reg_button.setOnClickListener(new View.OnClickListener(){public void onClick(View v){submitRegister();}});
    }
    private class NativeLangSelectionListener implements AdapterView.OnItemSelectedListener{
	    public void onItemSelected(AdapterView<?> parent, View view, 
		    int pos, long id) {
		    switch(pos){
			    case 0: nativelang="en";break;
			    case 1: nativelang="zh";break;
			    case 2: nativelang="ru";break;
		    }
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	    }
    }
}
