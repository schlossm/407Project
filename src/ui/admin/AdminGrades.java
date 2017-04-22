package ui.admin;

import json.CourseQuery;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import ui.Window;
import ui.util.Alert;
import ui.util.ButtonType;
import ui.util.UIStrings;
import uikit.DFNotificationCenter;
import uikit.DFNotificationCenterDelegate;
import uikit.UIFont;
import uikit.autolayout.LayoutAttribute;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutRelation;
import uikit.autolayout.uiobjects.ALJPanel;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("unchecked")
public class AdminGrades extends ALJPanel implements DFNotificationCenterDelegate
{
	private final JLabel ninetyPlusLabel;
	private final JLabel eightyToNinetyLabel;
	private final JLabel seventyToEightyLabel;
	private final JLabel sixtyToSeventyLabel;
	private final JLabel belowSixtyLabel;

	public AdminGrades()
	{
		setBackground(Color.white);

		ninetyPlusLabel = new JLabel("Loading grades 90+", JLabel.LEFT);
		ninetyPlusLabel.setFont(UIFont.textLight.deriveFont(18f));
		ninetyPlusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		ninetyPlusLabel.setFocusable(false);
		add(ninetyPlusLabel);

		eightyToNinetyLabel = new JLabel("Loading grades 80-90", JLabel.LEFT);
		eightyToNinetyLabel.setFont(UIFont.textLight.deriveFont(18f));
		eightyToNinetyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		eightyToNinetyLabel.setFocusable(false);
		add(eightyToNinetyLabel);

		seventyToEightyLabel = new JLabel("Loading grades 70-80", JLabel.LEFT);
		seventyToEightyLabel.setFont(UIFont.textLight.deriveFont(18f));
		seventyToEightyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		seventyToEightyLabel.setFocusable(false);
		add(seventyToEightyLabel);

		sixtyToSeventyLabel = new JLabel("Loading grades 60-70", JLabel.LEFT);
		sixtyToSeventyLabel.setFont(UIFont.textLight.deriveFont(18f));
		sixtyToSeventyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		sixtyToSeventyLabel.setFocusable(false);
		add(sixtyToSeventyLabel);

		belowSixtyLabel = new JLabel("Loading grades 0-60", JLabel.LEFT);
		belowSixtyLabel.setFont(UIFont.textLight.deriveFont(18f));
		belowSixtyLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		belowSixtyLabel.setFocusable(false);
		add(belowSixtyLabel);

		addConstraint(new LayoutConstraint(ninetyPlusLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(ninetyPlusLabel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 20));

		addConstraint(new LayoutConstraint(eightyToNinetyLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(eightyToNinetyLabel, LayoutAttribute.top, LayoutRelation.equal, ninetyPlusLabel, LayoutAttribute.bottom, 1.0, 40));

		addConstraint(new LayoutConstraint(seventyToEightyLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(seventyToEightyLabel, LayoutAttribute.top, LayoutRelation.equal, eightyToNinetyLabel, LayoutAttribute.bottom, 1.0, 40));

		addConstraint(new LayoutConstraint(sixtyToSeventyLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(sixtyToSeventyLabel, LayoutAttribute.top, LayoutRelation.equal, seventyToEightyLabel, LayoutAttribute.bottom, 1.0, 40));

		addConstraint(new LayoutConstraint(belowSixtyLabel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.leading, 1.0, 20));
		addConstraint(new LayoutConstraint(belowSixtyLabel, LayoutAttribute.top, LayoutRelation.equal, sixtyToSeventyLabel, LayoutAttribute.bottom, 1.0, 40));

		DFNotificationCenter.defaultCenter.register(this, UIStrings.oneHourHasPassedNotification);

		loadGrades();

		layoutSubviews();
		layoutSubviews();
	}

	private void loadGrades()
	{
		//FIXME: Fix me

		final int[] num90Plus = {0};
		final int[] num80To90 = {0};
		final int[] num70To80 = {0};
		final int[] num60To70 = {0};
		final int[] below60 = {0};

		new CourseQuery().getAllGrades((returnedData, error) ->
		                               {
			                               if (error != null)
			                               {
				                               Alert errorAlert = new Alert("Error", "ABC could not load the grade counts.  Please try again.");
				                               errorAlert.addButton("OK", ButtonType.defaultType, null, false);
				                               errorAlert.show(Window.current.mainScreen);
				                               return;
			                               }
			                               if (returnedData instanceof ArrayList)
			                               {
				                               ArrayList<Integer> groups = (ArrayList<Integer>)returnedData;

				                               num90Plus[0] = groups.get(0);
				                               num80To90[0] = groups.get(1);
				                               num70To80[0] = groups.get(2);
				                               num60To70[0] = groups.get(3);
				                               below60[0] = groups.get(4);

				                               ninetyPlusLabel.setText(num90Plus[0] + " grades 90+");
				                               eightyToNinetyLabel.setText(num80To90[0] + " grades 80-89");
				                               seventyToEightyLabel.setText(num70To80[0] + " grades 70-79");
				                               sixtyToSeventyLabel.setText(num60To70[0] + " grades 60-69");
				                               belowSixtyLabel.setText(below60[0] + " grades <60");

				                               DefaultPieDataset dataset = new DefaultPieDataset();
				                               dataset.setValue("Grades 90+", num90Plus[0]);
				                               dataset.setValue("Grades 80-90", num80To90[0]);
				                               dataset.setValue("Grades 70-80", num70To80[0]);
				                               dataset.setValue("Grades 60-70", num60To70[0]);
				                               dataset.setValue("Grades <60", below60[0]);

				                               buildPieChart(dataset);
			                               }
			                               else
			                               {
				                               Alert errorAlert = new Alert("Error", "ABC could not load the grade counts.  Please try again.");
				                               errorAlert.addButton("OK", ButtonType.defaultType, null, false);
				                               errorAlert.show(Window.current.mainScreen);
			                               }
		                               });
	}

	private void buildPieChart(PieDataset dataset)
	{
		JFreeChart chart = ChartFactory.createPieChart("Grade Distribution", dataset, true, false, false);

		PiePlot plot = (PiePlot)chart.getPlot();
		PieSectionLabelGenerator gen = new StandardPieSectionLabelGenerator("{0}: {1} ({2})", new DecimalFormat("0"), new DecimalFormat("0%"));
		plot.setLabelGenerator(gen);

		ChartPanel panel = new ChartPanel(chart);
		add(panel);
		addConstraint(new LayoutConstraint(panel, LayoutAttribute.leading, LayoutRelation.equal, this, LayoutAttribute.centerX, 1.0, 0));
		addConstraint(new LayoutConstraint(panel, LayoutAttribute.top, LayoutRelation.equal, this, LayoutAttribute.top, 1.0, 8));
		addConstraint(new LayoutConstraint(panel, LayoutAttribute.bottom, LayoutRelation.equal, this, LayoutAttribute.bottom, 1.0, -8));
		addConstraint(new LayoutConstraint(panel, LayoutAttribute.trailing, LayoutRelation.equal, this, LayoutAttribute.trailing, 1.0, -8));
	}

	@Override
	public void performActionFor(String notificationName, Object userData)
	{
		if (Objects.equals(notificationName, UIStrings.oneHourHasPassedNotification))
		{
			loadGrades();
		}
	}
}
