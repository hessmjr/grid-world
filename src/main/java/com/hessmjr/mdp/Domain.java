package com.hessmjr.mdp;

import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.planning.Planner;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldRewardFunction;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.visualizer.Visualizer;

import java.util.List;


class Domain {

    static final String GRID_WORLD_SMALL = "Small Grid Domain";
    static final String GRID_WORLD_LARGE = "Large Grid Domain";

    private GridWorldDomain gridWorld;
    private OOSADomain oosaDomain;
    private State initState;
    private int maxSteps;

    Domain(String domainName) throws InstantiationException {

        switch (domainName) {
            case GRID_WORLD_SMALL:
                this.oosaDomain = createGWSmall();
                break;

            case GRID_WORLD_LARGE:
                this.oosaDomain = createGWLarge();
                break;

            default:
                throw new InstantiationException("Invalid domain selected: " + domainName);
        }

    }

    /**
     * @return OOSADomain - the domain that was generated on construction
     */
    OOSADomain getSADomain() {
        return this.oosaDomain;
    }

    /**
     * Creates a visualizer for the domain, good for testing and taking a screen shot of what
     * it looks like in real time
     */
    void visualizeDomain() {

        if (this.gridWorld == null) {
            throw new NullPointerException("Grid Domain not built yet");
        }

        // create visualizer and explorer
        Visualizer v = GridWorldVisualizer.getVisualizer(this.gridWorld.getMap());
        VisualExplorer exp = new VisualExplorer(this.oosaDomain, v, this.initState);

        // set control keys to use w-s-a-d
        exp.addKeyAction("w", GridWorldDomain.ACTION_NORTH, "");
        exp.addKeyAction("s", GridWorldDomain.ACTION_SOUTH, "");
        exp.addKeyAction("a", GridWorldDomain.ACTION_WEST, "");
        exp.addKeyAction("d", GridWorldDomain.ACTION_EAST, "");

        exp.initGUI();
    }

    /**
     * Creates a value function visualization for the given agent which showcases the route
     * planning the agent discovered
     * @param agent - Planner agent to simulate
     */
    void visualizeValueFunction(Planner agent) {
        List<State> allStates = StateReachability.getReachableStates(
                this.initState,
                this.oosaDomain,
                agent.getHashingFactory()
        );

        ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization (
                allStates,
                this.gridWorld.getWidth(),
                this.gridWorld.getHeight(),
                (ValueFunction) agent,
                agent.planFromState(this.initState())
        );

        gui.initGUI();
    }

    /**
     * @return State - initial and final state for the domain
     */
    State initState() {
        return this.initState;
    }

    int maxSteps() {
        return this.maxSteps;
    }

    /**
     * Helper that builds the 11 x 11 gridworld state that is default for BURLAP.  Uses the default
     * 4 room setup with two entry/exit points in each room.  The agent starts in one room diagonal
     * from the end target.  Tweak rewards and successes here.
     * @return OOSADomain
     */
    private OOSADomain createGWSmall() {
        int mapSize = 11;

        // create 11x11 grid world using Burlap's default 4 room setup and set the success rate
        // of each move to the value determined
        this.gridWorld = new GridWorldDomain(mapSize, mapSize);
        this.gridWorld.setMapToFourRooms();
        this.gridWorld.setProbSucceedTransitionDynamics(Config.SUCCESS_RATE);

        GridWorldTerminalFunction gwtf = new GridWorldTerminalFunction(
                this.gridWorld.getWidth() - 1,
                this.gridWorld.getHeight() - 1
        );

        GridWorldRewardFunction gwrf = buildRewardFunction();

        this.gridWorld.setTf(gwtf);
        this.gridWorld.setRf(gwrf);

        // setup initial state, goal location, and generator for state
        this.initState = new GridWorldState(
                new GridAgent(0, 0),
                new GridLocation(mapSize - 1, mapSize - 1, "X")
        );
        this.maxSteps = 200;

        // finally generate the domain to use
        return this.gridWorld.generateDomain();
    }

