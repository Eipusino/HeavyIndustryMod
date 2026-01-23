package endfield.type.shape;

import arc.func.Intc2;

/**
 * Represents a shape for various content types.
 * Shapes are defined by a set of relative coordinates from an origin (0,0).
 */
public interface Shape {
	/** The width of the shape's bounding box. */
	int width();

	/** The height of the shape's bounding box. */
	int height();

	/** Returns true if the tile at (x, y) relative to the shape's origin is part of the shape. */
	boolean get(int x, int y);

	/** Iterates over each point in the shape, providing relative coordinates from the origin. */
	void each(Intc2 consumer);

	/** Loads any resources required for this shape. */
	void load();
}
