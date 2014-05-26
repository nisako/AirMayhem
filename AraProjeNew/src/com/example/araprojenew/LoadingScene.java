package com.example.araprojenew;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.background.SpriteBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.opengl.util.GLState;

import com.example.araprojenew.SceneManager.SceneType;

public class LoadingScene extends BaseScene
{
	@Override
	public void createScene()
	{
		setBackground(new SpriteBackground(new Sprite(0, 0, ResourcesManager.getInstance().bacground_region, vbom){
		    protected void preDraw(final GLState pGLState, final Camera pCamera)
		    {
		        super.preDraw(pGLState, pCamera);
		        pGLState.enableDither();
		    }
		}
		    		));
	    attachChild(new Text(300, 200, resourcesManager.splashScreenFont, "Loading...", vbom));
	}

    @Override
    public void onBackKeyPressed()
    {
        return;
    }

    @Override
    public SceneType getSceneType()
    {
        return SceneType.SCENE_LOADING;
    }

    @Override
    public void disposeScene()
    {

    }

}