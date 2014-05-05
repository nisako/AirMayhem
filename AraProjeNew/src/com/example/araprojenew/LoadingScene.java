package com.example.araprojenew;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.color.Color;

import com.example.araprojenew.SceneManager.SceneType;

/*TODO boþ beyaz ekran bura hala*/
public class LoadingScene extends BaseScene
{
	@Override
	public void createScene()
	{
	    setBackground(new Background(Color.WHITE));
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