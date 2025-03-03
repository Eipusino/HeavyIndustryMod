package heavyindustry.util;

import arc.func.Func;
import arc.struct.LongMap;
import arc.util.io.Reads;
import arc.util.io.Writes;
import arc.util.serialization.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;

/**
 * Used to package an object, objects implementing this interface can customize the behavior of serializing data into a byte stream, as referenced in {@link Serializable},
 * but this is usually faster than Java serialization because we often do not need to pass the complete information of an object.
 * <p>Here is a use case:
 * <pre>{@code
 * //Declare a packable type
 * public class GltfData implements DataPackable {
 *	 private static final long typeID = 1587541965784324577l;
 *
 *	 static {
 *		 DataPackable.assignType(typeID, args -> new GltfData());
 *	 }
 *
 *	 String name;
 *	 float health;
 *	 boolean alive;
 *
 *	 @Override
 *	 public long typeID() {
 *		 return typeID;
 *	 }
 *
 *	 @Override
 *	 public void write(Writes write) {
 *		 write.str(name);
 *		 write.f(health);
 *		 write.bool(alive);
 *	 }
 *
 *	 @Override
 *	 public void read(Reads read) {
 *		 name = read.str();
 *		 health = read.f();
 *		 alive = read.bool();
 *	 }
 * }
 * }</pre>
 * So, use this object and complete data packaging and disassembly:
 * <pre>{@code
 * GltfData d = new GltfData();
 * d.name = "Template";
 * d.health = 100;
 * d.alive = true;
 *
 * byte[] dataArr = a.pack();//Packaging data
 * GltfData d1 = DataPackable.readObject(dataArr);//Read directly as a new instance with the same properties.
 * GltfData d2 = new GltfData();
 * d2.read(dataArr);//Instantiate first, then read properties from the array.
 * }</pre>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public interface DataPackable {
	LongMap<Func> objectProvMap = new LongMap<>();

	/**
	 * Register a constructor of a wrapper type that takes an array of objects
	 * as a parameter and returns the object in its initial state.
	 *
	 * @param typeID	  Construct an ID for this type
	 * @param constructor Method for creating raw objects when performing data parsing using this ID
	 */
	static void assignType(long typeID, Func<Object[], Object> constructor) {
		if (objectProvMap.put(typeID, constructor) != null)
			throw new SerializationException("conflicting id, type id: " + typeID + "was assigned");
	}

	/**
	 * Read and return a new object with wrapper information from a packaged byte array.
	 * Note that the passed constructor parameters do not have type checking,
	 * and it is generally not recommended to use and register constructors that contain parameters.
	 */
	static <T extends DataPackable> T readObject(byte[] bytes, Object... param) {
		long id = new Reads(new DataInputStream(new ByteArrayInputStream(bytes))).l();
		Func<Object[], T> objProv = (Func<Object[], T>) objectProvMap.get(id);
		if (objProv == null)
			throw new SerializationException("type id: " + id + " was not assigned");

		T result = objProv.get(param);
		result.read(bytes);
		return result;
	}

	/**
	 * The type identifier ID of this object, for a class,
	 * the method of its instance must return the same ID at any time.
	 */
	long typeID();

	/**
	 * The method of packaging this instance to write object information should be implemented by
	 * writing the information that the object needs to be packaged in this method.
	 *
	 * @param write Tools used to write data
	 */
	void write(Writes write);

	/**
	 * The method used to read packaging information when opening the package should be the reverse
	 * operation of {@link DataPackable#write(Writes)}, which sets the data back into the object.
	 *
	 * @param read Incoming data reading tool
	 */
	void read(Reads read);

	/** Wrap the object as a byte array and send it out. */
	default byte[] pack() {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Writes write = new Writes(new DataOutputStream(outputStream));
		write.l(typeID());
		write(write);

		return outputStream.toByteArray();
	}

	/**
	 * Read a byte array of data, which must be the array obtained from the object wrapper or structurally consistent.
	 *
	 * @param bytes GltfData array to be read
	 * @throws SerializationException If the given byte array is not packaged by this object
	 */
	default void read(byte[] bytes) {
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		Reads read = new Reads(new DataInputStream(inputStream));
		if (read.l() != typeID())
			throw new SerializationException("type id was unequal marked type id");
		read(read);
	}
}
