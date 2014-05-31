package com.example.araprojenew;


import java.io.IOException;
import java.net.InetAddress;


import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.extension.multiplayer.protocol.adt.message.IMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.client.IClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;
import org.andengine.extension.multiplayer.protocol.server.IClientMessageHandler;
import org.andengine.extension.multiplayer.protocol.server.SocketServer;
import org.andengine.extension.multiplayer.protocol.server.SocketServer.ISocketServerListener;
import org.andengine.extension.multiplayer.protocol.server.SocketServerDiscoveryServer;
import org.andengine.extension.multiplayer.protocol.server.SocketServerDiscoveryServer.ISocketServerDiscoveryServerListener;
import org.andengine.extension.multiplayer.protocol.server.connector.ClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector;
import org.andengine.extension.multiplayer.protocol.server.connector.SocketConnectionClientConnector.ISocketConnectionClientConnectorListener;
import org.andengine.extension.multiplayer.protocol.shared.IDiscoveryData.DefaultDiscoveryData;
import org.andengine.extension.multiplayer.protocol.shared.SocketConnection;
import org.andengine.extension.multiplayer.protocol.util.MessagePool;
import org.andengine.extension.multiplayer.protocol.util.WifiUtils;
import org.andengine.util.math.MathUtils;


import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.example.araprojenew.ClientMessages.*;
import com.example.araprojenew.ServerMessages.*;

//TODO Client game scene ile ayný notlar geçerli
/*TODO þunlar eklencek mermi kaybolsun diye
 * activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							x1.getBody().setTransform(-100, -100,0);						
						}
					});
 */
public class HostGameScene extends GameScene implements ISocketServerListener<SocketConnectionClientConnector>,ISocketConnectionClientConnectorListener {
	private float subX,subY;
	private boolean xBigger;
	private final int SERVER_PORT = 4444;
	private final MessagePool<IMessage> mMessagePool = new MessagePool<IMessage>();
	@SuppressWarnings("unused") private PowerupManager pupManager;
	// Server object
	private SocketServer<SocketConnectionClientConnector> mSocketServer;
	private SocketServerDiscoveryServer<DefaultDiscoveryData> mSocketServerDiscoveryServer;
	
