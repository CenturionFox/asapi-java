package com.attributestudios.api.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.SwingConstants;

/**
 * @author Bridger Maskrey (maskreybe@live.com)
 * @version 2014-10-23
 */
public class JImagePane extends JComponent
{
	private static final long	serialVersionUID	= 5437939503106455141L;
	private final Object lock = new Object();
	
	private Point2D imageSize;
	
	protected BufferedImage image;
	private String	text;
	
	private int horizontalAlignment;
	private int verticalAlignment;
	
	private boolean textBackground;
	private Color textBackgroundColor;
	
	private boolean preserveAspectRatio;
	
	public JImagePane(BufferedImage image)
	{
		super();
		this.setImage(image);
		
		this.setVerticalAlignment(SwingConstants.CENTER);
		this.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.setTextBackgroundColor(Color.BLACK);
		
		this.setPreserveAspectRatio(true);
		
		this.setTextBackgroundColor(this.getBackground());
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
	
	public void setTextBackground(boolean textBackground)
	{
		this.textBackground = textBackground;
	}
	
	public boolean getTextBackground()
	{
		return this.textBackground;
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
		
		int newWidth, newHeight, diffSizeX, diffSizeY;
		
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		if(this.preserveAspectRatio)
		{
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
			
			diffSizeX = (int)((this.getWidth() - newWidth) * 0.5F);
			diffSizeY = (int)((this.getHeight() - newHeight) * 0.5F);
		}
		else
		{
			newWidth = this.getWidth();
			newHeight = this.getHeight();
			
			diffSizeX = diffSizeY = 0;
		}
		
		g2d.drawImage(this.image, diffSizeX, diffSizeY, newWidth, newHeight, this);
		
		if(this.getBorder() != null)
		{
			this.getBorder().paintBorder(this, g, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		}
		
		if(this.getText() != null && !this.getText().isEmpty())
		{
			int textX;
			int textY;
			
			switch(this.horizontalAlignment)
			{
			case SwingConstants.CENTER:
				textX = newWidth / 2 - (g2d.getFontMetrics().stringWidth(this.getText()) / 2) + diffSizeX;
				break;
				
			case SwingConstants.TRAILING:
			case SwingConstants.RIGHT:
				textX = newWidth - g2d.getFontMetrics().stringWidth(this.getText()) + diffSizeX;
				break;
				
			case SwingConstants.LEADING:
			case SwingConstants.LEFT:
			default:
				textX = diffSizeX;
				break;
			}
			
			switch(this.verticalAlignment)
			{
			case SwingConstants.BOTTOM:
				textY = newHeight - g2d.getFontMetrics().getHeight() - g2d.getFontMetrics().getLeading() + diffSizeY;
				break;
			
			case SwingConstants.CENTER:
				textY = newHeight / 2 - (g2d.getFontMetrics().getHeight() / 2) + diffSizeY;
				break;
			
			case SwingConstants.TOP:
			default:
				textY = g.getFontMetrics().getLeading() + diffSizeY;
				break;
			}
			
			if(this.textBackground)
			{
				g2d.setColor(this.textBackgroundColor);
				
				g2d.fillRect(textX - (g2d.getFontMetrics().getMaxAdvance() / 4),
							 textY - g2d.getFontMetrics().getHeight() + g2d.getFontMetrics().getMaxDescent(), 
							 g2d.getFontMetrics().stringWidth(this.getText()) + (g2d.getFontMetrics().getMaxAdvance() / 2),
							 g2d.getFontMetrics().getHeight() + g2d.getFontMetrics().getLeading());
			}
			
			g2d.setColor(this.getForeground());
			
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

	public Color getTextBackgroundColor()
	{
		return this.textBackgroundColor;
	}

	public void setTextBackgroundColor(Color textBackgroundColor)
	{
		this.textBackgroundColor = textBackgroundColor;
	}

	public boolean getPreserveAspectRatio()
	{
		return this.preserveAspectRatio;
	}

	public void setPreserveAspectRatio(boolean preserveAspectRatio)
	{
		this.preserveAspectRatio = preserveAspectRatio;
	}
}
