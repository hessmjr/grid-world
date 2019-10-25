# Grid World MDP
Markov Decision Process experiment and analysis using Burlap's grid world


## Usage
Steps necessary to re-run this application:

  1. Update the Configuration class.  All pertinent settings are stored in this class such as which
  domain to build and which type of agent or planner to utilize in creating the best value function.
  
  2. Compile the JAR using the Gradle wrapper and then execute the application.  The following 
  commands will handle this, from the project root directory:
  
  ```
  ./gradlew clean build
  java -jar build/libs/mdp.jar
  ```


## Analysis
A Markov decision process (MDP) is the process of modeling decision making for situations that are 
stochastic in nature.  The grid world is the simplest representation of this process.  The goal 
of this experiment and analysis is to showcase various methods to evaluate different domains (i.e. 
grid worlds) as well as reinforcement learning's impact on solving MDP's.

### Grid World
Solving MDP's is made most apparent utilizing the canonical example of grid worlds.  The agent lives
in a grid and moves semi-stochastically through each square.  There are usually obstacles and 
various positive/negative reward locations

#### Small Grid World
The first domain studied is the small gride world.  Consisting of only a 10 x 10 grid, the small 
grid world has 4 walls with one entry point in each as well as a single start and finish point. 
This basic setup is setup to easily illustrate the effectiveness of each planning/learning method 
as well as a good domain to test against correct implementation.

- TODO image

#### Large Grid World
The second domain analyzed is a larger much more complex of the grid world.  Consisting of a 
21 x 21 square grid, the larg grid world contains multiple walls, dead ends, negative reward 
obstacles and the same single start and finish point.  This more complex setup truly showcases
the capabilities of the planning and learning algorithms analyzed below

- TODO image

### Planning/Learning
- https://people.eecs.berkeley.edu/~pabbeel/cs287-fa09/lecture-notes/lecture11-6pp.pdf

#### Policy Iteration

#### Value Iteration

#### Reinforcement Learning
