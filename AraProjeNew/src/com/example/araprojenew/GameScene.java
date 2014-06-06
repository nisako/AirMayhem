package com.example.araprojenew;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.ParallaxBackground;
import org.andengine.entity.scene.background.ParallaxBackground.ParallaxEntity;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.constants.PhysicsConstants;
import org.andengine.opengl.util.GLState;
import org.andengine.engine.camera.*;

import android.content.Context;
import android.os.PowerManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.example.araprojenew.SceneManager.SceneType;

public class GameScene extends BaseScene implements IOnMenuItemClickListener{
	
	public static final int WORLD_WIDTH = 1600;
	public static final int WORLD_HEIGHT = 960;
	
	private static final int MENU_RESUME = 0;
	private static final int MENU_QUIT = 1;
	
	protected Sprite arrowSprite;
	protected AnimatedSprite explosionSprite;
	protected Boolean animationFlagForPlaneCrush = true;
	
	private Body groundBody,groundBody2,groundBody3;
	
	private Sprite fieldSprite;
	private Sprite backgroundSprite;

	public Hud gameHUD;
	public PhysicsWorld physicsWorld;
	public Plane plane;
	public PlaneEnemy planeEnemy;
	public static int score,enemyScore;
	protected boolean isMultipalyerPause=false;
	protected Rectangle left,right,ground,roof;
	
	public MenuScene pauseChildScene,waitChildScene,postVictoryGameChildScene,postDefeatGameChildScene;
	
	@Override
	public void createScene() {
		createPhysics();
		createGameObjects();
	    createHUD();
	    createBackground();
	    createPauseChildScene();
	    createWaitChildScene();
	    createVictoryPostGameScene();
	    createDefeatPostGameScene();
	    //engine.registerUpdateHandler(new FPSLogger());
	}
	
	

