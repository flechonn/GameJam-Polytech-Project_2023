package info3.game.entity.blocks;

import java.io.IOException;

import info3.game.entity.Block;
import info3.game.entity.DynamicEntity;
import info3.game.entity.TEAM;

public class SpawnerPoint extends Block{

    private static String blockTexture = "resources/blocks/grass.png";
    
    public SpawnerPoint(int x, int y) throws IOException {
        super(x, y, blockTexture);
    }
}
