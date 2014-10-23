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
 * Swing component used to display an image and an optional caption.
 * Has functionality allowing it to align the caption in a multitude of
 * 	fashions and display the image with or without aspect ratio alignment /
 * 	with or without scaling.
 * 
 * @author Bridger Maskrey (maskreybe@live.com)
 * @version 1.3.0 (2014-10-23)
 */
public class JImagePane extends JComponent
{
	/**
	 * Version ID of this bean.
	 */
	private static final long	serialVersionUID	= 130L;
	
	/**
	 * Lock object used to synchronize access to certain values across threads.
	 */
	private final Object lock = new Object();
	
	/**
	 * The size of the image object.
	 */
	private Point2D imageSize;
	
	/**
	 * The image to display in the image pane.
	 */
	private BufferedImage image;
	
	/**
	 * The text caption to display on the image.
	 */
	private String text;
	
	/**
	 * The horizontal alignment of the text caption.
	 * Valid values include:
	 * 	CENTER:   Centers the text horizontally on-screen
	 * 	LEFT:	  Draws the text left-aligned on the image
	 * 	RIGHT:	  Draws the text right-aligned on the image.
	 * 	LEADING:  See LEFT.
	 * 	TRAILING: See RIGHT.
	 */
	private int horizontalAlignment;
	
	/**
	 * The vertical alignment of the text caption.
	 * Valid values include:
	 * 	CENTER:	Centers the text horizontally on-screen.
	 * 	TOP:	Draws the text at the top of the component or image.
	 * 	BOTTOM:	Draws the text at the bottom of the component or image.
	 */
	private int verticalAlignment;
	
	/**
	 * Determines whether a rectangle is drawn behind the text to 
	 * 	separate it from the image colors that it may be easier to
	 * 	read.
	 */
	private boolean textBackgroundDrawn;
	
	/**
	 * What color the text background should be drawn as (by default is
	 * 	equivalent to the actual background color).
	 */
	private Color textBackgroundColor;
	
	/**
	 * Determines whether or not the aspect ratio of the image is preserved.
	 */
	private boolean aspectRatioPreserved;
	
	/**
	 * Determines whether text is positioned relative to the bottom of the
	 * 	component or the bottom of the displayed image.  If aspect ratio is
	 * 	not preserved, these values are equivalent. However, if aspect ratio
	 * 	is preserved, this is required to 
	 */
	private boolean textRelativeToImage;
	
	private boolean imageScalingOn;
	
	/**
	 * Creates a new JImagePane with the specified image.
	 * @param image A BufferedImage to display on the image pane.
	 */
	public JImagePane(BufferedImage image)
	{
		this();
		
		this.setImage(image);
	}
	
	/**
	 * Creates a new JImagePane with no image.
	 */
	public JImagePane()
	{
		super();
		
		this.setImage(null);
		
		this.setVerticalAlignment(SwingConstants.CENTER);
		this.setHorizontalAlignment(SwingConstants.CENTER);
		
		this.setTextBackgroundColor(Color.BLACK);
		
		this.setTextBackgroundColor(this.getBackground());
		this.setAspectRatioPreserved(true);
		this.setImageScalingOn(true);
	}
	
	public synchronized BufferedImage getImage()
	{
		return this.image;
	}
	
	public String getText()
	{
		return this.text;
	}

	public int getHorizontalAlignment()
	{
		return this.horizontalAlignment;
	}

	public int getVerticalAlignment()
	{
		return this.verticalAlignment;
	}

	public boolean isTextBackgroundDrawn()
	{
		return this.textBackgroundDrawn;
	}

	public Color getTextBackgroundColor()
	{
		return this.textBackgroundColor;
	}

	public boolean isAspectRatioPreserved()
	{
		return this.aspectRatioPreserved;
	}

	public boolean isTextRelativeToImage()
	{
		return this.textRelativeToImage;
	}

	public boolean isImageScalingOn()
	{
		return this.imageScalingOn;
	}
	
	public Point2D getImageSize()
	{
		return this.imageSize;
	}

