package net.imyeyu.netdisk.ui;

import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.imyeyu.utils.gui.AnchorPaneX;
import net.imyeyu.utils.gui.BorderX;

public class ServerStateChart extends HBox {

	private String labelText;
	private double yMax = -1, width, height;

	private long i = 0;
	private XYChart.Series<String, Number> series;

	public ServerStateChart(String labelText, double width, double height) {
		this.labelText = labelText;
		this.width = width;
		this.height = height;
		init();
	}

	public ServerStateChart(String labelText, double yMax, double width, double height) {
		this.labelText = labelText;
		this.yMax = yMax;
		this.width = width;
		this.height = height;
		init();
	}

	private void init() {
		Label label = new Label(labelText);
		AnchorPane canvas = new AnchorPane();

		final CategoryAxis xAxis = new CategoryAxis();
		final NumberAxis yAxis;
		yAxis = yMax == -1 ? new NumberAxis() : new NumberAxis(0, yMax, yMax);
		xAxis.setOpacity(0);
		xAxis.setTickLabelsVisible(false);
		yAxis.setOpacity(0);
		yAxis.setTickLabelsVisible(false);
		final LineChart<String, Number> lineChart = new LineChart<String, Number>(xAxis, yAxis);

		series = new XYChart.Series<String, Number>();

		lineChart.setAnimated(false);
		lineChart.setCreateSymbols(false);
		lineChart.setLegendVisible(false);
		lineChart.setVerticalGridLinesVisible(false);
		lineChart.setHorizontalZeroLineVisible(false);
		lineChart.setAlternativeRowFillVisible(false);
		lineChart.setHorizontalGridLinesVisible(false);
		lineChart.setAlternativeColumnFillVisible(false);
		lineChart.getData().add(series);
		lineChart.setMinSize(width + 52, height + 4);
		lineChart.setPrefSize(width + 52, height + 4);

		AnchorPaneX.def(lineChart, -12, -14, -22, -32);
		canvas.getChildren().add(lineChart);

		setBorder(new BorderX("#B5B5B5", BorderX.SOLID, 1).top());
		setAlignment(Pos.CENTER_RIGHT);
		setPrefHeight(height);
		setSpacing(2);
		getChildren().addAll(label, canvas);
	}

	public void setValue(double value) {
		series.getData().add(new Data<String, Number>(String.valueOf(i), value));
		if (60 < i) series.getData().remove(0);
		i++;
	}
}