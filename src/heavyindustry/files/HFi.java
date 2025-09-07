package heavyindustry.files;

import arc.Files.FileType;
import arc.files.Fi;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The HFi class extends the Fi class to handle resources in the class path.
 * <p>It locates and loads resources through ClassLoader and does not support write operations.
 */
public class HFi extends Fi {
	/** ClassLoader for loading class path resources */
	public final ClassLoader loader;

	/**
	 * Constructor, create an HFi instance using the specified ClassLoader
	 * @param loader ClassLoader used to load resources, cannot be empty
	 */
	public HFi(ClassLoader loader) {
		this("", loader);
	}

	/**
	 * Private constructor to create HFi instance from path string
	 * @param path resource path
	 * @param loader ClassLoader used to load resources, cannot be empty
	 */
	private HFi(String path, ClassLoader loader) {
		this(new File(path), loader);
	}

	/**
	 * Private constructor, create HFi instance from File object.
	 * @param file Specify the File object of the resource
	 * @param classLoader ClassLoader used to load resources, cannot be empty
	 */
	private HFi(File file, ClassLoader classLoader) {
		super(file, FileType.classpath);

		if (classLoader == null) throw new IllegalArgumentException("classLoader cannot be null.");
		loader = classLoader;
	}

	/**
	 * Check if resources exist.
	 * @return If the resource exists, return true; otherwise, return false
	 */
	@Override
	public boolean exists() {
		return loader.getResource(path()) != null;
	}

	/**
	 * Get the resource path and remove the starting '/'.
	 * @return The Path of Resources
	 */
	@Override
	public String path() {
		return super.path().substring(1);
	}

	/**
	 * Read the input stream of resources
	 * @return Input flow of resources
	 * @throws UnsupportedOperationException If attempting to read the root path, throw an exception
	 */
	@Override
	public InputStream read() {
		if (file.getPath().isEmpty()) throw new UnsupportedOperationException("Cannot read the root.");
		return loader.getResourceAsStream(path());
	}

	/**
	 * Throwing an exception, indicating that write operations are not supported
	 * @return OutputStream object, but it will never be reached
	 * @throws UnsupportedOperationException Always throwing unsupported operation exceptions
	 */
	@Override
	public OutputStream write() {
		throw new UnsupportedOperationException("HFi cannot write anything.");
	}

	/**
	 * Retrieve the parent resource of the current resource
	 * @return The Fi instance of the parent resource of the current resource
	 */
	@Override
	public Fi parent() {
		return new HFi(file.getParent(), loader);
	}

	/**
	 * Create or retrieve sub resources of the current resource
	 * @param name Name of sub resource
	 * @return Fi instances of sub resources of the current resource
	 */
	@Override
	public Fi child(String name) {
		return new HFi(new File(file, name), loader);
	}
}
