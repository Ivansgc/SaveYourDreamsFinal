package es.ivan.syd.Layout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import es.ivan.syd.R;



public class Wellcome extends AppCompatActivity {

    ImageView imageview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        imageview = (ImageView)findViewById(R.id.imageView);


        Thread myThread = new Thread(){
            public void run(){

                try{
                    sleep(1000);
                    Intent startMainScreen = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(startMainScreen);
                    finish();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }        };
        myThread.start();



        Animation animations = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.welcome_splash_anim);
        imageview.setAnimation(animations);


        animations.setAnimationListener(new Animation.AnimationListener(){


            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }



        });
    }
}
