package com.example.video;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

public class MenuActivity extends Activity  {
 private EditText live;
 private EditText dian;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.menu);
	//	this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		live= (EditText)this.findViewById(R.id.live);
		dian= (EditText)this.findViewById(R.id.dian);
	}

 public void enterLive(View v){
	 String str = live.getText().toString();
	 if(str == null || "".equals(str)){
		 Toast.makeText(getApplicationContext(), "tian", Toast.LENGTH_SHORT).show();
		 
	 }else{
		 Toast.makeText(getApplicationContext(), " no kong" + str, Toast.LENGTH_SHORT).show();
		 Intent intent = new Intent();
		 intent.putExtra("path", str);
		 intent.setClass(getApplicationContext(), MainActivity.class);
		 startActivity(intent);
	 }
 }
public void enterDian(View v){
	String str = dian.getText().toString();
	 if(str == null || "".equals(str)){
		 Toast.makeText(getApplicationContext(), "kong", Toast.LENGTH_SHORT).show();
		 
	 }else{
		 Toast.makeText(getApplicationContext(), " no kong" + str, Toast.LENGTH_SHORT).show();
		 
		 Intent intent = new Intent();
		 intent.putExtra("path", str);
		 intent.setClass(getApplicationContext(), VideoActivity.class);
		 startActivity(intent);
	 }
 }

}

 
 
