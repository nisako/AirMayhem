package com.example.araprojenew;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

import com.badlogic.gdx.physics.box2d.Body;

//bknz. client msg notlarý
public class ServerMessages {

	
	public static final short SERVER_MESSAGE_SHOOT = 2;//ClientMessages.CLIENT_FLAG_COUNT;
	public static final short SERVER_MESSAGE_TEST = 3;
	public static final short SERVER_MESSAGE_DEATH = 5;//TODO rename this
	public static final short SERVER_MESSAGE_POWERUP = 7;
	
public static class serverShootMessage extends ServerMessage{

	public int shotType;
	public serverShootMessage(){
		
	}
	
	public serverShootMessage(int pShotType){
		shotType = pShotType;
	}
	@Override
	public short getFlag() {
		return 2;
	}

	@Override
	protected void onReadTransmissionData(DataInputStream pDataInputStream)
			throws IOException {
		this.shotType = pDataInputStream.readInt();
	}

	@Override
	protected void onWriteTransmissionData(
			DataOutputStream pDataOutputStream) throws IOException {
		pDataOutputStream.writeInt(this.shotType);
	}
		
	}
	public static class serverSpritePositionMessage extends ServerMessage{
		float x,y,angle;
		public serverSpritePositionMessage(){
			
		}
		public serverSpritePositionMessage(float x,float y,float angle){
			this.x = x;
			this.y = y;
			this.angle = angle;
		}
		@Override
		public short getFlag() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		protected void onReadTransmissionData(DataInputStream pDataInputStream)
				throws IOException {
			this.x = pDataInputStream.readFloat();
			this.y = pDataInputStream.readFloat();
			this.angle = pDataInputStream.readFloat();
			
		}

		@Override
		protected void onWriteTransmissionData(
				DataOutputStream pDataOutputStream) throws IOException {
			pDataOutputStream.writeFloat(this.x);
			pDataOutputStream.writeFloat(this.y);
			pDataOutputStream.writeFloat(this.angle);
			
		}
		
	}
		public static class serverDeathMessage extends ServerMessage{
			public int type;
			public serverDeathMessage(){
				
			}
			public serverDeathMessage(int pType){
				type = pType;
			}
			@Override
			public short getFlag() {
				return SERVER_MESSAGE_DEATH;
			}

			@Override
			protected void onReadTransmissionData(
					DataInputStream pDataInputStream) throws IOException {
				type = pDataInputStream.readInt();
			}

			@Override
			protected void onWriteTransmissionData(
					DataOutputStream pDataOutputStream) throws IOException {
				pDataOutputStream.writeInt(type);
			}
			
		}
		public static class serverPowerupMessage extends ServerMessage{
			float x,y;
			public serverPowerupMessage(){
				
			}
			public serverPowerupMessage(Powerup pPup){
				x = pPup.getX();
				y = pPup.getY();
			}
			@Override
			public short getFlag() {
				return SERVER_MESSAGE_POWERUP;
			}

			@Override
			protected void onReadTransmissionData(
					DataInputStream pDataInputStream) throws IOException {
				x = pDataInputStream.readFloat();
				y = pDataInputStream.readFloat();
			}

			@Override
			protected void onWriteTransmissionData(
					DataOutputStream pDataOutputStream) throws IOException {
				pDataOutputStream.writeFloat(x);
				pDataOutputStream.writeFloat(y);
			}
			
		}
}
