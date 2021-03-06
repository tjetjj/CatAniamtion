package com.example.flappycat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;


public class MainActivity extends AppCompatActivity {

    AsteroidView asteroidView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        asteroidView = new AsteroidView(this);
        setContentView(asteroidView);
    }

    class AsteroidView extends SurfaceView implements Runnable {
        Thread gameThread = null;
        SurfaceHolder ourHolder;
        volatile boolean playing;
        boolean paused = false;
        Canvas canvas;
        Paint paint;
        int y;
        int posx, posy;
        int dx, dy;
        int height, width;
        boulder[] b;
        ship s;

        private long thisTimeFrame;
        public AsteroidView(Context context) {
            super(context);

            ourHolder = getHolder();
            paint = new Paint();
        }

        @Override
        public void run() {
            Random r = new Random();
            b = new boulder[20];
            s = new ship();
            posx = 50;
            posy = 50;
            dx = 20;
            dy = 45;
            for (int i = 0; i < 20; ++i) {
                b[i] = new boulder();
                b[i].x = r.nextInt(50);
                b[i].y = r.nextInt(50);
                b[i].dx = r.nextInt(30) - 15;
                b[i].dy = r.nextInt(30) - 15;
                b[i].diameter = 45;
            }

            s.x = 250;
            s.y = 250;
            s.dx = 250;
            s.dy = 250;


            while (playing)
            {
                if (!paused) {
                    update();
                }
                draw();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {

                }
            }
        }
        public void update() {
            y = y + 5;
            if (y > 200)
                y = 5;


            posx += dx;
            posy += dy;
            if ((posx > width) || (posx < 0))
                dx = -dx;
            if ((posy > height) || (posy < 0))
                dy = -dy;

            for (int i = 0; i < 20; ++i)
                b[i].update();


        }
        public void draw() {
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                canvas = ourHolder.lockCanvas();

                width = canvas.getWidth();
                height = canvas.getHeight();

                // Draw the background color
                canvas.drawColor(Color.argb(255, 0, 0, 0));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255, 255, 255, 255));


                // canvas.drawCircle(posx, posy, 30l, paint);

                canvas.drawCircle(300, 500, 20, paint);

                for (int i = 0; i < 20; ++i) {
                    b[i].width = width;
                    b[i].height = height;
                    b[i].draw(canvas, paint);
                }
                    s.draw(canvas, paint);


                // canvas.drawCircle(b.x, b.y, 50, paint);

                ourHolder.unlockCanvasAndPost(canvas);
            }
        }

        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            if (motionEvent.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                System.out.println("running");
                s.update();
            }
            return true;
        }
    }


    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        asteroidView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        asteroidView.pause();
    }

}