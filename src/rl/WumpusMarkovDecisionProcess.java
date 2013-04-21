package rl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.NavigableMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import dist.Distribution;

/**
 * A markov decision process representing a maze
 * @author Andrew Guillory gtg008g@mail.gatech.edu
 * @version 1.0
 */
public class WumpusMarkovDecisionProcess implements MarkovDecisionProcess {
	public static int transitionProbabilityCalls = 0;
	public static int rewardCalls = 0;
	public static int sampleStateCalls = 0;
	
	public static void resetStats() {
		transitionProbabilityCalls = 0;
		rewardCalls = 0;
		sampleStateCalls = 0;
	}
	
	public static void printStats() {
		System.out.println("Called: transition => " + transitionProbabilityCalls + ", reward => " + rewardCalls + ", sample => " + sampleStateCalls);
	}
	
    /** The failure probabilities */
    private static final double FAIL_MOVE = .1; // probability we don't move
    private static final double FAIL_TURN = .2; // probability we do about-face
    private static final double FAIL_KILL = .25; // probability we don't kill wumpus
    private static final double FAIL_GRAB = .05; // probability we drop the gold after already grabbing!
    
    /** The rewards */
	private static final int RWD_GOLD = 1000; // holding gold?
	private static final int RWD_PIT = -1000; // in a pit?
	private static final int RWD_WUMPUS = -1000; // in the live wumpus den?
	private static final int RWD_ARROW = -10; // used our arrow?
	private static final int RWD_NONTERM = -1; // nonterminal reward
	
	/** The actions */
    public static final int ACTIONS = 5;
    public static final int MOVE = 0;
    public static final int TURN_LEFT = 1;
    public static final int TURN_RIGHT = 2;
    public static final int SHOOT = 3;
    public static final int GRAB = 4;
    
    /** The directions */
    public static final int DIRECTIONS = 4;
    public static final int RIGHT = 0;
    public static final int UP = 1;
    public static final int LEFT = 2;
    public static final int DOWN = 3;
    
    /** The boolean attributes */
    private static final int SHFT_GOLD = 0x1; // have gold
	private static final int SHFT_ARROW = 0x2; // used arrow
	private static final int SHFT_WUMPUS = 0x4; // killed wumpus
   
    /** The character representing an empty square */
    public static final char EMPTY = ' ';
    /** The character representing a pit */
    public static final char PIT = 'o';
    /** The character representing the wumpus */
    public static final char WUMPUS = 'w';
    /** The character representing the gold */
    public static final char GOLD = 'x';
    /** The character representing the gold AND den! */
    public static final char GOLDEN = '@';
//    /** The character representing the agent */
//    public static final char AGENT = 'o';
    
    /**
     * The world itself
     */
    private char[][] world;
    /**
     * The initial state
     */
    private int initial;
    /**
     * The wumpus den position
     */
    private int wumpus;
    
    private Random random;
    
    public static void main(String[] args) throws Exception {
    	WumpusMarkovDecisionProcess wmdp = WumpusMarkovDecisionProcess.load("/Users/Jonathan/Development/Java/AbagailFork/wumpus.lay");
    	
    	int x = 2;
    	int y = 3;
    	int d = 1;
    	int g = 1;
    	int a = 1;
    	int w = 1;
    	int s = wmdp.stateFor(x,y,d,g,a,w);
    	
    	if (
    		wmdp.xFor(s) == x &&
    		wmdp.yFor(s) == y &&
    		wmdp.dFor(s) == d &&
    		wmdp.gFor(s) == g &&
    		wmdp.aFor(s) == a &&
    		wmdp.wFor(s) == w
    			)
    		System.out.println("That worked!!!");
    	else
    		System.out.println("DIDN'T WORK");
    	
    	System.out.println(wmdp.xFor(s));
    	System.out.println(wmdp.yFor(s));
    	System.out.println(wmdp.dFor(s));
    	System.out.println(wmdp.gFor(s));
    	System.out.println(wmdp.aFor(s));
    	System.out.println(wmdp.wFor(s));
    	
    	System.out.println(wmdp.y(wmdp.p(1, 3)));
    	
    	System.out.println(wmdp.squareAt(2,3) == PIT);
    	
    	int m = wmdp.moveBy(wmdp.moveBy(0, 3, 3), 1, -1);
    	System.out.println(wmdp.stateToString(m));
    	
    	m = wmdp.move(wmdp.turnRight(wmdp.move(0)));
    	System.out.println(wmdp.stateToString(m));
    	
    	m = wmdp.arrow(0);
    	System.out.println(wmdp.stateToString(m));
    	
    	m = wmdp.shootKill(wmdp.turnRight(0));
    	System.out.println(wmdp.stateToString(m));
    	
//    	m = wmdp.stateFor(x, y, d, g, a, w)
    }
    
