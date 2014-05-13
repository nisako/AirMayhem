package com.example.araprojenew;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.BaseGameActivity;

import android.view.KeyEvent;


/*TODO genel olarak scene sonunda yok edilmeyen kaynaklar oluyor, bi araþtýrýlmalý
 * Pause resume ile ilgili de sorun var.
 */
public class GameActivity extends BaseGameActivity{
	
	private BoundCamera camera;
	@SuppressWarnings("unused") private ResourcesManager resourcesManager;
	
	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) 
	{
	    return new LimitedFPSEngine(pEngineOptions, 60);//60 fps
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new BoundCamera(0, 0, GameScene.WORLD_WIDTH/2, GameScene.WORLD_HEIGHT/2); 
		camera.setBoundsEnabled(true);
		camera.setBounds(0, 0, GameScene.WORLD_WIDTH, GameScene.WORLD_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), this.camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
	    engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
	    engineOptions.getTouchOptions().setNeedsMultiTouch(true);
	    return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException
	{
	    ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
	    resourcesManager = ResourcesManager.getInstance();
	    pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback)
	{
    	SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException
	{
	    mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
	    {
	            public void onTimePassed(final TimerHandler pTimerHandler) 
	            {
	                mEngine.unregisterUpdateHandler(pTimerHandler);
	                SceneManager.getInstance().createMenuScene();
	            }
	    }));
	    pOnPopulateSceneCallback.onPopulateSceneFinished();
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		if (this.isGameLoaded())
			System.exit(0);	
	}
	
	@Override
	protected void onPause()
	{
	    super.onPause();
	    if (this.isGameLoaded()){
	        try{
	        ResourcesManager.getInstance().music.pause();
	        ((GameScene) SceneManager.getInstance().getCurrentScene()).pause();
	        }
	        catch(Exception e){
	        	//pause yoksa biþi yapma
	        }
	    }
	}

	@Override
	protected synchronized void onResume()
	{
	    super.onResume();
	    System.gc();
	    if (this.isGameLoaded()){ 	
	    	 try{
	    		 if(SharedPreferencesManager.getInstance().getMusicEnabled())
	    			 ResourcesManager.getInstance().music.resume();
	 	        }
	 	        catch(Exception e){
	 	        	//resume yoksa biþi yapma
	 	        }
	    }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{  
	    if (keyCode == KeyEvent.KEYCODE_BACK)
	    {
	        SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
	    }
	    return false; 
	}
	

	
}
