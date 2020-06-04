# Adaption of FairGhosts, Applied Computational Intelligence Spring Semester 2020
### Table of Content

- [Introduction](#introduction)
  - [Contributors](#contributors)
- [Installation Guide](#installation)
  - [Requirements](#requirements)
  - [Guide](#guide)
  - [Executionmodes](#executionmodes)

### Introduction


#### Contributors

- Timothy Applewhite
- Roger Kaufmann

The fairGhosts adaption is based on the prototype developed by Iris Hunkeler and Fabian Schaer.
The game engine used is from the *Ms. Pac-Man vs. Ghost Competition* held during the IEEE World Congress on Computational Intelligence (WCCI 2012). 

### Installation

#### Requirements

- JRE 1.8.0_251
- Eclipse 2020-03 (4.15.0)

##### Guide

- Install JRE and Eclipse
- Run the file Execution.java

##### Executionmodes

The simulation can run in different modes. Depending on the desired mode, the code after line 50 in the Executor.java file has to me modified.

- Experiment - A number of simulation runs is executed without showing visuals. The console returns the score of each execution and the average score after the last run.

		
		int numTrials=10;
		exec.runExperiment(new Eiisolver(),new FairGhosts(),numTrials);


		
- Visualization Synchron - The game runs synchronous. The game waits until the controllers respond with the next move.


		int delay=5;
		boolean visual=true;
		exec.runGame(new Eiisolver(),new FairGhosts(),visual,delay);


- Visualization Asynchron - The game runs asynchronous. The game does not wait until the controllers respond with the next move.


		boolean visual=true;
		exec.runGameTimed(new StarterPacMan(),new FairGhosts(),visual);


- Speedoptimized Asynchron - The game runs asynchronous. The game advances as soon as both controllers respond with the next move.


		boolean visual=true;
		boolean fixedTime=false;
		exec.runGameTimedSpeedOptimised(new StarterPacMan(),new FairGhosts(),fixedTime,visual);
		
