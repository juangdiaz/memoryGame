package com.sho.hire.hw.DiazJuanMemory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;


/**
 * This Activity Implements the Memory Game.
 * The task is to build a basic memory game. Display a 4x4 grid of the default image of your choice.
 * Allow the user to flip two images at a time to display the images you've fetched. If the images
 * match, leave them flipped over, otherwise flip them back. When the user has matched all the
 * images, display the number of flips it took and allow them to restart the game with new images.
 *
 * @author  Juan G. Diaz
 * @version 1.0
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int numOfElements;
    int tries;
    int matchesFound = 0;

    boolean isDelayed = false;

    MemoryImage[] images;
    int[] imageLocation;
    Bitmap[] image;

    MemoryImage selectedImage1;
    MemoryImage selectedImage2;

    public RequestHandler requestHandler;


    TextView triesTextView;
    Button playAgainButton ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Init Request Handler
        requestHandler = new RequestHandler();

        //Init UI Elements
        triesTextView = (TextView) findViewById(R.id.text_tries);
        triesTextView.setText(String.format(getResources().getString(R.string.tries), 0));
        playAgainButton = (Button) findViewById(R.id.btn_play_again);
        playAgainButton.setOnClickListener(this);

        //Set the board images
        getImages();


    }

    /**
     * set the Memory GridLayout to be a 4x4
     */
    private void setImagesGrid(ArrayList<Image> imagesURL) {

        GridLayout gridLayout = (GridLayout) findViewById(R.id.grid_game);

        int numOfColumns = gridLayout.getColumnCount();
        int numOfRows = gridLayout.getRowCount();

        numOfElements = numOfColumns * numOfRows;

        images = new MemoryImage[numOfElements];

        image = new Bitmap[numOfElements / 2];


        for(int i = 0; i < 8; i++ ){
            image[i] = FlickrApi.getImageBitmap(imagesURL.get(i));
        }

        imageLocation = new int[numOfElements];

        shuffleImages();


        for (int row = 0; row < numOfRows; row++){
            for (int column = 0; column < numOfColumns; column++){

                //set up the 4x4 board
                MemoryImage tempMemoryImage = new MemoryImage(this, row, column, image[imageLocation[row * numOfColumns + column]], imagesURL.get(imageLocation[row * numOfColumns + column]).getId());
                tempMemoryImage.setId(View.generateViewId());
                tempMemoryImage.setOnClickListener(this);
                images[row * numOfColumns + column] = tempMemoryImage;
                gridLayout.addView(tempMemoryImage);
            }
        }
    }

    /**
     *  Run Flickr API to get the images
     */
    public void getImages(){

        Runnable getMetadata = new Runnable() {
            @Override
            public void run() {
                String tag = "kitten";
                if (tag != null && tag.length() >= 3)
                    FlickrApi.imageSearchByTag(requestHandler, getApplicationContext(), tag);

            }
        };

        new Thread(getMetadata).start();
    }


    /**
     *
     *  Request Handler to handle flickr messages from thread
     */
    public class RequestHandler extends Handler {
        static final int ID_METADATA_DOWNLOADED = 0;

        @Override
        public void handleMessage(Message msg) {

            if (msg.obj != null) {
                setImagesGrid( (ArrayList<Image>) msg.obj);
            }

            super.handleMessage(msg);
        }
    }

    /**
     *
     *  Logic to shuffle randomly the images
     */
    private void shuffleImages() {
        Random rand = new Random();

        for(int i = 0; i < numOfElements; i++){
            imageLocation[i] = i % (numOfElements / 2);
        }

        for(int i = 0; i < numOfElements; i++){

            int tempImagePlace = imageLocation[i];
            int swapImage = rand.nextInt(16);

            imageLocation[i] = imageLocation[swapImage];
            imageLocation[swapImage] = tempImagePlace;
        }
    }

    @Override
    public void onClick(View view) {

        //Restart the game
        if(view == playAgainButton){

            //Refresh Activity and clear Activity stack
            Intent intent = getIntent();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            startActivity(intent);

        }

        //Pairing Logic
        else if( view instanceof MemoryImage){
            if(isDelayed)
                return;

            //Type casting to custom image button
            MemoryImage image = (MemoryImage) view;

            if(image.isMatch())
                return;

            if(selectedImage1 == null){
                selectedImage1 = image;
                selectedImage1.flip();
                return;
            }

            if (selectedImage1.getId() == image.getId())
                return;

            if(selectedImage1.getFrontImageDrawableId() == image.getFrontImageDrawableId()){

                image.flip();
                image.setMatch(true);
                image.setEnabled(false);

                selectedImage1.setMatch(true);
                selectedImage1.setEnabled(false);
                matchesFound++;

                if(matchesFound == 8){
                    Button playAgainButton = (Button) findViewById(R.id.btn_play_again);
                    playAgainButton.setVisibility(View.VISIBLE);
                }

            } else {
                selectedImage2 = image;
                selectedImage2.flip();
                isDelayed = true;

                tries ++;
                triesTextView.setText(String.format(getResources().getString(R.string.tries), tries));

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        selectedImage1.flip();
                        selectedImage1 = null;

                        selectedImage2.flip();
                        selectedImage2 = null;
                        isDelayed = false;
                    }
                }, 500);
            }

        }

    }


}
