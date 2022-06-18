package cc.harmo.scmpp;

import cc.harmo.scmpp.config.ScmppConfig;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class Scmpp implements ModInitializer {
    public static final int MAP_ID = -114514;
    public static final Logger LOGGER = LogManager.getLogger("SlimeChunkMap++");
    public static final File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "scmpp.json");

    private ScmppConfig scmppConfig;

    private boolean isInitSuccess = true;

    // 通过fabric获取本mod版本号的方法
    public static String getVersion() {
        ModContainer a = FabricLoader.getInstance().getModContainer("scmpp").orElse(null);
        if (a != null) {
            return a.getMetadata().getVersion().getFriendlyString();
        } else {
            return "unknown";
        }
    }

    @Override
    public void onInitialize() {

        LOGGER.info("Loading SlimeChunkMap++ v" + getVersion() + "...");
        try {
            scmppConfig = ScmppConfig.read();
        } catch (IOException e) {
            LOGGER.error("配置文件读取失败，指令将不可用！", e);
            this.isInitSuccess = false;
        }

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            LiteralCommandNode<ServerCommandSource> rootNode = CommandManager.literal("scmpp").executes(context -> ScmppCommand.scmpp(context, this.isInitSuccess)).build();

            if (!this.isInitSuccess) return;

            LiteralCommandNode<ServerCommandSource> getNode = CommandManager.literal("get").executes(context -> ScmppCommand.getscm(context, this.scmppConfig)).build();
            dispatcher.getRoot().addChild(rootNode);
            rootNode.addChild(getNode);
        });

        LOGGER.info("SlimeChunkMap++ v" + getVersion() + " loaded!");

        ServerLifecycleEvents.SERVER_STOPPED.register((server -> {
            try {
                ScmppConfig.save(scmppConfig);
                LOGGER.info("数据保存完成");
            } catch (IOException e) {
                LOGGER.error("数据保存失败", e);
            }
        }));
    }
}
