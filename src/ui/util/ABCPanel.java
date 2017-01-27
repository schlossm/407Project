package ui.util;

import uikit.autolayout.Constrainable;
import uikit.autolayout.LayoutConstraint;
import uikit.autolayout.LayoutEngine;

import javax.swing.*;
import java.util.ArrayList;

public class ABCPanel extends JPanel implements Constrainable
{
	ArrayList<LayoutConstraint> _constraints;

	public int panelCompressionWidth = 750;
	public int panelCompressionHeight = 750;

	@Override
	public void addConstraint(LayoutConstraint constraint)
	{
		_constraints.add(constraint);
	}

	@Override
	public LayoutConstraint[] allConstraints()
	{
		return (LayoutConstraint[])_constraints.toArray();
	}

	@Override
	public void layoutSubviews()
	{
		LayoutEngine.current.processConstraintsIn(this);
	}

	@Override
	public int compressionResistanceWidth()
	{
		return panelCompressionWidth;
	}

	@Override
	public int compressionResistanceHeight()
	{
		return panelCompressionHeight;
	}
}
