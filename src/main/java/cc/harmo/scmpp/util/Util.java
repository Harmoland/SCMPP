package cc.harmo.scmpp.util;

import cc.harmo.scmpp.Scmpp;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.random.ChunkRandom;

import java.io.*;
import java.lang.reflect.Type;

public class Util {
    public final static String encoding = "UTF-8";//文件编码

    public static GridPos blockPos2GridPos(BlockPos blockPos) {
        return new GridPos(blockPos.getX() >> 2, blockPos.getZ() >> 2);
    }

    public static ChunkPos gridPos2ChunkPos(GridPos gridPos) {
        return new ChunkPos(gridPos.x() >> 2, gridPos.z() >> 2);
    }

    public static boolean isSlimeChunk(StructureWorldAccess world, int chunkX, int chunkZ) {
        return ChunkRandom.getSlimeRandom(chunkX, chunkZ, world.getSeed(), 987234911L).nextInt(10) == 0;
    }

    public static boolean hasSlimeSpawnEntry(ServerWorld world, BlockPos pos) {
        return world.getBiome(pos).value().getSpawnSettings().getSpawnEntries(SpawnGroup.MONSTER).getEntries().
                stream().anyMatch(it -> it.type == EntityType.SLIME);
    }

    /**
     * 对象转json文本
     *
     * @param o
     * @param type
     * @return
     */
    public static String toJson(Object o, Type type) {
        return new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create().toJson(o, type);
    }

    /**
     * 文本转json对象
     *
     * @param json
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T toClass(String json, Type type) {
        if (json == null) {
            return null;
        }
        JsonReader jsonReader = new JsonReader(new StringReader(json));
        jsonReader.setLenient(true);
        return new GsonBuilder().setPrettyPrinting().create().fromJson(jsonReader, type);
    }

    /**
     * 加载文件
     */
    public static <T> T readFile(File file, Type type) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists() || !file.isFile()) {
            throw new FileNotFoundException(file.getAbsolutePath());
        }

        String json = read(file);
        return toClass(json, type);
    }

    /**
     * 保存文件
     */
    public static void writeFile(File file, Object obj) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists() || !file.isFile()) {
            if (!file.createNewFile()) {
                throw new IOException("文件创建失败");
            }
        }

        if (obj == null) {
            Scmpp.LOGGER.warn("请求写入null对象，已结束本次写入：" + file);
            return;
        }

        String json = toJson(obj, obj.getClass());
        write(file, json);
    }

    /**
     * 写入文件
     *
     * @param file
     * @param str
     * @return
     */
    public static void write(File file, String str) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        // 创建临时文件
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), encoding);
        outputStreamWriter.write(str);
        outputStreamWriter.close();
    }

    /**
     * 文件读取
     *
     * @param file
     * @return
     */
    public static String read(File file) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }

        StringBuilder stringBuilder = new StringBuilder();
        InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = bufferedReader.readLine();
        while (lineTxt != null) {
            stringBuilder.append(lineTxt);

            lineTxt = bufferedReader.readLine();
            if (lineTxt != null) {
                stringBuilder.append("\n");
            }
        }
        read.close();
        return stringBuilder.toString();
    }

    public static String toDateStr(long time) {
        float day = (float) time / 86400000;
        String s = String.format("%.1f", day);

        return s + "天";
    }
}
