package ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

interface MLMDelegate
{
	public void mousePoint(MouseEvent action, MLMEventType eventType);
}

/**
 * Created by michaelschloss on 1/18/17.
 */
public class MouseListenerManager implements MouseListener
{
	private MLMDelegate delegate;

	public MouseListenerManager(MLMDelegate delegate)
	{
		this.delegate = delegate;
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
		delegate.mousePoint(e, MLMEventType.clicked);
	}

	@Override
	public void mousePressed(MouseEvent e)
	{
		delegate.mousePoint(e, MLMEventType.pressed);
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
		delegate.mousePoint(e, MLMEventType.released);
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
		delegate.mousePoint(e, MLMEventType.draggedIn);
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
		delegate.mousePoint(e, MLMEventType.draggedOut);
	}
}
