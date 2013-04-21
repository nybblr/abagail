package rl;

import java.awt.Color;
import java.awt.Graphics;

/**
 * A class for visualizing a maze markov decision process
 * @author guillory
 * @version 1.0
 */
public class WumpusMarkovDecisionProcessVisualization {
    /** How many pixels each square in the maze should be */
    private int RESOLUTION = 20;
    
    /**
     * The maze that is being visualized
     */
    private WumpusMarkovDecisionProcess wmdp;
    
    /**
     * Make a new maze markov decision process
     * @param wmdp the maze to visualize
     */
    public WumpusMarkovDecisionProcessVisualization(WumpusMarkovDecisionProcess wmdp) {
        this.wmdp = wmdp;
    }
    
    /**
     * Get the width of the maze visualization
     * @return the width (in pixels)
     */
    public int getWidth() {
        return wmdp.getWidth() * RESOLUTION;
    }
    
    /**
     * Get the height of the maze visualization
     * @return the height (in pixels) 
     */
    public int getHeight() {
        return wmdp.getHeight() * RESOLUTION;
    }
    
    /**
     * Draw the maze onto this graphics object
     * @param g the graphics to draw on
     */
//    public void drawMaze(Graphics g) {
//        g.setColor(Color.BLACK);
//        for (int x = 0; x < getWidth(); x += RESOLUTION) {
//            for (int y = 0; y < getWidth(); y += RESOLUTION) {
//                if (wmdp.isObstacle(x, y)) {
//                    g.fillRect(x, y, x + RESOLUTION, y + RESOLUTION);
//                } else {
//                    g.drawRect(x, y, x + RESOLUTION, y + RESOLUTION);
//                }
//            }
//        }
//    }
    
    /**
     * Get a string visualization of the maze with a policy
     * @param p the policy
     * @return the string
     */
//    public String toString(Policy p) {
//        String ret = "";
//        for (int y = 0; y < wmdp.getHeight(); y++) {
//            for (int x = 0; x < wmdp.getWidth(); x++) {
//                if (wmdp.isObstacle(x, y)) {
//                    ret += WumpusMarkovDecisionProcess.OBSTACLE;
//                } else {
//                    int a = p.getAction(wmdp.stateFor(x,y));
//                    switch(wmdp.dFor(state)) {
//                    	case WumpusMarkovDecisionProcess.MOVE_DOWN:
//                    	    ret += 'v';
//                    		break;
//                    	case WumpusMarkovDecisionProcess.MOVE_UP:
//                    	    ret += '^';
//                    		break;
//                    	case WumpusMarkovDecisionProcess.MOVE_LEFT:
//                    	    ret += '<';
//                    		break;
//                    	case WumpusMarkovDecisionProcess.MOVE_RIGHT:
//                    	    ret += '>';
//                    		break;                    
//                    }
//                }
//            }
//            ret += "\n";
//        }
//        return ret;
//    }
    
    public String toString() {
        return wmdp.toString();
    }

}
