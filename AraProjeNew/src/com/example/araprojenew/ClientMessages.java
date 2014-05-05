package com.example.araprojenew;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.andengine.extension.multiplayer.protocol.adt.message.client.ClientMessage;
import org.andengine.extension.multiplayer.protocol.adt.message.server.ServerMessage;

public class ClientMessages {


	public static final short CLIENT_MESSAGE_SHOOT = 0;
	public static final short CLIENT_MESSAGE_POSITION = 1;
	public static final short CLIENT_MESSAGE_UTIL = 4;

	//public static final int	CLIENT_FLAG_COUNT = CLIENT_MESSAGE_SHOOT + 1;

	//TODO hatalý isimlendirme ve sabit deðerlerdeki anlamsýzlýk devam
	public static class clientShootMessage extends ClientMessage{
		
		
		public clientShootMessage(){
			
		}
		
		@Override
		public short getFlag() {
			return 0;
		}

		@Override
		protected void onReadTransmissionData(DataInputStream pDataInputStream)
				throws IOException {
		}

		@Override
		protected void onWriteTransmissionData(
				DataOutputStream pDataOutputStream) throws IOException {
			
		}
		
	}
	public static class clientSpritePositionMesseage extends ClientMessage{
		float x;
		float y;
		float angle;
		public clientSpritePositionMesseage(){
			
		}
		public clientSpritePositionMesseage(float x,float y,float angle){
			this.x = x;
			this.y = y;
			this.angle = angle;
		}
		@Override
		public short getFlag() {
			return 1;
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
	public static class clientUtilMessage extends ClientMessage{
		public int type;
		public clientUtilMessage(){
			
		}
		public clientUtilMessage(int pType){
			type = pType;
		}
		@Override
		public short getFlag() {
			return CLIENT_MESSAGE_UTIL;
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
	
}