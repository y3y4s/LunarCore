package emu.lunarcore.game.rogue;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.GroupInfo;
import emu.lunarcore.data.config.MonsterInfo;
import emu.lunarcore.data.config.PropInfo;
import emu.lunarcore.data.excel.NpcMonsterExcel;
import emu.lunarcore.data.excel.PropExcel;
import emu.lunarcore.data.excel.RogueMonsterExcel;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.SceneEntityLoader;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.extra.PropRogueData;

public class RogueEntityLoader extends SceneEntityLoader {
    
    public EntityMonster loadMonster(Scene scene, GroupInfo group, MonsterInfo monsterInfo) {
        // Make sure player is in a rogue instance
        RogueInstance rogue = scene.getPlayer().getRogueInstance();
        if (rogue == null) return null;

        // Get rogue group content
        int content = rogue.getCurrentRoom().getExcel().getGroupContent(group.getId());
        if (content <= 0) return null;
        
        // Get rogue monster excel and npc monster excel
        RogueMonsterExcel rogueMonster = GameData.getRogueMonsterExcelMap().get((content * 10) + 1);
        if (rogueMonster == null) return null;
        
        NpcMonsterExcel npcMonster = GameData.getNpcMonsterExcelMap().get(rogueMonster.getNpcMonsterID());
        if (npcMonster == null) return null;
        
        // Actually create the monster now
        EntityMonster monster = new EntityMonster(scene, npcMonster, monsterInfo.getPos());
        monster.getRot().set(monsterInfo.getRot());
        monster.setGroupId(group.getId());
        monster.setInstId(monsterInfo.getID());
        monster.setEventId(rogueMonster.getEventID());
        monster.setOverrideStageId(rogueMonster.getEventID());
        
        return monster;
    }
    
    public EntityProp loadProp(Scene scene, GroupInfo group, PropInfo propInfo) {
        // Make sure player is in a rogue instance
        RogueInstance rogue = scene.getPlayer().getRogueInstance();
        if (rogue == null) return null;
        
        // Set variables here so we can override them later if we need
        int propId = propInfo.getPropID();
        PropState state = propInfo.getState();
        PropRogueData propExtra = null;
        
        // Rogue Door id is 1000
        if (propId == 1000) {
            // Site index
            int index = 0;
            
            // Eww
            if (propInfo.getName().equals("Door2")) {
                index = 1;
            }
            
            // Get portal data
            RogueRoomData room = rogue.getCurrentRoom();
            if (room.getNextSiteIds().length > 0) {
                int siteId = room.getNextSiteIds()[index];
                int roomId = rogue.getRooms().get(siteId).getRoomId();
                
                propExtra = new PropRogueData(roomId, siteId);
            } else {
                // Exit portal?
            }
            
            // Force rogue door to be open
            propId = 1021; // TODO get proper portal ids
            state = PropState.Open;
        }
        
        // Get prop excel
        PropExcel propExcel = GameData.getPropExcelMap().get(propId);
        if (propExcel == null) return null;
        
        // Create prop from prop info
        EntityProp prop = new EntityProp(scene, propExcel, propInfo.getPos());
        prop.getRot().set(propInfo.getRot());
        prop.setPropInfo(propInfo);
        prop.setGroupId(group.getId());
        prop.setInstId(propInfo.getID());
        prop.setState(state);
        
        // Overrides
        if (propExtra != null) {
            prop.setRogueData(propExtra);
        }
        
        // Add trigger
        if (propInfo.getTrigger() != null) {
            scene.getTriggers().add(propInfo.getTrigger());
        }
        
        return prop;
    }
}