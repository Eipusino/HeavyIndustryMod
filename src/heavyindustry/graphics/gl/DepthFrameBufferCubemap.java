package heavyindustry.graphics.gl;

import arc.graphics.Cubemap;
import arc.graphics.Cubemap.CubemapSide;
import arc.graphics.Gl;
import arc.graphics.Pixmap.Format;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.gl.FrameBufferCubemap;
import org.jetbrains.annotations.Nullable;

/**
 * A cubemap framebuffer that requests depth (and stencil) textures instead of renderbuffers, letting users sample from
 * them. Requires at least GL ES 3.0 to function, since the spec is really dumb and decided to include depth and stencil
 * texture attachments at that version.
 *
 * @since 1.0.6
 */
public class DepthFrameBufferCubemap extends FrameBufferCubemap {
	protected Format format;
	protected boolean hasDepth, hasStencil;

	public DepthFrameBufferCubemap(int width, int height, boolean hasDepth) {
		this(Format.rgba8888, width, height, hasDepth, false);
	}

	public DepthFrameBufferCubemap(Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
		super(create(format, width, height, hasDepth, hasStencil));
		this.format = format;
		this.hasDepth = hasDepth;
		this.hasStencil = hasStencil;
	}

	public static FrameBufferCubemapBuilder create(Format format, int width, int height, boolean hasDepth, boolean hasStencil) {
		if (width < 2) width = 2;
		if (height < 2) height = 2;

		FrameBufferCubemapBuilder builder = new FrameBufferCubemapBuilder(width, height);
		builder.addBasicColorTextureAttachment(format);
		if (hasDepth) builder.addDepthTextureAttachment(Gl.depthComponent, Gl.floatV);
		if (hasStencil) builder.addStencilTextureAttachment(Gl.stencilIndex8, Gl.unsignedByte);

		return builder;
	}

	@Override
	protected Cubemap createTexture(FrameBufferTextureAttachmentSpec spec) {
		Cubemap result = super.createTexture(spec);
		if (!spec.isColorTexture()) result.setFilter(TextureFilter.nearest);

		return result;
	}

	protected void attachTexture(int attachment, Cubemap texture) {
		int glHandle = texture.getTextureObjectHandle();
		CubemapSide[] sides = CubemapSide.values();
		for (CubemapSide side : sides) Gl.framebufferTexture2D(Gl.framebuffer, attachment, side.glEnum, glHandle, 0);
	}

	public @Nullable Cubemap getDepthTexture() {
		return hasDepth ? textureAttachments.get(1) : null;
	}

	public @Nullable Cubemap getStencilTexture() {
		return hasStencil ? textureAttachments.get(hasDepth ? 2 : 1) : null;
	}

	@Override
	public void bindSide(CubemapSide side) {
		Gl.framebufferTexture2D(Gl.framebuffer, Gl.colorAttachment0, side.glEnum, getTexture().getTextureObjectHandle(), 0);

		Cubemap depthTexture = getDepthTexture(), stencilTexture = getStencilTexture();
		if (depthTexture != null)
			Gl.framebufferTexture2D(Gl.framebuffer, Gl.depthAttachment, side.glEnum, depthTexture.getTextureObjectHandle(), 0);
		if (stencilTexture != null)
			Gl.framebufferTexture2D(Gl.framebuffer, Gl.stencilAttachment, side.glEnum, stencilTexture.getTextureObjectHandle(), 0);
	}

	public void resize(int width, int height) {
		if (width < 2) width = 2;
		if (height < 2) height = 2;

		if (width == getWidth() && height == getHeight()) return;

		TextureFilter min = getTexture().getMinFilter(), mag = getTexture().getMagFilter();
		dispose();

		bufferBuilder = create(format, width, height, hasDepth, hasStencil);
		textureAttachments.clear();
		framebufferHandle = depthbufferHandle = stencilbufferHandle = depthStencilPackedBufferHandle = 0;
		hasDepthStencilPackedBuffer = isMRT = false;

		build();

		// Ignore filters for depth and stencil textures, as changing them in the first place is always a wrong choice.
		getTexture().setFilter(min, mag);
	}
}
