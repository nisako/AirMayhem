package com.example.araprojenew;


import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.IServerMessage;
import org.andengine.extension.multiplayer.protocol.client.IServerMessageHandler;
import org.andengine.extension.multiplayer.protocol.client.SocketServerDiscoveryClient;
import org.andengine.extension.multiplayer.protocol.client.SocketServerDiscoveryClient.ISocketServerDiscoveryClientListener;
import org.andengine.extension.multiplayer.protocol.client.connector.ServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector;
import org.andengine.extension.multiplayer.protocol.client.connector.SocketConnectionServerConnector.ISocketConnectionServerConnectorListener;
import org.andengine.extension.multiplayer.protocol.shared.IDiscoveryData.DefaultDiscoveryData;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.extension.multiplayer.protocol.util.IPUtils;
import org.andengine.extension.multiplayer.protocol.util.MessagePool;
import org.andengine.extension.multiplayer.protocol.util.WifiUtils;
import org.andengine.util.math.MathUtils;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.example.araprojenew.ServerMessages.serverDeathMessage;
import com.example.araprojenew.ServerMessages.serverPowerupMessage;
import com.example.araprojenew.ClientMessages.*;
import com.example.araprojenew.ServerMessages.*;

public class ClientGameScene extends GameScene implements
		ISocketConnectionServerConnectorListener {

	private static final int SERVER_PORT = 4444;
	String mServerIP;

	private final MessagePool<IMessage> mMessagePool = new MessagePool<IMessage>();
	private ServerConnector<SocketConnection> mServerConnector;
	private SocketServerDiscoveryClient<DefaultDiscoveryData> mSocketServerDiscoveryClient;
	// Client object
	private float subX,subY;
	private boolean xBigger;
	private PowerupManager pupManager;
	public ClientGameScene() {
		super();
		plane.body.setTransform(plane.body.getPosition().x-plane.getWidth()/32, plane.body.getPosition().y, plane.body.getAngle()+(float)Math.PI);
		super.physicsWorld.setContactListener(clientContactListener());
		setupMessages();
		ResourcesManager.getInstance().activity.toastOnUIThread("Server Seeking");
  	    //ClientGameScene.this.setIgnoreUpdate(true);
  	   setChildScene(waitChildScene,false,true,true);
  	   createClientGameLoopUpdate();
  	    pupManager = new PowerupManager(this, plane,planeEnemy,false);
		try {
			initDiscoveryClient();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	private void setupMessages() {
		this.mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_TEST,
				serverSpritePositionMessage.class);
		this.mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_POSITION,
				clientSpritePositionMesseage.class);
		this.mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_SHOOT,
				serverShootMessage.class);
		this.mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_SHOOT,
				clientShootMessage.class);
		this.mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_DEATH, serverDeathMessage.class);
		this.mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_UTIL, clientShootMessage.class);
		//this.mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_POWERUP, clientPowerupMessage.class);
		this.mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_POWERUP, serverPowerupMessage.class);
	}

	private void clientStart() {
		/*ResourcesManager.getInstance().engine.runOnUpdateThread(new Runnable(){
			@Override
			public void run() {
		//Zaten update thread de olduðumuz için bu satýrlar kalktý
		 * */
				try{
					Socket socket = new Socket(mServerIP,SERVER_PORT);
					// Socket socket = new Socket("www.google.com", 80); test

					SocketConnection socketConnection = new SocketConnection(socket);

					ClientGameScene.this.mServerConnector = new SocketConnectionServerConnector(socketConnection, ClientGameScene.this);

		ClientGameScene.this.mServerConnector.registerServerMessage(
				ServerMessages.SERVER_MESSAGE_TEST,
				(Class<? extends IServerMessage>) serverSpritePositionMessage.class,
				new IServerMessageHandler<SocketConnection>() {

					@Override
					public void onHandleMessage(
							ServerConnector<SocketConnection> pServerConnector,
							IServerMessage pServerMessage) throws IOException {
						serverSpritePositionMessage incoming = (serverSpritePositionMessage) pServerMessage;
						planeEnemy.setPosition(incoming.x, incoming.y);
						planeEnemy.setRotation(incoming.angle);
						sendMessage(new clientSpritePositionMesseage(plane.getX(),plane.getY(), plane.getRotation()));
				        
						//sendMessage(new clientSpritePositionMesseage(plane.getX(),plane.getY(), plane.getRotation()));
					}

				});
		ClientGameScene.this.mServerConnector.registerServerMessage(
				ServerMessages.SERVER_MESSAGE_SHOOT,
				(Class<? extends IServerMessage>) serverShootMessage.class,new IServerMessageHandler<SocketConnection>() {

					@Override
					public void onHandleMessage(
							ServerConnector<SocketConnection> pServerConnector,
							IServerMessage pServerMessage) throws IOException {
						serverShootMessage incoming =(serverShootMessage) pServerMessage;
						planeEnemy.shotType = incoming.shotType;
						planeEnemy.isShot = true;
						// sendMessage(new clientPlaneBodyMessage());
						//ResourcesManager.getInstance().activity.toastOnUIThread("shot message arrived");
					}
					

				});
		mServerConnector.registerServerMessage(ServerMessages.SERVER_MESSAGE_DEATH,serverDeathMessage.class,new IServerMessageHandler<SocketConnection>() {

			@Override
			public void onHandleMessage(
					ServerConnector<SocketConnection> pServerConnector,
					IServerMessage pServerMessage) throws IOException {
				serverDeathMessage incoming = (serverDeathMessage) pServerMessage;
				if(incoming.type == 0){ //dead
					ClientGameScene.super.gameHUD.updateHudScore();
					planeEnemy.crush();
				}
				else if(incoming.type == 1){ //pause et oyununu
					ClientGameScene.super.pause();
				}
				else if(incoming.type == 2){ //resume edebilir misin oyununu
					if(ResourcesManager.getInstance().activity.isActive){
						ClientGameScene.super.resume();
						sendMessage(new clientUtilMessage(3));
					}
					
				}
				else if(incoming.type == 3){ //ben resume ettim sen de edebilirsin
					ClientGameScene.super.resume();
				}
				else if(incoming.type == 10){ //plane 0 secildi
					planeEnemy.changePlane(0);
				}
				else if(incoming.type == 11){ //plane 0 secildi
					planeEnemy.changePlane(1);
				}
				else if(incoming.type == 12){ //plane 0 secildi
					planeEnemy.changePlane(2);
				}
				else if(incoming.type == 13){ //plane 0 secildi
					planeEnemy.changePlane(3);
				}
				else if(incoming.type == 14){ //plane 0 secildi
					planeEnemy.changePlane(4);
				}
				else if(incoming.type == 15){ //plane 0 secildi
					planeEnemy.changePlane(5);
				}
				else if(incoming.type == 16){ //plane 0 secildi
					planeEnemy.changePlane(6);
				}
				else if(incoming.type == 17){ //plane 0 secildi
					planeEnemy.changePlane(7);
				}
				
				
			}
		
			
		});
		
		mServerConnector.registerServerMessage(ServerMessages.SERVER_MESSAGE_POWERUP,serverPowerupMessage.class,new IServerMessageHandler<SocketConnection>() {

			@Override
			public void onHandleMessage(
					ServerConnector<SocketConnection> pServerConnector,
					IServerMessage pServerMessage) throws IOException {
				serverPowerupMessage incoming = (serverPowerupMessage) pServerMessage;
				pupManager.spawnPowerUp(incoming.x,incoming.y,incoming.type);
				
			}
		
			
		});

		mServerConnector.getConnection().start();
		sendMessage(new clientSpritePositionMesseage(plane.getX(), plane.getY(),plane.getRotation()));
		} catch (UnknownHostException e) {
			ResourcesManager.getInstance().activity.toastOnUIThread("Server not found!");
		} catch (IOException e) {
			ResourcesManager.getInstance().activity.toastOnUIThread("Server not found!");
		}
			}
		/*});
}*/
	private ContactListener clientContactListener()
	{
	    ContactListener contactListener = new ContactListener()
	    {
	        @Override
	        public void beginContact(Contact contact)
	        {
	            final Fixture x2 = contact.getFixtureA();
	            final Fixture x1 = contact.getFixtureB();
	           
	            if (x1.getBody().getUserData().equals("shotEnemy") && x2.getBody().getUserData().equals("plane"))
	            {
	            	plane.damage(10);
	            	if(plane.getHealth()<=0){
	            		plane.crush();
	            		sendDeathMessage();
	            	}
	            	engine.runOnUpdateThread(new Runnable() {
							@Override
							public void run() {
								x1.getBody().setTransform(-10000, -10000,0);						
							}
						});

	            }
	            if (x2.getBody().getUserData().equals("shot") && x1.getBody().getUserData().equals("planeEnemy"))
	            {
	            	engine.runOnUpdateThread(new Runnable() {
							@Override
							public void run() {
								x2.getBody().setTransform(-10000, -10000,0);						
							}
						});

	            }
	            if (x2.getBody().getUserData().equals("ground") && x1.getBody().getUserData().equals("plane"))
	            {
	            	plane.crush();
	            	sendDeathMessage();
	            }
	            
	            if (x2.getBody().getUserData().equals("roof") && x1.getBody().getUserData().equals("plane"))
	            {
	            	//plane.body.setTransform(plane.body.getPosition().x, GameScene.WORLD_HEIGHT, plane.body.getAngle());
	            }
	        } 
	        @Override public void endContact(Contact contact){}
	        @Override public void preSolve(Contact contact, Manifold oldManifold){}
	        @Override public void postSolve(Contact contact, ContactImpulse impulse){}
	    };
	    return contactListener;
	}	
	
	private void createClientGameLoopUpdate() {
		TimerHandler clientGameLoopUpdateTimer = new TimerHandler(0.01f, true, new ITimerCallback() {
	        public void onTimePassed(TimerHandler pTimerHandler) {	        	
	        	
	        	subX = plane.getX() - planeEnemy.getX();
	        	subY = plane.getY() - planeEnemy.getY();
	        	if(Math.abs(subX)>Math.abs(subY)) xBigger = true;
	        	else xBigger = false;
	        	if(camera.isRectangularShapeVisible(planeEnemy)){
	        		arrowSprite.setPosition(-500, -500);
	        	}
	        	else if((subX > 0) && xBigger){//Sol kenara çizerken
	        		arrowSprite.setPosition(camera.getCenterX()-camera.getWidth()/2,(camera.getCenterY()+planeEnemy.getY())/2);
	        		arrowSprite.setRotation(180+MathUtils.radToDeg((float) Math.atan(subY/subX)));
	        	}
	        	else if((subX < 0) && xBigger){//Sað kenara çizilirken
	        		arrowSprite.setPosition((camera.getCenterX()+camera.getWidth()/2)-arrowSprite.getWidth(),(camera.getCenterY()+planeEnemy.getY())/2);
	        		arrowSprite.setRotation(MathUtils.radToDeg((float) Math.atan(subY/subX)));
	        	}
	        	else if((subY < 0) && !xBigger){//Aþaðý çizilirken
	        		if(subX>0){
	        		arrowSprite.setPosition((camera.getCenterX()+planeEnemy.getX())/2,(camera.getCenterY()+camera.getHeight()/2)-arrowSprite.getHeight());
	        		arrowSprite.setRotation(180+MathUtils.radToDeg((float) Math.atan(subY/subX)));
	        		}
	        		else{
	        			arrowSprite.setPosition((camera.getCenterX()+planeEnemy.getX())/2,(camera.getCenterY()+camera.getHeight()/2)-arrowSprite.getHeight());
		        		arrowSprite.setRotation(MathUtils.radToDeg((float) Math.atan(subY/subX)));
	        		}
	        	}
				else if((subY > 0) && !xBigger){//Yukara çizemezken
					if(subX>0){
					arrowSprite.setPosition((camera.getCenterX()+planeEnemy.getX())/2,camera.getCenterY()-camera.getHeight()/2);
					arrowSprite.setRotation(180+MathUtils.radToDeg((float) Math.atan(subY/subX)));
					}
					else{
						arrowSprite.setPosition((camera.getCenterX()+planeEnemy.getX())/2,camera.getCenterY()-camera.getHeight()/2);
						arrowSprite.setRotation(MathUtils.radToDeg((float) Math.atan(subY/subX)));
					}
				}
	        }
	    });
		
	    registerUpdateHandler(clientGameLoopUpdateTimer);
	}
	public void sendShootMessage() {
		sendMessage(new clientShootMessage(plane.shotType));
	}
	@Override
	public void sendPauseMessage(boolean b) {	
		if(b){
			this.sendMessage(new clientUtilMessage(1));
			super.pause();
		}		
		else this.sendMessage(new clientUtilMessage(2));
	}
	public void sendDeathMessage(){
		super.gameHUD.updateHudEnemyScore();
		sendMessage(new clientUtilMessage());
	}
	/*public void sendPowerUp(Powerup testPup) {//remove msg
		this.sendMessage(new clientPowerupMessage(testPup.getTag()));		
	}*/

	@Override
	public void onStarted(ServerConnector<SocketConnection> pServerConnector) {
		ResourcesManager.getInstance().activity.toastOnUIThread("Connected");
		sendMessage(new clientUtilMessage(MainMenuScene.selected_plane+10));
		//ClientGameScene.this.setIgnoreUpdate(false);
		ClientGameScene.this.waitChildScene.closeMenuScene();
	}

	@Override
	public void onTerminated(ServerConnector<SocketConnection> pServerConnector) {
		ResourcesManager.getInstance().activity.toastOnUIThread("Connection Lost");
		//ClientGameScene.this.setIgnoreUpdate(true);
		ClientGameScene.this.setChildScene(waitChildScene,false,true,true);
	}

	public void sendMessage(ClientMessage pClientMessage) {
		try {
			this.mServerConnector.sendClientMessage(pClientMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void disposeScene(){
		super.disposeScene();
		if(mServerConnector != null)mServerConnector.terminate();
		if(mSocketServerDiscoveryClient != null)mSocketServerDiscoveryClient.terminate();
	}
	public void pause(){
		try{
		sendPauseMessage(true);
		}
		catch(Exception e){
			//
		}
	}
	public void resume(){
		try{
		sendPauseMessage(false);
		}
		catch(Exception e){
			//yakaladim yakaladim yakaladim yakalayamadim
		}
	}
	//Discovery alanlarý baslangýc
    private void initDiscoveryClient() throws Throwable {
        final byte[] broadcastIPAddress = WifiUtils.getBroadcastIPAddressRaw(activity);
       
        this.mSocketServerDiscoveryClient = new SocketServerDiscoveryClient<DefaultDiscoveryData>(
            broadcastIPAddress,
            DefaultDiscoveryData.class,
            new ISocketServerDiscoveryClientListener<DefaultDiscoveryData>() {
              @Override
              public void onDiscovery(final SocketServerDiscoveryClient<DefaultDiscoveryData> pSocketServerDiscoveryClient, final DefaultDiscoveryData pDiscoveryData) {
            	  
            	  try {
					ClientGameScene.this.mServerIP  = IPUtils.ipAddressToString(pDiscoveryData.getServerIP());
					if(mServerIP.equals("0.0.0.0")) mServerIP="192.168.43.1";
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	  clientStart();
              }

			@Override
			public void onTimeout(
					SocketServerDiscoveryClient<DefaultDiscoveryData> pSocketServerDiscoveryClient,
					SocketTimeoutException pSocketTimeoutException) {				
				try {
					if(SceneManager.getInstance().getCurrentScene().equals(ClientGameScene.this)){
						initDiscoveryClient();
						ResourcesManager.getInstance().activity.toastOnUIThread("Seeking...");
					}
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		
			}

			@Override
			public void onException(
					SocketServerDiscoveryClient<DefaultDiscoveryData> pSocketServerDiscoveryClient,
					Throwable pThrowable) {
				// TODO Auto-generated method stub
				
			}
        });
       
        this.mSocketServerDiscoveryClient.discoverAsync();
      }
    	//Discovery alanlarý son

	
}