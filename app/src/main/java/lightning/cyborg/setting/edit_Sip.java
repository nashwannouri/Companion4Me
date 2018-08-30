package lightning.cyborg.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import com.gc.materialdesign.views.ButtonFloatSmall;
import com.gc.materialdesign.views.LayoutRipple;
import com.gc.materialdesign.widgets.ColorSelector.OnColorSelectedListener;
import com.nineoldandroids.view.ViewHelper;

import lightning.cyborg.R;
import lightning.cyborg.activity.UserHomepage;


public class edit_Sip extends Activity implements OnColorSelectedListener {
	
	int backgroundColor = Color.parseColor("#1E88E5");
	ButtonFloatSmall buttonSelectColor;

    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sip_page);

        
        LayoutRipple layoutRipple = (LayoutRipple) findViewById(R.id.userDetails);


        setOriginRiple(layoutRipple);



		//amos this is where you put your work in....

        layoutRipple.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent(edit_Sip.this,UserDetails.class);
				//intent.putExtra("BACKGROUND", backgroundColor);
				//startActivity(intent);
			}
		});


		layoutRipple = (LayoutRipple) findViewById(R.id.Communication);
        
        
        setOriginRiple(layoutRipple);


		//amos this is where you put your work in....


		layoutRipple.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//Intent intent = new Intent(edit_Sip.this, Communication.class);
				//intent.putExtra("BACKGROUND", backgroundColor);
				//startActivity(intent);
			}
		});



    }
    
	private void setOriginRiple(final LayoutRipple layoutRipple){
    	
    	layoutRipple.post(new Runnable() {
			
			@Override
			public void run() {
				View v = layoutRipple.getChildAt(0);
		    	layoutRipple.setxRippleOrigin(ViewHelper.getX(v)+v.getWidth()/2);
		    	layoutRipple.setyRippleOrigin(ViewHelper.getY(v)+v.getHeight()/2);
		    	
		    	layoutRipple.setRippleColor(Color.parseColor("#1E88E5"));
		    	
		    	layoutRipple.setRippleSpeed(100);
			}
		});
    	
    }

	@Override
	public void onColorSelected(int color) {
		backgroundColor = color;
		buttonSelectColor.setBackgroundColor(color);
	}


	public void backToSetting(View view){

		Intent intent = new Intent(edit_Sip.this, UserHomepage.class);
		startActivity(intent);



	}

    

}
