Project 1 for CSC375.  Assignment instructions are provided below.

Assignment 1:
Write a parallel genetic algorithm program for an Assignment problem in which:
There are N people (N at least 32) and N seats in a rectangular (classroom-style) arrangement. People are initially assigned random seats.
There is an affinity measure for each person to each other (you may create this randomly). The goal is to assign seats maximizing affinity among adjacent seats. The exact metrics are up to you.
Each of K parallel tasks solve by (at least in part randomly) swapping assignments, occasionally exchanging parts of solutions with others. (This is the main concurrent coordination problem.) Run the program on a computer with at least 32 cores (and K at least 32). (You can develop with smaller K.)
The program occasionally (for example twice per second) graphically displays solutions until converged or a given number of iterations. Details are up to you.
