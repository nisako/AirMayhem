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
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.transition.Scene;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
//TODO bu class'ýn plane enemy ile birlikte ortak bi atasý olmalýydý
public class Plane extends AnimatedSprite{
	public Body body; 
	private AnimatedSprite explosionSprite,smokeSprite;
	private boolean isGas;
	private boolean isBreak;
	public boolean isShoot,isMissile;
	public boolean invul=false;
	private int maxSpeed=20;
	private int acceleration=10;
	
	public ArrayList<Body> shots;
	public Body missileBody;
	public Sprite shotSprite,missileSprite;
	public int shotIndex=0;
	public int maxShot=35;
	public int maxMissileShot=5;
	public int shotType=0; //0 default , 2 double,3 triple

	private int missileCount=0;
	
	public int maxHealth = 100;
	private int health = maxHealth;
	
	
	public int getHealth() {
		return health;
	}

	public void setHealth(int phealth) {
		this.health = phealth;
	}

	private float gravity;

	public boolean animationFlagForPlaneCrush = true;
	private Sprite shieldSprite;
	
	
	public Plane(float pX, float pY, VertexBufferObjectManager vbo, Camera camera, PhysicsWorld physicsWorld,ITiledTextureRegion planeRegion,int planeType)
    {
        super(pX, pY, planeRegion, vbo);
        setStats(planeType);
        createPhysics(camera, physicsWorld);
        explosionSprite = new AnimatedSprite(0, 0, ResourcesManager.getInstance().explosion_region, vbo);
        shieldSprite = new Sprite(-35,-35,ResourcesManager.getInstance().shield_region,vbo);
        this.attachChild(shieldSprite);
        shieldSprite.setVisible(false);
        
      /* smokeSprite = new AnimatedSprite(0, 0, ResourcesManager.getInstance().smoke_region, vbo);
        smokeSprite.animate(100);
        attachChild(smokeSprite);*/
        camera.setChaseEntity(this);
        animate(1);
        createMissile(physicsWorld,vbo,camera);
        shots = new ArrayList<Body>();
        createShots(physicsWorld,vbo,camera);
        
    }
	

	
	private void setStats(int planeType) {
		switch(planeType){
		case 0:
			maxHealth = 100;
			maxSpeed = 20;
			acceleration = 10;
			break;
		case 1:
			maxHealth = 90;
			maxSpeed = 18;
			acceleration = 12;
			break;
		case 2:
			maxHealth = 120;
			maxSpeed = 14;
			acceleration = 7;
			break;
		case 3:
			maxHealth = 110;
			maxSpeed = 17;
			acceleration = 9;
			break;
		default:
			maxHealth = 110;
			maxSpeed = 17;
			acceleration = 9;
			break;
		}
		health = maxHealth;
		
	}

	private void createMissile(PhysicsWorld physicsWorld,VertexBufferObjectManager vbo,final Camera camera) {		
		
			missileSprite = new Sprite(9999,9999,ResourcesManager.getInstance().missile_region,vbo);
			missileBody = PhysicsFactory.createBoxBody(physicsWorld, missileSprite, BodyType.KinematicBody, PhysicsFactory.createFixtureDef(0, 1, 0));
			missileBody.setUserData(missileSprite);
			missileBody.setBullet(true);

			physicsWorld.registerPhysicsConnector(new PhysicsConnector(missileSprite, missileBody, true, true){
				@Override
		        public void onUpdate(float pSecondsElapsed)
		        {
		            super.onUpdate(pSecondsElapsed);
		            //camera.onUpdate(0.1f);
		           
		        }
			});
			
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
	        	if(isMissile && missileCount>0){
	        		alternateShoot();
	        		isMissile = false;
	        	}
	        	
	            if(body.getPosition().y < 0) body.setTransform(body.getPosition().x, 0,body.getAngle());
	            gravity = 1/(1+body.getLinearVelocity().len());
	            if(gravity>0.04) gravity = 0.04f;
	            body.setTransform((body.getPosition().x+50) % 50, body.getPosition().y+gravity, body.getAngle());
	            if(body.getLinearVelocity().len() > 4){
	            	//TODO bura uçak yavaþken yavaþ dönsün hýzlýyken hýzlý þeklinde olabilir
	            	body.setAngularVelocity(ResourcesManager.getInstance().sensor2/2);
	            	if(!Plane.this.isAnimationRunning()){
	            		Plane.this.animate(1);
	            	}
	            }
	            else{//uçak yavaþsa
	            	if(Plane.this.isAnimationRunning()){
	            		Plane.this.stopAnimation();
	            	}
	            }
	            /*if((Plane.this.getRotation()%360) +360 > 90) setFlippedVertical(true);
	            else if ((Plane.this.getRotation()%360) +360 > 180) setFlippedVertical(false);
	            else setFlippedVertical(false);*/
	            
	            body.setLinearVelocity((float)(body.getLinearVelocity().len()*Math.cos(body.getAngle())), (float)(body.getLinearVelocity().len()*Math.sin(body.getAngle())));
	            if(isGas)  body.applyForce((float) (acceleration*Math.cos(body.getAngle())), (float) (acceleration*Math.sin(body.getAngle())), mShapeHalfBaseWidth, mShapeHalfBaseHeight);
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
		float x = body.getPosition().x+(float)Math.cos(angle);
		float y = body.getPosition().y+(float)Math.sin(angle);
		shots.get(shotIndex).setTransform(x,y, angle);
		shots.get(shotIndex).setLinearVelocity(35*(float)Math.cos(shots.get(shotIndex).getAngle()), 35*(float)Math.sin(shots.get(shotIndex).getAngle()));
		shotIndex = (shotIndex+1)% maxShot;		
		ResourcesManager.getInstance().fireSound.play();
	}
	
