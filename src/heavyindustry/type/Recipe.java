package heavyindustry.type;

import heavyindustry.util.Arrays2;
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
		StringBuilder builder = new StringBuilder();
		builder.append("Recipe{").append("inputItem=");
		Arrays2.append(builder, inputItem);
		builder.append(", inputLiquid=");
		Arrays2.append(builder, inputLiquid);
		builder.append(", inputPayload=");
		Arrays2.append(builder, inputPayload);
		builder.append(", outputItem=");
		Arrays2.append(builder, outputItem);
		builder.append(", outputLiquid=");
		Arrays2.append(builder, outputLiquid);
		builder.append(", outputPayload=");
		Arrays2.append(builder, outputPayload);

		return builder.append(", craftTime=").append(craftTime).append('}').toString();
	}
}
