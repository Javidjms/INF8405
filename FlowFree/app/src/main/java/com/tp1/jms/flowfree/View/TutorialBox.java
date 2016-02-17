package com.tp1.jms.flowfree.View;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tp1.jms.flowfree.R;

/** This class describes the dialogbox for the tutorial
 * Created by JMS on 11/02/2016.
 */
public class TutorialBox extends Dialog {

    private int WINDOW_NUMBER; // Tutorial current window number
    private int MAX_WINDOW_NUMBER =3; // Maximal window number
    private int[] image = {R.drawable.tutorial_image_1,
                                    R.drawable.tutorial_image_2,
                                    R.drawable.tutorial_image_3,
                                    R.drawable.tutorial_image_4};
    private String[] description;
    private Button skipbutton,backbutton,nextbutton;
    private ImageView imageview;
    private TextView descriptionview;

    /**
     * Constructor which match the layout with this class with the method setContentView
     * @param context
     */
    public TutorialBox(Context context) {
        super(context);
        this.setContentView(R.layout.tutorial_dialog);
        this.setTitle(R.string.tutorial_title);
        description = context.getResources().getStringArray(R.array.tutorial_description_array);
        init();
    }

    /**
     * Initialisation of the tutorial box
     */
    public void init() {
        WINDOW_NUMBER = 0;
        imageview = (ImageView) findViewById(R.id.tutorial_image_view);
        descriptionview = (TextView) findViewById(R.id.tutorial_description);
        skipbutton = (Button) findViewById(R.id.tutorial_skip_button);
        backbutton = (Button) findViewById(R.id.tutorial_back_button);
        nextbutton = (Button) findViewById(R.id.tutorial_next_button);

        skipbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WINDOW_NUMBER--;
                update();
            }
        });

        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WINDOW_NUMBER++;
                update();
            }
        });
        update();

    }

    /**
     * Update the eventlistener on the buttons each time we changed the window of tutorial box
     */
    public void update(){

        descriptionview.setText(description[WINDOW_NUMBER]);
        imageview.setImageResource(image[WINDOW_NUMBER]);
        backbutton.setEnabled(!(WINDOW_NUMBER == 0));
        nextbutton.setEnabled(!(WINDOW_NUMBER == MAX_WINDOW_NUMBER));
    }


}
