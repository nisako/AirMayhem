package com.example.araprojenew;

import java.util.Random;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;

public class PowerupManager {
	Random rand = new Random();
	private int pTimerSeconds=5;
	private Plane plane;
	private Powerup testPup;
	public PowerupManager(final Scene pScene,Plane pPlane){
		plane = pPlane;
		pScene.registerUpdateHandler(new TimerHandler(pTimerSeconds, true, new ITimerCallback() {	
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				testPup = new Powerup(rand.nextInt(GameScene.WORLD_WIDTH), rand.nextInt(GameScene.WORLD_HEIGHT-200), plane, powerupType.getRandom());
				pScene.attachChild(testPup);
			}
		}));
	}
}
