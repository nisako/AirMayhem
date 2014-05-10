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
public class HostGameScene extends GameScene implements ISocketServerListener<SocketConnectionClientConnector>,ISocketConnectionClientConnectorListener {
	private float subX,subY;
	private boolean xBigger;
	private final int SERVER_PORT = 4444;
	private final MessagePool<IMessage> mMessagePool = new MessagePool<IMessage>();

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
		serverStart();
	}

	private void setupMessages() {	
		this.mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_TEST, serverSpritePositionMessage.class);
		this.mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_POSITION, clientSpritePositionMesseage.class);
		this.mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_SHOOT, serverShootMessage.class);
		this.mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_SHOOT, clientShootMessage.class);
		this.mMessagePool.registerMessage(ServerMessages.SERVER_MESSAGE_DEATH, serverDeathMessage.class);
		this.mMessagePool.registerMessage(ClientMessages.CLIENT_MESSAGE_UTIL, clientShootMessage.class);
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
									score++;
									planeEnemy.crush();
								}
								else if(incoming.type == 1){ //pause
									HostGameScene.super.pause();
								}
								
							}
						
							
						});
						
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
	            	plane.health -= 10;
	            	if(plane.health<=0){
	            		plane.crush();
	            		sendDeathMessage();
	            	}
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
		this.sendMessage(new serverShootMessage());
		//this.sendMessage(new serverSpritePositionMessage(plane.getX(),plane.getY(),plane.getRotation()));
	}
	public void sendPauseMessage() {
		super.pause();
		this.sendMessage(new serverDeathMessage(1));		
	}
	public void sendDeathMessage(){
		enemyScore++;
		this.sendMessage(new serverDeathMessage());
	}
	
	public void sendMessage(ServerMessage pServerMessage){
		try {
			this.mSocketServer.sendBroadcastServerMessage(pServerMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void terminate(){
		if(this.mSocketServer != null)
		this.mSocketServer.terminate();
	}
	
	
	@Override
	public void onStarted(ClientConnector<SocketConnection> pClientConnector) {
		ResourcesManager.getInstance().activity.toastOnUIThread("Client connected.");
		this.setIgnoreUpdate(false);
		/*Log.i(TAG, "Client Connected: "
				+ pClientConnector.getConnection().getSocket().getInetAddress()
						.getHostAddress());*/
	}

	// Listener - In the event of a client terminating the connection
	@Override
	public void onTerminated(ClientConnector<SocketConnection> pClientConnector) {
		ResourcesManager.getInstance().activity.toastOnUIThread("Client disconnected.");
		this.setIgnoreUpdate(true);
		/*Log.i(TAG, "Client Disconnected: "
				+ pClientConnector.getConnection().getSocket().getInetAddress()
						.getHostAddress());*/
	}

	// Listener - In the event of the server starting up
	@Override
	public void onStarted(SocketServer<SocketConnectionClientConnector> pSocketServer) {
		this.setIgnoreUpdate(true);
		ResourcesManager.getInstance().activity.toastOnUIThread("Server started waiting for clients.");
	}

	// Listener - In the event of the server shutting down
	@Override
	public void onTerminated(SocketServer<SocketConnectionClientConnector> pSocketServer) {
		this.setIgnoreUpdate(false); //olmalý mý?
		ResourcesManager.getInstance().activity.toastOnUIThread("Server terminated.");
	}

	@Override
	public void onException(
			SocketServer<SocketConnectionClientConnector> pSocketServer,
			Throwable pThrowable) {

	}
	public void disposeScene(){
		super.disposeScene();
		mSocketServer.terminate();
		mSocketServerDiscoveryServer.terminate();
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