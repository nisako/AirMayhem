package com.example.araprojenew;

import org.andengine.entity.sprite.Sprite;
public class Powerup extends Sprite
{
	private Plane plane;
	private powerupType pType;
	public Powerup(float pX, float pY,Plane mPlane,powerupType mpType){
		super(pX, pY, ResourcesManager.getInstance().powerup_region, ResourcesManager.getInstance().vbom);
		this.plane = mPlane;
		this.pType = mpType;
	}
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		if (plane.collidesWith(this))
        {
            plane.aplyPowerup(pType);
            this.setVisible(false);
            this.setIgnoreUpdate(true);
            //this.detechSelf();
        }
	}
}