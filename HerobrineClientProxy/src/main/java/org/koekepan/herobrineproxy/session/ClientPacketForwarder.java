package org.koekepan.herobrineproxy.session;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.HerobrineProxyProtocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.io.NetInput;
import com.github.steveice10.packetlib.io.NetOutput;
import com.github.steveice10.packetlib.io.buffer.ByteBufferNetInput;
import com.github.steveice10.packetlib.io.buffer.ByteBufferNetOutput;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.google.gson.Gson;

import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Handshake;

public class ClientPacketForwarder extends SessionAdapter {
	private final  Session session;
	private Socket socket;
	private String host;
	private int port;
	private String username;
	private int clientID;
	private String sub;
	private HerobrineProxyProtocol protocol;
	private ByteBuffer buffer;
	
	public ClientPacketForwarder(Session session, String host, int port) 
	{	
		this.session = session;
		this.host = host;
		this.port = port;
		
		protocol = new HerobrineProxyProtocol(session);
		
		try 
		{
			this.socket = IO.socket("http://"+this.host+":"+this.port);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		socket.on("clients", new Emitter.Listener() {
			
			@Override
			public void call(Object... arg) {
				String[] temp = ((String) arg[0]).split(" ");
				//ConsoleIO.println("clientID: " +temp[0]);
				//ConsoleIO.println("Entire client string received: "+temp);
			}
		}).on("packet received", new Emitter.Listener() {
			
			@Override
			public void call(Object... args) {
				ConsoleIO.println("The packet was received by the SPS");
			}
		}).on("test packet received", new Emitter.Listener() {
			
			@Override
			public void call(Object... arg0) {
				ConsoleIO.println("test packet recieved: "+arg0[0].toString());				
			}
		}).on("type", new Emitter.Listener() {
			
			@Override
			public void call(Object... data) {
				clientID = (int) data[1];
				ConsoleIO.println("clientID:"+clientID);
				socket.emit("client connected", clientID);
				ConsoleIO.println("sub string: " +sub);
				socket.emit("function", sub);
				ConsoleIO.println("socket emitted 'function' event");
				
			}
		}).on("publish acknowledge", new Emitter.Listener() {
			
			@Override
			public void call(Object... data) {
				ConsoleIO.println("Packet has been sent to server");
			}
		}).on("publish", new Emitter.Listener() {
			
			@Override
			public void call(Object... data) {
				Packet packet = decodePacketHeader((String) data[0]);
				ConsoleIO.println("Packet received from SPS: "+packet);
				sendPacket(packet);
			}
		});	

		socket.connect();
	}
	
	@Override
	public void packetReceived(PacketReceivedEvent event) 
	{
		
		//Receive packet
		Packet packet = event.getPacket();
		ConsoleIO.println("Packet: "+packet);
		
		//protocol.setSubProtocol(event.getSession().getPacketProtocol(), true);
		
		//determine packet type
		if (packet instanceof HandshakePacket) {
			HandshakePacket tempPacket = new HandshakePacket(((HandshakePacket) packet).getProtocolVersion(), ((HandshakePacket) packet).getHostName(), 25565, ((HandshakePacket) packet).getIntent());
			packet = tempPacket;
		}
		
		if (packet instanceof LoginStartPacket) {
			this.username = ((LoginStartPacket) packet).getUsername();
			sub = clientID + ";sub;true;80;0;0;"+username;
		} 		

		byte[] buf = new byte[50];
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
		PacketHeader header = new PacketHeader(clientID, "client", id, buf, buffer.position());
		
		//convert to JSON
		Gson gson = new Gson();
		String json = gson.toJson(header);
		
		//send JSON		
		socket.emit("packet received", "client", username, json, packet.toString());
		ConsoleIO.println("Packet sent to SPS");
	}
	
	private void writeLength(int length) {
		while((length & ~0x7F) != 0) {
            this.writeByte((length & 0x7F) | 0x80);
            length >>>= 7;
        }

        this.writeByte(length);
	}
	
	private void writeByte(int length) {
        this.buffer.put((byte) length);
	}
	private void sendPacket(Packet packet) {
		session.send(packet);
	}
	
	private Packet decodePacketHeader(String header) {
		Gson gson = new Gson();
		Packet packetIn = null;
		try {
			PacketHeader headerIn = gson.fromJson(header, PacketHeader.class);
			
			//create incoming packet
			packetIn = protocol.createIncomingPacket(headerIn.getPacket_ID());
			
			//populating the input buffer
			byte[] buff = headerIn.getPayload();
			ByteBuffer bufferIn = ByteBuffer.wrap(buff);
			
			ByteBufferNetInput in = new ByteBufferNetInput(bufferIn);
			try {
				//create packet
				packetIn.read(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} catch (Throwable e) {
			ConsoleIO.println(e.toString());
		}
		
		return packetIn;
	}
}
