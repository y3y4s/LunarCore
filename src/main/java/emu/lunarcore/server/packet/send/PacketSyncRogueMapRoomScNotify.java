package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.SyncRogueMapRoomScNotifyOuterClass.SyncRogueMapRoomScNotify;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketSyncRogueMapRoomScNotify extends BasePacket {

    public PacketSyncRogueMapRoomScNotify(Player player) {
        super(CmdId.SyncRogueMapRoomScNotify);
        
        var data = SyncRogueMapRoomScNotify.newInstance();
        
        if (player.getRogueData() != null) {
            data.setMapId(player.getRogueData().getExcel().getMapId());
            data.setCurRoom(player.getRogueData().getCurrentRoom().toProto());
        }
        
        this.setData(data);
    }
}