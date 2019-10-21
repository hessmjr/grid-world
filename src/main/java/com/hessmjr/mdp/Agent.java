package com.hessmjr.mdp;

import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.simple.SimpleHashableStateFactory;


class Agent {

    static final String VALUE_ITERATION = "Value Iteration";
    static final String POLICY_ITERATION = "Policy Iteration";
    static final String Q_LEARNER = "Q-Learning";

    // constants that all planning algorithms will utilize
    private static final SimpleHashableStateFactory HASH_FACTORY = new SimpleHashableStateFactory();

    /**
     * Main planner building method which decides which planner is being requested then calls
     * the appropriate helper method
     * @param domain - SADomain the planner will process
     * @return Planner
     */
    static Planner buildAgent(SADomain domain, int maxIters) {

        switch (Config.AGENT_NAME) {
            case VALUE_ITERATION:
                return buildValueIteration(domain, maxIters);

            case POLICY_ITERATION:
                return buildPolicyIteration(domain, maxIters);

            case Q_LEARNER:
                return buildQLearner(domain, maxIters);

            default:
                throw new IllegalArgumentException("Invalid planner requested: " + Config.AGENT_NAME);
        }
    }

    /**
     * Builds a value iteration planner
     * @param domain - SADomain to process
     * @param maxIters - int of max iterations
     * @return ValueIteration Planner
     */
    private static Planner buildValueIteration(SADomain domain, int maxIters) {
        return new ValueIteration(domain, Config.GAMMA, HASH_FACTORY, Config.MAX_DELTA, maxIters);
    }

    /**
     * Builds a policy iteration planner
     * @param domain - SADomain to process
     * @param maxIters - int of max iterations
     * @return PolicyIteration Planner
     */
    private static Planner buildPolicyIteration(SADomain domain, int maxIters) {
        return new PolicyIteration(domain, Config.GAMMA, HASH_FACTORY, Config.MAX_DELTA, maxIters, maxIters);
    }

    /**
     * Builds a Q Learning agent
     * @param domain - SADomain to process
     * @param maxIters - int of max iterations
     * @return QLearning Planner
     */
    static QLearning buildQLearner(SADomain domain, int maxIters) {

        // hyper params that can be tweaked with the learning agent
        double initQ = 0.5;
        double alpha = 0.3;

        // build the new Q learning agent and setup for planning experiments
        QLearning agent = new QLearning(domain, Config.GAMMA, HASH_FACTORY, initQ, alpha, maxIters);
        agent.initializeForPlanning(1);
        return agent;
    }
}
