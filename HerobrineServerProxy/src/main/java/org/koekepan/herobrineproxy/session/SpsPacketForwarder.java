package org.koekepan.herobrineproxy.session;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.HerobrineProxyProtocol;

import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.io.buffer.ByteBufferNetOutput;
import com.github.steveice10.packetlib.packet.Packet;
import com.google.gson.Gson;

import io.socket.client.Socket;

public class SpsPacketForwarder extends SessionAdapter {
	private Socket socket = null;
	private HerobrineProxyProtocol protocol;
	private ByteBuffer buffer;
	private int serverID;
	private String username;
	private String sub;
	
	public SpsPacketForwarder(Socket socket, HerobrineProxyProtocol protocol) 
	{
		this.socket = socket;	
		this.protocol = protocol;
	}

	@Override
	public void packetReceived(PacketReceivedEvent event) 
	{
		//Receive packet
		Packet packet = event.getPacket();
		//ConsoleIO.println("Packet: "+event.getPacket());
		
		//protocol.setSubProtocol(event.getSession().getPacketProtocol(), true);
		
		byte[] buf = new byte[55000];
		buffer = ByteBuffer.wrap(buf);
		//buffer.position(2);
		ByteBufferNetOutput out = new ByteBufferNetOutput(buffer);
		
		//get packet ID
		int id = -1;
		try {		
			id = protocol.getOutgoingId(packet.getClass());
		} catch (Throwable e) {
			ConsoleIO.println(e.toString());
		}
		
		try {
			//populate output buffer
			packet.write(out);
			//int length = buffer.position() - 2;
			//buffer.position(0);
			//this.writeLength(length);
			
		} catch (IOException e) {
			ConsoleIO.println("IOException: " + e.toString());
		}
		
		//create packet header
		PacketHeader header = new PacketHeader(serverID, "server", id, buf, buffer.position());
		
		//convert to JSON
		Gson gson = new Gson();
		String json = gson.toJson(header);

		socket.emit("packet received", "server", username, json, packet.toString());
		//ConsoleIO.println("Packet sent to SPS");
	}
	
	public void setID(int serverID) {
		this.serverID = serverID;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
}
