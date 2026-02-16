package endfield.type;

import endfield.util.Arrays2;
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
		Arrays2.toString(builder, inputItem);
		builder.append(", inputLiquid=");
		Arrays2.toString(builder, inputLiquid);
		builder.append(", inputPayload=");
		Arrays2.toString(builder, inputPayload);
		builder.append(", outputItem=");
		Arrays2.toString(builder, outputItem);
		builder.append(", outputLiquid=");
		Arrays2.toString(builder, outputLiquid);
		builder.append(", outputPayload=");
		Arrays2.toString(builder, outputPayload);

		return builder.append(", craftTime=").append(craftTime).append('}').toString();
	}
}
