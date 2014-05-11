package com.example.araprojenew;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
/*TODO bu class fazla statik biraz elden geçmesi gerek*/
public class Plane extends AnimatedSprite{
	public Body body; 
	private AnimatedSprite explosionSprite;
	private boolean isGas;
	private boolean isBreak;
	public boolean isShoot,isAlternateShoot;
	private final int maxSpeed=20;
	
	public ArrayList<Body> shots;
	public Sprite shotSprite;
	public int shotIndex=0;
	public int maxShot=35;
	public int shotType=0; //0 default , 2 double,3 triple
	
	public int maxHealth = 100;
	public int health = maxHealth;
	
	
	private float gravity;

	public boolean animationFlagForPlaneCrush = true;
	
	
	public Plane(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld)
    {
        super(pX, pY, ResourcesManager.getInstance().plane_region, vbo);
        createPhysics(camera, physicsWorld);
        explosionSprite = new AnimatedSprite(0, 0, ResourcesManager.getInstance().explosion_region, vbo);
        camera.setChaseEntity(this);
        animate(1);
        shots = new ArrayList<Body>();
        createShots(physicsWorld,vbo,camera);
    }
	

	
	private void createShots(PhysicsWorld physicsWorld,VertexBufferObjectManager vbo,final Camera camera) {		
		for(int i=0;i<maxShot;i++){
			shotSprite = new Sprite(0,9999,ResourcesManager.getInstance().shot_region,vbo);
			shots.add(PhysicsFactory.createBoxBody(physicsWorld, shotSprite, BodyType.KinematicBody, PhysicsFactory.createFixtureDef(0, 1, 0)));
			shots.get(i).setUserData(shotSprite);
			shots.get(i).setBullet(true);

			physicsWorld.registerPhysicsConnector(new PhysicsConnector(shotSprite, shots.get(i), true, true){
				@Override
		        public void onUpdate(float pSecondsElapsed)
		        {
		            super.onUpdate(pSecondsElapsed);
		            //camera.onUpdate(0.1f);
		           
		        }
			});
			}
	}
	
	private void createPhysics(final Camera camera, PhysicsWorld physicsWorld)
	{   
	    body = PhysicsFactory.createBoxBody(physicsWorld, this, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0));
	    body.setUserData("plane");
        this.body.getMassData().mass = 0f;
        for(int i=0; i<body.getFixtureList().size();i++){
	        this.body.getFixtureList().get(i).setSensor(true);
	    }
	    physicsWorld.registerPhysicsConnector(new PhysicsConnector(this, body,true , true)
	    {
	        @Override
	        public void onUpdate(float pSecondsElapsed)
	        {
	        	if(isShoot){
	        		if(shotType == 0){
	        			shoot();
	        		}
	        		else if(shotType == 2){
	        			doubleShot();
	        		}
	        		else if(shotType == 3){
	        			tripleShot();
	        		}
	        		isShoot = false;
	        	}
	        	if(isAlternateShoot){
	        		
	        	}
	            if(body.getPosition().y < 0) body.setTransform(body.getPosition().x, 0,body.getAngle());
	            gravity = 1/(1+body.getLinearVelocity().len());
	            if(gravity>0.04) gravity = 0.04f;
	            body.setTransform((body.getPosition().x+50) % 50, body.getPosition().y+gravity, body.getAngle());
	            if(body.getLinearVelocity().len() > 4){
	            	body.setAngularVelocity(ResourcesManager.getInstance().sensor2/2);
	            }
	            body.setLinearVelocity((float)(body.getLinearVelocity().len()*Math.cos(body.getAngle())), (float)(body.getLinearVelocity().len()*Math.sin(body.getAngle())));
	            if(isGas)  body.applyForce((float) (10*Math.cos(body.getAngle())), (float) (10*Math.sin(body.getAngle())), mShapeHalfBaseWidth, mShapeHalfBaseHeight);
	            else if(isBreak && body.getLinearVelocity().len() > 0) body.applyForce((float) (-7*Math.cos(body.getAngle())), 0, mShapeHalfBaseWidth, mShapeHalfBaseHeight);
	            body.applyForce((float) (-1*Math.cos(body.getAngle())), (float) (-1*Math.sin(body.getAngle())), mShapeHalfBaseWidth, mShapeHalfBaseHeight);
	            if(body.getLinearVelocity().len()>maxSpeed) body.setLinearVelocity((float)Math.cos(body.getAngle())*maxSpeed, (float)Math.sin(body.getAngle())*maxSpeed); 
	            super.onUpdate(pSecondsElapsed);
	            camera.onUpdate(0);
	        }
	    });
	    
	}

	public void gasShip(Boolean status) {
		isGas = status;
	}


	public void breakShip(Boolean status) {
		isBreak = status;	
	}



	public void shoot() {
		float angle = body.getAngle();
		float x = body.getPosition().x+1*(float)Math.cos(angle);
		float y = body.getPosition().y+1*(float)Math.sin(angle);
		shots.get(shotIndex).setTransform(x,y, angle);
		shots.get(shotIndex).setLinearVelocity(35*(float)Math.cos(shots.get(shotIndex).getAngle()), 35*(float)Math.sin(shots.get(shotIndex).getAngle()));
		shotIndex = (shotIndex+1)% maxShot;		
		ResourcesManager.getInstance().fireSound.play();
	}
	
	public void shoot(float dx,float dy,float dAngle) {
		float angle = body.getAngle()+dAngle;
		float x = body.getPosition().x+dx+1*(float)Math.cos(angle);
		float y = body.getPosition().y+dy+1*(float)Math.sin(angle);
		shots.get(shotIndex).setTransform(x,y, angle);
		shots.get(shotIndex).setLinearVelocity(35*(float)Math.cos(shots.get(shotIndex).getAngle()), 35*(float)Math.sin(shots.get(shotIndex).getAngle()));
		shotIndex = (shotIndex+1)% maxShot;	
		ResourcesManager.getInstance().fireSound.play();
	}
	
