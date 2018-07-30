package org.koekepan.herobrineproxy;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.koekepan.herobrineproxy.session.ProxySession;

import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.server.ServerAdapter;
import com.github.steveice10.packetlib.event.server.SessionAddedEvent;
import com.github.steveice10.packetlib.event.server.SessionRemovedEvent;
import com.github.steveice10.packetlib.tcp.TcpConnectionListener;
import com.github.steveice10.packetlib.tcp.TcpSessionFactory;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


// the main class for the proxy
// this class creates a proxy session for every client session that is connected
@SuppressWarnings("unused")
public class HerobrineProxy {

	// interval at which poll thread will check whether the target server is accessible
	private static final int pollInterval = 1000;

	private ScheduledExecutorService serverPoll = Executors.newSingleThreadScheduledExecutor();

	private String spsHost = null;
	private int spsPort = 0;
	private String serverHost = null;
	private int serverPort = 0;
	private int serverID = 0;

	private Socket socket = null;
	private Server server = null;
	private Map<Session, ProxySession> sessions = new HashMap<Session, ProxySession>();


	public HerobrineProxy(String spsHost, int spsPort, String serverHost, int serverPort) {
		this.spsHost = spsHost;
		this.spsPort = spsPort;
		this.serverHost = serverHost;
		this.serverPort = serverPort;

		// setup proxy server and add listener to create and store/discard proxy sessions as clients connect/disconnect		
		ProxySession proxySession = new ProxySession();
		
		ConsoleIO.println("Connecting to proxy at "+spsHost+":"+spsPort);
			
		proxySession.connect(this.spsHost, this.spsPort, this.serverHost, this.serverPort);
	}

	// initializes the proxy
	public void bind() {
		// check whether the target server is accessible every poll interval
		// the proxy must only listen for client connections if the target server is accessible
		/*
		serverPoll.scheduleAtFixedRate(new Runnable() {

			private static final int noConnectionLimit = 3;

			private boolean serverAccessible = false;

			private String host = null;
			private int port = 0;
			private int noConnection = 0;

			public Runnable initialize(String host, int port) {
				this.host = host;
				this.port = port;
				return this;
			}

			@Override
			public void run() {

				try {
					// setup ping client for the target server
					Client client = new Client(host, port, new MinecraftProtocol(SubProtocol.STATUS), new TcpSessionFactory(null));
					client.getSession().setFlag(MinecraftConstants.SERVER_PING_TIME_HANDLER_KEY, new ServerPingTimeHandler() {
						@Override
						public void handle(Session session, long ping) {
							// if the ping attempt was successful, the server is accessible
							serverAccessible = true;
						}
					});

					// attempt to ping the target server
					serverAccessible = false;
					client.getSession().connect(true);
					while(client.getSession().isConnected()) {
						Thread.sleep(10);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

				if(serverAccessible ) { // if the server is accessible and the proxy server is not listening, start listening
					noConnection = 0;
					if(!server.isListening()) server.bind(true);
				} else if(!serverAccessible) { // if the server is not accessible and the proxy server is listening, stop listening
					noConnection++;
					if(server.isListening() && noConnection >= noConnectionLimit) server.close(true);
				}
			}
		}.initialize(serverHost, serverPort), 0, pollInterval, TimeUnit.MILLISECONDS);
		 */

		server.bind(true);
	}

	// closes the proxy
	public void close() {
		// stop the target server poll thread
		/*
		try {
			serverPoll.shutdown();
			while(!serverPoll.isTerminated()) {
				serverPoll.awaitTermination(pollInterval, TimeUnit.MILLISECONDS);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		// stop listening for client connections
		if(isListening()) {
			server.close(true);
		}
		*/

		server.close(true);
	}

	public String getProxyHost() {
		return spsHost;
	}

	public int getProxyPort() {
		return spsPort;
	}

	public String getServerHost() {
		return serverHost;
	}

	public int getServerPort() {
		return serverPort;
	}

	public List<ProxySession> getSessions() {
		return new ArrayList<ProxySession>(sessions.values());
	}

}
