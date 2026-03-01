package endfield.util;

public class WeightedOption {
	public float weight;
	public Runnable option;

	public WeightedOption() {
		weight = 0;
		option = Constant.RUNNABLE_NOTHING;
	}

	public WeightedOption(float weight, Runnable option) {
		this.weight = weight;
		this.option = option;
	}

	public WeightedOption set(float weight, Runnable option) {
		this.weight = weight;
		this.option = option;

		return this;
	}

	public void setWeight(float weight) {
		if (weight < 0) weight = 0;
		this.weight = weight;
	}
}
