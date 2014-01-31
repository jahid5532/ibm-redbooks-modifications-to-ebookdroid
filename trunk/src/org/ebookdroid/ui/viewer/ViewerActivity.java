package org.ebookdroid.ui.viewer;

import java.io.File;

import org.ebookdroid.EBookDroidApp;
import org.ebookdroid.common.bitmaps.BitmapManager;
import org.ebookdroid.common.bitmaps.ByteBufferManager;
import org.ebookdroid.common.settings.AppSettings;
import org.ebookdroid.common.settings.SettingsManager;
import org.ebookdroid.common.settings.books.Bookmark;
import org.ebookdroid.common.settings.types.ToastPosition;
import org.ebookdroid.common.touch.TouchManagerView;
import org.ebookdroid.core.models.DocumentModel;
import org.ebookdroid.ui.viewer.stubs.ActivityControllerStub;
import org.ebookdroid.ui.viewer.stubs.ViewStub;
import org.ebookdroid.ui.viewer.viewers.GLView;
import org.ebookdroid.ui.viewer.views.ManualCropView;
import org.ebookdroid.ui.viewer.views.PageViewZoomControls;
import org.ebookdroid.ui.viewer.views.SearchControls;
import org.emdev.common.android.AndroidVersion;
import org.emdev.ui.AbstractActionActivity;
import org.emdev.ui.actions.ActionEx;
import org.emdev.ui.actions.ActionMenuHelper;
import org.emdev.ui.gl.GLConfiguration;
import org.emdev.ui.uimanager.IUIManager;
import org.emdev.utils.LayoutUtils;
import org.emdev.utils.LengthUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import org.ebookdroid.R;
//import android.view.ViewGroup.LayoutParams;

public class ViewerActivity extends AbstractActionActivity<ViewerActivity, ViewerActivityController> {

	ActionEx action;
	
    public static final DisplayMetrics DM = new DisplayMetrics();

    static IView view;

    private Toast pageNumberToast;

    private Toast zoomToast;

    public static PageViewZoomControls zoomControls;

    public static SearchControls searchControls;

    private FrameLayout frameLayout;

    private TouchManagerView touchView;

    private boolean menuClosedCalled;

    private ManualCropView cropControls;
    public static int leybrdcounter=0;
    
    SharedPreferences mPrefs;
	SharedPreferences.Editor editor;
    //static public LinearLayout onelay;
    
    static TextView pagenumbercustomlabel;
    public static SeekBar livedraggingslider;
    Uri data=null;
    public static RelativeLayout lytContainer;
    public static boolean fromcancelbutton=false;
    public static int heightoftheheader=0;
    public String searchtextboxofpdfreaderstr=null;
    
