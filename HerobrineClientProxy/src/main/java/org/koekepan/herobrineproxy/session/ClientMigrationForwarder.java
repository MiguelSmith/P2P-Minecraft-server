package org.koekepan.herobrineproxy.session;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.koekepan.herobrineproxy.ConsoleIO;

import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;

public class ClientMigrationForwarder extends SessionAdapter {

	private ProxySession proxySession = null;
	private Session session = null;

	private boolean flush = false;
	private BlockingQueue<Packet> packets = new LinkedBlockingQueue<Packet>();
	private Thread thread = null;
	private String host;
	private int port;


	public ClientMigrationForwarder(ProxySession proxySession, String host, int port) {
		this.proxySession = proxySession;
		this.host = host;
		this.port = port;
	}

	public void flush(Session session) {
		this.session = session;
		flush = true;
		
		// set up and start packet thread
		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while(!Thread.currentThread().isInterrupted()) {
					try {
						ClientMigrationForwarder.this.session.send(packets.take()); // TODO git gud
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				}
			}
		});
		
		thread.start();
	}

	@Override
	public void packetReceived(PacketReceivedEvent event) {
		if(flush && packets.size() == 0) {
			// shutdown thread
			thread.interrupt();
			
			// remove migration packet forwarder 
			event.getSession().removeListener(this);
			
			// send last packet before new forwarder
			session.send(event.getPacket());
						
			// add packet forwarder
			PacketForwarder forwarder = new PacketForwarder(session, host, port);
			proxySession.setClientPacketForwarder(forwarder);
			proxySession.getClient().addListener(forwarder);
			
			// end migration
			proxySession.setMigrating(false);
			
			//System.out.println("Migration complete.");
			ConsoleIO.println("Player \"" + proxySession.getUsername() + "\" migration complete."); // TODO ConsoleIO
			
		} else {
			packets.add(event.getPacket());
		}

	}

}
