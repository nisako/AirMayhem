package com.example.araprojenew;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.modifier.LoopEntityModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.util.GLState;

import com.example.araprojenew.SceneManager.SceneType;


public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {
	private MenuScene menuChildScene,optionsMenuChildScene,preGameMenuChildScene,playMenuChildScene,aboutMenuChildScene,howMenuChildScene;
	private final int ITEM_WIDTH = 125;
	private final int MENU_PLAY = 0;
	private final int MENU_OPTIONS = 1;
	private final int MENU_AUDIO = 2;
	private final int MENU_MUSIC = 3;
	private final int MENU_PRACTICE = 4;
	private final int MENU_HOST = 5;
	private final int MENU_JOIN = 6;
	private final int MENU_ABOUT = 7;
	private final int MENU_HOW = 8;
	private final int MENU_BACK = 9;
	//TODO bu backler tam olmadý sanýrým
	
	
	public static int selected_plane=0; //tasarým faciasý olduðunu biliyorum ama daha iyi bi çözüm bulamadým
	
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
		createOptionsMenuChildScene();
		createPlayMenuChildScene();
		createAboutMenuChildScene();
		createHowMenuChildScene();
		createPreGameMenuChildScene();
	}
	
	

	@Override
	public void onBackKeyPressed() {
		//TODO zaten menüde ise back'e bastýðýnda çýkabilir
		if(getChildScene()==optionsMenuChildScene)setChildScene(menuChildScene);
		else if(getChildScene()==playMenuChildScene)setChildScene(preGameMenuChildScene);
		else if(getChildScene()==preGameMenuChildScene)setChildScene(menuChildScene);
		else if(getChildScene()==aboutMenuChildScene)setChildScene(menuChildScene);
		else if(getChildScene()==howMenuChildScene)setChildScene(menuChildScene);
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		// TODO boþ dispose gene
	}
	
	//setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
	private void createBackground()
	{
	    setBackground(new SpriteBackground(new Sprite(0, 0, ResourcesManager.getInstance().bacground_region, vbom){
	    protected void preDraw(final GLState pGLState, final Camera pCamera)
	    {
	        super.preDraw(pGLState, pCamera);
	        pGLState.enableDither();
	    }
	}
	    		));
	    		
	    AnimatedSprite menuplane1 = new AnimatedSprite(20, 20, ResourcesManager.getInstance().plane_regions[0], vbom);
	    AnimatedSprite menuplane2 = new AnimatedSprite(20, 420, ResourcesManager.getInstance().plane_regions[6], vbom);
	    AnimatedSprite menuplane3 = new AnimatedSprite(-120, 120, ResourcesManager.getInstance().plane_regions[2], vbom);
	    AnimatedSprite menuplane4 = new AnimatedSprite(-120, 320, ResourcesManager.getInstance().plane_regions[5], vbom);
	    AnimatedSprite menuplane5 = new AnimatedSprite(-170, 220, ResourcesManager.getInstance().plane_regions[7], vbom);
	    menuplane1.animate(1);
	    menuplane2.animate(1);
	    menuplane3.animate(1);
	    menuplane4.animate(1);
	    menuplane5.animate(1);
	    attachChild(menuplane1);
	    attachChild(menuplane2);
	    attachChild(menuplane3);
	    attachChild(menuplane4);
	    attachChild(menuplane5);
	    menuplane1.registerEntityModifier(new LoopEntityModifier(new MoveXModifier(13,0,850 )));
	    menuplane2.registerEntityModifier(new LoopEntityModifier(new MoveXModifier(13,0,850 )));
	    menuplane3.registerEntityModifier(new LoopEntityModifier(new MoveXModifier(13,-120,850 )));
	    menuplane4.registerEntityModifier(new LoopEntityModifier(new MoveXModifier(13,-120,850 )));
	    menuplane5.registerEntityModifier(new LoopEntityModifier(new MoveXModifier(13,-170,850 )));
	    
	  
	}
	
	private void createMenuChildScene()
	{
	    menuChildScene = new MenuScene(camera);
	    menuChildScene.setPosition(400, 240);
	    
	    final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);
	    final IMenuItem optionsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OPTIONS, resourcesManager.options_region, vbom), 1.2f, 1);
	    final IMenuItem aboutMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_ABOUT, resourcesManager.about_region, vbom), 1.2f, 1);
	    final IMenuItem howMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HOW, resourcesManager.howtoplay_region, vbom), 1.2f, 1);
	   
	    
	    menuChildScene.addMenuItem(playMenuItem);
	    menuChildScene.addMenuItem(optionsMenuItem);
	    menuChildScene.addMenuItem(aboutMenuItem);
	    menuChildScene.addMenuItem(howMenuItem);
	    
	    menuChildScene.buildAnimations();
	    menuChildScene.setBackgroundEnabled(false);
	    
	    playMenuItem.setPosition(ITEM_WIDTH, -200);
	    optionsMenuItem.setPosition(ITEM_WIDTH, -100);
	    howMenuItem.setPosition(ITEM_WIDTH, 0);
	    aboutMenuItem.setPosition(ITEM_WIDTH, +100);
	    
	    
	    menuChildScene.setOnMenuItemClickListener(this);
	    
	    setChildScene(menuChildScene);
	}
	
	private void createPlayMenuChildScene() 
	{
			playMenuChildScene = new MenuScene(camera);
			playMenuChildScene.setPosition(400, 240);
			final IMenuItem practiceMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PRACTICE, resourcesManager.practice_region, vbom), 1.2f, 1);
		    final IMenuItem hostMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_HOST, resourcesManager.host_region, vbom), 1.2f, 1);
		    final IMenuItem joinMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_JOIN, resourcesManager.join_region, vbom), 1.2f, 1);
		    final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_BACK, resourcesManager.menubackbutton_region, vbom), 1.2f, 1);
		   
		    playMenuChildScene.addMenuItem(practiceMenuItem);
		    playMenuChildScene.addMenuItem(hostMenuItem);
		    playMenuChildScene.addMenuItem(joinMenuItem);
		    playMenuChildScene.addMenuItem(backMenuItem);
		    
		    playMenuChildScene.buildAnimations();
		    playMenuChildScene.setBackgroundEnabled(false);
		    
		    practiceMenuItem.setPosition(ITEM_WIDTH, -170);
		    hostMenuItem.setPosition(ITEM_WIDTH, - 50);
		    joinMenuItem.setPosition(ITEM_WIDTH, +70);
		    backMenuItem.setPosition(-400,120);
		    
		    playMenuChildScene.setOnMenuItemClickListener(this);
	}
	
	public void createOptionsMenuChildScene()
	{
		optionsMenuChildScene = new MenuScene(camera);
		optionsMenuChildScene.setPosition(400, 240);
		final IMenuItem audioMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_AUDIO, resourcesManager.audio_region, vbom), 1.2f, 1);
	    final IMenuItem musicMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MUSIC, resourcesManager.music_region, vbom), 1.2f, 1);
	    final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_BACK, resourcesManager.menubackbutton_region, vbom), 1.2f, 1); 
	    
	    optionsMenuChildScene.addMenuItem(audioMenuItem);
	    optionsMenuChildScene.addMenuItem(musicMenuItem);
	    optionsMenuChildScene.addMenuItem(backMenuItem);
	    
	    optionsMenuChildScene.buildAnimations();
	    optionsMenuChildScene.setBackgroundEnabled(false);
	    
	    backMenuItem.setPosition(-400,120);
	    audioMenuItem.setPosition(ITEM_WIDTH, -90);
	    musicMenuItem.setPosition(ITEM_WIDTH, + 20);
	    
	    optionsMenuChildScene.setOnMenuItemClickListener(this);
		
	}
	
	public void createAboutMenuChildScene(){
		aboutMenuChildScene = new MenuScene(camera);
		aboutMenuChildScene.setPosition(400, 240);
		
		final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_BACK, resourcesManager.menubackbutton_region, vbom), 1.2f, 1); 
		final IMenuItem creditsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(99, resourcesManager.credits_region, vbom), 1, 1);
		
		aboutMenuChildScene.addMenuItem(backMenuItem);
		aboutMenuChildScene.addMenuItem(creditsMenuItem);
		aboutMenuChildScene.setBackgroundEnabled(false);
		
		backMenuItem.setPosition(-400,120);
		creditsMenuItem.setPosition(-creditsMenuItem.getWidth()/2,-creditsMenuItem.getHeight()/2);
		
		  aboutMenuChildScene.setOnMenuItemClickListener(this);
	}
	
	public void createHowMenuChildScene(){
		howMenuChildScene = new MenuScene(camera);
		howMenuChildScene.setPosition(400, 240);	
		
		final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_BACK, resourcesManager.menubackbutton_region, vbom), 1.2f, 1); 
		howMenuChildScene.addMenuItem(backMenuItem);
		howMenuChildScene.setBackgroundEnabled(false);
		
		backMenuItem.setPosition(-400,120);
		
		  howMenuChildScene.setOnMenuItemClickListener(this);
	}
	
	public void createPreGameMenuChildScene()
	{
		preGameMenuChildScene = new MenuScene(camera);
		preGameMenuChildScene.setPosition(400, 350);
		
		final IMenuItem choose = new ScaleMenuItemDecorator(new SpriteMenuItem(123, resourcesManager.planechoose_region, vbom), 1, 1);
		final IMenuItem backMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_BACK, resourcesManager.menubackbutton_region, vbom), 1.2f, 1); 
		final IMenuItem plane11 = new ScaleMenuItemDecorator(new SpriteMenuItem(11, resourcesManager.plane_regions[0], vbom), 1.2f, 1);
	    final IMenuItem plane12 = new ScaleMenuItemDecorator(new SpriteMenuItem(12, resourcesManager.plane_regions[1], vbom), 1.2f, 1);
	    final IMenuItem plane21 = new ScaleMenuItemDecorator(new SpriteMenuItem(21, resourcesManager.plane_regions[2], vbom), 1.2f, 1);
	    final IMenuItem plane22 = new ScaleMenuItemDecorator(new SpriteMenuItem(22, resourcesManager.plane_regions[3], vbom), 1.2f, 1);
	    final IMenuItem plane31 = new ScaleMenuItemDecorator(new SpriteMenuItem(31, resourcesManager.plane_regions[4], vbom), 1.2f, 1);
	    final IMenuItem plane32 = new ScaleMenuItemDecorator(new SpriteMenuItem(32, resourcesManager.plane_regions[5], vbom), 1.2f, 1);
	    final IMenuItem plane41 = new ScaleMenuItemDecorator(new SpriteMenuItem(41, resourcesManager.plane_regions[6], vbom), 1.2f, 1);
	    final IMenuItem plane42 = new ScaleMenuItemDecorator(new SpriteMenuItem(42, resourcesManager.plane_regions[7], vbom), 1.2f, 1);
	    
	    preGameMenuChildScene.addMenuItem(choose);
	    preGameMenuChildScene.addMenuItem(backMenuItem);
	    preGameMenuChildScene.addMenuItem(plane11);
	    preGameMenuChildScene.addMenuItem(plane12);
	    preGameMenuChildScene.addMenuItem(plane21);
	    preGameMenuChildScene.addMenuItem(plane22);
	    preGameMenuChildScene.addMenuItem(plane31);
	    preGameMenuChildScene.addMenuItem(plane32);
	    preGameMenuChildScene.addMenuItem(plane41);
	    preGameMenuChildScene.addMenuItem(plane42);
	    
	    preGameMenuChildScene.buildAnimations();
	    preGameMenuChildScene.setBackgroundEnabled(false);
	    
	    choose.setPosition(-325,-250);
	    backMenuItem.setPosition(-400,0);
	    plane11.setPosition(-200, -100);
	    plane12.setPosition(-200, 0);
	    plane21.setPosition(-100, -100);
	    plane22.setPosition(-100, 0);
	    plane31.setPosition(0, -100);
	    plane32.setPosition(0, 0);
	    plane41.setPosition(100, -100);
	    plane42.setPosition(100, 0);
	    
	    preGameMenuChildScene.setOnMenuItemClickListener(this);
	    
		
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
	        switch(pMenuItem.getID())
	        {
	        case MENU_PLAY:
	        	setChildScene(preGameMenuChildScene);
	        	return true;
	        case MENU_PRACTICE:
	        	SceneManager.getInstance().loadPractiseGameScene(engine);
	        	return true;
	        case MENU_HOST:
	        	SceneManager.getInstance().loadHostGameScene(engine);
	        	return true;
	        case MENU_JOIN:
	        	SceneManager.getInstance().loadClientGameScene(engine);
	        	return true;
	        case MENU_OPTIONS:
	    	    setChildScene(optionsMenuChildScene);
	    	    return true;
	        case MENU_AUDIO:
	        	//TODO Audio'larý master bir noktada yönetmek çok olasý gözükmüyor þu anda
	        	return true;
	        case MENU_MUSIC:
	        	if(ResourcesManager.getInstance().music.isPlaying()){
	        		ResourcesManager.getInstance().music.pause();
	        		SharedPreferencesManager.getInstance().setMusicEnabled(false);
	        	}
	        	else {
	        		ResourcesManager.getInstance().music.play();
	        		SharedPreferencesManager.getInstance().setMusicEnabled(true);
	        	}
	        	return true;
	        case MENU_ABOUT:
	        	setChildScene(aboutMenuChildScene);
	        	return true;
	        case MENU_HOW:
	        	setChildScene(howMenuChildScene);
	        	return true;
	        case MENU_BACK:
	        	if(getChildScene()==optionsMenuChildScene)setChildScene(menuChildScene);
	    		else if(getChildScene()==playMenuChildScene)setChildScene(preGameMenuChildScene);
	    		else if(getChildScene()==preGameMenuChildScene)setChildScene(menuChildScene);
	    		else if(getChildScene()==aboutMenuChildScene)setChildScene(menuChildScene);
	    		else if(getChildScene()==howMenuChildScene)setChildScene(menuChildScene);
	        	return true;
	        case 11:
	        	selected_plane = 0;
	        	setChildScene(playMenuChildScene);
	        	return true;
	        case 12:
	        	selected_plane = 1;
	        	setChildScene(playMenuChildScene);
	        	return true;
	        case 21:
	        	selected_plane = 2;
	        	setChildScene(playMenuChildScene);
	        	return true;
	        case 22:
	        	selected_plane = 3;
	        	setChildScene(playMenuChildScene);
	        	return true;
	        case 31:
	        	selected_plane = 4;
	        	setChildScene(playMenuChildScene);
	        	return true;
	        case 32:
	        	selected_plane = 5;
	        	setChildScene(playMenuChildScene);
	        	return true;
	        case 41:
	        	selected_plane = 6;
	        	setChildScene(playMenuChildScene);
	        	return true;
	        case 42:
	        	selected_plane = 7;
	        	setChildScene(playMenuChildScene);
	        	return true;
	        default:
	            return false;
	    }
	}

	

}