	public void alternateShoot(){
		float angle = body.getAngle();
		float x = body.getPosition().x+(float)Math.cos(angle);
		float y = body.getPosition().y+(float)Math.sin(angle);
		missileBody.setTransform(x,y, angle);
		missileBody.setLinearVelocity(55*(float)Math.cos(missileBody.getAngle()), 55*(float)Math.sin(missileBody.getAngle()));
		missileCount--;	
		ResourcesManager.getInstance().alternateFireSound.play();
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
		GameScene a = (GameScene) SceneManager.getInstance().getCurrentScene();
    	a.gameHUD.updateHudHealth();
	}
	
	public void crush(){
		this.shotType = -1;
		try{
			this.getParent().attachChild(explosionSprite);
		}
		catch(Exception e){
			//TODO burdaki try catch yerine current scene bir yerden öðrenilip ona attach edilmeli
		}
		body.setUserData("invul");
		if( animationFlagForPlaneCrush){
    		explosionSprite.setPosition(this);
    		explosionSprite.setVisible(true);
    		ResourcesManager.getInstance().camera.setChaseEntity(explosionSprite);
    		this.setVisible(false);
    		explosionSprite.animate(50,false, new IAnimationListener() { 		
				public void onAnimationStarted(AnimatedSprite pAnimatedSprite,int pInitialLoopCount) {
					ResourcesManager.getInstance().explosionSound.play();
				}					
				public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,int pRemainingLoopCount, int pInitialLoopCount) {
												}								
				public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,int pOldFrameIndex, int pNewFrameIndex) {}
				
				public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
		    	explosionSprite.setVisible(false);
		    	respawn();	
				}
			});
    		animationFlagForPlaneCrush= false;	
    	}
	}
	
	public void respawn(){	
		this.shotType = 0;
		this.setVisible(true);
		this.body.setTransform(1,1,0);
		health = maxHealth;
		GameScene a = (GameScene) SceneManager.getInstance().getCurrentScene();
    	a.gameHUD.updateHudHealth();
		ResourcesManager.getInstance().camera.setChaseEntity(this);
		animationFlagForPlaneCrush= true;
		body.setLinearVelocity(0, 0);
		body.setUserData("plane");
		invul = true;
		registerUpdateHandler(new TimerHandler(1.5f, new ITimerCallback() {			
			@Override
			public void onTimePassed(TimerHandler pTimerHandler) {
				invul = false;
			}
		}));
	}

	public void damage(int dmg){
		if(!invul){
			health -= dmg;
			GameScene a = (GameScene) SceneManager.getInstance().getCurrentScene();
        	a.gameHUD.updateHudHealth();
        	
		}
	}

	public void aplyPowerup(powerupType pType) {
		if(pType == powerupType.HEALTUP){
			healtPack();
		}
		else if(pType == powerupType.MISSILE){
			if(missileCount<1)
			this.missileCount++;

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
		else if(pType == powerupType.INVUL){
			invul = true;
			shieldSprite.setVisible(true);
			registerUpdateHandler(new TimerHandler(3, new ITimerCallback() {			
				@Override
				public void onTimePassed(TimerHandler pTimerHandler) {
					invul = false;
					shieldSprite.setVisible(false);
				}
			}));
		}
	}
	
}
