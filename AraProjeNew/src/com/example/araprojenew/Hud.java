package com.example.araprojenew;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class Hud extends HUD {
	private TiledSprite gasButton, breakButton,shootButton,pauseButton,alternateShootButton;
	private Camera camera;
	private Plane plane;
	private VertexBufferObjectManager vbom;
	
	public Hud(final Plane plane, Camera camera, VertexBufferObjectManager vbom) {
		this.plane = plane;
		this.camera = camera;
		this.vbom = vbom;	
		createButtons();
		createScreenTexts();
		setTouchAreaBindingOnActionDownEnabled(true); 
		camera.setHUD(this);
	}

	private void createScreenTexts() {
		//TODO Burayý temizlemek gerek
		final Text scoreText = new Text(20, 20, ResourcesManager.getInstance().hudFont, "-Scorepeed:0123456789", vbom);
		final Text healthText = new Text(525, 20, ResourcesManager.getInstance().hudFont, "-health:0123456789", vbom);
		scoreText.setColor(Color.RED);
		healthText.setColor(Color.RED);
		attachChild(healthText);
		attachChild(scoreText);
		TimerHandler scoreUpdateTimer = new TimerHandler(0.1f, true, new ITimerCallback() {
	        public void onTimePassed(TimerHandler pTimerHandler) {
	        	//scoreText.setText("S:"+plane.body.getPosition().y);
	            healthText.setText("Health:"+plane.health);
	        	scoreText.setText("Speed:"+(int)plane.body.getLinearVelocity().len()+"       Score:"+GameScene.score+"-"+GameScene.enemyScore);
	            //scoreText.setText("S:"+(int)plane.shots.get(0).getPosition().x+" d:"+(int)plane.shots.get(0).getPosition().y);
	        }
	    });
	    registerUpdateHandler(scoreUpdateTimer);
	    	

	}
	
	public void pauseButtonTileChanger(){
		pauseButton.setCurrentTileIndex((pauseButton.getCurrentTileIndex()+1)%2);
	}
	public void setPauseButtonTile(int t){
		pauseButton.setCurrentTileIndex(t);
	}
	
	private void createButtons() {
		gasButton = new ButtonSprite(700, 300,
				ResourcesManager.getInstance().button_region, vbom) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionUp()) {
					plane.gasShip(false);
					gasButton.setCurrentTileIndex(0);
				}
				if (touchEvent.isActionDown()) {
					plane.gasShip(true);
					gasButton.setCurrentTileIndex(1);
				}
				return true;
			};
		};
		breakButton = new ButtonSprite(625, 375,
				ResourcesManager.getInstance().button_region, vbom) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionUp()) {
					plane.breakShip(false);
					breakButton.setCurrentTileIndex(0);

				}
				if (touchEvent.isActionDown()) {
					plane.breakShip(true);
					breakButton.setCurrentTileIndex(1);
				}
				return true;
			};
		};
		shootButton = new ButtonSprite(25, 300,
				ResourcesManager.getInstance().button_region, vbom) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionUp()) {
					shootButton.setCurrentTileIndex(0);

				}
				if (touchEvent.isActionDown()) {
					if(SceneManager.getInstance().getCurrentScene().getClass().equals(HostGameScene.class)){
					((HostGameScene) SceneManager.getInstance().getCurrentScene()).sendShootMessage();
					}
					else if(SceneManager.getInstance().getCurrentScene().getClass().equals(ClientGameScene.class)){
						((ClientGameScene) SceneManager.getInstance().getCurrentScene()).sendShootMessage();
					}
					plane.isShoot = true;
					shootButton.setCurrentTileIndex(1);
				}
				return true;
			};
		};
		alternateShootButton = new ButtonSprite(100, 375,ResourcesManager.getInstance().button_region, vbom) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					((GameScene) SceneManager.getInstance().getCurrentScene()).pause();
				}
				return true;
			};
		};
		pauseButton = new ButtonSprite(GameScene.WORLD_WIDTH/2-100, 20,
				ResourcesManager.getInstance().pause_button_region, vbom) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					((GameScene) SceneManager.getInstance().getCurrentScene()).pause();
				}
				return true;
			};
		};
		
		registerTouchArea(pauseButton);
		attachChild(pauseButton);
		registerTouchArea(gasButton);
		attachChild(gasButton);
		registerTouchArea(alternateShootButton);
		attachChild(alternateShootButton);
		registerTouchArea(shootButton);
		attachChild(shootButton);
		registerTouchArea(breakButton);
		attachChild(breakButton);
		

	}
}