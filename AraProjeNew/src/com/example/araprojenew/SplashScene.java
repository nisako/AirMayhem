package com.example.araprojenew;

import org.andengine.entity.IEntity;
import org.andengine.entity.modifier.DelayModifier;
import org.andengine.entity.modifier.MoveXModifier;
import org.andengine.entity.modifier.IEntityModifier.IEntityModifierListener;
import org.andengine.entity.modifier.MoveYModifier;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.util.modifier.IModifier;

import com.example.araprojenew.SceneManager.SceneType;

//TODO ufak bi temizlik yapýlsa iyi olur
public class SplashScene extends BaseScene{
	
	
	@Override
	public void createScene() {
		setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		Sprite air = new Sprite(0, 0, ResourcesManager.getInstance().splash1_region,vbom);
		Sprite mayhem = new Sprite(0, 0, ResourcesManager.getInstance().splash2_region,vbom);

		air.setPosition(300,-108);
		mayhem.setPosition(200,480);

		attachChild(air);
		attachChild(mayhem);
		
		
		air.registerEntityModifier(new MoveYModifier(1, air.getY(),100 ));
		mayhem.registerEntityModifier(new MoveYModifier(1, mayhem.getY(),208));
		
		loadResources();
	}
	
	void loadResources() {
		DelayModifier dMod = new DelayModifier( 10000,
				new IEntityModifierListener() {

					@Override
					public void onModifierStarted(IModifier<IEntity> arg0,
							IEntity arg1) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onModifierFinished(IModifier<IEntity> arg0,
							IEntity arg1) {

			        	SceneManager.getInstance().loadMenuScene(engine);
					}
				});

		registerEntityModifier(dMod);
	}
	

	@Override
	public void onBackKeyPressed() {
		// TODO direk menüye geçse mantýklý olur hatta týklama ilede geçilebilmeli sanki bura ama?
		
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene()
	{
	    this.detachSelf();
	    this.dispose();
	}

}

