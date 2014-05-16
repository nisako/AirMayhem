package com.example.araprojenew;

import java.io.IOException;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.graphics.Color;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class ResourcesManager {
	//singleton class,tüm ortak nesnelere bu class üstünden eriþilecek
	//TODO her texture için bi texture atlas yaratmak daha performanslý olabilir?
	private static final ResourcesManager INSTANCE = new ResourcesManager();
	
	//Ortak nesneler
    public Engine engine;
    public GameActivity activity;
    public Camera camera; //niye bound deil
    public VertexBufferObjectManager vbom;
    public float sensor1,sensor2;
    
    private SensorManager sensorManager;
    
    
    public ITiledTextureRegion button_region;
    public ITiledTextureRegion pause_button_region;
    public ITextureRegion resume_button_region;
    public ITextureRegion back_button_region;
    
    public ITextureRegion play_region;
    public ITextureRegion options_region;
    public ITextureRegion music_region;
    public ITextureRegion audio_region;
    public ITiledTextureRegion explosion_region;
    public ITextureRegion practice_region;
    public ITextureRegion host_region;
    public ITextureRegion join_region;
    public ITextureRegion splash1_region;
    public ITextureRegion splash2_region;
    public ITextureRegion pennant1_region;
    public ITextureRegion pennant2_region;
  
    
    private BuildableBitmapTextureAtlas menuTextureAtlas;
    private BuildableBitmapTextureAtlas worldTextureAtlas;
    public BuildableBitmapTextureAtlas splashTextureAtlas;
    public BuildableBitmapTextureAtlas gameTextureAtlas;
    
    public ITiledTextureRegion plane_region;
    public ITiledTextureRegion planeEnemy_region;
    public ITextureRegion world_region;
    public ITextureRegion bacground_region;
    public ITextureRegion shot_region;
    public ITiledTextureRegion balloon_region;
    public ITextureRegion arrow_region;
    public ITextureRegion powerup_region;

    public Music music;
    public Sound fireSound;
    public Sound explosionSound;
    public Sound alternateFireSound;    
    
    public Font splashScreenFont;
    public Font hudFont;

	
    
    public void loadMenuResources()
    {
        loadMenuGraphics();
        loadMenuAudio();
        loadMenuFonts();
    }
    
    public void loadGameResources()
    {
        loadGameGraphics();
        loadGameFonts();
        loadGameAudio();
        loadCensor();
    }
    
    private void loadCensor() {
    	
    	sensorManager = (SensorManager) activity.getSystemService(GameActivity.SENSOR_SERVICE);
    	SensorListener.getSharedInstance();
    	sensorManager.registerListener(SensorListener.getSharedInstance(),
    	sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
    	SensorManager.SENSOR_DELAY_GAME);

		
	}

	private void loadMenuGraphics()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
    	menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
    	play_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "play.png");
    	options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "options.png");
    	audio_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "audio.png");
    	music_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "music.png");
    	practice_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity , "practice.png");
        host_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity , "host.png");
        join_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity , "join.png");
        
    	try 
    	{
    	    this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
    	    this.menuTextureAtlas.load();
    	} 
    	catch (final TextureAtlasBuilderException e)
    	{
    	        Debug.e(e);
    	}
    }
    
    private void loadMenuAudio()
    {
    	

    	try
    	{
    	    music = MusicFactory.createMusicFromAsset(engine.getMusicManager(), activity,"mfx/music.ogg");
    	    fireSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/fire.ogg");
    	    explosionSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/explosion.ogg");
    	    alternateFireSound = SoundFactory.createSoundFromAsset(engine.getSoundManager(), activity, "mfx/alternateFire.ogg");
    	}
    	catch (IOException e)
    	{
    	    e.printStackTrace();
    	}
    	if(SharedPreferencesManager.getInstance().getMusicEnabled())
    		getInstance().music.play();
    	music.setLooping(true);
    }

    private void loadGameGraphics()
    {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
        gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 2000, 2000, TextureOptions.DEFAULT);
        button_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "button1.png", 2, 1);
        pause_button_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "pause.png", 2, 1);
        resume_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "resume.png");
        back_button_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "menu.png");
        world_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "field.png");
        plane_region =  BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "planex.png",6,1);
        planeEnemy_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "enemyPlanex.png",4,1);
        bacground_region =  BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "background.png");
        explosion_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "explosion.png", 12, 1);
        shot_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "shot.png");
        balloon_region = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(gameTextureAtlas, activity, "balloon_tiled.png", 6, 1);
        arrow_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "arrow.png");
        powerup_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "powerup.png");
        pennant1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "pennant1.png");
        pennant2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "pennant2.png");
        try 
        {
            this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
            this.gameTextureAtlas.load();
        } 
        catch (final TextureAtlasBuilderException e)
        {
            Debug.e(e);
        }
    }

    
    private void loadGameFonts()
    {
    	FontFactory.setAssetBasePath("font/");
        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        hudFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 28, true, Color.WHITE, 2, Color.WHITE);
        hudFont.load();
        //unload yazýlmadý
        //hudFont = FontFactory.create(activity.getFontManager(),activity.getTextureManager(), 256, 256,Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 28);
       // hudFont.load();

    }
    
    private void loadGameAudio()
    {
        
    }
    
    public void loadSplashScreen()
    {
    	BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");    	
    	splashTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.DEFAULT);
    	splash1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "thunder.png");
    	splash2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "struck.png");
    	splashScreenFont = FontFactory.create(activity.getFontManager(),activity.getTextureManager(), 256, 256,Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		splashScreenFont.load();
		 try 
	        {
	            this.splashTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
	            this.splashTextureAtlas.load();
	        } 
	        catch (final TextureAtlasBuilderException e)
	        {
	            Debug.e(e);
	        }
		
    }
    
    public void unloadSplashScreen()
    {
    	splashTextureAtlas.unload();
    }
    
    private void loadMenuFonts()
    {
        FontFactory.setAssetBasePath("font/");
        final ITexture mainFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

        splashScreenFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), mainFontTexture, activity.getAssets(), "font.ttf", 50, true, Color.WHITE, 2, Color.WHITE);
        splashScreenFont.load();
    }
    
    public void unloadMenuTextures()
    {
        menuTextureAtlas.unload();
    }
        
    public void unloadGameTextures()
    {
        // TODO gerekliliði tartýþýlýr
    }
    
    public void loadMenuTextures()
    {
        menuTextureAtlas.load();
    }
    
    /**
     * @param engine
     * @param activity
     * @param camera
     * @param vbom
     * <br><br>
     * We use this method at beginning of game loading, to prepare Resources Manager properly,
     * setting all needed parameters, so we can latter access them from different classes (eg. scenes)
     */
    public static void prepareManager(Engine engine, GameActivity activity, Camera camera, VertexBufferObjectManager vbom)
    {
        getInstance().engine = engine;
        getInstance().activity = activity;
        getInstance().camera = camera;
        getInstance().vbom = vbom;
    }

    public static ResourcesManager getInstance()
    {
        return INSTANCE;
    }
}
