package com.example.araprojenew;

import java.util.ArrayList;
import java.util.Random;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;

public class PowerupManager {
	Random rand = new Random();
	private int pTimerSeconds=3;
	private Plane plane;
	private PlaneEnemy planeEnemy;
	private Powerup testPup;
	private Scene scene;
	private int tagCounter=0;
	public PowerupManager(final Scene pScene,Plane pPlane,PlaneEnemy pplaneEnemy,boolean spawnAuto){
		plane = pPlane;
		planeEnemy = pplaneEnemy;
		scene = pScene;
		if(spawnAuto){
		pScene.registerUpdateHandler(new TimerHandler(pTimerSeconds, true, new ITimerCallback() {	
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				if(plane.animationFlagForPlaneCrush == true){
					powerupType a =  powerupType.getRandom();
					testPup = new Powerup(rand.nextInt(GameScene.WORLD_WIDTH), rand.nextInt(GameScene.WORLD_HEIGHT-200), plane,planeEnemy, a,ResourcesManager.getInstance().powerup_regions[powerupType.toInt(a)]);
					pScene.attachChild(testPup);
					testPup.setTag(tagCounter);
					tagCounter++;
					((HostGameScene) pScene).sendPowerUp(testPup);
				}
			}
		}));
		}
	}
	public void spawnPowerUp(float x,float y,int type){
		if(plane.animationFlagForPlaneCrush == true){
			testPup = new Powerup(x, y, plane,planeEnemy, powerupType.toPowerupType(type),ResourcesManager.getInstance().powerup_regions[type]);
			testPup.setTag(tagCounter);
			tagCounter++;
			scene.attachChild(testPup);
		}
	}
	public void removePowerUp(int pTag){
		((Powerup) scene.getChildByTag(pTag)).remove();
	}
	
}
