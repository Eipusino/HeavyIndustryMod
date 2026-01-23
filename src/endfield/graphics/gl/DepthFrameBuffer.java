package endfield.graphics.gl;

import arc.graphics.Color;
import arc.graphics.Gl;
import arc.graphics.Pixmap.Format;
import arc.graphics.Texture;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.gl.FrameBuffer;
import org.jetbrains.annotations.Nullable;

/**
 * A framebuffer that requests depth (and stencil) textures instead of renderbuffers, letting users sample from them.
 * Requires at least GL ES 3.0 to function, since the spec is really dumb and decided to include depth and stencil
 * texture attachments at that version.
 *
 * @since 1.0.6
 */
public class DepthFrameBuffer extends FrameBuffer {
	protected Format format;
	protected boolean hasDepth, hasStencil;

	public DepthFrameBuffer() {
		this(2, 2);
	}

	public DepthFrameBuffer(int width, int height) {
		this(Format.rgba8888, width, height, false, false);
	}

	public DepthFrameBuffer(Format format, int width, int height) {
		this(format, width, height, false, false);
	}

	public DepthFrameBuffer(Format format, int width, int height, boolean hasDepth) {
		this(format, width, height, hasDepth, false);
	}

	public DepthFrameBuffer(int width, int height, boolean hasDepth) {
		this(Format.rgba8888, width, height, hasDepth, false);
	}

	public DepthFrameBuffer(Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
		create(format, width, height, hasDepth, hasStencil);
	}

	public @Nullable Texture getDepthTexture() {
		return hasDepth ? textureAttachments.get(1) : null;
	}

	public @Nullable Texture getStencilTexture() {
		return hasStencil ? textureAttachments.get(hasDepth ? 2 : 1) : null;
	}

	@Override
	protected void create(Format form, int width, int height, boolean hasDep, boolean hasSte) {
		width = Math.max(width, 2);
		height = Math.max(height, 2);
		format = form;

		FrameBufferBuilder builder = new FrameBufferBuilder(width, height);
		builder.addBasicColorTextureAttachment(form);
		if (hasDep) builder.addDepthTextureAttachment(Gl.depthComponent, Gl.floatV);
		if (hasSte) builder.addStencilTextureAttachment(Gl.stencilIndex8, Gl.unsignedByte);

		hasDepth = hasDep;
		hasStencil = hasSte;
		bufferBuilder = builder;
		build();
	}

	@Override
	protected Texture createTexture(FrameBufferTextureAttachmentSpec spec) {
		Texture result = super.createTexture(spec);
		if (!spec.isColorTexture()) result.setFilter(TextureFilter.nearest);

		return result;
	}

	@Override
	public void resize(int width, int height) {
		width = Math.max(width, 2);
		height = Math.max(height, 2);
		if (width == getWidth() && height == getHeight()) return;

		TextureFilter min = getTexture().getMinFilter(), mag = getTexture().getMagFilter();
		dispose();

		FrameBufferBuilder builder = new FrameBufferBuilder(width, height);
		builder.addBasicColorTextureAttachment(format);
		if (hasDepth) builder.addDepthTextureAttachment(Gl.depthComponent, Gl.floatV);
		if (hasStencil) builder.addStencilTextureAttachment(Gl.stencilIndex8, Gl.unsignedByte);

		bufferBuilder = builder;
		textureAttachments.clear();
		framebufferHandle = depthbufferHandle = stencilbufferHandle = depthStencilPackedBufferHandle = 0;
		hasDepthStencilPackedBuffer = isMRT = false;

		build();

		// Ignore filters for depth and stencil textures, as changing them in the first place is always a wrong choice.
		getTexture().setFilter(min, mag);
	}

	@Override
	public void begin(Color clearColor) {
		begin();
		Gl.clearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);
		Gl.clearDepthf(1f);
		Gl.clear(Gl.colorBufferBit | (hasDepth ? Gl.depthBufferBit : 0) | (hasStencil ? Gl.stencilBufferBit : 0));
	}
}
