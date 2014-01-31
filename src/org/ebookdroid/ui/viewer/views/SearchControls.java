package org.ebookdroid.ui.viewer.views;

import org.ebookdroid.ui.viewer.ViewerActivity;
import org.emdev.ui.actions.ActionEx;
import org.emdev.ui.actions.params.Constant;
import org.emdev.ui.actions.params.EditableValue;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import org.ebookdroid.R;

public class SearchControls extends LinearLayout {

	public static EditText m_edit;
	private ImageButton m_prevButton;
	private ImageButton m_nextButton;
	private ImageButton m_cancelButton;
    public SearchControls(final ViewerActivity parent) {
        super(parent);
        setVisibility(View.GONE);
        setOrientation(LinearLayout.VERTICAL);
        
       

        LayoutInflater.from(parent).inflate(R.layout.seach_controls, this, true);
        m_prevButton = (ImageButton) findViewById(R.id.search_controls_prev);
        m_nextButton = (ImageButton) findViewById(R.id.search_controls_next);
        m_cancelButton = (ImageButton) findViewById(R.id.calc_clear_txt_Prise);
        
        
        m_edit = (EditText) findViewById(R.id.search_controls_edit);
        m_edit.setOnClickListener(new OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
            	ViewerActivity.leybrdcounter=0;
            }
        });
        /*
        m_edit.requestFocus();
        m_edit.post(new Runnable() {
        	@Override public void run() { 
        		InputMethodManager keyboard = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE); keyboard.showSoftInput(m_edit, 0); } }); 
		*/
       

        ActionEx forwardSearch = parent.getController().getOrCreateAction(R.id.actions_doSearch);
        ActionEx backwardSearch = parent.getController().getOrCreateAction(R.id.actions_doSearchBack);
        ActionEx cancelSearch = parent.getController().getOrCreateAction(R.id.actions_cancelSearch);

        forwardSearch.addParameter(new EditableValue("input", m_edit)).addParameter(new Constant("forward", "true"));
        cancelSearch.addParameter(new EditableValue("input", m_edit)).addParameter(new Constant("forward", "true"));
        backwardSearch.addParameter(new EditableValue("input", m_edit)).addParameter(new Constant("forward", "false"));

        m_prevButton.setOnClickListener(backwardSearch);
        m_nextButton.setOnClickListener(forwardSearch);
        m_cancelButton.setOnClickListener(cancelSearch);
        
        m_edit.setOnEditorActionListener(forwardSearch);
        
        
        final EditText searchtextboxofpdfreader = (EditText) findViewById(R.id.search_controls_edit);
        final ImageButton canceliconofsearchtextboxofpdfreader = (ImageButton) findViewById(R.id.calc_clear_txt_Prise);
        
        searchtextboxofpdfreader.addTextChangedListener(new TextWatcher()
	    {
		    public void onTextChanged(CharSequence s, int start, int before, int count) 
		    {
		                
		    }
		    public void beforeTextChanged(CharSequence s, int start, int count,int after) 
		    {
		     
		    }
		    @Override
		    public void afterTextChanged(Editable arg0) 
		    {
		    	String searchtextboxofpdfreaderstr = searchtextboxofpdfreader.getText().toString();
	            if (searchtextboxofpdfreaderstr.length() > 0) 
	            {
	            	canceliconofsearchtextboxofpdfreader.setVisibility(View.VISIBLE);
	            	searchtextboxofpdfreader.setPadding(13, 0, 35, 0);
	            	canceliconofsearchtextboxofpdfreader.setImageResource(R.drawable.redx);
	            }
	            else 
	            {
	            	canceliconofsearchtextboxofpdfreader.setVisibility(View.GONE);
	            	canceliconofsearchtextboxofpdfreader.setPadding(0, 0, 0, 0);
	            	searchtextboxofpdfreader.setPadding(13, 0, 0, 0);
	            	canceliconofsearchtextboxofpdfreader.setImageResource(R.drawable.blank);
	            }
		    }
	    });

        
    }

    /*public void canceltheseach(View v)
    {
    	try
    	{
    		Log.i("Cancel the searc ","<==============");
    		m_edit.setText("");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }*/
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if (visibility == VISIBLE) {
            m_edit.requestFocus();
        }
    }
    
    public void resetthestaticVariable(View v)
    {
    	ViewerActivity.leybrdcounter=0;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        return false;
    }

    public int getActualHeight() {
        return m_edit.getHeight();
    }
}
