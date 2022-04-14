package com.fcl.flowRPC.serialize;

import io.netty.buffer.ByteBuf;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtostuffUtil {

    private static final Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();
    private static final byte[] LENGTH_PLACEHOLDER = new byte[4]; // 4 byte = 32 bit , 即1个int的占位符

    private static <T> Schema<T> getSchema(Class<T> clazz) {
        if (!schemaCache.containsKey(clazz)) {
            schemaCache.put(clazz, RuntimeSchema.getSchema(clazz));
        }
        return (Schema<T>) schemaCache.get(clazz);
    }

    public static <T> T decode(ByteBuf in, Class<T> clazz) {
        int size = in.readInt();
        if (size == 0) return null;
        byte[] data = new byte[size];
        in.readBytes(data);
        Schema<T> schema = getSchema(clazz);
        T object = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, object, schema);
        return object;
    }

    public static <T> void encode(T msg, ByteBuf out) {
        if (msg == null) {
            out.writeInt(0);
            return;
        }
        int startIndex = out.writerIndex();
        Schema<T> schema = (Schema<T>) getSchema(msg.getClass());
        LinkedBuffer linkedBuffer = LinkedBuffer.allocate(1024 * 4);
        out.writeBytes(LENGTH_PLACEHOLDER); // 提前给length占位
        out.writeBytes(ProtostuffIOUtil.toByteArray(msg, schema, linkedBuffer)); // 序列化
        out.setInt(startIndex, out.writerIndex() - startIndex - 4); // 回头设置length
    }
}
