package cc.harmo.scmpp;

import cc.harmo.scmpp.config.ScmppConfig;
import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
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

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("scmpp")
                .executes(context -> {
                    MutableText text = new LiteralText("\nSlimeChunkMap++ v" + getVersion());
                    if (!isInitSuccess) {
                        text.append(new LiteralText("\n配置文件加载失败，部分指令将不可用！").formatted(Formatting.RED, Formatting.BOLD));
                    }
                    text.append(new LiteralText("\n--------------------------").formatted(Formatting.GRAY));
                    text.append(new LiteralText("\n/getscm"));
                    text.append(new LiteralText(" - ").formatted(Formatting.GRAY));
                    if (!this.isInitSuccess) {
                        text.append(new LiteralText("获得一个史莱姆区块地图\n").formatted(Formatting.RED));
                    } else {
                        text.append(new LiteralText("获得一个史莱姆区块地图\n").formatted(Formatting.GREEN));
                    }
                    text.append(new LiteralText("--------------------------\n").formatted(Formatting.GRAY));
                    context.getSource().sendFeedback(text, false);
                    return Command.SINGLE_SUCCESS;
                })
        ));

        if (!this.isInitSuccess) return;

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(CommandManager.literal("getscm")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    // 时间判断
                    if (scmppConfig.isEnable(player.getUuid())) {
                        // 判断玩家背包是否已满
                        if (player.getInventory().getEmptySlot() == -1) {
                            MutableText text = new LiteralText("[SlimeChunkMap++] ").formatted(Formatting.GRAY);
                            text.append(new LiteralText("你的背包已满，请清理背包后再试！").formatted(Formatting.RED));
                            source.sendFeedback(text, false);
                            return Command.SINGLE_SUCCESS;
                        }
                        // 给玩家一个地图
                        ItemStack itemStack = new ItemStack(Items.FILLED_MAP);
                        itemStack.getOrCreateNbt().putInt("map", MAP_ID);

                        player.giveItemStack(itemStack);

                        scmppConfig.update(player.getUuid());
                        MutableText text = new LiteralText("[SlimeChunkMap++] ").formatted(Formatting.GRAY);
                        text.append(new LiteralText("一个史莱姆区块地图已发放到你的背包！").formatted(Formatting.GREEN));
                        source.sendFeedback(text, false);
                        player.sendMessage(new LiteralText("一个史莱姆区块地图已发放到你的背包").formatted(Formatting.GREEN, Formatting.BOLD), true);
                    } else {
                        MutableText text = new LiteralText("[SlimeChunkMap++] ").formatted(Formatting.GRAY);
                        text.append(new LiteralText(
                                "你已经获得过一个史莱姆区块地图了！请" + scmppConfig.getCoolingTime(player.getUuid()) + "天后再来噢"
                        ).formatted(Formatting.LIGHT_PURPLE));
                        source.sendFeedback(text, false);
                    }
                    return Command.SINGLE_SUCCESS;
                })
        ));

        LOGGER.info("SlimeChunkMap++ v" + getVersion() + "loaded successfully!");

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
