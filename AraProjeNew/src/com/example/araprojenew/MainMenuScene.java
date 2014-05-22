package com.example.araprojenew;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;

import com.example.araprojenew.SceneManager.SceneType;


public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {
	private MenuScene menuChildScene,optionsMenuChildScene,playMenuChildScene,aboutMenuChildScene,howMenuChildScene;
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
	
	
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
		createOptionsMenuChildScene();
		createPlayMenuChildScene();
		createAboutMenuChildScene();
		createHowMenuChildScene();
	}
	
	

	@Override
	public void onBackKeyPressed() {
		//TODO zaten menüde ise back'e bastýðýnda çýkabilir
		if(getChildScene()==optionsMenuChildScene)setChildScene(menuChildScene);
		else if(getChildScene()==playMenuChildScene)setChildScene(menuChildScene);
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
	    setBackground(new SpriteBackground(new Sprite(0, 0, ResourcesManager.getInstance().bacground_region, vbom)));
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
	
		    playMenuChildScene.addMenuItem(practiceMenuItem);
		    playMenuChildScene.addMenuItem(hostMenuItem);
		    playMenuChildScene.addMenuItem(joinMenuItem);
		    
		    playMenuChildScene.buildAnimations();
		    playMenuChildScene.setBackgroundEnabled(false);
		    
		    practiceMenuItem.setPosition(ITEM_WIDTH, -170);
		    hostMenuItem.setPosition(ITEM_WIDTH, - 50);
		    joinMenuItem.setPosition(ITEM_WIDTH, +70);
		    
		    playMenuChildScene.setOnMenuItemClickListener(this);
	}
	
	public void createOptionsMenuChildScene()
	{
		optionsMenuChildScene = new MenuScene(camera);
		optionsMenuChildScene.setPosition(400, 240);
		final IMenuItem audioMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_AUDIO, resourcesManager.audio_region, vbom), 1.2f, 1);
	    final IMenuItem musicMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MUSIC, resourcesManager.music_region, vbom), 1.2f, 1);
	    
	    optionsMenuChildScene.addMenuItem(audioMenuItem);
	    optionsMenuChildScene.addMenuItem(musicMenuItem);
	    
	    optionsMenuChildScene.buildAnimations();
	    optionsMenuChildScene.setBackgroundEnabled(false);
	    
	    audioMenuItem.setPosition(ITEM_WIDTH, -90);
	    musicMenuItem.setPosition(ITEM_WIDTH, + 20);
	    
	    optionsMenuChildScene.setOnMenuItemClickListener(this);
		
	}
	
	public void createAboutMenuChildScene(){
		aboutMenuChildScene = new MenuScene(camera);
		aboutMenuChildScene.setPosition(400, 240);
		
		final IMenuItem creditsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(99, resourcesManager.credits_region, vbom), 1, 1);
		aboutMenuChildScene.addMenuItem(creditsMenuItem);
		aboutMenuChildScene.setBackgroundEnabled(false);
		
		//creditsMenuItem.setPosition(-creditsMenuItem.getWidth()/2, -creditsMenuItem.getHeight()/2);
		creditsMenuItem.setPosition(-creditsMenuItem.getWidth()/2,-creditsMenuItem.getHeight()/2);
	}
	
	public void createHowMenuChildScene(){
		howMenuChildScene = new MenuScene(camera);
		howMenuChildScene.setPosition(400, 240);		
		howMenuChildScene.setBackgroundEnabled(false);
		
		
	}
	
	/*public void preGamesMenuChildScene()
	{
		optionsMenuChildScene = new MenuScene(camera);
		optionsMenuChildScene.setPosition(400, 240);
		final IMenuItem audioMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_AUDIO, resourcesManager.audio_region, vbom), 1.2f, 1);
	    final IMenuItem musicMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_MUSIC, resourcesManager.music_region, vbom), 1.2f, 1);
	    
	    optionsMenuChildScene.addMenuItem(audioMenuItem);
	    optionsMenuChildScene.addMenuItem(musicMenuItem);
	    
	    optionsMenuChildScene.buildAnimations();
	    optionsMenuChildScene.setBackgroundEnabled(false);
	    
	    audioMenuItem.setPosition(-200, -110);
	    musicMenuItem.setPosition(-200, + 0);
	    
	    optionsMenuChildScene.setOnMenuItemClickListener(this);
		
	}*/

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
	        switch(pMenuItem.getID())
	        {
	        case MENU_PLAY:
	        	//SceneManager.getInstance().loadPractiseGameScene(engine);
	        	setChildScene(playMenuChildScene);
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
	        default:
	            return false;
	    }
	}

	

}
