package com.attributestudios.api.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

public class JImagePane extends JComponent
{
	private static final long	serialVersionUID	= 5437939503106455141L;
	private final Object lock = new Object();
	
	private Point2D imageSize;
	
	protected BufferedImage image;
	private String	text;
	
	private int horizontalAlignment;
	private int verticalAlignment;
	
	public JImagePane(BufferedImage image)
	{
		super();
		this.setImage(image);
		
		this.verticalAlignment = SwingConstants.BOTTOM;
		this.horizontalAlignment = SwingConstants.CENTER;
	}
	
	public void setImage(BufferedImage image)
	{
		synchronized(this.lock)
		{
			this.image = image;
			
			if(this.image != null)
			{
				this.imageSize = new Point(this.image.getWidth(), this.image.getHeight());
			} else
			{
				this.imageSize = new Point(1, 1);
			}
		}
		
		this.repaint();
	}
	
	public BufferedImage getImage()
	{
		return this.image;
	}
	
	public void setVerticalAlignment(int alignment)
	{
		this.verticalAlignment = alignment;
	}
	
	public int getVerticalAlignment()
	{
		return this.verticalAlignment;
	}
	
	public void setHorizontalAlignment(int alignment)
	{
		this.horizontalAlignment = alignment;
	}
	
	public int getHorizontalAlignment()
	{
		return this.horizontalAlignment;
	}
	
	@Override
	public synchronized void paint(Graphics g)
	{		
		Graphics2D g2d = (Graphics2D)g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setColor(this.getBackground());
		g2d.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		
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
		
		g2d.drawImage(this.image, diffSizeX, diffSizeY, newWidth, newHeight, this);
		
		if(this.getBorder() != null)
		{
			this.getBorder().paintBorder(this, g, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		}
		
		if(this.getText() != null)
		{
			g2d.setColor(this.getForeground());
			
			int textX;
			int textY;
			
			switch(this.horizontalAlignment)
			{
			case SwingConstants.CENTER:
				textX = this.getWidth() / 2 - (g.getFontMetrics().stringWidth(this.getText()) / 2);
				break;
				
			case SwingConstants.TRAILING:
			case SwingConstants.RIGHT:
				textX = this.getWidth() - g.getFontMetrics().stringWidth(this.getText());
				break;
				
			case SwingConstants.LEADING:
			case SwingConstants.LEFT:
			default:
				textX = 0;
				break;
			}
			
			switch(this.verticalAlignment)
			{
			case SwingConstants.BOTTOM:
				textY = this.getHeight() - g.getFontMetrics().getHeight() - g.getFontMetrics().getLeading();
				break;
			
			case SwingConstants.CENTER:
				textY = this.getHeight() / 2 - (g.getFontMetrics().getHeight() / 2);
				break;
			
			case SwingConstants.TOP:
			default:
				textY = g.getFontMetrics().getLeading();
				break;
			}
			
			g2d.drawString(this.getText(), textX, textY);
			
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
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
