package rl.test;

import rl.EpsilonGreedyStrategy;
import rl.MazeMarkovDecisionProcess;
import rl.MazeMarkovDecisionProcessVisualization;
import rl.Policy;
import rl.PolicyIteration;
import rl.QLambda;
import rl.SarsaLambda;
import rl.ValueIteration;
import rl.WumpusMarkovDecisionProcess;
import shared.FixedIterationTrainer;
import shared.ThresholdTrainer;

/**
 * Tests out the maze markov decision process classes
 * @author guillory
 * @version 1.0
 */
public class WumpusMDPTest {
    /**
     * Tests out things
     * @param args ignored
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        WumpusMarkovDecisionProcess maze = WumpusMarkovDecisionProcess.load("deathThroes.lay");
        System.out.println(maze.toString(0));
        
        ValueIteration vi = new ValueIteration(.95, maze);
        ThresholdTrainer tt = new ThresholdTrainer(vi);
        maze.resetStats();
        long startTime = System.currentTimeMillis();
        tt.train();
        Policy p = vi.getPolicy();
        long finishTime = System.currentTimeMillis();
        System.out.println("Value iteration learned : " + p);
        System.out.println("in " + tt.getIterations() + " iterations");
        System.out.println("and " + (finishTime - startTime) + " ms");
        maze.printStats();
//        MazeMarkovDecisionProcessVisualization mazeVis =
//            new MazeMarkovDecisionProcessVisualization(maze);
//        System.out.println(mazeVis.toString(p));
        
        int curr = maze.sampleInitialState();
        double reward = 0;
        while (!maze.isTerminalState(curr)) {
        	int action = p.getAction(curr);
        	reward += maze.reward(curr, action);
        	curr = maze.sampleState(curr, action);
        	System.out.println(maze.actionToString(action)+" => "+maze.stateToString(curr));
        }
        
        System.out.println("I got a reward of "+reward);

        PolicyIteration pi = new PolicyIteration(.95, maze);
        tt = new ThresholdTrainer(pi);
        maze.resetStats();
        startTime = System.currentTimeMillis();
        tt.train();
        p = pi.getPolicy();
        finishTime = System.currentTimeMillis();
        System.out.println("Policy iteration learned : " + p);
        System.out.println("in " + tt.getIterations() + " iterations");
        System.out.println("and " + (finishTime - startTime) + " ms");
        maze.printStats();
//        System.out.println(mazeVis.toString(p));
        
        curr = maze.sampleInitialState();
        reward = 0;
        while (!maze.isTerminalState(curr)) {
        	int action = p.getAction(curr);
        	reward += maze.reward(curr, action);
        	curr = maze.sampleState(curr, action);
        	System.out.println(maze.actionToString(action)+" => "+maze.stateToString(curr));
        }
        
        System.out.println("I got a reward of "+reward);
        
        int iterations = 2000000;
        QLambda ql = new QLambda(.5, .95, .2, 1, new EpsilonGreedyStrategy(.3), maze);
        FixedIterationTrainer fit = new FixedIterationTrainer(ql, iterations);
        maze.resetStats();
        startTime = System.currentTimeMillis();
        fit.train();
        p = ql.getPolicy();
        finishTime = System.currentTimeMillis();
        System.out.println("Q lambda learned : " + p);
        System.out.println("in " + iterations + " iterations");
        System.out.println("and " + (finishTime - startTime) + " ms");
        System.out.println("Acquiring " + ql.getTotalReward() + " reward");
        maze.printStats();
//        System.out.println(mazeVis.toString(p));

        curr = maze.sampleInitialState();
        reward = 0;
        while (!maze.isTerminalState(curr)) {
        	int action = p.getAction(curr);
        	reward += maze.reward(curr, action);
        	curr = maze.sampleState(curr, action);
        	System.out.println(maze.actionToString(action)+" => "+maze.stateToString(curr));
        }
        
        System.out.println("I got a reward of "+reward);
        
        SarsaLambda sl = new SarsaLambda(.5, .95, .2, 1, new EpsilonGreedyStrategy(.3), maze);
        fit = new FixedIterationTrainer(sl, iterations);
        maze.resetStats();
        startTime = System.currentTimeMillis();
        fit.train();
        p = sl.getPolicy();
        finishTime = System.currentTimeMillis();
        System.out.println("Sarsa lambda learned : " + p);
        System.out.println("in " + iterations + " iterations");
        System.out.println("and " + (finishTime - startTime) + " ms");
        System.out.println("Acquiring " + sl.getTotalReward() + " reward");
        maze.printStats();
//        System.out.println(mazeVis.toString(p));

        curr = maze.sampleInitialState();
        reward = 0;
        while (!maze.isTerminalState(curr)) {
        	int action = p.getAction(curr);
        	reward += maze.reward(curr, action);
        	curr = maze.sampleState(curr, action);
        	System.out.println(maze.actionToString(action)+" => "+maze.stateToString(curr));
        }
        
        System.out.println("I got a reward of "+reward);
    }

}
