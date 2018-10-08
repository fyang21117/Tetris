/*
b * Copyright 2013 Simon Willeke
 * contact: hamstercount@hotmail.com
 */

/*
    This file is part of Blockinger.

    Blockinger is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Blockinger is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Blockinger.  If not, see <http://www.gnu.org/licenses/>.

    Diese Datei ist Teil von Blockinger.

    Blockinger ist Freie Software: Sie k�nnen es unter den Bedingungen
    der GNU General Public License, wie von der Free Software Foundation,
    Version 3 der Lizenz oder (nach Ihrer Option) jeder sp�teren
    ver�ffentlichten Version, weiterverbreiten und/oder modifizieren.

    Blockinger wird in der Hoffnung, dass es n�tzlich sein wird, aber
    OHNE JEDE GEW�HELEISTUNG, bereitgestellt; sogar ohne die implizite
    Gew�hrleistung der MARKTF�HIGKEIT oder EIGNUNG F�R EINEN BESTIMMTEN ZWECK.
    Siehe die GNU General Public License f�r weitere Details.

    Sie sollten eine Kopie der GNU General Public License zusammen mit diesem
    Programm erhalten haben. Wenn nicht, siehe <http://www.gnu.org/licenses/>.
 */

package com.fyang21117.tetris.activities;

import com.fyang21117.tetris.BlockBoardView;
import com.fyang21117.tetris.R;
import com.fyang21117.tetris.WorkThread;
import com.fyang21117.tetris.components.Controls;
import com.fyang21117.tetris.components.Display;
import com.fyang21117.tetris.components.GameState;
import com.fyang21117.tetris.components.Sound;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;

public class GameActivity extends FragmentActivity implements OnClickListener,OnTouchListener{

	public Sound sound;
	public Controls controls;
	public Display display;
	public GameState game;
	private WorkThread mainThread;
	private DefeatDialogFragment dialog;
	private boolean layoutSwap;

	public Button pause;
	public ImageButton rotateLeft;
	public ImageButton rotateRight;
	public ImageButton hardDrop;
	public ImageButton softDrop;
	public ImageButton left;
	public ImageButton right;
	public ImageButton board;