    static public boolean touchdecisionbox=false;
    Intent intent=null;
    static boolean isTempFolder=false;
    /**
     * Instantiates a new base viewer activity.
     */
    public ViewerActivity() {
        super(false, ON_CREATE, ON_RESUME, ON_PAUSE, ON_DESTROY);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.emdev.ui.AbstractActionActivity#createController()
     */
    @Override
    protected ViewerActivityController createController() {
        return new ViewerActivityController(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onNewIntent(android.content.Intent)
     */
    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        /*if (LCTX.isDebugEnabled()) {
            LCTX.d("onNewIntent(): " + intent);
        }*/
    }
    
    
    
    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event) 
    {
        if (keyCode == KeyEvent.KEYCODE_BACK) 
        {
        	if(ViewerActivity.searchControls.getVisibility()==0)
	      	{
        		/*
        		//ViewerActivity.lytContainer.setAlpha(0.4f);
        		if (Build.VERSION.SDK_INT < 11)
        		//if(true)
                {
        			AlphaAnimation alpha = new AlphaAnimation(0.4f, 0.4f);
                    alpha.setDuration(0); 
                    alpha.setFillAfter(true);
                    ViewerActivity.lytContainer.startAnimation(alpha);
                }
        		else
        		{
        			ViewerActivity.lytContainer.setAlpha(0.4f);
        		}
        		*/
	      	}
	      	else
	      	{
	      		/*
	      		//ViewerActivity.lytContainer.setAlpha(1.0f);
	      		if (Build.VERSION.SDK_INT < 11)
	      		//if(true)
                {
        			AlphaAnimation alpha = new AlphaAnimation(1.0f, 1.0f);
                    alpha.setDuration(0); 
                    alpha.setFillAfter(true);
                    ViewerActivity.lytContainer.startAnimation(alpha);
                }
        		else
        		{
        			ViewerActivity.lytContainer.setAlpha(1.0f);
        		}
        		*/
	      	}
        	return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.emdev.ui.AbstractActionActivity#onCreateImpl(android.os.Bundle)
     */
    @Override
    protected void onCreateImpl(final Bundle savedInstanceState) {
        getWindowManager().getDefaultDisplay().getMetrics(DM);
        //LCTX.i("XDPI=" + DM.xdpi + ", YDPI=" + DM.ydpi);

        frameLayout = new FrameLayout(this);

        view = ViewStub.STUB;
        
        intent = getIntent();

        try {
            GLConfiguration.checkConfiguration();

            view = new GLView(getController());
            //this.registerForContextMenu(view.getView());

            LayoutUtils.fillInParent(frameLayout, view.getView());
            
            
            data = intent.getData();
            String fullpath =  data.getPath();
    		if(fullpath.contains("temp"))
    		    isTempFolder = true;
    		else
    		    isTempFolder = false;
    		data=null;
    		fullpath=null;
            
            /*
            onelay= new LinearLayout(this);
            
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER_HORIZONTAL|Gravity.TOP;
            onelay.setLayoutParams(params);
            onelay.setBackgroundColor(Color.parseColor("#C3C3C3"));
            
            onelay.setOrientation(LinearLayout.HORIZONTAL);
            //onelay.setWeightSum(1);
            onelay.setPadding(5,5,5,5);
            onelay.setVisibility(1);
            
            
          //Outline
            Button back_button = new Button(this);
            //back_button.setId(2131427497);
            back_button.setBackgroundResource(R.drawable.back_reader);
            back_button.setOnClickListener(myhandler1);
            //back_button.setWidth(41);
            //back_button.setHeight(37);
            
            back_button.setWidth(LayoutParams.WRAP_CONTENT);
            back_button.setHeight(LayoutParams.WRAP_CONTENT);
            
            back_button.setOnClickListener(new Button.OnClickListener() 
            {
                public void onClick(View v) 
                {
                	action = getController().getOrCreateAction(R.id.dummyid);
                    if (action.getMethod().isValid()) 
                    {
                        ActionMenuHelper.setActionParameters(null, action);
                        action.run();
                        livedraggingslider.setMax(ViewerActivityController.maxpagenumberis+1);
                        pagenumbercustomlabel.setText(Integer.toString(ViewerActivityController.curpagenumberis+1)+"/"+Integer.toString(ViewerActivityController.maxpagenumberis+1));
                    }
                }
            });
            
            //back_button.setWidth(0);
            //back_button.setGravity(Gravity.CENTER);
            
            
            //Outline
            Button table_of_contents_button = new Button(this);
            table_of_contents_button.setId(2131427497);
           // myButton1.setText("O");
            table_of_contents_button.setBackgroundResource(R.drawable.toc);
            table_of_contents_button.setOnClickListener(myhandler1);
            //table_of_contents_button.setWidth(41);
            //table_of_contents_button.setHeight(37);
            
            table_of_contents_button.setWidth(LayoutParams.WRAP_CONTENT);
            table_of_contents_button.setHeight(LayoutParams.WRAP_CONTENT);
            
            //myButton1.setWidth(0);
            //myButton1.setGravity(Gravity.CENTER);
            
            
            //Search
            Button search_button = new Button(this);
            search_button.setId(2131427496);
            //myButton2.setText("S");
            search_button.setBackgroundResource(R.drawable.magnifyingglass);
            search_button.setOnClickListener(myhandler1);
            //search_button.setWidth(41);
            //search_button.setHeight(37);
            
            search_button.setWidth(LayoutParams.WRAP_CONTENT);
            search_button.setHeight(LayoutParams.WRAP_CONTENT);
            
            //myButton2.setWidth(0);
            //myButton2.setGravity(Gravity.CENTER);
            
            
            //Bookmark
            Button bookmark_button = new Button(this);
            bookmark_button.setId(2131427494);
            //myButton3.setText("B");
            bookmark_button.setBackgroundResource(R.drawable.bookmark);
            bookmark_button.setOnClickListener(myhandler1);
            //bookmark_button.setWidth(41);
            //bookmark_button.setHeight(37);
            
            bookmark_button.setWidth(LayoutParams.WRAP_CONTENT);
            bookmark_button.setHeight(LayoutParams.WRAP_CONTENT);
            
            //myButton3.setWidth(0);
            //myButton3.setGravity(Gravity.CENTER);
            
            
            //Goto
            Button goto_button = new Button(this);
            goto_button.setId(2131427495);
            //goto_button.setText("Go");
            goto_button.setBackgroundResource(R.drawable.decreasefont);
            goto_button.setOnClickListener(myhandler1);
            //goto_button.setWidth(41);
            //goto_button.setHeight(37);
            
            goto_button.setWidth(LayoutParams.WRAP_CONTENT);
            goto_button.setHeight(LayoutParams.WRAP_CONTENT);
            
            //myButton4.setWidth(0);
            //myButton4.setGravity(Gravity.CENTER);
            
            
            //Dayandnight
            Button daynight_button = new Button(this);
            daynight_button.setId(2131427504);
            //myButton5.setText("D");
            daynight_button.setBackgroundResource(R.drawable.daymode);
            daynight_button.setOnClickListener(myhandler1);
            //daynight_button.setWidth(41);
            //daynight_button.setHeight(37);
            
            daynight_button.setWidth(LayoutParams.WRAP_CONTENT);
            daynight_button.setHeight(LayoutParams.WRAP_CONTENT);
            
            //myButton5.setWidth(0);
            //myButton5.setGravity(Gravity.CENTER);
            
            //Zoom
            Button zoom_button = new Button(this);
            zoom_button.setId(2131427498);
            //myButton6.setText("Z");
            zoom_button.setBackgroundResource(R.drawable.increasefont);
            zoom_button.setOnClickListener(myhandler1);
            //zoom_button.setWidth(41);
            //zoom_button.setHeight(37);
            
            zoom_button.setWidth(LayoutParams.WRAP_CONTENT);
            zoom_button.setHeight(LayoutParams.WRAP_CONTENT);
            
            //myButton6.setWidth(0);
            //myButton6.setGravity(Gravity.CENTER);
            
            onelay.addView(back_button);
            onelay.addView(table_of_contents_button);
            onelay.addView(bookmark_button);
            onelay.addView(zoom_button);
            onelay.addView(daynight_button);
            onelay.addView(search_button);
            onelay.addView(goto_button);
            */
            
            //frameLayout.addView(onelay);
            
            
            
            frameLayout.addView(view.getView());
            frameLayout.addView(getZoomControls());
           // frameLayout.addView(getManualCropControls());
            //frameLayout.addView(getSearchControls());
            //frameLayout.addView(getTouchView());
            
            
           // frameLayout.addView(onelay);
            
            
           /* View v;
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.id.newheaderid, null);
            frameLayout.addView(v);*/
            
            

            
            //LinearLayout llay=new LinearLayout(this);


            lytContainer = (RelativeLayout) View.inflate(this, R.layout.mycustomlayout, null);
            pagenumbercustomlabel=(TextView)lytContainer.findViewById(R.id.pagenumbercustomlabel);
            livedraggingslider=(SeekBar)lytContainer.findViewById(R.id.navigation_slider);
            livedraggingslider.setOnSeekBarChangeListener( new OnSeekBarChangeListener()
            {
	            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
	            {
	            	
	            	pagenumbercustomlabel.setText(""+((progress<=0)?1:progress)+"/"+Integer.toString(ViewerActivityController.maxpagenumberis+1)+"");
	            	/*
	            	try
	            	{
	            		pagenumbercustomlabel.setText(""+Integer.toString(progress)+"/"+Integer.toString(ViewerActivityController.maxpagenumberis+1)+"");
	            		ViewerActivityController.base.jumpToPage(progress, 0.0f, 0.0f,AppSettings.current().storeGotoHistory);
	            	}
	            	catch(Exception e)
	            	{
	            		action = getController().getOrCreateAction(R.id.dummyid);
	                    if (action.getMethod().isValid()) 
	                    {
	                        ActionMenuHelper.setActionParameters(null, action);
	                        action.run();
	                        
	                        livedraggingslider.setMax(ViewerActivityController.maxpagenumberis+1);
	                        pagenumbercustomlabel.setText(Integer.toString(ViewerActivityController.curpagenumberis+1)+"/"+Integer.toString(ViewerActivityController.maxpagenumberis+1));
	                    }
	            	}
	            	*/
	            }
	            public void onStartTrackingTouch(SeekBar seekBar)
	            {
	            	
	            }
	            public void onStopTrackingTouch(SeekBar seekBar)
	            {
	            	//pagenumbercustomlabel.setText(""+Integer.toString(seekBar.getProgress())+"/"+Integer.toString(ViewerActivityController.maxpagenumberis+1)+"");
            		ViewerActivityController.base.jumpToPage(seekBar.getProgress(), 0.0f, 0.0f,AppSettings.current().storeGotoHistory);
	            }
            });
            
            
            //lytContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

            
            frameLayout.addView(lytContainer);
           
                     
            
            
            
            //frameLayout.addView(relativelayout);
            
           /* 
            View v;
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(onelay.getId(), null);
            frameLayout.addView(v);
            */
            
            
            
            //LayoutParams sasa=new LayoutParams(10, 12, Gravity.BOTTOM);
            
           // frameLayout.set
            //frameLayout.addView(getSearchControls(),)
            //frameLayout.addView(getSearchControls(),new LayoutParams(320, 40, Gravity.BOTTOM));
            //frameLayout.addView(getSearchControls());
            
            //LinearLayout hightobj=(LinearLayout)findViewById(R.id.newheaderid);
            final LinearLayout tv = (LinearLayout)lytContainer.findViewById(R.id.newheaderid);
            ViewTreeObserver vto = tv.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() 
            {
	            public void onGlobalLayout() 
	            {
                    ViewGroup.MarginLayoutParams vlp = (MarginLayoutParams) tv.getLayoutParams();
                    int btnsize =tv.getMeasuredHeight()+vlp.topMargin;
                    LayoutParams paramsa = new LayoutParams( RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                    paramsa.setMargins(0, btnsize, 0, 0);
                    heightoftheheader=btnsize;
                    frameLayout.addView(getSearchControls(), paramsa);
                    ViewTreeObserver obs = tv.getViewTreeObserver();
                    obs.removeGlobalOnLayoutListener(this);
	            }
            });
            
            
            //LayoutParams params = new LayoutParams( RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            //params.setMargins(0, 113, 0, 0);
            
            //params.
            
            
            
            
            

            
            /*
            RelativeLayout relativelayout = new RelativeLayout(this);
            //relativelayout.setPadding(20, 0, 20, 0);
            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            relativelayout.addView(livedraggingslider, relativeParams);
            relativelayout.addView(pagenumbercustomlabel, relativeParams);
            
            
            frameLayout.addView(relativelayout);
            */
            
           // onelay=(LinearLayout)findViewById(R.id.onoroffhearder);
            
            /*
            action = getController().getOrCreateAction(R.id.dummyid);
            if (action.getMethod().isValid()) 
            {
                ActionMenuHelper.setActionParameters(null, action);
                action.run();
                
                livedraggingslider.setMax(ViewerActivityController.maxpagenumberis+1);
                pagenumbercustomlabel.setText(Integer.toString(ViewerActivityController.curpagenumberis+1)+"/"+Integer.toString(ViewerActivityController.maxpagenumberis+1));
            }
           */
            
        } 
        //catch (final Throwable th) 
        catch (Exception e) 
        {
        	e.printStackTrace();
        	//Log.i("Exception raised ====> ",e.getMessage()+"<======");
        	
            /*final ActionDialogBuilder builder = new ActionDialogBuilder(this, getController());
            builder.setTitle(R.string.error_dlg_title);
            builder.setMessage(th.getMessage());
            builder.setPositiveButton(R.string.error_close, R.id.mainmenu_close);
            builder.show();*/
        }
        setContentView(frameLayout);
        
        ///addmethodforseekbar();
    }
    
    
    public void makeabackevent(View v)
    {
    	
        
        
        
    	if(isTempFolder) 
    	{
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewerActivity.this);
            //alertDialogBuilder.setTitle("Warning!");
            alertDialogBuilder.setMessage("Are you sure you want to exit?").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog,int id) 
                  {
                      //Delete publication from temp folder
                      final File fav = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Redbooks"+ "/temp");
                      File[] files=fav.listFiles();
                      for (int i=0;i<files.length;i++)
                      files[i].delete();
                      dialog.cancel();
                      
                      view.onDestroy();
                      EBookDroidApp.instance.onLowMemory();
                      EBookDroidApp.instance.onActivityClose(true);
                      DocumentModel documentModel = ActivityControllerStub.DM_STUB;
                      if (documentModel != null)
                      documentModel.recycle();

                      SettingsManager.removeListener(this);
                      BitmapManager.clear("on finish");
                      ByteBufferManager.clear("on finish");

                      EBookDroidApp.onActivityClose(true);
                      System.exit(0);
                  }
              })
              .setNegativeButton("Cancel",new DialogInterface.OnClickListener() 
              {
                  public void onClick(DialogInterface dialog,int id) 
                  {
                      // if this button is clicked, just close
                      // the dialog box and do nothing
                      dialog.cancel();
                  }
              });
              AlertDialog alertDialog = alertDialogBuilder.create();
              alertDialog.setCanceledOnTouchOutside(true);
              alertDialog.show();
        }
    	else
    	{
    		//Delete publication from temp folder
            final File fav = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Redbooks"+ "/temp");
            File[] files=fav.listFiles();
            if(files!=null && files.length>0)
            {
            	for (int i=0;i<files.length;i++)
            		files[i].delete();
            }
            
            view.onDestroy();
            EBookDroidApp.instance.onLowMemory();
            EBookDroidApp.instance.onActivityClose(true);
            DocumentModel documentModel = ActivityControllerStub.DM_STUB;
            if (documentModel != null)
            documentModel.recycle();

            SettingsManager.removeListener(this);
            BitmapManager.clear("on finish");
            ByteBufferManager.clear("on finish");

            EBookDroidApp.onActivityClose(true);
            System.exit(0);
    	}
    	
        
       
    }
    

    public void touchEvetMethod(View v)
    {
    	
    }
        public void myhandler1(View v) 
        {
          // final int actionId = item.getItemId();
          if(v.getId()==R.id.mainmenu_nightmode)
          {
        	  ImageButton b=(ImageButton)v;
        	  if("plainnight".equalsIgnoreCase((b.getTag()+"")))
        	  {
        		  b.setBackgroundResource(R.drawable.daymode);
        		  b.setTag("plainday");
        	  }
        	  else
        	  {
        		  b.setBackgroundResource(R.drawable.nightmode);
        		  b.setTag("plainnight");
        	  }
          }
          else if(v.getId()==R.id.mainmenu_search)
          {
        	  ViewerActivity.searchControls.clearAnimation();
        	  ViewerActivity.lytContainer.clearAnimation();
        	  ViewerActivity.zoomControls.clearAnimation();
        	  if(ViewerActivity.searchControls.getVisibility()==0)
        	  {
        		  //ViewerActivity.searchControls.setAlpha(1.0f);
        		  
        		  /*
        		  //ViewerActivity.searchControls.setAlpha(1.0f);
        		  //ViewerActivity.lytContainer.setAlpha(1.0f);
        		  if (Build.VERSION.SDK_INT < 11)
        		  //if(true)
                  {
          			  AlphaAnimation alpha = new AlphaAnimation(1.0f, 1.0f);
                      alpha.setDuration(0); 
                      alpha.setFillAfter(true);
                      ViewerActivity.searchControls.startAnimation(alpha);
                      ViewerActivity.lytContainer.startAnimation(alpha);
	              }
	          	  else
	          	  {
	          		  ViewerActivity.searchControls.setAlpha(1.0f);
	        		  ViewerActivity.lytContainer.setAlpha(1.0f);
	          	  }
	          	  */
        	  }
        	  else
        	  {
        		  //ViewerActivity.searchControls.setAlpha(1.0f);
        		  
        		  /*
        		  //ViewerActivity.searchControls.setAlpha(1.0f);
        		  //ViewerActivity.lytContainer.setAlpha(0.4f);
        		  if (Build.VERSION.SDK_INT < 11)
        		  //if(true)
                  {
          			  AlphaAnimation alpha = new AlphaAnimation(1.0f, 1.0f);
                      alpha.setDuration(0); 
                      alpha.setFillAfter(true);
                      ViewerActivity.searchControls.startAnimation(alpha);
                      
                      alpha = new AlphaAnimation(0.4f, 0.4f);
                      alpha.setDuration(0); 
                      alpha.setFillAfter(true);
                      ViewerActivity.lytContainer.startAnimation(alpha);
	              }
	          	  else
	          	  {
	          		  ViewerActivity.searchControls.setAlpha(1.0f);
	        		  ViewerActivity.lytContainer.setAlpha(0.4f);
	          	  }
	          	  */
        	  }
          }
      	  int actionId=v.getId();
          final ActionEx action = getController().getOrCreateAction(actionId);
          if (action.getMethod().isValid()) 
          {
              ActionMenuHelper.setActionParameters(null, action);
              action.run();
          }
        }
    
   
    
    
    /*View.OnClickListener myhandler1 = new View.OnClickListener() 
    {
        public void onClick(View v) 
        {
          Log.i("Its first button ===============> ","Its first button ===============> ");
      	  Log.i("its a nerw method","-----------------------------------4");
          // final int actionId = item.getItemId();
      	  int actionId=v.getId();
          Log.i("Action id inn newmethod is  ====> ", (actionId+""));
          final ActionEx action = getController().getOrCreateAction(actionId);
          
          
          
          Log.i("Action method is new mentodis  ====> ",(action.getMethod()+""));
          
         
          
          
          if (action.getMethod().isValid()) 
          {
        	  
        	  
        	  
              ActionMenuHelper.setActionParameters(null, action);
              action.run();
          }
        }
    };*/
    
    View.OnClickListener myhandler2 = new View.OnClickListener() 
    {
        public void onClick(View v) 
        {
        	
        }
    };
   
    View.OnClickListener myhandler3 = new View.OnClickListener() 
    {
        public void onClick(View v) 
        {
        	
        }
    };
    
    View.OnClickListener myhandler4 = new View.OnClickListener() 
    {
        public void onClick(View v) 
        {
        	
        }
    };
    
    View.OnClickListener myhandler5 = new View.OnClickListener() 
    {
        public void onClick(View v) 
        {
        	
        }
    };
    

    /**
     * {@inheritDoc}
     * 
     * @see org.emdev.ui.AbstractActionActivity#onResumeImpl()
     */
    
    
    
        
    
    @Override
    protected void onResumeImpl() {
        IUIManager.instance.onResume(this);
        
        data = intent.getData();
        String fullpath =  data.getPath();
		if(fullpath.contains("temp"))
		    isTempFolder = true;
		else
		    isTempFolder = false;
		data=null;
		fullpath=null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.emdev.ui.AbstractActionActivity#onPauseImpl(boolean)
     */
    @Override
    protected void onPauseImpl(final boolean finishing) {
        IUIManager.instance.onPause(this);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.emdev.ui.AbstractActionActivity#onDestroyImpl(boolean)
     */
    @Override
    protected void onDestroyImpl(final boolean finishing) {
        view.onDestroy();
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onWindowFocusChanged(boolean)
     */
    @Override
    public void onWindowFocusChanged(final boolean hasFocus) {
        if (hasFocus && this.view != null) {
            //IUIManager.instance.setFullScreenMode(this, this.view.getView(), AppSettings.current().fullScreen);
        }
    }

    public TouchManagerView getTouchView() {
        if (touchView == null) {
            touchView = new TouchManagerView(getController());
        }
        return touchView;
    }

    public void currentPageChanged(final String pageText, final String bookTitle) {
    	
    	try
    	{
    		action = getController().getOrCreateAction(R.id.dummyid);
            if (action.getMethod().isValid()) 
            {
                ActionMenuHelper.setActionParameters(null, action);
                action.run();
            }
    	}
    	catch(Exception e)
    	{
    		
    	}
    	
        if (LengthUtils.isEmpty(pageText)) {
            return;
        }

        final AppSettings app = AppSettings.current();
        if (IUIManager.instance.isTitleVisible(this) && app.pageInTitle) {
        	pagenumbercustomlabel.setText(pageText);
            //getWindow().setTitle("(" + pageText + ") " + bookTitle);
            return;
        }

        if (app.pageNumberToastPosition == ToastPosition.Invisible) {
            return;
        }
        if (pageNumberToast != null) {
            pageNumberToast.setText(pageText);
        } else {
            pageNumberToast = Toast.makeText(this, pageText, Toast.LENGTH_SHORT);
        }

        pageNumberToast.setGravity(app.pageNumberToastPosition.position, 0, 0);
        pageNumberToast.show();
        
        
    }

    public void zoomChanged(final float zoom) {
    	//Murali change new
    	/*
        if (getZoomControls().isShown()) {
            return;
        }

        final AppSettings app = AppSettings.current();

        if (app.zoomToastPosition == ToastPosition.Invisible) {
            return;
        }

        final String zoomText = String.format("%.2f", zoom) + "x";

        if (zoomToast != null) {
            zoomToast.setText(zoomText);
        } else {
            zoomToast = Toast.makeText(this, zoomText, Toast.LENGTH_SHORT);
        }

        zoomToast.setGravity(app.zoomToastPosition.position, 0, 0);
        zoomToast.show();
        */
    }

    public PageViewZoomControls getZoomControls() {
        if (zoomControls == null) {
            zoomControls = new PageViewZoomControls(this, getController().getZoomModel());
            zoomControls.setGravity(Gravity.RIGHT | Gravity.CENTER);
        }
        return zoomControls;
    }

    public SearchControls getSearchControls() {
        if (searchControls == null) {
            searchControls = new SearchControls(this);
            //searchControls.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            searchControls.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.CENTER_VERTICAL);
            //searchControls.
           // searchControls.
            
        }
        return searchControls;
    }

    public ManualCropView getManualCropControls() {
        if (cropControls == null) {
            cropControls = new ManualCropView(getController());
        }
        return cropControls;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View,
     *      android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
        menu.clear();
        menu.setHeaderTitle(R.string.app_name);
        menu.setHeaderIcon(R.drawable.application_icon);
        final MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu_context, menu);
        updateMenuItems(menu);
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        menu.clear();

        final MenuInflater inflater = getMenuInflater();

        if (hasNormalMenu()) {
        	//Log.i("If looooppppppppppppppppppppppppppppp","If looooppppppppppppppppppppppppppppp");
            //inflater.inflate(R.menu.mainmenu, menu);
        } else {
        	//Log.i("else looooppppppppppppppppppppppppppppp","esle looooppppppppppppppppppppppppppppp");
            //inflater.inflate(R.menu.mainmenu_context, menu);
        }

        return true;
    }

    protected boolean hasNormalMenu() {
        return AndroidVersion.lessThan4x || IUIManager.instance.isTabletUi(this) || AppSettings.current().showTitle;
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onMenuOpened(int, android.view.Menu)
     */
    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        view.changeLayoutLock(true);
        IUIManager.instance.onMenuOpened(this);
        return super.onMenuOpened(featureId, menu);
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.emdev.ui.AbstractActionActivity#updateMenuItems(android.view.Menu)
     */
    @Override
    protected void updateMenuItems(final Menu menu) 
    {
    	/*
        final AppSettings as = AppSettings.current();
        ActionMenuHelper.setMenuItemChecked(menu, as.fullScreen, R.id.mainmenu_fullscreen);

        if (!AndroidVersion.lessThan3x) 
        {
            ActionMenuHelper.setMenuItemChecked(menu, as.showTitle, R.id.mainmenu_showtitle);
        } 
        else 
        {
            ActionMenuHelper.setMenuItemVisible(menu, false, R.id.mainmenu_showtitle);
        }

        ActionMenuHelper.setMenuItemChecked(menu, getZoomControls().getVisibility() == View.VISIBLE, R.id.mainmenu_zoom);

        final BookSettings bs = getController().getBookSettings();
        if (bs == null) 
        {
            return;
        }

        ActionMenuHelper.setMenuItemChecked(menu, bs.rotation == BookRotationType.PORTRAIT,R.id.mainmenu_force_portrait);
        ActionMenuHelper.setMenuItemChecked(menu, bs.rotation == BookRotationType.LANDSCAPE,R.id.mainmenu_force_landscape);
        ActionMenuHelper.setMenuItemChecked(menu, bs.nightMode, R.id.mainmenu_nightmode);
        ActionMenuHelper.setMenuItemChecked(menu, bs.cropPages, R.id.mainmenu_croppages);
        ActionMenuHelper.setMenuItemChecked(menu, bs.splitPages, R.id.mainmenu_splitpages,R.drawable.viewer_menu_split_pages, R.drawable.viewer_menu_split_pages_off);

        final DecodeService ds = getController().getDecodeService();

        final boolean cropSupported = ds.isFeatureSupported(CodecFeatures.FEATURE_CROP_SUPPORT);
        ActionMenuHelper.setMenuItemVisible(menu, cropSupported, R.id.mainmenu_croppages);
        ActionMenuHelper.setMenuItemVisible(menu, cropSupported, R.id.mainmenu_crop);

        final boolean splitSupported = ds.isFeatureSupported(CodecFeatures.FEATURE_SPLIT_SUPPORT);
        ActionMenuHelper.setMenuItemVisible(menu, splitSupported, R.id.mainmenu_splitpages);

        final MenuItem navMenu = menu.findItem(R.id.mainmenu_nav_menu);
        if (navMenu != null) 
        {
            final SubMenu subMenu = navMenu.getSubMenu();
            subMenu.removeGroup(R.id.actions_goToBookmarkGroup);
            if (AppSettings.current().showBookmarksInMenu && LengthUtils.isNotEmpty(bs.bookmarks)) 
            {
                for (final Bookmark b : bs.bookmarks) 
                {
                    addBookmarkMenuItem(subMenu, b);
                }
            }
        }
        */

    }

    protected void addBookmarkMenuItem(final Menu menu, final Bookmark b) {
        final MenuItem bmi = menu.add(R.id.actions_goToBookmarkGroup, R.id.actions_goToBookmark, Menu.NONE, b.name);
        bmi.setIcon(R.drawable.viewer_menu_bookmark);
        ActionMenuHelper.setMenuItemExtra(bmi, "bookmark", b);
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onPanelClosed(int, android.view.Menu)
     */
    @Override
    public void onPanelClosed(final int featureId, final Menu menu) {
        menuClosedCalled = false;
        super.onPanelClosed(featureId, menu);
        if (!menuClosedCalled) {
            onOptionsMenuClosed(menu);
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onOptionsMenuClosed(android.view.Menu)
     */
    @Override
    public void onOptionsMenuClosed(final Menu menu) {
        menuClosedCalled = true;
        IUIManager.instance.onMenuClosed(this);
        view.changeLayoutLock(false);
    }

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#dispatchKeyEvent(android.view.KeyEvent)
     */
    @Override
    public final boolean dispatchKeyEvent(final KeyEvent event) {
        view.checkFullScreenMode();
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            if (!hasNormalMenu()) {
                getController().getOrCreateAction(R.id.actions_openOptionsMenu).run();
                return true;
            }
        }

        if (getController().dispatchKeyEvent(event)) {
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    public void showToastText(final int duration, final int resId, final Object... args) {
        Toast.makeText(getApplicationContext(), getResources().getString(resId, args), duration).show();
    }

}
