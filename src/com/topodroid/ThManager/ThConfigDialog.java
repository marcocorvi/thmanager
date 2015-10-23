/* @file ThConfigDialog.java
 *
 * @author marco corvi
 * @date nov 2011
 *
 * --------------------------------------------------------
 *  Copyright This sowftare is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * --------------------------------------------------------
 * CHANGES
 */
package com.topodroid.ThManager;

import android.os.Bundle;
import android.app.Dialog;
// import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;


public class ThConfigDialog extends Dialog 
                            implements View.OnClickListener
{
    private EditText mLabel;
    private Button mBtnOK;
    // private Button mBtnCancel;

    private ThManagerActivity mActivity;

    public ThConfigDialog( Context context, ThManagerActivity activity )
    {
      super(context);
      mActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.thconfig_dialog);
      getWindow().setLayout( LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT );

      mLabel     = (EditText) findViewById(R.id.label_text);
      mBtnOK     = (Button) findViewById(R.id.label_ok);
      // mBtnCancel = (Button) findViewById(R.id.label_cancel);

      mBtnOK.setOnClickListener( this );
      // mBtnCancel.setOnClickListener( this );

      setTitle("ThConfig filename");
    }

    public void onClick(View view)
    {
      if (view.getId() == R.id.label_ok ) {
        String name = mLabel.getText().toString();
        if ( ! name.endsWith( ".thconfig" ) ) {
          name = name + ".thconfig";
        }
        mActivity.addThConfig( name );
      }
      dismiss();
    }
}
        

