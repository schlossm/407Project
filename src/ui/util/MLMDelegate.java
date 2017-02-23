package ui.util;

import ui.util.MLMEventType;

import java.awt.event.MouseEvent;

public interface MLMDelegate
{
	void mousePoint(MouseEvent action, MLMEventType eventType);
}
