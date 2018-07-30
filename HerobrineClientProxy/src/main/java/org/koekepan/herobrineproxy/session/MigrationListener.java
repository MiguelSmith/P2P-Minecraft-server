package org.koekepan.herobrineproxy.session;

import java.nio.charset.StandardCharsets;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MigrationListener extends SessionAdapter {
	private ProxySession session;

	public MigrationListener(ProxySession session) {
		this.session = session;
	}

	// client-bound
	@Override
	public void packetReceived(PacketReceivedEvent event) {

		// register session for migrate channel when join game packet is received
		if (event.getPacket() instanceof ServerJoinGamePacket) {
			registerSessionForChannel(event.getSession(), "Koekepan|migrate");
		} 

		// listen for migration packet
		if(event.getPacket() instanceof ServerPluginMessagePacket) {
			ServerPluginMessagePacket packet = (ServerPluginMessagePacket) event.getPacket();

			if (packet.getChannel().equals("Koekepan|migrate")) {
				try {
					String host = readStringFromData(packet.getData());
					session.migrate(host, 25565); // TODO where to get port?
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void registerSessionForChannel(Session session, String channel) {
		byte[] payload = writeStringToPluginMessageData(channel);
		String registerMessage = "REGISTER";
		ClientPluginMessagePacket registerPacket = new ClientPluginMessagePacket(registerMessage, payload);
		session.send(registerPacket);
	}

	public static byte[] writeStringToPluginMessageData(String message) {
		byte[] data = message.getBytes(StandardCharsets.UTF_8);
		ByteBuf buff = Unpooled.buffer();        
		buff.writeBytes(data);
		return buff.array();
	}

	public String readStringFromData(byte[] data) {
		ByteBuf buffer = Unpooled.wrappedBuffer(data);
		byte[] bytes = new byte[readVarIntFromBuffer(buffer)];
		buffer.readBytes(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public int readVarIntFromBuffer(ByteBuf buffer) {
		int value = 0;
		int size = 0;
		int b = 0;
		while(((b = buffer.readByte()) & 0x80) == 0x80) {
			value |= (b & 0x7F) << (size++ * 7);
			if(size > 5) {
				throw new RuntimeException("VarInt too long (length must be <= 5)"); // TODO IOException rather than RuntimeException?
			}
		}

		return value | ((b & 0x7F) << (size * 7));
	}

}