	public void setImage(BufferedImage image)
	{
		synchronized(this.lock)
		{
			this.image = image;
			
			if(this.getImage() != null)
			{
				this.setImageSize(new Point(this.getImage().getWidth(), this.getImage().getHeight()));
			} else
			{
				this.setImageSize(new Point(1, 1));
			}
		}
		
		this.repaint();
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public void setHorizontalAlignment(int horizontalAlignment)
	{
		this.horizontalAlignment = horizontalAlignment;
	}

	public void setVerticalAlignment(int verticalAlignment)
	{
		this.verticalAlignment = verticalAlignment;
	}

	public void setTextBackgroundDrawn(boolean textBackgroundDrawn)
	{
		this.textBackgroundDrawn = textBackgroundDrawn;
	}

	public void setTextBackgroundColor(Color textBackgroundColor)
	{
		this.textBackgroundColor = textBackgroundColor;
	}

	public void setAspectRatioPreserved(boolean aspectRatioPreserved)
	{
		this.aspectRatioPreserved = aspectRatioPreserved;
	}

	public void setTextRelativeToImage(boolean textRelativeToImage)
	{
		this.textRelativeToImage = textRelativeToImage;
	}

	public void setImageScalingOn(boolean imageScalingOn)
	{
		this.imageScalingOn = imageScalingOn;
	}

	private void setImageSize(Point2D imageSize)
	{
		this.imageSize = imageSize;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 * 
	 * Draws the JImagePane background, image (with / without scaling, etc)
	 */
	@Override
	public synchronized void paint(Graphics g)
	{	
		// Cast graphics down to Graphics2D.
		Graphics2D g2d = (Graphics2D)g;
		
		// Enable antialiasing.
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Draw background
		g2d.setColor(this.getBackground());
		g2d.fillRect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		
		
		
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		
		int newWidth, newHeight, diffSizeX, diffSizeY;
		
		// Begin drawing the image.
		
		if(this.isImageScalingOn())
		{
			if(this.isAspectRatioPreserved())
			{
				float xRatio = (float) (this.getWidth() / this.getImageSize().getX());
				float yRatio = (float) (this.getHeight() / this.getImageSize().getY());
				
				if(xRatio > yRatio)
				{
					newWidth = (int)(this.getImageSize().getX() * yRatio);
					newHeight = (int)(this.getImageSize().getY() * yRatio);
				}
				else
				{
					newWidth = (int)(this.getImageSize().getX() * xRatio);
					newHeight = (int)(this.getImageSize().getY() * xRatio);
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
		}
		else
		{
			newWidth = (int) this.getImageSize().getX();
			newHeight = (int) this.getImageSize().getY();
			diffSizeX = (int)((this.getWidth() - newWidth) * 0.5F);
			diffSizeY = (int)((this.getHeight() - newHeight) * 0.5F);
		}
		
		g2d.drawImage(this.getImage(), diffSizeX, diffSizeY, newWidth, newHeight, this);
		
		if(this.getBorder() != null)
		{
			this.getBorder().paintBorder(this, g, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		}
		
		if(this.getText() != null && !this.getText().isEmpty())
		{
			int textX, textY, startTextX, startTextY, xOffset, yOffset;
			
			if(this.isTextRelativeToImage())
			{
				startTextX = newWidth;
				startTextY = newHeight;
				xOffset = diffSizeX;
				yOffset = diffSizeY;
			}
			else
			{
				startTextX = this.getWidth();
				startTextY = this.getHeight();
				xOffset = 0;
				yOffset = 0;
			}
			
			switch(this.horizontalAlignment)
			{
			case SwingConstants.CENTER:
				textX = startTextX / 2 - (g2d.getFontMetrics().stringWidth(this.getText()) / 2) + xOffset;
				break;
				
			case SwingConstants.TRAILING:
			case SwingConstants.RIGHT:
				textX = startTextX - g2d.getFontMetrics().stringWidth(this.getText()) + xOffset;
				break;
				
			case SwingConstants.LEADING:
			case SwingConstants.LEFT:
			default:
				textX = xOffset;
				break;
			}
			
			switch(this.verticalAlignment)
			{
			case SwingConstants.BOTTOM:
				textY = startTextY - g2d.getFontMetrics().getHeight() - g2d.getFontMetrics().getLeading() + yOffset;
				break;
			
			case SwingConstants.CENTER:
				textY = startTextY / 2 - (g2d.getFontMetrics().getHeight() / 2) + yOffset;
				break;
			
			case SwingConstants.TOP:
			default:
				textY = g.getFontMetrics().getLeading() + yOffset;
				break;
			}
			
			if(this.textBackgroundDrawn)
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
}
