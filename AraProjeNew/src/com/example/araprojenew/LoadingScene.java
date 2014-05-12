package com.example.araprojenew;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;

import com.example.araprojenew.SceneManager.SceneType;

public class LoadingScene extends BaseScene
{
	@Override
	public void createScene()
	{
	    setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
	    attachChild(new Text(400, 240, resourcesManager.splashScreenFont, "Loading...", vbom));
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