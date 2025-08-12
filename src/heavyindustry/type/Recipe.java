package heavyindustry.type;

import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;

public class Recipe {
	public static final Recipe empty = new Recipe();

	public Seq<ItemStack> inputItem = new Seq<>(ItemStack.class);
	public Seq<LiquidStack> inputLiquid = new Seq<>(LiquidStack.class);
	public Seq<PayloadStack> inputPayload = new Seq<>(PayloadStack.class);

	public Seq<ItemStack> outputItem = new Seq<>(ItemStack.class);
	public Seq<LiquidStack> outputLiquid = new Seq<>(LiquidStack.class);
	public Seq<PayloadStack> outputPayload = new Seq<>(PayloadStack.class);

	public float craftTime = 60f;

	public Recipe(Object... objects) {
		for (int i = 0; i < objects.length / 2; i++) {
			if (objects[i * 2] instanceof Item item && objects[i * 2 + 1] instanceof Integer count) {
				inputItem.add(new ItemStack(item, count));
			} else if (objects[i * 2] instanceof Liquid liquid && objects[i * 2 + 1] instanceof Float count) {
				inputLiquid.add(new LiquidStack(liquid, count));
			} else if (objects[i * 2] instanceof UnlockableContent payload && objects[i * 2 + 1] instanceof Integer count) {
				inputPayload.add(new PayloadStack(payload, count));
			}
		}
	}
}