	private void createPauseChildScene() {
		pauseChildScene = new MenuScene(camera);
		
		final IMenuItem resumeMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_RESUME, resourcesManager.resume_button_region, vbom), 1.2f, 1);
	    final IMenuItem quitMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_QUIT, resourcesManager.back_button_region, vbom), 1.2f, 1);
	    
	    pauseChildScene.addMenuItem(resumeMenuItem);
	    pauseChildScene.addMenuItem(quitMenuItem);
	    
	    pauseChildScene.buildAnimations();
	    pauseChildScene.setBackgroundEnabled(false);
	    
	    resumeMenuItem.setPosition(250, 125);
	    quitMenuItem.setPosition(250,  275);
	    
	    pauseChildScene.setOnMenuItemClickListener(this);		
	}
	
	private void createVictoryPostGameScene(){
		postVictoryGameChildScene = new MenuScene(camera);
			
		final IMenuItem quitMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_QUIT, resourcesManager.back_button_region, vbom), 1.2f, 1);
		final IMenuItem victoryMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(33, resourcesManager.victory_region, vbom), 1, 1);
		
		postVictoryGameChildScene.addMenuItem(victoryMenuItem);
		postVictoryGameChildScene.addMenuItem(quitMenuItem);
	    
	    postVictoryGameChildScene.buildAnimations();
		postVictoryGameChildScene.setBackgroundEnabled(false);

		victoryMenuItem.setPosition(250, 125);
	    quitMenuItem.setPosition(250,  275);
	    
		postVictoryGameChildScene.setOnMenuItemClickListener(this);
	}
	
	private void createDefeatPostGameScene(){
		postDefeatGameChildScene = new MenuScene(camera);
			
		final IMenuItem quitMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_QUIT, resourcesManager.back_button_region, vbom), 1.2f, 1);
		final IMenuItem defeatMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(33, resourcesManager.defeat_region, vbom), 1, 1);
		
		postDefeatGameChildScene.addMenuItem(defeatMenuItem);
		postDefeatGameChildScene.addMenuItem(quitMenuItem);
		
	    postDefeatGameChildScene.buildAnimations();
		postDefeatGameChildScene.setBackgroundEnabled(false);

		defeatMenuItem.setPosition(250, 125);
	    quitMenuItem.setPosition(250,  275);
		
		postDefeatGameChildScene.setOnMenuItemClickListener(this);
	}
	private void createWaitChildScene() {
		
		
		waitChildScene = new MenuScene(camera);
		
	    final IMenuItem quitMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_QUIT, resourcesManager.back_button_region, vbom), 1.2f, 1);

	    waitChildScene.addMenuItem(quitMenuItem);
	    
	    waitChildScene.buildAnimations();
	    waitChildScene.setBackgroundEnabled(false);
	    quitMenuItem.setPosition(250,  275);
	    
	    waitChildScene.setOnMenuItemClickListener(this);
		
	}


	@Override
	public void onBackKeyPressed()
	{
		((GameScene) SceneManager.getInstance().getCurrentScene()).pause();
	}
	
	public void activePauseChildScene() {
		if(getChildScene()!=postDefeatGameChildScene && getChildScene()!=postVictoryGameChildScene ){
		setChildScene(pauseChildScene,false,true,true);
		}
	}
	
	@Override
	public SceneType getSceneType() {
		// TODO bu yok?
		return null;
	}

	@Override
	public void disposeScene()
	{	
		camera.setChaseEntity(null); //heb
	    camera.setHUD(null);
	    camera.setCenter(400, 240);
	}
	
	private void createBackground()
	{
		fieldSprite = new Sprite(0, 0, ResourcesManager.getInstance().world_region, vbom);
		backgroundSprite = new Sprite(0, 0, ResourcesManager.getInstance().bacground_region, vbom){
			@Override
			protected void preDraw(final GLState pGLState, final Camera pCamera)
		    {
		        super.preDraw(pGLState, pCamera);
		        pGLState.enableDither();
		    }
		};//Geçiþleri yumuþatýyor
		ParallaxBackground background = new ParallaxBackground(0, 0, 0);		
		background.attachParallaxEntity(new ParallaxEntity(0, backgroundSprite));
		setBackground(background);
		attachChild(fieldSprite);

		
	}

	private void createHUD()
	{
	    gameHUD = new Hud(plane, camera, vbom);
	    
	}
	
	public void createPhysics()
	{
		Vector2[] vertices = { //Custom body for ground
				new Vector2(0,806/32f),
				new Vector2(187/32f,792/32f),
				new Vector2(558/32f,815/32f),
				new Vector2(558/32f,857/32f),
		};
		Vector2[] vertices2 = { //Custom body for ground
				new Vector2(559/32f,815/32f),
				new Vector2(860/32f,783/32f),
				new Vector2(1268/32f,822/32f),
				new Vector2(1268/32f,862/32f),
		};
		Vector2[] vertices3 = { //Custom body for ground
				new Vector2(1268/32f,862/32f),
				new Vector2(1600/32f,791/32f),
				new Vector2(1600/32f,841/32f),
		};
	    physicsWorld = new FixedStepPhysicsWorld(60, new Vector2(0, 0), false);		
	    //bu kýsým world isimli yeni bir classa taþýnabilir
	    ground = new Rectangle(0, 0 , 0, 0, vbom);
		//roof = new Rectangle(0, 0, WORLD_WIDTH, 1, vbom);
		groundBody = PhysicsFactory.createPolygonBody(physicsWorld, ground, vertices, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		groundBody.setUserData("ground");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(ground, groundBody,true , true));
		groundBody2 = PhysicsFactory.createPolygonBody(physicsWorld, ground, vertices2, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		groundBody2.setUserData("ground");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(ground, groundBody2,true , true));
		groundBody3 = PhysicsFactory.createPolygonBody(physicsWorld, ground, vertices3, BodyType.StaticBody, PhysicsFactory.createFixtureDef(0, 0, 0));
		groundBody3.setUserData("ground");
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(ground, groundBody3,true , true));

		//end of kýsým
		registerUpdateHandler(physicsWorld);
	}
	
	public void createGameObjects(){
		arrowSprite = new Sprite(-9999, -9999, ResourcesManager.getInstance().arrow_region, vbom);
		attachChild(arrowSprite);
		score = 0;
	    enemyScore = 0;
		explosionSprite = new AnimatedSprite(0, 0, ResourcesManager.getInstance().explosion_region, vbom);
		plane = new Plane(0, 0, vbom, camera, physicsWorld,ResourcesManager.getInstance().plane_regions[MainMenuScene.selected_plane],(int)MainMenuScene.selected_plane/2);
	    attachChild(plane);
	    planeEnemy = new PlaneEnemy(0, 0, vbom, camera, physicsWorld);
	    attachChild(planeEnemy);
	    attachChild((Sprite) plane.missileBody.getUserData());
	    plane.missileBody.setUserData("missile");
	    attachChild((Sprite) planeEnemy.enemyMissileBody.getUserData());
	    planeEnemy.enemyMissileBody.setUserData("missileEnemy");
	    for(int i=0;i<plane.maxShot;i++){
		   /*plane.*/attachChild((Sprite) plane.shots.get(i).getUserData());
		   plane.shots.get(i).setUserData("shot");
	    }	    			
	    for(int i=0;i<planeEnemy.maxShot;i++){
			/*planeEnemy.*/attachChild((Sprite) planeEnemy.shots.get(i).getUserData());
			planeEnemy.shots.get(i).setUserData("shotEnemy");
	    }
	   
	}
	
	//TODO burda biþiler çok fena yanlýþ
	


	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		switch(pMenuItem.getID())
        {
        case MENU_RESUME:
        	((GameScene) SceneManager.getInstance().getCurrentScene()).resume();
        	return true;
        case MENU_QUIT:
        	SceneManager.getInstance().loadMenuScene(engine);
        	return true;
        default:
            return false;
        }
	}
	public void victory(){
		
		setChildScene(postVictoryGameChildScene,false,true,true);
	}
	public void defeat(){
		setChildScene(postDefeatGameChildScene, false, true, true);
	}

	public void pause() {
		activePauseChildScene();
	}
	
	public void resume(){
		pauseChildScene.closeMenuScene();
	}


	public void sendPauseMessage(boolean b) {
		//oyun multi deðilse buna gerek yok, multiler için override edilir	
	}

}
