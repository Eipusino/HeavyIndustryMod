package heavyindustry.type;

import heavyindustry.util.ArrayUtils;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;

public class Recipe {
	public static final Recipe empty = new Recipe();

	public ItemStack[] inputItem = {};
	public LiquidStack[] inputLiquid = {};
	public PayloadStack[] inputPayload = {};

	public ItemStack[] outputItem = {};
	public LiquidStack[] outputLiquid = {};
	public PayloadStack[] outputPayload = {};

	public float craftTime = 60f;

	@Override
	public String toString() {
		if (this == empty) return "EmptyRecipe";

		StringBuilder builder = new StringBuilder();
		builder.append("Recipe{").append("inputItem=");
		ArrayUtils.append(builder, inputItem);
		builder.append(", inputLiquid=");
		ArrayUtils.append(builder, inputLiquid);
		builder.append(", inputPayload=");
		ArrayUtils.append(builder, inputPayload);
		builder.append(", outputItem=");
		ArrayUtils.append(builder, outputItem);
		builder.append(", outputLiquid=");
		ArrayUtils.append(builder, outputLiquid);
		builder.append(", outputPayload=");
		ArrayUtils.append(builder, outputPayload);

		return builder.append(", craftTime=").append(craftTime).append('}').toString();
	}
}
