package com.hessmjr.mdp;

import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.singleagent.planning.stochastic.policyiteration.PolicyIteration;
import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.mdp.singleagent.SADomain;
import burlap.statehashing.simple.SimpleHashableStateFactory;


class AgentFactory {

    private static final String VALUE_ITERATION = "Value Iteration";
    private static final String POLICY_ITERATION = "Policy Iteration";
    private static final String Q_LEARNER = "Q-Learning";

    // constants that all planning algorithms will utilize
    private static final double GAMMA = 0.99;
    private static final SimpleHashableStateFactory HASH_FACTORY = new SimpleHashableStateFactory();

    /**
     * Main planner building method which decides which planner is being requested then calls
     * the appropriate helper method
     * @param name - String of planner to build
     * @param domain - SADomain the planner will process
     * @return Planner
     */
    static Planner buildAgent(String name, SADomain domain, int maxIters) {
        double maxDelta = 0.0001;

        switch (name) {
            case VALUE_ITERATION:
                return buildValueIteration(domain, maxIters, maxDelta);

            case POLICY_ITERATION:
                return buildPolicyIteration(domain, maxIters, maxDelta);

            case Q_LEARNER:
                return buildQLearner(domain, maxIters);

            default:
                throw new IllegalArgumentException("Invalid planner requested: " + name);
        }
    }

    /**
     * Builds a value iteration planner
     * @param domain - SADomain to process
     * @param maxIters - int of max iterations
     * @param maxDelta - double of max delta shared by all planners
     * @return ValueIteration Planner
     */
    private static Planner buildValueIteration(SADomain domain, int maxIters, double maxDelta) {
        return new ValueIteration(domain, GAMMA, HASH_FACTORY, maxDelta, maxIters);
    }

    /**
     * Builds a policy iteration planner
     * @param domain - SADomain to process
     * @param maxIters - int of max iterations
     * @param maxDelta - double of max delta shared by all planners
     * @return PolicyIteration Planner
     */
    private static Planner buildPolicyIteration(SADomain domain, int maxIters, double maxDelta) {
        return new PolicyIteration(domain, GAMMA, HASH_FACTORY, maxDelta, maxIters, maxIters);
    }

    /**
     * Builds a Q Learning agent
     * @param domain - SADomain to process
     * @param maxIters - int of max iterations
     * @return QLearning Planner
     */
    private static QLearning buildQLearner(SADomain domain, int maxIters) {

        // hyper params that can be tweaked with the learning agent
        double initQ = 0.5;
        double alpha = 0.3;

        // build the new Q learning agent and setup for planning experiments
        QLearning agent = new QLearning(domain, GAMMA, HASH_FACTORY, initQ, alpha, maxIters);
        agent.initializeForPlanning(1);
        return agent;
    }

    /**
     * Calls Q learning builder for learning experiment usage
     * @param domain - SADomain
     * @param maxIters - int of max iterations
     * @return LearningAgent
     */
    static LearningAgent buildAgent(SADomain domain, int maxIters) {
        return buildQLearner(domain, maxIters);
    }
}
