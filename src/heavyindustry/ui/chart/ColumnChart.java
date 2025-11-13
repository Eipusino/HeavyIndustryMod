package heavyindustry.ui.chart;

import heavyindustry.ui.chart.Chart.StatGroup;

public class ColumnChart extends Chart<StatGroup> {
	public ColumnChart(int maxValuesCount){
		super(maxValuesCount);
	}

	public ColumnChart(int maxValuesCount, int defaultViewLength){
		super(maxValuesCount, defaultViewLength);
	}

	@Override
	public void draw(){
		super.draw();

	}

	@Override
	protected void drawValueScale(float valuesWidth){

	}

	@Override
	protected void drawHorizonScale(float horHeight){

	}
}