    /**
     * Make a new wumpus markov decision process
     */
    public WumpusMarkovDecisionProcess(char[][] world) {
        this.world = world;
        // Start out in 0,0 facing right
        // No gold, have arrow, wumpus alive
        this.initial = stateFor(0, 0, 0, 0, 0, 0);
        
        this.wumpus = 0;
        boolean brk = false;
        for (int y = 0; y < getHeight(); y++) {
        	for (int x = 0; x < getWidth(); x++) {
        		if (world[y][x] == WUMPUS || world[y][x] == GOLDEN) {
        			this.wumpus = p(x, y);
        			brk = true;
        		}
        		if (brk) break;
        	}
        	if (brk) break;
        }
        
        this.random = new Random();
    }
    /**
     * Get the height of the world
     * @return the height
     */
    public int getHeight() {
        return world.length;
    }
    /**
     * Get the width of the world
     * @return the width
     */
    public int getWidth() {
        return world[0].length;
    }

    public int x(int p) {
    	return p % getWidth();
    }
    
    public int y(int p) {
    	return p / getWidth();
    }
    
    public int p(int x, int y) {
    	return x + getWidth()*y;
    }
    
    /**
     * Get the state for
     * @param x the x location
     * @param y the y location
     * @param d the direction
     * @param g do we have gold
     * @param a did we use arrow
     * @param w did we kill wumpus
     * @return the state number
     */
    public int stateFor(int x, int y, int d, int g, int a, int w) {
    	int state = 0;
    	state |= g*SHFT_GOLD | a*SHFT_ARROW | w*SHFT_WUMPUS; // encode booleans
    	state |= d << 3; // encode direction with two bits
    	state |= p(x, y) << 5; // encode position
    	
        return state;
    }
    /**
     * Get the x coordinate for the given state
     * @param state the state
     * @return the x coordinate
     */
    public int xFor(int state) {
        return x(state >>> 5);
    }
    /**
     * Get the y coordinate for the given state
     * @param state the state
     * @return the y coordinate
     */
    public int yFor(int state) {
    	return y(state >>> 5);
    }
    /**
     * Get the d direction for the given state
     * @param state the state
     * @return the d direction
     */
    public int dFor(int state) {
    	return (state >>> 3) & 0x3;
    }

    /**
     * Get the g gold for the given state
     * @param state the state
     * @return the g gold
     */
    public int gFor(int state) {
    	return (state & SHFT_GOLD) >>> 0;
    }
    /**
     * Get the a arrow for the given state
     * @param state the state
     * @return the a arrow
     */
    public int aFor(int state) {
    	return (state & SHFT_ARROW) >>> 1;
    }
    /**
     * Get the w wumpus for the given state
     * @param state the state
     * @return the w wumpus
     */
    public int wFor(int state) {
    	return (state & SHFT_WUMPUS) >>> 2;
    }
    
    public boolean inPit(int state) {
    	return squareAt(xFor(state), yFor(state)) == PIT;
    }
    
    public boolean inDen(int state) {
    	char s = squareAt(xFor(state), yFor(state));
    	return s == WUMPUS || s == GOLDEN;
    }
    

    public boolean inGold(int state) {
    	char s = squareAt(xFor(state), yFor(state));
    	return s == GOLD || s == GOLDEN;
    }
    
    public char squareAt(int x, int y) {
    	return world[y][x];
    }
    
    public int moveBy(int state, int dx, int dy) {
    	int x = xFor(state);
    	int y = yFor(state);
    	if (x+dx >= 0 && x+dx < getWidth()) x += dx;
    	if (y+dy >= 0 && y+dy < getHeight()) y += dy;
    	
    	return stateFor(x, y, dFor(state), gFor(state), aFor(state), wFor(state));
    }
    
    public int move(int state) {
    	switch(dFor(state)) {
    	case RIGHT:
    		return moveBy(state, 1, 0);
    	case UP:
    		return moveBy(state, 0, -1);
    	case LEFT:
    		return moveBy(state, -1, 0);
    	case DOWN:
    		return moveBy(state, 0, 1);
    	default:
    		return state;
    	}
    }
    
