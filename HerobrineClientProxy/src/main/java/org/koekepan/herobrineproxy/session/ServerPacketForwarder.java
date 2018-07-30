package org.koekepan.herobrineproxy.session;

import org.koekepan.herobrineproxy.ConsoleIO;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

public class ServerPacketForwarder extends SessionAdapter
{
	private Session session = null;
	
	public ServerPacketForwarder(Session session) {
		this.session = session;
	}

	@Override
	public void packetReceived(PacketReceivedEvent event) {
		ConsoleIO.println("Forwarding packet to client: "+event.getPacket());
		
		if (session.isConnected()) {
			session.send(event.getPacket());
		} else {
			ConsoleIO.println("PacketForwarded::packetReceived => Not connected to host <"+session.getHost()+":"+session.getPort()+">");
		}
	}
}
