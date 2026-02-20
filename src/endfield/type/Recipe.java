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
		StringBuilder buf = new StringBuilder();
		buf.append("Recipe{").append("inputItem=");
		Arrays2.objectToString(buf, inputItem);
		buf.append(", inputLiquid=");
		Arrays2.objectToString(buf, inputLiquid);
		buf.append(", inputPayload=");
		Arrays2.objectToString(buf, inputPayload);
		buf.append(", outputItem=");
		Arrays2.objectToString(buf, outputItem);
		buf.append(", outputLiquid=");
		Arrays2.objectToString(buf, outputLiquid);
		buf.append(", outputPayload=");
		Arrays2.objectToString(buf, outputPayload);
		return buf.append(", craftTime=").append(craftTime).append('}').toString();
	}
}