public void alternateShoot(){
		ResourcesManager.getInstance().alternateFireSound.play();
	}
	
	public void doubleShot(){
		shoot(shotSprite.getWidth()/32,shotSprite.getHeightScaled()/32,0);
		shoot(-shotSprite.getWidth()/32,-shotSprite.getHeightScaled()/32,0);
	}
	
	public void tripleShot(){
		shoot(0,0,0.5f);
		shoot(0,0,0);
		shoot(0,0,-0.5f);
	}
	
	public void healtPack(){
		health += (int)maxHealth*0.25;
		if(health>maxHealth) health = maxHealth;
	}
	
	public void crush(){
		body.setUserData("invul");
		if( animationFlagForPlaneCrush){
    		explosionSprite.setPosition(this);
    		getParent().attachChild(explosionSprite);
    		ResourcesManager.getInstance().camera.setChaseEntity(explosionSprite);
    		this.setVisible(false);
    		explosionSprite.animate(100,false, new IAnimationListener() { 		
				public void onAnimationStarted(AnimatedSprite pAnimatedSprite,int pInitialLoopCount) {
					ResourcesManager.getInstance().explosionSound.play();
				}					
				public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,int pRemainingLoopCount, int pInitialLoopCount) {
												}								
				public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,int pOldFrameIndex, int pNewFrameIndex) {}
				
				public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
		    	explosionSprite.detachSelf();
		    	respawn();	
				}
			});
    		animationFlagForPlaneCrush= false;	
    	}
	}
	
	public void respawn(){	
		this.setVisible(true);
		this.body.setTransform(1,1,0);
		health = 100;
		ResourcesManager.getInstance().camera.setChaseEntity(this);
		animationFlagForPlaneCrush= true;
		body.setLinearVelocity(0, 0);
		body.setUserData("plane");
	}



	public void aplyPowerup(powerupType pType) {
		if(pType == powerupType.HEALTUP){
			healtPack();
		}
		//TODO yeni powerup almýþ olsanda normale dönüyor süre bitince düzeltilmesi lazým
		else if(pType == powerupType.DOUBLESHOT){
			this.shotType = 2;
			registerUpdateHandler(new TimerHandler(10, new ITimerCallback() {			
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					Plane.this.shotType = 0;
				}
			}));
		}
		else if(pType == powerupType.TRIPLESHOT){
			this.shotType = 3;
			registerUpdateHandler(new TimerHandler(10, new ITimerCallback() {			
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					Plane.this.shotType = 0;
				}
			}));
		}
		else if(pType == powerupType.SHIELD){
			//TODO bu böyle olmaz yerin dibine de giriyor
			/*this.body.setUserData("invul");
			registerUpdateHandler(new TimerHandler(5, new ITimerCallback() {			
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					Plane.this.body.setUserData("invul");
				}
			}));*/
		}
	}
	
}
