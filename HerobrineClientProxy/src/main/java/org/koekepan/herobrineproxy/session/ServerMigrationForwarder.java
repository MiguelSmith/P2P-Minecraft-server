
package org.koekepan.herobrineproxy.session;

import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.data.handshake.HandshakeIntent;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSetCompressionPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.ConnectedEvent;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;

public class ServerMigrationForwarder extends SessionAdapter {

	private String host;
	private int port;
	private ProxySession proxySession = null;
	private ClientMigrationForwarder clientMigrationForwarder = null;

	public ServerMigrationForwarder(String host, int port, ProxySession proxySession, ClientMigrationForwarder clientMigrationForwarder) {
		this.host = host;
		this.port = port;
		this.proxySession = proxySession;
		this.clientMigrationForwarder = clientMigrationForwarder;
	}
	

	@Override
	public void connected(ConnectedEvent event) {
		event.getSession().send(new HandshakePacket(MinecraftConstants.PROTOCOL_VERSION, host, port, HandshakeIntent.LOGIN));
		event.getSession().send(new LoginStartPacket(proxySession.getUsername()));
	}

	
	@Override
	public void packetReceived(PacketReceivedEvent event) {
		Packet packet = event.getPacket();
		Session client = proxySession.getClient();
		
		if(packet instanceof LoginSetCompressionPacket) {
			// do nothing
		} else if(packet instanceof LoginSuccessPacket) {
			
			// disconnect old server (clientside and serverside)
			proxySession.getServer().removeListener(proxySession.getServerPacketForwarder());
			proxySession.setServerPacketForwarder(null);
			proxySession.getClient().removeListener(proxySession.getClientPacketForwarder());
			proxySession.setClientPacketForwarder(null);
			//proxySession.getServer().disconnect("Finished.", false); // TODO make certain its ok to no wait for old server connection to close.
			proxySession.getServer().disconnect("Finished.", true);

			// set proxy session server to new server
			proxySession.setServer(event.getSession());
			
		} else if(packet instanceof ServerJoinGamePacket) {
			// do nothing
		} else if(packet instanceof ServerPlayerPositionRotationPacket) {
			
			// Respond to server player position rotation packet
			// TODO onGround?
			ServerPlayerPositionRotationPacket p = (ServerPlayerPositionRotationPacket) packet;
			event.getSession().send(new ClientPlayerPositionRotationPacket(true, p.getX(), p.getY(), p.getZ(), p.getYaw(), p.getPitch()));
			
			// change forwarder
			event.getSession().removeListener(this);
			ServerPacketForwarder forwarder = new ServerPacketForwarder(proxySession.getClient());
			event.getSession().addListener(forwarder);
			proxySession.setServerPacketForwarder(forwarder);
			
			// empty client migration packet buffer
			clientMigrationForwarder.flush(event.getSession());
		} else {
			client.send(packet);
		}
	}

}
