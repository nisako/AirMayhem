package com.example.araprojenew;

import java.util.Random;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.util.math.MathUtils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
//TODO 
public class PracticeGameScene extends GameScene{
	private Body balloonBody;
	private AnimatedSprite balloonSprite;
	private boolean reLocateBalloonFlag=false;
	private float subX,subY;
	private boolean xBigger;
	public PracticeGameScene(){
		super();
		createBalloon();
		createGameLoopUpdate();
		super.physicsWorld.setContactListener(createContactListener());
		planeEnemy.setVisible(false);
		detachChild(planeEnemy);
	}
 
	private ContactListener createContactListener()
	{
	    ContactListener contactListener = new ContactListener()
	    {
	        @Override
	        public void beginContact(Contact contact)
	        {
	            final Fixture x2 = contact.getFixtureA();
	            final Fixture x1 = contact.getFixtureB();
	            if (x2.getBody().getUserData().equals("balloon") || x1.getBody().getUserData().equals("balloon"))
	            {
	            	 score++;
	            	 balloonSprite.animate(35,false, new IAnimationListener() { 		
						public void onAnimationStarted(AnimatedSprite pAnimatedSprite,int pInitialLoopCount) {
							balloonBody.setUserData("balloonnotanimate");
						}
						
						public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,int pRemainingLoopCount, int pInitialLoopCount) {}
						
						public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,int pOldFrameIndex, int pNewFrameIndex) {}
						
						public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
							reLocateBalloonFlag = true;
							balloonBody.setUserData("balloon");
						}
					});
	            }
	            if (x2.getBody().getUserData().equals("ground") && x1.getBody().getUserData().equals("plane"))
	            {
	            	plane.crush();
	            	
	        		camera.setChaseEntity(null);
	            /*	if(animationFlagForPlaneCrush){
		        		explosionSprite.setPosition(plane);	        			        		
		        		attachChild(explosionSprite);
		        		detachChild(plane);
		        		//explosionSprite.animate(100,false);
		        		explosionSprite.animate(100,false, new IAnimationListener() { 		
								public void onAnimationStarted(AnimatedSprite pAnimatedSprite,int pInitialLoopCount) {}					
								public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite,int pRemainingLoopCount, int pInitialLoopCount) {
																}								
								public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite,int pOldFrameIndex, int pNewFrameIndex) {}
								
								public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
									PracticeGameScene.this.setIgnoreUpdate(true);
									detachChild(line);
								}
							});
		        	
		        		animationFlagForPlaneCrush= false;	
		        		camera.setChaseEntity(null);
	            	}*/
	            }
	        } 
	        @Override public void endContact(Contact contact){}
	        @Override public void preSolve(Contact contact, Manifold oldManifold){}
	        @Override public void postSolve(Contact contact, ContactImpulse impulse){}
	    };
	    return contactListener;
	}
	
	private void createGameLoopUpdate() {
		TimerHandler gameLoopUpdateTimer = new TimerHandler(0.01f, true, new ITimerCallback() {
	        public void onTimePassed(TimerHandler pTimerHandler) {	        	
	        	if(reLocateBalloonFlag) {
	        		relocateBalloon();
	        		reLocateBalloonFlag = false;
	        		}
	        	subX = plane.getX() - balloonSprite.getX();
	        	subY = plane.getY() - balloonSprite.getY();
	        	if(Math.abs(subX)>Math.abs(subY)) xBigger = true;
	        	else xBigger = false;
	        	if(camera.isRectangularShapeVisible(balloonSprite)){
	        		arrowSprite.setPosition(-500, -500);
	        	}
	        	else if((subX > 0) && xBigger){//Sol kenara çizerken
	        		arrowSprite.setPosition(camera.getCenterX()-camera.getWidth()/2,(camera.getCenterY()+balloonSprite.getY())/2);
	        		arrowSprite.setRotation(180+MathUtils.radToDeg((float) Math.atan(subY/subX)));
	        	}
	        	else if((subX < 0) && xBigger){//Sað kenara çizilirken
	        		arrowSprite.setPosition((camera.getCenterX()+camera.getWidth()/2)-arrowSprite.getWidth(),(camera.getCenterY()+balloonSprite.getY())/2);
	        		arrowSprite.setRotation(MathUtils.radToDeg((float) Math.atan(subY/subX)));
	        	}
	        	else if((subY < 0) && !xBigger){//Aþaðý çizilirken
	        		if(subX>0){
	        		arrowSprite.setPosition((camera.getCenterX()+balloonSprite.getX())/2,(camera.getCenterY()+camera.getHeight()/2)-arrowSprite.getHeight());
	        		arrowSprite.setRotation(180+MathUtils.radToDeg((float) Math.atan(subY/subX)));
	        		}
	        		else{
	        			arrowSprite.setPosition((camera.getCenterX()+balloonSprite.getX())/2,(camera.getCenterY()+camera.getHeight()/2)-arrowSprite.getHeight());
		        		arrowSprite.setRotation(MathUtils.radToDeg((float) Math.atan(subY/subX)));
	        		}
	        	}
				else if((subY > 0) && !xBigger){//Yukara çizemezken
					if(subX>0){
					arrowSprite.setPosition((camera.getCenterX()+balloonSprite.getX())/2,camera.getCenterY()-camera.getHeight()/2);
					arrowSprite.setRotation(180+MathUtils.radToDeg((float) Math.atan(subY/subX)));
					}
					else{
						arrowSprite.setPosition((camera.getCenterX()+balloonSprite.getX())/2,camera.getCenterY()-camera.getHeight()/2);
						arrowSprite.setRotation(MathUtils.radToDeg((float) Math.atan(subY/subX)));
					}
				}
	        }
	    });
		
	    registerUpdateHandler(gameLoopUpdateTimer);	
	}
	
	private void relocateBalloon(){
		Random rand = new Random();
		balloonSprite.setCurrentTileIndex(0);
		balloonBody.setTransform(new Vector2(rand.nextInt(50),1+rand.nextInt(23)), balloonBody.getAngle());
     	//balloonBody.setTransform(new Vector2(balloonBody.getPosition().x+3,balloonBody.getPosition().y),0);
	}
	

	private void createBalloon(){
		Random rand = new Random();		

		balloonSprite = new AnimatedSprite(rand.nextInt(1400)+200, rand.nextInt(760)+200, ResourcesManager.getInstance().balloon_region, vbom);
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(balloonSprite,balloonBody = PhysicsFactory.createBoxBody(physicsWorld, balloonSprite, BodyType.DynamicBody, PhysicsFactory.createFixtureDef(0, 0, 0)),true , false));
		balloonBody.setBullet(true);
		balloonSprite.setZIndex(10);
		balloonBody.setUserData("balloon");
		balloonBody.getMassData().mass = 0f;
		for(int i=0; i<balloonBody.getFixtureList().size();i++){
	        this.balloonBody.getFixtureList().get(i).setSensor(true);
	    }
		attachChild(balloonSprite);
	}
	
}
