package org.koekepan.herobrineproxy.session;

import org.json.JSONException;
import org.json.JSONObject;
import org.koekepan.herobrineproxy.ConsoleIO;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PacketHeader {
	private int sender_ID;
	private String type;
	private int packet_ID;
	private int position;
	private byte[] payload;
	
	
	public PacketHeader() {		
	}
	
	public PacketHeader(int sender_id,String type,int packet_id,byte[] payload, int position) {
		this.sender_ID = sender_id;
		this.type = type;
		this.packet_ID = packet_id;
		this.payload = payload;
		this.position = position;
	}
	
	public String toJSON() {
		String json = "";
		try {
			json = new JSONObject()
							.put("sender_ID", this.sender_ID)
							.put("type", this.type)
							.put("packet_ID", packet_ID)
							.put("payload", this.payload).toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		ConsoleIO.println("json string: " +json);
		return json;
	}
	
	@SuppressWarnings("unused")
	public void fromJSON(String json) {
		JsonParser parser = new JsonParser();
		JsonObject jsonObj = parser.parse(json).getAsJsonObject();
		//this.sender_id = (int) jsonObj.get("Sender ID");
	}
	
	public int getPacket_ID() {
		return this.packet_ID;
	}
	
	public int getSender_ID() {
		return this.sender_ID;
	}
	
	public String getType() {
		return this.type;
	}
	
	public byte[] getPayload() {
		return this.payload;
	}
	
	public int getPosition() {
		return this.position;
	}
	
	public void setPacket_ID(int packet_id) {
		this.packet_ID = packet_id;
	}
	
	public void setSender_ID(int sender_id) {
		this.sender_ID = sender_id;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
}