    public int turnLeft(int state) {
    	return stateFor(xFor(state), yFor(state), (dFor(state)+1) % 4, gFor(state), aFor(state), wFor(state));
    }
    
    public int turnRight(int state) {
    	return stateFor(xFor(state), yFor(state), (dFor(state)-1+4) % 4, gFor(state), aFor(state), wFor(state));
    }
    
    public int turnAbout(int state) {
    	return stateFor(xFor(state), yFor(state), (dFor(state)+2) % 4, gFor(state), aFor(state), wFor(state));
    }
    
    // Use up the arrow
    public int arrow(int state) {
    	return stateFor(xFor(state), yFor(state), dFor(state), gFor(state), 1, wFor(state));
    }
    
    // Kill wumpus!
    public int shootKill(int state) {
    	if (aFor(state) == 1) return state;
    	state = arrow(state);
    	int w = wFor(state);
    	int wx = x(wumpus);
    	int wy = y(wumpus);
    	int x = xFor(state);
    	int y = yFor(state);
    	switch(dFor(state)) {
    	case RIGHT: // wumpus to the right?
    		if (y == wy && x <= wx) w = 1;
    		break;
    	case UP: // wumpus above?
    		if (x == wx && y >= wy) w = 1;
    		break;
    	case LEFT: // wumpus to the left?
    		if (y == wy && x >= wx) w = 1;
    		break;
    	case DOWN: // wumpus below?
    		if (x == wx && y <= wy) w = 1;
    		break;
    	}
    	
    	return stateFor(xFor(state), yFor(state), dFor(state), gFor(state), aFor(state), w);
    }

    // Drop gold
    public int drop(int state) {
    	return stateFor(xFor(state), yFor(state), dFor(state), 0, aFor(state), wFor(state));
    }
    
    // Grab gold
    public int grab(int state) {
    	return stateFor(xFor(state), yFor(state), dFor(state), 1, aFor(state), wFor(state));
    }
    
    // All states we might reach on this action
    public Pair moveStates(int state) {
    	Pair p = new Pair();
    	int move = move(state);
    	
    	int r = 0;
    	if (inDen(move) && (wFor(move) == 0)) r = RWD_WUMPUS;
    	if (inPit(move)) r = RWD_PIT;
    	
    	p.add(state, FAIL_MOVE, RWD_NONTERM); // it might stay put
    	p.add(move, 1-FAIL_MOVE, r); // it might work
    	
    	return p;
    }

    public Pair turnLeftStates(int state) {
    	Pair p = new Pair();
    	
    	p.add(turnAbout(state), FAIL_TURN, RWD_NONTERM); // it might turn about
    	p.add(turnLeft(state), 1-FAIL_TURN, RWD_NONTERM); // it might work
    	
    	return p;
    }
    
    public Pair turnRightStates(int state) {
    	Pair p = new Pair();
    	
    	p.add(turnAbout(state), FAIL_TURN, RWD_NONTERM); // it might turn about
    	p.add(turnRight(state), 1-FAIL_TURN, RWD_NONTERM); // it might work
    	
    	return p;
    }
    
    public Pair shootStates(int state) {
    	Pair p = new Pair();
    	
    	p.add(arrow(state), FAIL_KILL, RWD_ARROW); // might not kill wumpus
    	p.add(shootKill(state), 1-FAIL_KILL, RWD_ARROW); // it might work
    	
    	return p;
    }
    
    public Pair grabStates(int state) {
    	Pair p = new Pair();
    	
    	double d = (gFor(state) == 0) ? RWD_NONTERM : -RWD_GOLD;
    	double g = (gFor(state) == 1 || !inGold(state)) ? RWD_NONTERM : RWD_GOLD;
    	
    	p.add(drop(state), FAIL_GRAB, d); // might drop gold
    	p.add(grab(state), 1-FAIL_GRAB, g); // it might work
    	
    	return p;
    }
    
    public Pair actionStates(int state, int action) {
    	Pair p;
    	
    	switch(action) {
    	case MOVE:
    		p = moveStates(state);
    		break;
    	case TURN_LEFT:
    		p = turnLeftStates(state);
    		break;
    	case TURN_RIGHT:
    		p = turnRightStates(state);
    		break;
    	case SHOOT:
    		p = shootStates(state);
    		break;
    	case GRAB:
    		p = grabStates(state);
    		break;
    	default:
    		p = new Pair();
    		System.out.println("UNRECOGNIZED!!!");
    	}
    	
    	return p;
    }

