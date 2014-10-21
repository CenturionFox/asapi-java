package com.attributestudios.api.swing;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class JImagePane extends JComponent
{
	private static final long	serialVersionUID	= 5437939503106455141L;
	private final Object lock = new Object();
	
	private Point2D imageSize;
	
	protected BufferedImage theImage;
	private String	text;
	
	public JImagePane(BufferedImage image)
	{
		super();
		this.setImage(image);
	}
	
	public void setImage(BufferedImage image)
	{
		synchronized(this.lock)
		{
			this.theImage = image;
			
			if(this.theImage != null)
			{
				this.imageSize = new Point(this.theImage.getWidth(), this.theImage.getHeight());
			} else
			{
				this.imageSize = new Point(1, 1);
			}
		}
		
		this.repaint();
	}
	
	@Override
	public synchronized void paint(Graphics g)
	{		
		g.setColor(this.getBackground());
		g.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		
		float xRatio = (float) (this.getWidth() / this.imageSize.getX());
		float yRatio = (float) (this.getHeight() / this.imageSize.getY());
		
		int newWidth = 0, newHeight = 0;
		
		if(xRatio > yRatio)
		{
			newWidth = (int)(this.imageSize.getX() * yRatio);
			newHeight = (int)(this.imageSize.getY() * yRatio);
		}
		else
		{
			newWidth = (int)(this.imageSize.getX() * xRatio);
			newHeight = (int)(this.imageSize.getY() * xRatio);
		}
		
		int diffSizeX = (int)((this.getWidth() - newWidth) * 0.5F);
		int diffSizeY = (int)((this.getHeight() - newHeight) * 0.5F);
		
		g.drawImage(this.theImage, diffSizeX, diffSizeY, newWidth, newHeight, this);
		
		if(this.getBorder() != null)
		{
			this.getBorder().paintBorder(this, g, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		}
		
		if(this.getText() != null)
		{
			g.setColor(this.getForeground());
			g.drawString(this.getText(), this.getWidth() / 2 - (g.getFontMetrics().stringWidth(this.getText()) / 2), this.getHeight() / 2 - (g.getFontMetrics().getHeight() / 2));
		}
	}
	
	public String getText()
	{
		return this.text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
}
