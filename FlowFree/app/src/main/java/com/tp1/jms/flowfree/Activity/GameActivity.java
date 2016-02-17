package com.tp1.jms.flowfree.Activity;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tp1.jms.flowfree.Db.LevelAdapter;
import com.tp1.jms.flowfree.Controller.GameState;
import com.tp1.jms.flowfree.Model.Level;
import com.tp1.jms.flowfree.R;
import com.tp1.jms.flowfree.View.BoardView;

/**
 * This class defines the main Activity which will load the game
 */
public class GameActivity extends AppCompatActivity {

    private GameState game;
    private int selectedsize;
    private int selectedlevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);
        //Set the custom toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ImageView backbutton = (ImageView) findViewById(R.id.toolbar_back_button);
        backbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return false;
            }
        });
        //Get the parameters size and level
        Intent intent= getIntent();
        Bundle extras= intent.getExtras();
        selectedsize = (int) extras.get("size");
        selectedlevel = (int) extras.get("level");
        //Get the board view
        final BoardView boardview = (BoardView) findViewById(R.id.board_view);
        boardview.setLevelNumber(selectedlevel);
        //Get the desired level to load
        LevelAdapter leveladapter = new LevelAdapter(this);
        Level level = leveladapter.getLevel(selectedsize, selectedlevel);
        game = new GameState(level);
        //Config the board view size
        boardview.setNbCells(level.getSize());
        boardview.setGameState(game);
        // update labels and highscore
        updateLabels();
        setHighScoreLabel();
        // Config buttons and eventlistener
        boardview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boardview.onTouchEvent(event);
                updateLabels();
                return true;
            }
        });
        ImageView resetbutton = (ImageView) findViewById(R.id.reset_grid_button);
        resetbutton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boardview.reset();
                updateLabels();
                return true;
            }
        });
        ImageView previouslevelbutton = (ImageView) findViewById(R.id.previous_level_arrow);
        if(leveladapter.getLevelNumbers(selectedsize).contains(selectedlevel - 1)){

            previouslevelbutton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Intent intent = getIntent();
                    intent.putExtra("level", selectedlevel - 1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    finish();
                    return false;
                }
            });
        }
        else{
            previouslevelbutton.setImageResource(R.drawable.left_arrow_inactive);
        }

        ImageView nextlevelbutton = (ImageView) findViewById(R.id.next_level_arrow);
        if(leveladapter.getLevelNumbers(selectedsize).contains(selectedlevel + 1) &&
                leveladapter.getSucceedLevelNumbers(selectedsize).contains(selectedlevel) ){

            nextlevelbutton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Intent intent = getIntent();
                    intent.putExtra("level", selectedlevel + 1);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    finish();
                    return false;
                }
            });
        }
        else{
            nextlevelbutton.setImageResource(R.drawable.right_arrow_inactive);
        }
    }


    /**
     * Update the labels information
     */
    public void updateLabels() {
        //Set flow details
        TextView flowtext = (TextView) findViewById(R.id.flows_text);
        String flowstr = String.format(getString(R.string.flows_text), game.getnbFinishedFlow(),game.getnbFlow());
        flowtext.setText(flowstr);
        //Set empty square details
        TextView emptysquaretext = (TextView) findViewById(R.id.empty_square_text);
        String squarestr = String.format(getString(R.string.empty_square_text), game.getnbEmptySquare());
        emptysquaretext.setText(squarestr);
        //Set moves details
        TextView movestext = (TextView) findViewById(R.id.moves_text);
        String movesstr = String.format(getString(R.string.moves_text), game.getMoves());
        movestext.setText(movesstr);
    }

    /**
     * Set highscore label information
     */
    public void setHighScoreLabel(){
        TextView highscoretext = (TextView) findViewById(R.id.highscore_text);
        String highscore;
        highscore = game.getHighScore()==0?"NA":String.valueOf(game.getHighScore());
        String hgscorestr = String.format(getString(R.string.highscore_text), highscore);
        highscoretext.setText(hgscorestr);
    }

    @Override
    public void onBackPressed() {
        //Exit confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.exit_game_confirmation_title)
                .setMessage(R.string.exit_game_confirmation_message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent intent = new Intent(GameActivity.this, SelectLevelActivity.class);
                        intent.putExtra("size", selectedsize);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        finish();
                    }})
                .setNegativeButton(android.R.string.no, null).show();


    }

    /**
     * Play the next level
     */
    public void playnextlevel(){
        Intent intent = getIntent();
        intent.putExtra("level", selectedlevel + 1);
        finish();
        startActivity(intent);
    }

    /**
     * Update the next level button when a level is succeed
     */
    public void updateUnlockNextLevelButton(){
        ImageView nextlevelbutton = (ImageView) findViewById(R.id.next_level_arrow);
        LevelAdapter leveladapter = new LevelAdapter(this);
        if(leveladapter.getLevelNumbers(selectedsize).contains(selectedlevel + 1)){
            nextlevelbutton.setImageResource(R.drawable.right_arrow_active);
            nextlevelbutton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Intent intent = getIntent();
                    intent.putExtra("level", selectedlevel + 1);
                    finish();
                    startActivity(intent);
                    return false;
                }
            });
        }
        else{
            nextlevelbutton.setImageResource(R.drawable.right_arrow_inactive);
        }

    }

    /**
     * Display victoty dialog box when the player succeed a level
     */
    public void victory(){
            //Custom dialog
            final Dialog dialog = new Dialog(this);
            final BoardView boardview = (BoardView) findViewById(R.id.board_view);
            dialog.setContentView(R.layout.won_dialog);
            //Get buttons of the dialog box
            LevelAdapter leveladapter = new LevelAdapter(this);
            Button nextlevelbuton = (Button) dialog.findViewById(R.id.next_level_button);
            Button replaybutton = (Button) dialog.findViewById(R.id.replay_button);
            Button exitbutton = (Button) dialog.findViewById(R.id.exit_button);
            //Set buttons eventlistener
            //If all levels are succeed
            if(!leveladapter.getLevelNumbers(selectedsize).contains(selectedlevel +1) &&
                    leveladapter.getLevelSizes().contains(selectedsize+1)){

                dialog.setTitle(R.string.won_message_special_title);
                ImageView wonimage = (ImageView) dialog.findViewById(R.id.won_image);
                TextView wonmessage = (TextView) dialog.findViewById(R.id.won_message);
                wonimage.setImageResource(R.drawable.star_icon);
                String str = String.format(getString(R.string.won_special_message_text),selectedsize+1);
                wonmessage.setText(str);
                nextlevelbuton.setText(R.string.next_grid);
                nextlevelbuton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(GameActivity.this, SelectLevelActivity.class);
                        intent.putExtra("size", selectedsize + 1);
                        finish();
                        startActivity(intent);

                    }
                });
            }
            else{//If only one level is succeed
                dialog.setTitle(R.string.won_message_title);
                if(leveladapter.getLevelNumbers(selectedsize).contains(selectedlevel + 1)){
                    nextlevelbuton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            playnextlevel();
                        }
                    });
                }
                else{
                    nextlevelbuton.setEnabled(false);
                }
            }
            replaybutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boardview.reset();
                    dialog.dismiss();

                }
            });
            exitbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                }
            });

            dialog.show();
    }

    /**
     * Displays the defeat dialog box
     */
    public void defeat() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.lost_dialog);
        dialog.setTitle(R.string.lost_message_title);
        dialog.show();
    }
}