    /**
     * @see rl.MarkovDecisionProcess#getStateCount()
     */
    public int getStateCount() {
        return 8*DIRECTIONS*getWidth()*getHeight(); // for a 4x4 world, 1024
    }

    /**
     * @see rl.MarkovDecisionProcess#getActionCount()
     */
    public int getActionCount() {
        return ACTIONS;
    }

    /**
     * @see rl.MarkovDecisionProcess#reward(int, int)
     */
    public double reward(int state, int action) {
    	rewardCalls++;
        return actionStates(state, action).reward;
    }

    /**
     * @see rl.MarkovDecisionProcess#transitionProbability(int, int, int)
     */
    public double transitionProbability(int i, int j, int a) {
    	transitionProbabilityCalls++;
    	Map<Integer,Double> map = actionStates(i, a).map;
    	if (map.containsKey(j))
    		return map.get(j);
    	else return 0;
    }

    /**
     * @see rl.MarkovDecisionProcess#sampleState(int, int)
     */
    public int sampleState(int i, int a) {
    	sampleStateCalls++;
        return ((NavigableMap<Double,Integer>)actionStates(i, a).rmap)
        		.ceilingEntry(random.nextDouble()).getValue();
    }

    /**
     * @see rl.MarkovDecisionProcess#sampleInitialState()
     */
    public int sampleInitialState() {
        return initial;
    }

    /**
     * @see rl.MarkovDecisionProcess#isTerminalState(int)
     */
    public boolean isTerminalState(int state) {
        return
        		(gFor(state) == 1) ||
        		(inDen(state) && wFor(state) != 1) ||
        		(inPit(state));
    }
    
    /**
     * Load a world from a text file
     * @param fileName the file to read from
     * @throws an exception when there's an error reading
     * the file
     */
    public static WumpusMarkovDecisionProcess load(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        int height = 1;
        String line = br.readLine();
        int width = line.length();
        while((line = br.readLine()) != null) {
            height++;
        }
        br.close();
        char[][] world = new char[height][width];
        br = new BufferedReader(new FileReader(fileName));
        for (int i = 0; i < world.length; i++) {
            line = br.readLine();
            for (int j = 0; j < world[i].length; j++) {
                char c = line.charAt(j);
                if (c == PIT) {
                    world[i][j] = PIT;
                } else if (c == WUMPUS) {
                    world[i][j] = WUMPUS;
                } else if (c == GOLD) {
                    world[i][j] = GOLD;
                } else if (c == GOLDEN) {
                	world[i][j] = GOLDEN;
                } else {
                    world[i][j] = EMPTY;
                }
            }
        }
        br.close();
        return new WumpusMarkovDecisionProcess(world);
    }
    
    /**
     * Return a string representation
     * @return the string representation
     */
    public String toString(int state) {
        String ret = "";
        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
//                if (x == xFor(state) && y == yFor(state)) {
//                	switch(dFor(state)) {
//                	case RIGHT:
//                		ret += ">";
//                	case UP:
//                		ret += "^";
//                	case LEFT:
//                		ret += "<";
//                	case DOWN:
//                		ret += "v";
//                	}
//                } else {
                    ret += world[y][x];
//                }
            }
            ret += "\n";
        }
        return ret;
    }
    
    public String stateToString(int state) {
    	return "("+xFor(state)+","+yFor(state)+","+dFor(state)+") : ["+gFor(state)+aFor(state)+wFor(state)+"]";
    }
    
    public String actionToString(int action) {
    	switch(action) {
    	case MOVE:
    		return "MOVE";
    	case TURN_LEFT:
    		return "LEFT";
    	case TURN_RIGHT:
    		return "RIGHT";
    	case SHOOT:
    		return "SHOOT";
    	case GRAB:
    		return "GRAB";
    	default:
    		return "INVALID";
    	}
    }

	private class Pair {
		public Map<Integer,Double> map;
		public Map<Double,Integer> rmap;
		public double reward;
		private double cump;
		
		public Pair() {
			this.map = new TreeMap<Integer,Double>();
			this.rmap = new TreeMap<Double,Integer>();
			this.reward = 0;
			this.cump = 0;
		}
		
		public Pair(Map<Integer,Double> map, double reward) {
			this.map = map;
			this.reward = reward;
		}
		
	    public Pair add(int state, double prob, double reward) {
	    	this.reward += prob * reward;
	    	this.cump += prob;
	    	map.put(state, prob);
	    	rmap.put(cump, state);
	    	return this;
	    }
	}
}