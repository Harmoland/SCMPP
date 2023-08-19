package cc.harmo.scmpp;

import cc.harmo.scmpp.config.ScmppConfig;
import cc.harmo.scmpp.util.SimpleCommandException;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static cc.harmo.scmpp.Scmpp.MAP_ID;
import static cc.harmo.scmpp.Scmpp.getVersion;

public class ScmppCommand {
    public static int scmpp(CommandContext<ServerCommandSource> context, boolean isInitSuccess) throws CommandSyntaxException {
        MutableText text = Text.literal("SlimeChunkMap++ v" + getVersion());
        if (!isInitSuccess) {
            text.append(Text.literal("\n配置文件加载失败，部分指令将不可用！").formatted(Formatting.RED, Formatting.BOLD));
        }
        text.append(Text.literal("\n--------------------------").formatted(Formatting.GRAY));
        text.append(Text.literal("\n/scmpp get"));
        text.append(Text.literal(" - ").formatted(Formatting.GRAY));
        if (!isInitSuccess) {
            text.append(Text.literal("获得一个史莱姆区块地图\n").formatted(Formatting.RED));
        } else {
            text.append(Text.literal("获得一个史莱姆区块地图\n").formatted(Formatting.GREEN));
        }
        text.append(Text.literal("--------------------------").formatted(Formatting.GRAY));
        context.getSource().sendFeedback(() -> text, false);
        return com.mojang.brigadier.Command.SINGLE_SUCCESS;
    }

    public static int getscm(CommandContext<ServerCommandSource> context, ScmppConfig scmppConfig) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player == null) throw new SimpleCommandException(Text.literal("请在游戏内使用该命令！")).create();
        // 时间判断
        if (scmppConfig.isEnable(player.getUuid())) {
            // 判断玩家背包是否已满
            if (player.getInventory().getEmptySlot() == -1) {
                MutableText text = Text.literal("[SlimeChunkMap++] ").formatted(Formatting.GRAY);
                text.append(Text.literal("你的背包已满，请清理背包后再试！").formatted(Formatting.RED));
                source.sendFeedback(() -> text, false);
                return Command.SINGLE_SUCCESS;
            }
            // 给玩家一个地图
            ItemStack itemStack = new ItemStack(Items.FILLED_MAP);
            itemStack.getOrCreateNbt().putInt("map", MAP_ID);

            player.giveItemStack(itemStack);

            scmppConfig.update(player.getUuid());
            MutableText text = Text.literal("[SlimeChunkMap++] ").formatted(Formatting.GRAY);
            text.append(Text.literal("一个史莱姆区块地图已发放到你的背包！").formatted(Formatting.GREEN));
            source.sendFeedback(() -> text, false);
            player.sendMessage(Text.literal("一个史莱姆区块地图已发放到你的背包").formatted(Formatting.GREEN, Formatting.BOLD), true);
        } else {
            MutableText text = Text.literal("[SlimeChunkMap++] ").formatted(Formatting.GRAY);
            text.append(Text.literal(
                    "你已经获得过一个史莱姆区块地图了! 请" + scmppConfig.getCoolingTime(player.getUuid()) + "后再来噢"
            ).formatted(Formatting.LIGHT_PURPLE));
            source.sendFeedback(() -> text, false);
        }
        return Command.SINGLE_SUCCESS;
    }
}
