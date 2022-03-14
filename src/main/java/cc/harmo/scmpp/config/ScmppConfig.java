package cc.harmo.scmpp.config;

import cc.harmo.scmpp.Scmpp;
import cc.harmo.scmpp.util.Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScmppConfig {
    private Map<String, Long> data = new HashMap<>();

    public static void save(ScmppConfig scmppConfig) throws IOException {
        Util.writeFile(Scmpp.configFile, scmppConfig);
    }

    public static ScmppConfig read() throws IOException {
        if (!Scmpp.configFile.exists()) {
            ScmppConfig scmppConfig = new ScmppConfig();
            save(scmppConfig);
            return scmppConfig;
        }

        return Util.readFile(Scmpp.configFile, ScmppConfig.class);
    }

    /**
     * 是否可以操作
     *
     * @param uuid
     */
    public boolean isEnable(UUID uuid) {
        long time = 3 * 24 * 60 * 60 * 1000;//必须超过此时间

        long sysTime = System.currentTimeMillis();
        long playerTime = data.getOrDefault(uuid.toString(), 0L);

        return (sysTime - playerTime > time);
    }

    /**
     * 更新某人的操作时间
     *
     * @param uuid
     */
    public void update(UUID uuid) {
        data.put(uuid.toString(), System.currentTimeMillis());
    }

    public String getCoolingTime(UUID uuid) {
        if (isEnable(uuid)) {
            return "指令可用";
        } else {
            long time = 3 * 24 * 60 * 60 * 1000; //必须超过此时间
            long playerTime = data.get(uuid.toString()) + time;
            long sysTime = System.currentTimeMillis();

            return Util.toDateStr(playerTime - sysTime);
        }
    }
}
