package org.barnhorse.puzzlemod.dungeons;

import com.megacrit.cardcrawl.map.MapEdge;
import com.megacrit.cardcrawl.map.MapRoomNode;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import java.util.ArrayList;

public class SingletonMapRow {
    private ArrayList<MapRoomNode> row;
    private MapRoomNode node;

    public SingletonMapRow(AbstractRoom room, MapRoomNode prev, int y) {
        this.row = new ArrayList<>();
        row.add(new MapRoomNode(0, y));
        row.add(new MapRoomNode(1, y));
        row.add(new MapRoomNode(2, y));
        this.node = new MapRoomNode(3, y);
        this.node.room = room;
        if (prev != null) {
            connectNode(prev, this.node);
        }
        row.add(this.node);
        row.add(new MapRoomNode(4, y));
        row.add(new MapRoomNode(5, y));
        row.add(new MapRoomNode(6, y));
    }

    public ArrayList<MapRoomNode> getList() {
        return new ArrayList<>(this.row);
    }

    public MapRoomNode getNode() {
        return this.node;
    }

    private void connectNode(MapRoomNode src, MapRoomNode dst) {
        src.addEdge(
                new MapEdge(
                        src.x, src.y, src.offsetX, src.offsetY,
                        dst.x, dst.y, dst.offsetX, dst.offsetY, false));
    }
}