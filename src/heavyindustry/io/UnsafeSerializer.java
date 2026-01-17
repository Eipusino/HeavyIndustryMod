package heavyindustry.io;

import arc.util.Structs;
import heavyindustry.util.CollectionList;
import heavyindustry.util.CollectionObjectMap;
import heavyindustry.util.Reflects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.List;

import static heavyindustry.util.Unsafer.unsafe;

public final class UnsafeSerializer {
	static final CollectionObjectMap<Class<?>, SerializationInfo> classInfoCache = new CollectionObjectMap<>(Class.class, SerializationInfo.class);

	private UnsafeSerializer() {}

	public static @NotNull ByteBuffer serializeToDirectBuffer(Object obj) throws Exception {
		if (obj == null) return ByteBuffer.allocateDirect(0);

		Class<?> clazz = obj.getClass();
		SerializationInfo info = getSerializationInfo(clazz);

		ByteBuffer buffer = ByteBuffer.allocateDirect((int) info.objectSize);
		long address = Reflects.getAddress(buffer);

		unsafe.copyMemory(obj, 0, null, address, info.objectSize);

		return buffer;
	}

	public static byte @NotNull [] serialize(Object obj) throws Exception {
		if (obj == null) return new byte[0];

		Class<?> clazz = obj.getClass();
		SerializationInfo info = getSerializationInfo(clazz);

		byte[] bytes = new byte[(int) info.objectSize];
		long arrayBaseOffset = unsafe.arrayBaseOffset(byte[].class);

		unsafe.copyMemory(obj, 0, bytes, arrayBaseOffset, info.objectSize);

		return bytes;
	}

	public static void deserialize(Object obj, byte[] bytes) throws Exception {
		if (obj == null || bytes == null || bytes.length == 0) return;

		long arrayBaseOffset = unsafe.arrayBaseOffset(byte[].class);
		SerializationInfo info = getSerializationInfo(obj.getClass());

		unsafe.copyMemory(bytes, arrayBaseOffset, obj, 0, Math.min(bytes.length, info.objectSize));
	}

	@SuppressWarnings("unchecked")
	@Contract("null, _ -> null; !null, null -> null")
	public static <T> T deserialize(Class<T> type, byte[] bytes) throws Exception {
		if (type == null || bytes == null || bytes.length == 0) return null;

		T obj = (T) unsafe.allocateInstance(type);
		deserialize(obj, bytes);
		return obj;
	}

	@SuppressWarnings("unchecked")
	@Contract("null, _ -> null; !null, null -> null")
	public static <T> T deserializeFromDirectBuffer(Class<T> type, ByteBuffer buffer) throws Exception {
		if (type == null || buffer == null || !buffer.isDirect()) return null;

		T obj = (T) unsafe.allocateInstance(type);
		long address = Reflects.getAddress(buffer);
		SerializationInfo info = getSerializationInfo(type);

		unsafe.copyMemory(null, address, obj, 0, Math.min(buffer.capacity(), info.objectSize));
		return obj;
	}

	static SerializationInfo getSerializationInfo(Class<?> type) {
		return classInfoCache.get(type, () -> {
			List<FieldInfo> fields = new CollectionList<>(FieldInfo.class);
			long objectSize = 0;

			Class<?> current = type;
			while (current != null && current != Object.class) {
				for (Field field : current.getDeclaredFields()) {
					if (Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					field.setAccessible(true);
					long offset = unsafe.objectFieldOffset(field);
					fields.add(new FieldInfo(offset, field.getType(), field.getName()));
				}
				current = current.getSuperclass();
			}

			fields.sort(Structs.comparingLong(f -> f.offset));

			if (!fields.isEmpty()) {
				FieldInfo lastField = fields.get(fields.size() - 1);
				objectSize = lastField.offset + estimateFieldSize(lastField.type);
			}

			return new SerializationInfo(fields, objectSize);
		});
	}

	static byte estimateFieldSize(Class<?> type) {
		if (type == boolean.class || type == byte.class) return 1;
		if (type == short.class || type == char.class) return 2;
		if (type == int.class || type == float.class) return 4;
		//if (type == long.class || type == double.class) return 8;
		return 8;
	}

	static class SerializationInfo {
		final List<FieldInfo> fields;
		final long objectSize;

		SerializationInfo(List<FieldInfo> fields, long objectSize) {
			this.fields = fields;
			this.objectSize = objectSize;
		}
	}

	static class FieldInfo {
		final long offset;
		final Class<?> type;
		final String name;

		FieldInfo(long offset, Class<?> type, String name) {
			this.offset = offset;
			this.type = type;
			this.name = name;
		}
	}
}
