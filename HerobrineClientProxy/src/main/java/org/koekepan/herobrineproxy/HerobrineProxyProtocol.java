package org.koekepan.herobrineproxy;

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientRequestPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientResourcePackStatusPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientSettingsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientTabCompletePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerInteractEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPlaceBlockPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerStatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerSwingArmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientCreativeInventoryActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientEnchantItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSpectatePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSteerBoatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientSteerVehiclePacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientUpdateSignPacket;
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientVehicleMovePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerBossBarPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerCombatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDifficultyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPlayerListEntryPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerPluginMessagePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerResourcePackSendPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerRespawnPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerSetCooldownPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerStatisticsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerSwitchCameraPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTabCompletePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTitlePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAnimationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityAttachPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityCollectItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityDestroyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEffectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityEquipmentPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityHeadLookPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMetadataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityMovementPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityPropertiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityRemoveEffectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntitySetPassengersPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityStatusPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityVelocityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerVehicleMovePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerAbilitiesPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerHealthPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerSetExperiencePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerUseBedPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnExpOrbPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnGlobalEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnMobPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnObjectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPaintingPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.spawn.ServerSpawnPlayerPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerCloseWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerConfirmTransactionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowItemsPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerWindowPropertyPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockBreakAnimPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerBlockValuePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerChunkDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerExplosionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMapDataPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerMultiBlockChangePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerNotifyClientPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerOpenTileEntityEditorPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerPlayBuiltinSoundPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerPlayEffectPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerPlaySoundPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnParticlePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerSpawnPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUnloadChunkPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateTileEntityPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerUpdateTimePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.world.ServerWorldBorderPacket;
import com.github.steveice10.mc.protocol.packet.login.client.LoginStartPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginDisconnectPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSetCompressionPacket;
import com.github.steveice10.mc.protocol.MinecraftConstants;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.SubProtocol;
import com.github.steveice10.mc.protocol.packet.handshake.client.HandshakePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerDisplayScoreboardPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerScoreboardObjectivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerTeamPacket;
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerUpdateScorePacket;
import com.github.steveice10.mc.protocol.packet.login.client.EncryptionResponsePacket;
import com.github.steveice10.mc.protocol.packet.login.server.EncryptionRequestPacket;
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusPingPacket;
import com.github.steveice10.mc.protocol.packet.status.client.StatusQueryPacket;
import com.github.steveice10.mc.protocol.packet.status.server.StatusPongPacket;
import com.github.steveice10.mc.protocol.packet.status.server.StatusResponsePacket;
import com.github.steveice10.packetlib.Client;
import com.github.steveice10.packetlib.Server;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent;
import com.github.steveice10.packetlib.event.session.PacketSentEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.event.session.SessionListener;
import com.github.steveice10.packetlib.packet.Packet;

// this class describes the packet protocol that is used by the proxy and tracks protocol state changes.
// one instance per client or server connection.
public class HerobrineProxyProtocol extends MinecraftProtocol {
	private SubProtocol subProtocol;
	
	public HerobrineProxyProtocol() {
		super(SubProtocol.STATUS);
	}
	
	public HerobrineProxyProtocol(Session session) {
		super(SubProtocol.STATUS);
		this.init();
	}

	// add listener for a server connection (proxy <-> server)
	@Override
	public void newClientSession(Client client, Session session) {
		setSubProtocol(SubProtocol.HANDSHAKE, true, session); // start in handshake protocol state

		session.addListener(new SessionAdapter() {
			// client-bound packets
			@Override
			public void packetReceived(PacketReceivedEvent event) {
				processSubProtocolState(this, event.getSession(), event.getPacket(), true, true);
			}

			// server-bound packets
			@Override
			public void packetSent(PacketSentEvent event) {
				processSubProtocolState(this, event.getSession(), event.getPacket(), true, false);
			}
		});
	}

	// add listener for a client connection (client <-> proxy)
	@Override
	public void newServerSession(Server server, Session session) {
		setSubProtocol(SubProtocol.HANDSHAKE, false, session); // start in handshake protocol state

		session.addListener(new SessionAdapter() {
			// server-bound packets
			@Override
			public void packetReceived(PacketReceivedEvent event) {
				processSubProtocolState(this, event.getSession(), event.getPacket(), false, false);
			}

			// client-bound packets
			@Override
			public void packetSent(PacketSentEvent event) {
				processSubProtocolState(this, event.getSession(), event.getPacket(), false, true);
			}
		});
	}

