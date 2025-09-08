package heavyindustry.type;

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
}
