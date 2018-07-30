package org.koekepan.herobrineproxy.session;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.HerobrineProxyProtocol;

import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.event.session.SessionListener;
import com.github.steveice10.packetlib.io.buffer.ByteBufferNetInput;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;
import com.google.gson.Gson;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ProxySession {
	private Session client = null;
	private Session server = null;
	
	private Socket socket = null;

	private String host = null;
	private int port = 0;
	private int serverID = 0;

	private SessionListener clientForwarder = null;
	private SessionListener serverForwarder = null;

	private String username = null;

	private boolean migrating = false;
	private HerobrineProxyProtocol protocol;
	
	public ProxySession()
	{	
		protocol = new HerobrineProxyProtocol(client);
	}

	public void connect(String host, int port, String serverHost, int serverPort) {
		this.host = host;
		this.port = port;	
		
		//This is essentially the SPS listener
		try 
		{
			this.socket = IO.socket("http://"+this.host+":"+this.port);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		
		socket.on("type", new Emitter.Listener() 
		{
			
			@Override
			public void call(Object... data) {
				serverID = (int) data[0];
				ConsoleIO.println("serverID:"+serverID);
				socket.emit("server connected", serverID);
			}
		}).on("publish", new Emitter.Listener() 
		{
			
			@Override
			public void call(Object... data) 
			{
				Gson gson = new Gson();
				try {
					PacketHeader headerIn = gson.fromJson((String) data[0], PacketHeader.class);
					
					//create incoming packet
					Packet packetIn = protocol.createIncomingPacket(headerIn.getPacket_ID());
					
					//populating the input buffer
					byte[] buff = headerIn.getPayload();
					ByteBuffer bufferIn = ByteBuffer.wrap(buff);
					
					ByteBufferNetInput in = new ByteBufferNetInput(bufferIn);
					try {
						//create packet
						packetIn.read(in);
						ConsoleIO.println("PacketIn after read: "+packetIn.toString());
					} catch (IOException e) {
						e.printStackTrace();
					}

					ConsoleIO.println("Send packet to server");
					server.send(packetIn);
					
				} catch (Throwable e) {
					ConsoleIO.println(e.toString());
				}
			}
		}).on("packet received", new Emitter.Listener() {
			
			@Override
			public void call(Object... data) 
			{				
				ConsoleIO.println("Packet was received by the SPS ");
			}
		}).on("publish acknowledge", new Emitter.Listener() {
			
			@Override
			public void call(Object... data) {
				ConsoleIO.println("Packet has been sent to client"+data[0]);
			}
		});
		
		socket.connect();	
		
		server = new Client(serverHost, serverPort, new HerobrineProxyProtocol(), new TcpSessionFactory(null)).getSession();

		serverForwarder = new ServerPacketForwarder(socket, protocol);
		server.addListener(serverForwarder);

		server.connect(true);
		server.isConnected();
	}
	
	public void setID(int serverID) {
		this.serverID = serverID;
	}


	public void disconnect() {
		if(username != null) {
			ConsoleIO.println("player \"" + username + "\" is disconnecting from <" + host + ":" + port + ">");
		}

		if(server.isConnected()) {
			server.disconnect("Finished.", true);
		}
	}

	public Session getClient() {
		return client;
	}

	public void setClient(Session client) {
		this.client = client;
	}

	public Session getServer() {
		return server;
	}

	public void setServer(Session server) {
		this.server = server;
	}



	public SessionListener getClientPacketForwarder() {
		return clientForwarder;
	}

	public void setClientPacketForwarder(SessionListener forwarder) {
		clientForwarder = forwarder;
	}

	public SessionListener getServerPacketForwarder() {
		return serverForwarder;
	}

	public void setServerPacketForwarder(SessionListener forwarder) {
		serverForwarder = forwarder;
	}



	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	public boolean isMigrating() {
		return migrating;
	}

	public void setMigrating(boolean migrating) {
		this.migrating = migrating;
	}


	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

}
