package com.example.araprojenew;

import org.andengine.entity.sprite.Sprite;
public class Powerup extends Sprite
{
	private Plane plane;
	private powerupType pType;
	private PlaneEnemy planeEnemy;
	public Powerup(float pX, float pY,Plane mPlane,PlaneEnemy planeEnemy,powerupType mpType){
		
		super(pX, pY, ResourcesManager.getInstance().powerup_region, ResourcesManager.getInstance().vbom);
		this.plane = mPlane;
		this.pType = mpType;
		this.planeEnemy = planeEnemy;
	}
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		if (this.collidesWith(planeEnemy))
        {
            this.setVisible(false);
            this.setIgnoreUpdate(true);
        }
		else if (this.collidesWith(plane))
        {
			plane.aplyPowerup(pType);
            this.setVisible(false);
            this.setIgnoreUpdate(true);
        }
	}
	public powerupType getType(){
		return pType;
	}
	public void remove() {
		this.setVisible(false);
        this.setIgnoreUpdate(true);
	}
}