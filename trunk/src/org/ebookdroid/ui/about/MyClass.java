package org.ebookdroid.ui.about;

import java.io.File;

import org.ebookdroid.R;
import org.ebookdroid.ui.viewer.ViewerActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MyClass extends Activity {
	
	 @Override
	    protected void onCreate(final Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.my_layout);
	        
	 }
	 
	 public void launchPDF(View view) {
		 //Toast.makeText(getApplicationContext(), "button clicked", Toast.LENGTH_SHORT).show();
		 
		 final File file = new File("/sdcard/sg247117.pdf");
		 //final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		 //intent.setClass(getManagedComponent(), ViewerActivity.class);
		 //Intent movetopdfscreen = new Intent(getApplicationContext(),ViewerActivity.class);
		 final Intent movetopdfscreen = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
		 movetopdfscreen.setClass(getApplicationContext(), ViewerActivity.class);
		startActivityForResult(movetopdfscreen,0);
			
		 //RecentActivity ranew=new RecentActivity();
		 //ranew.showDocumentNewReader();
		 
	 }

}
