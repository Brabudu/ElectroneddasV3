package panels;
// Imports for the GUI classes.
import java.awt.*;

import javax.imageio.*;
import javax.swing.*;

import main.Electroneddas;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * JKnob.java - 
 *   A knob component.  The knob can be rotated by dragging 
 *   a spot on the knob around in a circle or by clicking it by side.
 *   The knob will report its position in radians when asked.
 *
 * @author Grant William Braught
 * @author Dickinson College
 * @author Franciscu Capuzzi
 * @version 1/7/2020
 * 
 *  */

public class JCKnob extends JPanel {
		JKnob jk;
	
	public JCKnob(int startpos, int start,int end, Color color, String name,String tip,int id) {
		this( startpos,  start, end,  color,  name, ' ', ' ', tip, id,true) ;
	}
	public JCKnob(int startpos, int start,int end, Color color, String name,String tip,int id,boolean big) {
		this( startpos,  start, end,  color,  name, ' ', ' ', tip, id,big) ;
	}
	
	
	public JCKnob(int startpos, int start,int end, Color color, String name, char h, char l, String tip,int id,boolean big) {
		super();
		this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		JLabel jl=new JLabel(name);
		jl.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(jl);
		jk=new JKnob(startpos,start,end,color,id,big,h,l);
		jk.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(jk);
		this.setToolTipText(tip);	
		this.setMaximumSize(new Dimension(50, 150));
		
	}
	public JKnob getKnob() {
		return jk;
	}
	public void setEnabled(boolean en) {
		this.jk.setEnabled(en);
	}
	