	// process protocol state changes from server-bound or client-bound packets
	private void processSubProtocolState(SessionListener listener, Session session, Packet packet, boolean client,
			boolean clientbound) {
		HerobrineProxyProtocol protocol = (HerobrineProxyProtocol) session.getPacketProtocol();

		if (clientbound) {
			// client-bound packets
			if (protocol.getSubProtocol() == SubProtocol.LOGIN) {
				if (packet instanceof LoginSetCompressionPacket) {
					session.setCompressionThreshold(((LoginSetCompressionPacket) packet).getThreshold());
				} else if (packet instanceof LoginSuccessPacket) {
					protocol.setSubProtocol(SubProtocol.GAME, client, session);
					session.removeListener(listener);
				}
			}
		} else {
			// server-bound packets
			if (protocol.getSubProtocol() == SubProtocol.HANDSHAKE) {
				if (packet instanceof HandshakePacket) {
					HandshakePacket handshake = (HandshakePacket) packet;
					switch (handshake.getIntent()) {
					case STATUS:
						protocol.setSubProtocol(SubProtocol.STATUS, client, session);
						session.removeListener(listener);
						break;
					case LOGIN:
						protocol.setSubProtocol(SubProtocol.LOGIN, client, session);
						if (handshake.getProtocolVersion() > MinecraftConstants.PROTOCOL_VERSION) {
							session.disconnect(
									"Outdated server! I'm still on " + MinecraftConstants.GAME_VERSION + ".");
						} else if (handshake.getProtocolVersion() < MinecraftConstants.PROTOCOL_VERSION) {
							session.disconnect("Outdated client! Please use " + MinecraftConstants.GAME_VERSION + ".");
						}
						break;
					default:
						throw new UnsupportedOperationException("Invalid client intent: " + handshake.getIntent());
					}
				}
			}
		}
	}
	
	/*public void setSubProtocol(SubProtocol subProtocol, boolean client) {
		this.clearPackets();
        switch (subProtocol) {
            case HANDSHAKE:
                if (client) {
                    this.initClientHandshake();
                } else {
                    this.initServerHandshake();
                }

                break;
            case LOGIN:
                if (client) {
                    this.initClientLogin();
                } else {
                    this.initServerLogin();
                }

                break;
            case GAME:
                if (client) {
                    this.initClientGame();
                } else {
                    this.initServerGame();
                }

                break;
            case STATUS:
                if (client) {
                    this.initClientStatus();
                } else {
                    this.initServerStatus();
                }

                break;
        }

        this.subProtocol = subProtocol;
		return ;
	}*/