	public static final int NEW_GAME = 0;
	public static final int RESUME_GAME = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//
		if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_layoutswap", false)) {
			setContentView(R.layout.activity_game_alt);
			layoutSwap = true;
		} else {
			setContentView(R.layout.activity_game);
			layoutSwap = false;
		}

		/* Read Starting Arguments */
		Bundle b = getIntent().getExtras();
		int value = NEW_GAME;
		
		/* Create Components */
		game = (GameState)getLastCustomNonConfigurationInstance();
		if(game == null) {
			if(b!=null)
				value = b.getInt("mode");
				
			if((value == NEW_GAME)) {
				game = GameState.getNewInstance(this);
				game.setLevel(b.getInt("level"));
			} else
				game = GameState.getInstance(this);
		}
		game.reconnect(this);
		dialog = new DefeatDialogFragment();
		controls = new Controls(this);
		display = new Display(this);
		sound = new Sound(this);
		
		/* Init Components */
		if(game.isResumable())
			sound.startMusic(Sound.GAME_MUSIC, game.getSongtime());
		sound.loadEffects();
		if(b!=null){
			value = b.getInt("mode");
			if(b.getString("playername") != null)
				game.setPlayerName(b.getString("playername"));
		} else 
			game.setPlayerName(getResources().getString(R.string.anonymous));
		dialog.setCancelable(false);
		if(!game.isResumable())
			gameOver(game.getScore(), game.getTimeString(), game.getAPM());

		pause=findViewById(R.id.pausebutton_1);
		hardDrop = findViewById(R.id.hardDropButton);
		softDrop = findViewById(R.id.softDropButton);
		rotateRight = findViewById(R.id.rotateRightButton);
		rotateLeft = findViewById(R.id.rotateLeftButton);
		right = findViewById(R.id.rightButton);
		left = findViewById(R.id.leftButton);
		board = findViewById(R.id.boardView);

		pause.setOnClickListener(this);
		hardDrop.setOnClickListener(this);
		softDrop.setOnClickListener(this);
		rotateRight.setOnClickListener(this);
		rotateLeft.setOnClickListener(this);
		right.setOnClickListener(this);
		left.setOnClickListener(this);
		board.setOnClickListener(this);

		((BlockBoardView)findViewById(R.id.boardView)).init();
		((BlockBoardView)findViewById(R.id.boardView)).setHost(this);
	}

	@Override
	public boolean onTouch(View v,MotionEvent event){
		switch (v.getId()){
			case R.id.boardView:{
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					controls.boardPressed(event.getX(), event.getY());
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					controls.boardReleased();
				}
				}break;
			case R.id.leftButton:{
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					controls.rightButtonPressed();
					left.setPressed(true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					controls.rightButtonReleased();
					left.setPressed(false);
				}
				}break;
			case R.id.rightButton:{
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					controls.rightButtonPressed();
					right.setPressed(true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					controls.rightButtonReleased();
					right.setPressed(false);
				}
				}break;
			case R.id.hardDropButton:{
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					controls.downButtonPressed();
					hardDrop.setPressed(true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					controls.downButtonReleased();
					hardDrop.setPressed(false);
				}
				}break;
			case R.id.softDropButton:{
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					controls.dropButtonPressed();
					softDrop.setPressed(true);
					} else if (event.getAction() == MotionEvent.ACTION_UP) {
						controls.dropButtonReleased();
						softDrop.setPressed(false);
				}
				}break;
			case R.id.rotateLeftButton:{
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					controls.rotateLeftPressed();
					rotateLeft.setPressed(true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					controls.rotateLeftReleased();
					rotateLeft.setPressed(false);
				}
				}break;
			case R.id.rotateRightButton:{
				if(event.getAction() == MotionEvent.ACTION_DOWN) {
					controls.rotateRightPressed();
					rotateRight.setPressed(true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					controls.rotateRightReleased();
					rotateRight.setPressed(false);
				}
				}break;
			default:break;
		}
		return true;
	}
	@Override
	public void onClick(View view)
	{
		switch (view.getId()){
			case R.id.pausebutton_1:{
				GameActivity.this.finish();
			}
		}
	}
	/*
	 * Called by BlockBoardView upon completed creation
	 * @param caller
	 */
	public void startGame(BlockBoardView caller){
		mainThread = new WorkThread(this, caller.getHolder()); 
		mainThread.setFirstTime(false);
		game.setRunning(true);
		mainThread.setRunning(true);
		mainThread.start();
	}

	/**
	 * Called by BlockBoardView upon destruction
	 */
	public void destroyWorkThread() {
        boolean retry = true;
        mainThread.setRunning(false);
        while (retry) {
            try {
            	mainThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
	}
	
	/*
	 * Called by GameState upon Defeat
	 * @param score
	 */
	public void putScore(long score) {
		String playerName = game.getPlayerName();
		if(playerName == null || playerName.equals(""))
			playerName = getResources().getString(R.string.anonymous);//"Anonymous";
		
		Intent data = new Intent();
		data.putExtra(MainActivity.PLAYERNAME_KEY, playerName);
		data.putExtra(MainActivity.SCORE_KEY, score);
		setResult(MainActivity.RESULT_OK, data);
		
		finish();
	}
	
	@Override
	protected void onPause() {
    	super.onPause();
    	sound.pause();
    	sound.setInactive(true);
    	game.setRunning(false);
	}
    
    @Override
    protected void onStop() {
    	super.onStop();
    	sound.pause();
    	sound.setInactive(true);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	game.setSongtime(sound.getSongtime());
    	sound.release();
    	sound = null;
    	game.disconnect();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	sound.resume();
    	sound.setInactive(false);
    	
    	/* Check for changed Layout */
    	boolean tempswap = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("pref_layoutswap", false);
		if(layoutSwap != tempswap) {
			layoutSwap = tempswap;
			if(layoutSwap) {
				setContentView(R.layout.activity_game_alt);
			} else {
				setContentView(R.layout.activity_game);
			}
		}
    	game.setRunning(true);
    }
    
    @Override
    public Object onRetainCustomNonConfigurationInstance () {
        return game;
    }
	
	public void gameOver(long score, String gameTime, int apm) {
		dialog.setData(score, gameTime, apm);
		dialog.show(getSupportFragmentManager(), "hamster");
	}

}
