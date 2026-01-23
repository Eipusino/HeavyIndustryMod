package endfield.graphics.g2d;

import arc.graphics.Color;
import arc.graphics.Mesh;
import arc.graphics.g2d.Batch;

public abstract class BaseBatch extends Batch {
	protected Mesh mesh;

	protected boolean sortAscending = true;

	protected final Color color = new Color(1, 1, 1, 1);

	protected final Color mixColor = Color.clear;

	@Override
	protected void z(float z) {
		this.z = sortAscending ? z : -z;
	}

	/** Sets the sorting order. The batch must be flushed for this to take effect properly. */
	protected void setSortAscending(boolean ascend) {
		sortAscending = ascend;
	}

	protected void setColor(Color tint) {
		color.set(tint);
		colorPacked = tint.toFloatBits();
	}

	protected void setColor(float r, float g, float b, float a) {
		color.set(r, g, b, a);
		colorPacked = color.toFloatBits();
	}

	protected Color getColor() {
		return color;
	}

	@Override
	protected void setPackedColor(float packedColor) {
		color.abgr8888(packedColor);
		colorPacked = packedColor;
	}

	protected void setMixColor(Color tint) {
		mixColor.set(tint);
		mixColorPacked = tint.toFloatBits();
	}

	protected void setMixColor(float r, float g, float b, float a) {
		mixColor.set(r, g, b, a);
		mixColorPacked = mixColor.toFloatBits();
	}

	protected Color getMixColor() {
		return mixColor;
	}

	@Override
	protected void setPackedMixColor(float packedColor) {
		mixColor.abgr8888(packedColor);
		mixColorPacked = packedColor;
	}

	@Override
	public void dispose() {
		if (mesh != null) {
			mesh.dispose();
		}
		if (ownsShader && shader != null) shader.dispose();
	}
}
