package com.example.araprojenew;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public class Hud extends HUD {
	private TiledSprite gasButton, breakButton,shootButton,pauseButton,alternateShootButton;
	private Plane plane;
	private VertexBufferObjectManager vbom;
	private Rectangle healtBar,healtBarEmptyArea,healtBarBorder;
	
	public Hud(final Plane plane, Camera camera, VertexBufferObjectManager vbom) {
		this.plane = plane;
		camera.setHUD(this);
		this.vbom = vbom;	
		createButtons();
		createHealtBar();
		createScreenTexts();
		setTouchAreaBindingOnActionDownEnabled(true); 
	}

	private void createHealtBar() {
		healtBar = new Rectangle(40, 20, plane.health, 30, ResourcesManager.getInstance().vbom);
		healtBarBorder = new Rectangle(healtBar.getX()-2, healtBar.getY()-3, healtBar.getWidth()+4, healtBar.getHeight()+5, ResourcesManager.getInstance().vbom);
		healtBarEmptyArea = new Rectangle(healtBar.getX(), healtBar.getY(), healtBar.getWidth(), healtBar.getHeight(), ResourcesManager.getInstance().vbom);
		healtBar.setColor(Color.RED);	
		healtBarBorder.setColor(Color.BLACK);	
		healtBarEmptyArea.setColor(0.21f,0.25f,0.24f);//Gray
		attachChild(healtBarBorder);
		attachChild(healtBarEmptyArea);	
		attachChild(healtBar);	
	}

	private void createScreenTexts() {
		//TODO Burayý temizlemek gerek
		//final Text scoreText = new Text(20, 20, ResourcesManager.getInstance().hudFont, "-Scorepeed:0123456789", vbom);
		//attachChild(healthText);
		//attachChild(scoreText);
		TimerHandler scoreUpdateTimer = new TimerHandler(0.1f, true, new ITimerCallback() {
	        public void onTimePassed(TimerHandler pTimerHandler) {
	        	//scoreText.setText("S:"+plane.body.getPosition().y);
	        	//scoreText.setText("Speed:"+(int)plane.body.getLinearVelocity().len()+"       Score:"+GameScene.score+"-"+GameScene.enemyScore);
	            //scoreText.setText("S:"+(int)plane.shots.get(0).getPosition().x+" d:"+(int)plane.shots.get(0).getPosition().y);
	        	healtBar.setWidth(plane.health);
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
				if (touchEvent.isActionUp()) {
					alternateShootButton.setCurrentTileIndex(0);
				}
				if (touchEvent.isActionDown()) {
					plane.alternateShoot();
					alternateShootButton.setCurrentTileIndex(1);
				}
				return true;
			};
		};
		pauseButton = new ButtonSprite(GameScene.WORLD_WIDTH/2-100, 20,
				ResourcesManager.getInstance().pause_button_region, vbom) {
			public boolean onAreaTouched(TouchEvent touchEvent, float X, float Y) {
				if (touchEvent.isActionDown()) {
					((GameScene) SceneManager.getInstance().getCurrentScene()).activePuaseChildScene();
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