	public HostGameScene(){
		super();
		super.physicsWorld.setContactListener(serverContactListener());
		setupMessages();
		createHostGameLoopUpdate();
		try {
			initDiscoveryServer();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		pupManager = new PowerupManager(this, plane,planeEnemy,true);
		serverStart();
	}

	private void setupMessages() {	
		this.mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_TEST, serverSpritePositionMessage.class);
		this.mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_POSITION, clientSpritePositionMesseage.class);
		this.mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_SHOOT, serverShootMessage.class);
		this.mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_SHOOT, clientShootMessage.class);
		this.mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_DEATH, serverDeathMessage.class);
		this.mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_UTIL, clientShootMessage.class);
		//this.mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_POWERUP, clientPowerupMessage.class);
		this.mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_POWERUP, serverPowerupMessage.class);
	}
	
	private void serverStart(){
		/*this.mEngine.runOnUpdateThread(new Runnable(){

			@Override
			public void run() {
				*/
		HostGameScene.this.mSocketServer = new SocketServer<SocketConnectionClientConnector>(
				HostGameScene.this.SERVER_PORT,
				HostGameScene.this, HostGameScene.this) {
					
					// Called when a new client connects to the server...
					@Override
					protected SocketConnectionClientConnector newClientConnector(
							SocketConnection pSocketConnection)
							throws IOException {
						
						final SocketConnectionClientConnector clientConnector = new SocketConnectionClientConnector(pSocketConnection);
						clientConnector.registerClientMessage(ClientMessages.CLIENT_MESSAGE_POSITION, clientSpritePositionMesseage.class, new IClientMessageHandler<SocketConnection>(){
							
							@Override
							public void onHandleMessage(
									ClientConnector<SocketConnection> pClientConnector,
									IClientMessage pClientMessage)
									throws IOException {
								clientSpritePositionMesseage incoming = (clientSpritePositionMesseage) pClientMessage;
							//ResourcesManager.getInstance().activity.toastOnUIThread("test message arrived");
							
							planeEnemy.setPosition(incoming.x, incoming.y);
							planeEnemy.setRotation(incoming.angle);
							sendMessage(new serverSpritePositionMessage(plane.getX(),plane.getY(),plane.getRotation()));
							}
							
						});
						
						clientConnector.registerClientMessage(ClientMessages.CLIENT_MESSAGE_SHOOT, clientShootMessage.class, new IClientMessageHandler<SocketConnection>(){
							// Handle message received by the server...
							@Override
							public void onHandleMessage(
									ClientConnector<SocketConnection> pClientConnector,
									IClientMessage pClientMessage)
									throws IOException {
								// Obtain the class-casted client message
								clientShootMessage incoming =(clientShootMessage) pClientMessage;
								planeEnemy.shotType = incoming.shotType;
								planeEnemy.isShot = true;
								
								//sendMessage(new serverPlaneBodyMessage());
								//ResourcesManager.getInstance().activity.toastOnUIThread("shot message arrived");
							}
							
						});
						
						clientConnector.registerClientMessage(ClientMessages.CLIENT_MESSAGE_UTIL,clientUtilMessage.class,new IClientMessageHandler<SocketConnection>() {

							@Override
							public void onHandleMessage(
									ClientConnector<SocketConnection> pClientConnector,
									IClientMessage pClientMessage)
									throws IOException {
								clientUtilMessage incoming = (clientUtilMessage) pClientMessage;
								if(incoming.type == 0){ //dead
									HostGameScene.super.gameHUD.updateHudScore();
									planeEnemy.crush();
								}
								else if(incoming.type == 1){ //pause et oyununu
									HostGameScene.super.pause();
								}
								else if(incoming.type == 2){ //resume edebilir misin oyununu
									if(ResourcesManager.getInstance().activity.isActive){
									HostGameScene.super.resume();
									sendMessage(new serverDeathMessage(3));
									}
								}
								else if(incoming.type == 3){ //ben resume ettim sen de edebilirsin
									HostGameScene.super.resume();
								}
								else if(incoming.type == 10){ //plane 0 secildi
									planeEnemy.changePlane(0);
									sendMessage(new serverDeathMessage(MainMenuScene.selected_plane+10));
								}
								else if(incoming.type == 11){ //plane 0 secildi
									planeEnemy.changePlane(1);
									sendMessage(new serverDeathMessage(MainMenuScene.selected_plane+10));
								}
								else if(incoming.type == 12){ //plane 0 secildi
									planeEnemy.changePlane(2);
									sendMessage(new serverDeathMessage(MainMenuScene.selected_plane+10));
								}
								else if(incoming.type == 13){ //plane 0 secildi
									planeEnemy.changePlane(3);
									sendMessage(new serverDeathMessage(MainMenuScene.selected_plane+10));
								}
								else if(incoming.type == 14){ //plane 0 secildi
									planeEnemy.changePlane(4);
									sendMessage(new serverDeathMessage(MainMenuScene.selected_plane+10));
								}
								else if(incoming.type == 15){ //plane 0 secildi
									planeEnemy.changePlane(5);
									sendMessage(new serverDeathMessage(MainMenuScene.selected_plane+10));
								}
								else if(incoming.type == 16){ //plane 0 secildi
									planeEnemy.changePlane(6);
									sendMessage(new serverDeathMessage(MainMenuScene.selected_plane+10));
								}
								else if(incoming.type == 17){ //plane 0 secildi
									planeEnemy.changePlane(7);
									sendMessage(new serverDeathMessage(MainMenuScene.selected_plane+10));
								}
							}	
						});
						
						/*clientConnector.registerClientMessage(ClientMessages.CLIENT_MESSAGE_POWERUP,clientPowerupMessage.class,new IClientMessageHandler<SocketConnection>() {

							@Override
							public void onHandleMessage(
									ClientConnector<SocketConnection> pClientConnector,
									IClientMessage pClientMessage)
									throws IOException {
								clientPowerupMessage incoming = (clientPowerupMessage) pClientMessage;
								HostGameScene.this.pupManager.removePowerUp(incoming.tag);
								
								
							}	
						});*/
						
						// Return the new client connector
						return clientConnector;
					}
				};
				
				HostGameScene.this.mSocketServer.start();
			}
			
		/*});

	}*/
	private ContactListener serverContactListener()
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
	            if (x1.getBody().getUserData().equals("shotSprite") && x2.getBody().getUserData().equals("planeEnemy"))
	            {
	            	engine.runOnUpdateThread(new Runnable() {
							@Override
							public void run() {
								x1.getBody().setTransform(-10000, -10000,0);						
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
	private void createHostGameLoopUpdate() {
		TimerHandler hostGameLoopUpdateTimer = new TimerHandler(0.01f, true, new ITimerCallback() {        

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
		
	    registerUpdateHandler(hostGameLoopUpdateTimer);
	}
	public void sendShootMessage(){ //Butona basýlýnca
		this.sendMessage(new serverShootMessage(plane.shotType));
		//this.sendMessage(new serverSpritePositionMessage(plane.getX(),plane.getY(),plane.getRotation()));
	}
	@Override
	public void sendPauseMessage(boolean b) {
		if(b){
			this.sendMessage(new serverDeathMessage(1));
			super.pause();
		}	
		else this.sendMessage(new serverDeathMessage(2));	
	}
	public void sendDeathMessage(){
		super.gameHUD.updateHudEnemyScore();
		this.sendMessage(new serverDeathMessage());
	}
	public void sendPowerUp(Powerup testPup) {//eklemek
		this.sendMessage(new serverPowerupMessage(testPup));		
	}
	
	public void sendMessage(ServerMessage pServerMessage){
		try {
			this.mSocketServer.sendBroadcastServerMessage(pServerMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void pause(){
		sendPauseMessage(true);
	}
	public void resume(){
		sendPauseMessage(false);
	}
	public void terminate(){
		if(this.mSocketServer != null)
		this.mSocketServer.terminate();
	}
	
	
	@Override
	public void onStarted(ClientConnector<SocketConnection> pClientConnector) {
		ResourcesManager.getInstance().activity.toastOnUIThread("Client connected.");
		mSocketServerDiscoveryServer.terminate();
		//this.setIgnoreUpdate(false);
		waitChildScene.closeMenuScene();
		/*Log.i(TAG, "Client Connected: "
				+ pClientConnector.getConnection().getSocket().getInetAddress()
						.getHostAddress());*/
	}

	// Listener - In the event of a client terminating the connection
	@Override
	public void onTerminated(ClientConnector<SocketConnection> pClientConnector) {
		ResourcesManager.getInstance().activity.toastOnUIThread("Client disconnected.");
		//this.setIgnoreUpdate(true);
		setChildScene(waitChildScene,false,true,true);
		/*Log.i(TAG, "Client Disconnected: "
				+ pClientConnector.getConnection().getSocket().getInetAddress()
						.getHostAddress());*/
	}

	// Listener - In the event of the server starting up
	@Override
	public void onStarted(SocketServer<SocketConnectionClientConnector> pSocketServer) {
		//this.setIgnoreUpdate(true);
		setChildScene(waitChildScene,false,true,true);
		ResourcesManager.getInstance().activity.toastOnUIThread("Server started waiting for clients.");
	}

	// Listener - In the event of the server shutting down
	@Override
	public void onTerminated(SocketServer<SocketConnectionClientConnector> pSocketServer) {
		//this.setIgnoreUpdate(false); //olmalý mý?
		ResourcesManager.getInstance().activity.toastOnUIThread("Server terminated.");
	}

	@Override
	public void onException(
			SocketServer<SocketConnectionClientConnector> pSocketServer,
			Throwable pThrowable) {

	}
	public void disposeScene(){		
		if(mSocketServer != null) mSocketServer.terminate();
		if(mSocketServerDiscoveryServer != null) mSocketServerDiscoveryServer.terminate();
		super.disposeScene();
	}
	
	//Discovery ile ilgili alanlarin baslangici
    private void initDiscoveryServer() throws Throwable{
        final byte[] serverIPAddress  = WifiUtils.getWifiIPv4AddressRaw(activity);
       
        this.mSocketServerDiscoveryServer = new SocketServerDiscoveryServer<DefaultDiscoveryData>(
          new ExampleSocketServerDiscoveryServerListener()) {
          @Override
          protected DefaultDiscoveryData onCreateDiscoveryResponse() {
            return new DefaultDiscoveryData(serverIPAddress, SERVER_PORT);
          }
        };
       
        this.mSocketServerDiscoveryServer.start();
      }
    public class ExampleSocketServerDiscoveryServerListener implements ISocketServerDiscoveryServerListener<DefaultDiscoveryData> {
    	@Override
    	public void onStarted(final SocketServerDiscoveryServer<DefaultDiscoveryData> pSocketServerDiscoveryServer) {
    		//ResourcesManager.getInstance().activity.toastOnUIThread("DiscoveryServer: Started.");
    	}

    	@Override
    	public void onTerminated(final SocketServerDiscoveryServer<DefaultDiscoveryData> pSocketServerDiscoveryServer) {
    		//ResourcesManager.getInstance().activity.toastOnUIThread("DiscoveryServer: Terminated.");
    	}

    	@Override
    	public void onException(final SocketServerDiscoveryServer<DefaultDiscoveryData> pSocketServerDiscoveryServer, final Throwable pThrowable) {
    	//ResourcesManager.getInstance().activity.toastOnUIThread("DiscoveryServer: Exception: " + pThrowable);
    	}

    	@Override
    	public void onDiscovered(final SocketServerDiscoveryServer<DefaultDiscoveryData> pSocketServerDiscoveryServer, final InetAddress pInetAddress, final int pPort) {
    		//ResourcesManager.getInstance().activity.toastOnUIThread("DiscoveryServer: Discovered by: " + pInetAddress.getHostAddress() + ":" + pPort);
    	}
    	}
    //Discovery ile ilgili alanlarýn bitimi
	
	
}