package com.example.araprojenew;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;

import com.example.araprojenew.SceneManager.SceneType;


public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {
	private MenuScene menuChildScene,optionsMenuChildScene,playMenuChildScene;
	private final int MENU_PLAY = 0;
	private final int MENU_OPTIONS = 1;
	private final int MENU_AUDIO = 2;
	private final int MENU_MUSIC = 3;
	private final int MENU_PRACTICE = 4;
	private final int MENU_HOST = 5;
	private final int MENU_JOIN = 6;
	
	
	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
		createOptionsMenuChildScene();
		createPlayMenuChildScene();
	}
	
	

	@Override
	public void onBackKeyPressed() {
		//TODO zaten menüde ise back'e bastýðýnda çýkabilir
		if(getChildScene()==optionsMenuChildScene)setChildScene(menuChildScene);
		else if(getChildScene()==playMenuChildScene)setChildScene(menuChildScene);
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
		// TODO boþ dispose gene
	}
		
	private void createBackground()
	{
	    setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
	}
	
	private void createMenuChildScene()
	{
	    menuChildScene = new MenuScene(camera);
	    menuChildScene.setPosition(400, 240);
	    
	    final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_PLAY, resourcesManager.play_region, vbom), 1.2f, 1);
	    final IMenuItem optionsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU_OPTIONS, resourcesManager.options_region, vbom), 1.2f, 1);
	    
	    menuChildScene.addMenuItem(playMenuItem);
	    menuChildScene.addMenuItem(optionsMenuItem);
	    
	    menuChildScene.buildAnimations();
	    menuChildScene.setBackgroundEnabled(false);
	    
	    playMenuItem.setPosition(-200, -90);
	    optionsMenuItem.setPosition(-200, + 20);
	    
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
		    
		    practiceMenuItem.setPosition(-150, -170);
		    hostMenuItem.setPosition(-150, - 50);
		    joinMenuItem.setPosition(-150, +70);
		    
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
	    
	    audioMenuItem.setPosition(-200, -90);
	    musicMenuItem.setPosition(-200, + 20);
	    
	    optionsMenuChildScene.setOnMenuItemClickListener(this);
		
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
	        	if(ResourcesManager.getInstance().music.isPlaying())ResourcesManager.getInstance().music.pause();
	        	else ResourcesManager.getInstance().music.play();
	        	return true;
	        default:
	            return false;
	    }
	}

	

}