	private void init() {
		this.register(0x00, HandshakePacket.class);
		
		this.register(0x01, LoginDisconnectPacket.class);
		this.register(0x02, EncryptionRequestPacket.class);
		this.register(0x03, LoginSuccessPacket.class);
		this.register(0x04, LoginSetCompressionPacket.class);
		this.register(0x05, LoginStartPacket.class);
		this.register(0x06, EncryptionResponsePacket.class);
		

		this.register(0x07, ServerSpawnObjectPacket.class);
		this.register(0x08, ServerSpawnExpOrbPacket.class);
		this.register(0x09, ServerSpawnGlobalEntityPacket.class);
		this.register(0x0A, ServerSpawnMobPacket.class);
		this.register(0x0B, ServerSpawnPaintingPacket.class);
		this.register(0x0C, ServerSpawnPlayerPacket.class);
		this.register(0x0D, ServerEntityAnimationPacket.class);
		this.register(0x0E, ServerStatisticsPacket.class);
		this.register(0x0F, ServerBlockBreakAnimPacket.class);
		this.register(0x10, ServerUpdateTileEntityPacket.class);
		this.register(0x11, ServerBlockValuePacket.class);
		this.register(0x12, ServerBlockChangePacket.class);
		this.register(0x13, ServerBossBarPacket.class);
		this.register(0x14, ServerDifficultyPacket.class);
		this.register(0x15, ServerTabCompletePacket.class);
		this.register(0x16, ServerChatPacket.class);
		this.register(0x17, ServerMultiBlockChangePacket.class);
		this.register(0x18, ServerConfirmTransactionPacket.class);
		this.register(0x19, ServerCloseWindowPacket.class);
		this.register(0x1A, ServerOpenWindowPacket.class);
		this.register(0x1B, ServerWindowItemsPacket.class);
		this.register(0x1C, ServerWindowPropertyPacket.class);
		this.register(0x1D, ServerSetSlotPacket.class);
		this.register(0x1E, ServerSetCooldownPacket.class);
		this.register(0x1F, ServerPluginMessagePacket.class);
		this.register(0x20, ServerPlaySoundPacket.class);
		this.register(0x21, ServerDisconnectPacket.class);
		this.register(0x22, ServerEntityStatusPacket.class);
		this.register(0x23, ServerExplosionPacket.class);
		this.register(0x24, ServerUnloadChunkPacket.class);
		this.register(0x25, ServerNotifyClientPacket.class);
		this.register(0x26, ServerKeepAlivePacket.class);
		this.register(0x27, ServerChunkDataPacket.class);
		this.register(0x28, ServerPlayEffectPacket.class);
		this.register(0x29, ServerSpawnParticlePacket.class);
		this.register(0x2A, ServerJoinGamePacket.class);
		this.register(0x2B, ServerMapDataPacket.class);
		this.register(0x2C, ServerEntityPositionPacket.class);
		this.register(0x2D, ServerEntityPositionRotationPacket.class);
		this.register(0x2E, ServerEntityRotationPacket.class);
		this.register(0x2F, ServerEntityMovementPacket.class);
		this.register(0x30, ServerVehicleMovePacket.class);
		this.register(0x31, ServerOpenTileEntityEditorPacket.class);
		this.register(0x32, ServerPlayerAbilitiesPacket.class);
		this.register(0x33, ServerCombatPacket.class);
		this.register(0x34, ServerPlayerListEntryPacket.class);
		this.register(0x35, ServerPlayerPositionRotationPacket.class);
		this.register(0x36, ServerPlayerUseBedPacket.class);
		this.register(0x37, ServerEntityDestroyPacket.class);
		this.register(0x38, ServerEntityRemoveEffectPacket.class);
		this.register(0x39, ServerResourcePackSendPacket.class);
		this.register(0x3A, ServerRespawnPacket.class);
		this.register(0x3B, ServerEntityHeadLookPacket.class);
		this.register(0x3C, ServerWorldBorderPacket.class);
		this.register(0x3D, ServerSwitchCameraPacket.class);
		this.register(0x3E, ServerPlayerChangeHeldItemPacket.class);
		this.register(0x3F, ServerDisplayScoreboardPacket.class);
		this.register(0x40, ServerEntityMetadataPacket.class);
		this.register(0x41, ServerEntityAttachPacket.class);
		this.register(0x42, ServerEntityVelocityPacket.class);
		this.register(0x43, ServerEntityEquipmentPacket.class);
		this.register(0x44, ServerPlayerSetExperiencePacket.class);
		this.register(0x45, ServerPlayerHealthPacket.class);
		this.register(0x46, ServerScoreboardObjectivePacket.class);
		this.register(0x47, ServerEntitySetPassengersPacket.class);
		this.register(0x48, ServerTeamPacket.class);
		this.register(0x49, ServerUpdateScorePacket.class);
		this.register(0x4A, ServerSpawnPositionPacket.class);
		this.register(0x4B, ServerUpdateTimePacket.class);
		this.register(0x4C, ServerTitlePacket.class);
		this.register(0x4D, ServerPlayBuiltinSoundPacket.class);
		this.register(0x4E, ServerPlayerListDataPacket.class);
		this.register(0x4F, ServerEntityCollectItemPacket.class);
		this.register(0x50, ServerEntityTeleportPacket.class);
		this.register(0x51, ServerEntityPropertiesPacket.class);
		this.register(0x52, ServerEntityEffectPacket.class);

		this.register(0x53, ClientTeleportConfirmPacket.class);
		this.register(0x54, ClientTabCompletePacket.class);
		this.register(0x55, ClientChatPacket.class);
		this.register(0x56, ClientRequestPacket.class);
		this.register(0x57, ClientSettingsPacket.class);
		this.register(0x58, ClientConfirmTransactionPacket.class);
		this.register(0x59, ClientEnchantItemPacket.class);
		this.register(0x5A, ClientWindowActionPacket.class);
		this.register(0x5B, ClientCloseWindowPacket.class);
		this.register(0x5C, ClientPluginMessagePacket.class);
		this.register(0x5D, ClientPlayerInteractEntityPacket.class);
		this.register(0x5E, ClientKeepAlivePacket.class);
		this.register(0x5F, ClientPlayerPositionPacket.class);
		this.register(0x60, ClientPlayerPositionRotationPacket.class);
		this.register(0x61, ClientPlayerRotationPacket.class);
		this.register(0x62, ClientPlayerMovementPacket.class);
		this.register(0x63, ClientVehicleMovePacket.class);
		this.register(0x64, ClientSteerBoatPacket.class);
		this.register(0x65, ClientPlayerAbilitiesPacket.class);
		this.register(0x66, ClientPlayerActionPacket.class);
		this.register(0x67, ClientPlayerStatePacket.class);
		this.register(0x68, ClientSteerVehiclePacket.class);
		this.register(0x69, ClientResourcePackStatusPacket.class);
		this.register(0x6A, ClientPlayerChangeHeldItemPacket.class);
		this.register(0x6B, ClientCreativeInventoryActionPacket.class);
		this.register(0x6C, ClientUpdateSignPacket.class);
		this.register(0x6D, ClientPlayerSwingArmPacket.class);
		this.register(0x6E, ClientSpectatePacket.class);
		this.register(0x6F, ClientPlayerPlaceBlockPacket.class);
		this.register(0x70, ClientPlayerUseItemPacket.class);
	
		this.register(0x71, StatusResponsePacket.class);
		this.register(0x72, StatusPongPacket.class);

		this.register(0x73, StatusQueryPacket.class);
		this.register(0x74, StatusPingPacket.class);
	}

}
