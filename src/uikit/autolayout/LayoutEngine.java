package uikit.autolayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LayoutEngine
{
	public static LayoutEngine current = new LayoutEngine();
	private LayoutEngine() { }

	public void processConstraintsIn(Constrainable view)
	{
		JComponent component;
		try
		{
			component = ((JComponent)view);
		}
		catch (Exception e)
		{
			System.out.println("The constrainable view must be a descendant of JComponent");
			e.printStackTrace();
			System.exit(0);
			return;
		}

		final Dimension componentSize = component.getSize();
		final LayoutConstraint[] allConstraints = view.allConstraints();
		Map<Component, ArrayList<LayoutConstraint>> map = new HashMap<>();

		//Build Map
		for (LayoutConstraint constraint : allConstraints)
		{
			ArrayList<LayoutConstraint> constraintsForView = map.get(constraint.viewOne);
			if (constraintsForView == null)
			{
				constraintsForView = new ArrayList<>();
			}
			constraintsForView.add(constraint);
			map.put(constraint.viewOne, constraintsForView);
		}

		//Check for any views with no constraints
		for (Component subComp : component.getComponents())
		{
			if (map.get(subComp) == null)
			{
				System.out.println(subComp.toString() + " does not have any constraints.  It will not be displayed on screen");
				component.remove(subComp);
				return;
			}
		}

		process(map, component);
	}

	private void process(Map<Component, ArrayList<LayoutConstraint>> map, JComponent parent)
	{
		while (map.size() != 0)
		{
			Component viewToConstrain = map.keySet().iterator().next();
			ArrayList<LayoutConstraint> constraints = map.get(viewToConstrain);


		}
	}
}