    /**
     * Builds a large grid world with more complex paths and hazards.  Grid world will model the
     * following example:
     *
     *      {X,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0},
     *      {0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0},
     *      {0,0,1,1,1,0,1,0,1,L,1,0,1,0,S,1,1,0,1,0,0},
     *      {0,0,0,0,1,0,1,0,1,0,0,0,1,0,0,0,1,0,1,0,0},
     *      {1,1,1,0,1,0,1,0,1,0,1,1,1,1,S,1,1,0,1,0,0},
     *      {0,0,0,0,1,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
     *      {0,0,1,1,1,0,1,S,1,0,1,0,1,0,1,1,1,0,1,0,0},
     *      {0,0,0,0,1,0,1,0,1,0,1,0,0,0,1,0,0,0,1,0,0},
     *      {0,0,1,0,1,0,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1},
     *      {0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0},
     *      {1,1,1,0,1,0,1,M,1,1,1,0,1,0,M,1,1,0,1,0,0},
     *      {0,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,0,0,1,0,0},
     *      {0,0,1,1,1,0,1,0,1,0,1,0,1,0,1,1,1,1,S,0,0},
     *      {0,0,0,0,1,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,0},
     *      {1,1,1,0,1,1,1,0,1,0,1,0,1,1,1,0,1,0,1,1,1},
     *      {0,0,1,0,1,0,0,0,1,0,1,0,0,0,1,0,0,0,1,0,0},
     *      {0,0,1,0,1,0,1,1,1,0,1,L,0,0,1,0,1,1,1,0,0},
     *      {0,0,0,0,0,0,1,S,0,0,0,0,1,0,1,0,1,0,0,0,0},
     *      {0,0,1,0,1,1,1,1,0,1,1,0,1,0,1,0,1,0,1,0,0},
     *      {0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0},
     *      {0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,G}
     *
     * @return OOSADomain
     */
    private OOSADomain createGWLarge() {
        int[][] map = new int[][] {
                {0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0},
                {0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0,0,1,0,0},
                {0,0,1,1,1,0,1,0,1,0,1,0,1,0,0,1,1,0,1,0,0},
                {0,0,0,0,1,0,1,0,1,0,0,0,1,0,0,0,1,0,1,0,0},
                {1,1,1,0,1,0,1,0,1,0,1,1,1,1,0,1,1,0,1,0,0},
                {0,0,0,0,1,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0},
                {0,0,1,1,1,0,1,0,1,0,1,0,1,0,1,1,1,0,1,0,0},
                {0,0,0,0,1,0,1,0,1,0,1,0,0,0,1,0,0,0,1,0,0},
                {0,0,1,0,1,0,1,0,1,0,1,1,1,1,1,0,1,0,1,1,1},
                {0,0,1,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0,0,0},
                {1,1,1,0,1,0,1,0,1,1,1,0,1,0,0,1,1,0,1,0,0},
                {0,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,0,0,1,0,0},
                {0,0,1,1,1,0,1,0,1,0,1,0,1,0,1,1,1,1,0,0,0},
                {0,0,0,0,1,0,0,0,1,0,1,0,0,0,1,0,1,0,0,0,0},
                {1,1,1,0,1,1,1,0,1,0,1,0,1,1,1,0,1,0,1,1,1},
                {0,0,1,0,1,0,0,0,1,0,1,0,0,0,1,0,0,0,1,0,0},
                {0,0,1,0,1,0,1,1,1,0,1,0,0,0,1,0,1,1,1,0,0},
                {0,0,0,0,0,0,1,0,0,0,0,0,1,0,1,0,1,0,0,0,0},
                {0,0,1,0,1,1,1,1,0,1,1,0,1,0,1,0,1,0,1,0,0},
                {0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0},
                {0,0,1,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,1,0,0}
        };

        this.gridWorld = new GridWorldDomain(map);
        this.gridWorld.setProbSucceedTransitionDynamics(Config.SUCCESS_RATE);

        // establish the domain terminating function
        GridWorldTerminalFunction gwtf = new GridWorldTerminalFunction(
                this.gridWorld.getWidth() - 1,
                this.gridWorld.getHeight() - 1
        );

        GridWorldRewardFunction gwrf = buildRewardFunction();

        gwrf.setReward(2, 9, -3.0 + Config.MOVE_REWARD);
        gwrf.setReward(2, 14, -1.0 + Config.MOVE_REWARD);
        gwrf.setReward(4, 14, -1.0 + Config.MOVE_REWARD);
        gwrf.setReward(6, 7, -1.0 + Config.MOVE_REWARD);
        gwrf.setReward(10, 7, -2.0 + Config.MOVE_REWARD);
        gwrf.setReward(10, 14, -2.0 + Config.MOVE_REWARD);
        gwrf.setReward(12, 18, -1.0 + Config.MOVE_REWARD);
        gwrf.setReward(16, 11, -3.0 + Config.MOVE_REWARD);
        gwrf.setReward(17, 7, -1.0 + Config.MOVE_REWARD);

        this.gridWorld.setTf(gwtf);
        this.gridWorld.setRf(gwrf);

        // setup initial state, goal location, and generator for state
        this.initState = new GridWorldState(
                new GridAgent(0, 0),
                new GridLocation(this.gridWorld.getWidth() - 1, this.gridWorld.getHeight() - 1, "X")
        );
        this.maxSteps = 1_000;

        // finally generate the domain to use
        return this.gridWorld.generateDomain();
    }

    /**
     * Builds Grid World reward function
     * @return GridWorldRewardFunction
     */
    private GridWorldRewardFunction buildRewardFunction() {
        GridWorldRewardFunction gwrf = new GridWorldRewardFunction(
                this.gridWorld.getWidth(),
                this.gridWorld.getHeight(),
                Config.MOVE_REWARD
        );

        gwrf.setReward(
                this.gridWorld.getWidth() - 1,
                this.gridWorld.getHeight() - 1,
                Config.GOAL_REWARD
        );

        return gwrf;
    }
}
