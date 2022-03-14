package cc.harmo.scmpp;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Scmpp implements ModInitializer {
    public static final int MAP_ID = -114514;
    public static final Logger LOGGER = LogManager.getLogger("SlimeChunkMap++");

    @Override
    public void onInitialize() {
        LOGGER.info("Mod loaded successfully");
    }

}
