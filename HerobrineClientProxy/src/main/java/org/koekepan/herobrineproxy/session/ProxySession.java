package org.koekepan.herobrineproxy.session;

import org.koekepan.herobrineproxy.ConsoleIO;
import org.koekepan.herobrineproxy.HerobrineProxyProtocol;

import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.event.session.SessionListener;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

public class ProxySession {
	private Session client = null;
	private Session server = null;
	
	private String host = null;
	private int port = 0;

	private SessionListener clientForwarder = null;
	private SessionListener serverForwarder = null;

	private String username = null;

	private boolean migrating = false;

	public ProxySession(final Session client) {
		this.client = client;

		// listen for login start packet to get username
		client.addListener(new SessionAdapter() {
			@Override
			public void packetReceived(PacketReceivedEvent event) {
				Packet packet = event.getPacket();
				if (packet instanceof LoginStartPacket) {
					username = ((LoginStartPacket) packet).getUsername();
					ConsoleIO.println("player \"" + username + "\" is connecting to <" + host + ":" + port + ">");
					client.removeListener(this);
				} 
			}
		});
	}


	public void connect(String host, int port, Session session) {
		this.host = host;
		this.port = port;
		
		//server = new Client(host, port, new HerobrineProxyProtocol(), new TcpSessionFactory(null)).getSession();

		//serverForwarder = new ServerPacketForwarder(client);
		//server.addListener(serverForwarder);
		//server.connect(true);

		clientForwarder = new PacketForwarder(session,host,port);
		client.addListener(clientForwarder);
	}


	public void disconnect() {
		if(username != null) {
			ConsoleIO.println("player \"" + username + "\" is disconnecting from <" + host + ":" + port + ">");
		}

		if(client.isConnected()) {
			client.disconnect("Server connection closed.", true);
		}

		if(server.isConnected()) {
			server.disconnect("Finished.", true);
		}
	}


	// TODO move migration to MigrationListener
	public void migrate(String host, int port) {
		if(!migrating) {
			migrating = true;

			this.host = host;
			this.port = port;

			ConsoleIO.println("player \"" + username + "\" is being migrated from <" + server.getHost() + ":" + server.getPort() + "> to <" + host + ":" + port + ">");

			new Thread(new Runnable() {
				private String host;
				private int port;

				@Override
				public void run() {
					ClientMigrationForwarder clientMigrationForwarder = new ClientMigrationForwarder(ProxySession.this, host, port);

					client.addListener(clientMigrationForwarder);

					Session newServer = new Client(host, port, new HerobrineProxyProtocol(), new TcpSessionFactory(null)).getSession();

					newServer.addListener(new MigrationListener(ProxySession.this));

					newServer.addListener(new ServerMigrationForwarder(host, port, ProxySession.this, clientMigrationForwarder));

					newServer.connect(true);
				}

				public Runnable init(String host, int port) {
					this.host = host;
					this.port = port;
					return this;
				}
			}.init(host, port)).start();
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
