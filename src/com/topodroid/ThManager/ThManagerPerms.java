/* @file ThManagerPerms.java
 *
 * @author marco corvi
 * @date may 2018
 *
 * @brief TopoDroid permission dialog
 *
 * --------------------------------------------------------
 *  Copyright This software is distributed under GPL-3.0 or later
 *  See the file COPYING.
 * ----------------------------------------------------------
 */
package com.topodroid.ThManager;

import android.content.Context;
// import android.content.Intent;

// import android.app.Dialog;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
// import android.view.ViewGroup.LayoutParams;
// import android.net.Uri;

// import android.widget.Toast;

class ThManagerPerms extends MyDialog
                     implements OnClickListener
{
  // private Button mBTok;
  // private Context mContext; // INHERITED

  ThManagerPerms( Context context, int check_perms )
  {
    super( context );
    // mContext = context;
    initLayout(R.layout.thmanager_perms,
      String.format( context.getResources().getString(R.string.welcome_title), ThManagerApp.VERSION ) );

    StringBuilder sb = new StringBuilder();
    if ( check_perms < 0 ) {
      sb.append( context.getResources().getString( R.string.perms_mandatory ));
      sb.append( "\nWRITE_EXTERNAL_STORAGE" );
    } else if ( check_perms > 0 ) {
      sb.append( context.getResources().getString( R.string.perms_optional ) );
    }
    TextView tv = (TextView)findViewById( R.id.text_perms );
    tv.setText( sb.toString() );

    Button btn_ok = (Button)findViewById(R.id.btn_ok);
    btn_ok.setOnClickListener( this );
  }

  @Override
  public void onClick( View v )
  {
    dismiss();
  }
  
}