	public void setValue(int pos) {
		jk.setValue(pos);
		
	}
	public void trigger() {
		jk.trigger();
	}
	
}
class JKnob 
    extends JComponent
    implements MouseListener, MouseMotionListener, Adjustable {

    private int k_start;
    private int k_end;
    private double theta;
    private Color knobColor;
    private Color spotColor;
    private Color borderColor;
    private boolean enabled=true;
    
    int radius = 25;
    int rradius = 20;
    int spotRadius = 5;
    	
    private char h;
    private char l;
    
    private double fc=2.4;

    private boolean pressedOnSpot;
    
    private BufferedImage knim;
    
    private AdjustmentListener adjl;
    
    private int id=0;

    /**
     * No-Arg constructor that initializes the position
     * of the knob to 0 radians (Up).
     */
    public JKnob() {
	this(0,0,10,Color.GRAY,0,true,' ',' ');
    }

    public JKnob(int startpos,int start,int end,Color col,int ident, boolean big, char h, char l) {
	this(startpos,start,end, Color.gray, Color.black,col,ident,big,h,l);
    }
    
   
    public JKnob(int start,int end,Color col,int ident) {
	this(start,start,end, Color.gray, Color.black,col,ident,true,' ',' ');
    }
    
    /**
     * Constructor that initializes the position of the
     * knob to the specified position and also allows the
     * colors of the knob and spot to be specified.
     *
     * @param initAngle the initial angle of the knob.
     * @param initColor the color of the knob.
     * @param initSpotColor the color of the spot.
     */
    public JKnob(int startpos, int start,int end, Color initKnobColor, 
		 Color initSpotColor, Color initBorderColor,int ident,boolean big, char l, char h) {

	this.h=h;
	this.l=l;
	
	pressedOnSpot = false;
	knobColor = initKnobColor;
	spotColor = initSpotColor;
	borderColor= initBorderColor;
	
	
	
	if (!big) {
		radius/=1.5;
		rradius/=1.5;
		spotRadius/=1.5;
		
	}

	setMinimum(start);
	setMaximum(end);
	
	setValue(startpos);
	id=ident;
	/*
	try {
		if (big) knim = ImageIO.read(JCKnob.class.getResourceAsStream("./../Img/knobB.png"));
		else knim = ImageIO.read(JCKnob.class.getResourceAsStream("./../Img/knobS.png"));
	} catch (IOException e) {
		//  Auto-generated catch block
		System.err.println("No image");
	}
	*/
	this.addMouseListener(this);	
	this.addMouseMotionListener(this);
    }
    public void trigger() {
    	this.mouseReleased(null);
    }

    /**
     * Paint the JKnob on the graphics context given.  The knob
     * is a filled circle with a small filled circle offset 
     * within it to show the current angular position of the 
     * knob.
     *
     * @param g The graphics context on which to paint the knob.
     */
    public void paint(Graphics g) {

    
	// Draw the knob.
    
    g.setColor(knobColor);
    g.fillOval(4,4,2*radius-8,2*radius-8);
	
	//g.setColor(Color.BLACK);
	//g.fillOval(radius/2,radius/2,radius,radius);
	
	if (this.enabled) {
    	g.setColor(borderColor);
    	//g.fillOval(4,4,2*radius-radius/10-8,2*radius-radius/10-8);
    	g.fillOval(radius/3,radius/3,(int)(radius*1.37),(int)(radius*1.37));
    } 
	

	// Find the center of the spot.
	Point pt = getSpotCenter();
	int xc = (int)pt.getX();
	int yc = (int)pt.getY();

	if (this.enabled) {
		// Draw the spot.
		g.setColor(spotColor);
		g.fillOval(xc-spotRadius, yc-spotRadius,
			   2*spotRadius, 2*spotRadius);
		g.drawLine(xc, yc, radius, radius);
		g.setColor(Color.WHITE);
		g.fillOval(xc-spotRadius/2, yc-spotRadius/2,
			   spotRadius, spotRadius);
	}
	//g.drawLine(xc, yc, radius, radius);
	
	g.setColor(Color.LIGHT_GRAY);
	
	String s=""+getValue();
	g.setFont(new Font("Arial", Font.BOLD, 10)); 
	g.drawString(s,radius-3*s.length(),2*radius+7);
	//g.drawImage(knim, 0, 0, null);
	g.setFont(new Font("Arial", Font.PLAIN, 6)); 
	g.drawString(""+l,5,2*radius);
	g.drawString(""+h,radius*2-10,2*radius);
	//g.drawImage(knim, 0, 0, null);
	
    }

    /**
     * Return the ideal size that the knob would like to be.
     *
     * @return the preferred size of the JKnob.
     */
    public Dimension getPreferredSize() {
	return new Dimension(2*radius,2*radius+8);
    }

    /**
     * Return the minimum size that the knob would like to be.
     * This is the same size as the preferred size so the
     * knob will be of a fixed size.
     *
     * @return the minimum size of the JKnob.
     */
    public Dimension getMinimumSize() {
	return new Dimension(2*radius,2*radius+8);
    }

    /**
     * Get the current anglular position of the knob.
     *
     * @return the current anglular position of the knob.
     */
    public double getAngle() {
	return theta;
    }
    public int getValue() {
    
   
    double span=(theta+fc)*(k_end-k_start)/(2*fc);
    span=Math.rint(span);
    return (int)span+k_start; 
    }
    public void setValue(int val) {
    	
    	if (val<this.getMinimum()) return;	//FC
    	if (val>this.getMaximum()) return;	//FC
        double pos=(double)(val-k_start)/(k_end-k_start);
        
        theta=pos*2*fc-fc;
        repaint();
   
    }


    /** 
     * Calculate the x, y coordinates of the center of the spot.
     *
     * @return a Point containing the x,y position of the center
     *         of the spot.
     */ 
    private Point getSpotCenter() {

	// Calculate the center point of the spot RELATIVE to the
	// center of the of the circle.

	int r = rradius - spotRadius;

	int xcp = (int)(r * Math.sin(theta));
	int ycp = (int)(r * Math.cos(theta));

	// Adjust the center point of the spot so that it is offset
	// from the center of the circle.  This is necessary becasue
	// 0,0 is not actually the center of the circle, it is  the 
        // upper left corner of the component!
	int xc = radius + xcp;
	int yc = radius - ycp;

	// Create a new Point to return since we can't  
	// return 2 values!
	return new Point(xc,yc);
    }

    /**
     * Determine if the mouse click was on the spot or
     * not.  If it was return true, otherwise return 
     * false.
     *
     * @return true if x,y is on the spot and false if not.
     */
    private boolean isOnSpot(Point pt) {
	return (pt.distance(getSpotCenter()) < spotRadius);
    }

    // Methods from the MouseListener interface.

    /**
     * Empy method because nothing happens on a click.
     *
     * @param e reference to a MouseEvent object describing 
     *          the mouse click.
     */
    public void mouseClicked(MouseEvent e) {
    	
    }

    /**
     * Empty method because nothing happens when the mouse
     * enters the Knob.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse entry.
     */
    public void mouseEntered(MouseEvent e) {}

    /**
     * Empty method because nothing happens when the mouse
     * exits the knob.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse exit.
     */
    public void mouseExited(MouseEvent e) {}

    /**
     * When the mouse button is pressed, the dragging of the
     * spot will be enabled if the button was pressed over
     * the spot.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse press.
     */
    public void mousePressed(MouseEvent e) {
    	if (this.enabled) {
			Point mouseLoc = e.getPoint();
			pressedOnSpot = isOnSpot(mouseLoc);
			if (!pressedOnSpot) {			//FCapuzzi
				if (mouseLoc.x<=this.radius) {
					this.setValue(this.getValue()-1);
				} else {
					this.setValue(this.getValue()+1);
				}
			}
    	}
    }

    /**
     * When the button is released, the dragging of the spot
     * is disabled.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse release.
     */
    public void mouseReleased(MouseEvent e) {
	pressedOnSpot = false;
    adjl.adjustmentValueChanged(new AdjustmentEvent(this,id,AdjustmentEvent.TRACK,this.getValue()));
    }
    
    // Methods from the MouseMotionListener interface.

    /**
     * Empty method because nothing happens when the mouse
     * is moved if it is not being dragged.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse move.
     */
    public void mouseMoved(MouseEvent e) {}

    /**
     * Compute the new angle for the spot and repaint the 
     * knob.  The new angle is computed based on the new
     * mouse position.
     *
     * @param e reference to a MouseEvent object describing
     *          the mouse drag.
     */
    public void mouseDragged(MouseEvent e) {
	if (pressedOnSpot) {

	    int mx = e.getX();
	    int my = e.getY();

	    // Compute the x, y position of the mouse RELATIVE
	    // to the center of the knob.
	    int mxp = mx - radius;
	    int myp = radius - my;

	    // Compute the new angle of the knob from the
	    // new x and y position of the mouse.  
	    // Math.atan2(...) computes the angle at which
	    // x,y lies from the positive y axis with cw rotations
	    // being positive and ccw being negative.
	    theta = Math.atan2(mxp, myp);
	    
	    if (theta>fc) {theta=fc;};
	    if (theta<-fc) {theta=-fc;};
	    repaint();
	}
    }
    @Override
	public void setEnabled(boolean en) {
		this.enabled=en;
		this.repaint();
	}
    


    /**
     * Here main is used simply as a test method.  If this file
     * is executed "java JKnob" then this main() method will be
     * run.  However, if another file uses a JKnob as a component
     * and that file is run then this main is ignored.
     */
 


	@Override
	public void addAdjustmentListener(AdjustmentListener arg0) {
		// TODO Auto-generated method stub
		adjl=arg0;
	}

	@Override
	public int getBlockIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaximum() {
		
		return k_end;
	}

	@Override
	public int getMinimum() {
		
		return k_start;
	}

	@Override
	public int getOrientation() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getUnitIncrement() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getVisibleAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeAdjustmentListener(AdjustmentListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBlockIncrement(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMaximum(int arg0) {
	
		k_end=arg0;
	}

	@Override
	public void setMinimum(int arg0) {
		
		k_start=arg0;
	}

	@Override
	public void setUnitIncrement(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVisibleAmount(int arg0) {
		// TODO Auto-generated method stub
		
	}
}
