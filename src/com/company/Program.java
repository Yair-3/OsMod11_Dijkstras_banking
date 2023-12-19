package com.company;

import java.util.ArrayList;
import java.util.Random;

public class Program
{

    final static int NUM_PROCS = 6; // How many concurrent processes
    final static int TOTAL_RESOURCES = 30; // Total resources in the system
    final static int MAX_PROC_RESOURCES = 13; // Highest amount of resources any process could need
    final static int ITERATIONS = 30; // How long to run the program
    static int totalHeldResources = 0; // How many resources are currently being held
    static Random rand = new Random();

    public static void main(String[] args)
    {

        // The list of processes:
        ArrayList<Proc> processes = new ArrayList<Proc>();
        for (int i = 0; i < NUM_PROCS; i++)
            processes.add(new Proc(MAX_PROC_RESOURCES - rand.nextInt(3))); // Initialize to a new Proc, with some small range for its max

        // Run the simulation:
        for (int i = 0; i < ITERATIONS; i++)
        {
            // loop through the processes and for each one get its request
            for (int j = 0; j < processes.size(); j++)
            {
                // Get the request
                Proc process = processes.get(j);

                int currRequest = processes.get(j).resourceRequest(TOTAL_RESOURCES - totalHeldResources);

                // just ignore processes that don't ask for resources
                if (currRequest == 0)
                    continue;

                if (currRequest > 0) {
                    if (checkIfSafeToGrant(processes, j, currRequest, TOTAL_RESOURCES - totalHeldResources)) {
                        process.addResources(currRequest);
                        totalHeldResources += currRequest;
                        System.out.println("Process " + j + " requested " + currRequest + ", granted.");
                    } else {
                        System.out.println("Process " + j + " requested " + currRequest + ", denied.");
                    }
                } else {
                    totalHeldResources += currRequest; // Releasing resources
                    System.out.println("Process " + j + " completed, releasing resources.");
                }
            }
            printStatus(processes);
        }
    }

    private static boolean checkIfSafeToGrant(ArrayList<Proc> processes, int procIndex, int request, int availableResources) {
        // SIMULATE ALLOCATION OF RESOURCES WITH THE CURRENT REQUEST OF RESOURCES;
        int tempAvailable = availableResources - request;
        int[] tempAllocated = new int[processes.size()];
        for (int i = 0; i < processes.size(); i++) {
            tempAllocated[i] = processes.get(i).getHeldResources();
        }
        tempAllocated[procIndex] += request;

        // Pseduo code from slides turned into real code.

        boolean[] finished = new boolean[processes.size()];
        while (true) {
            boolean foundProcess = false; // starts out false
            for (int i = 0; i < processes.size(); i++) {
                if (!finished[i]) {
                    int additionalNeed = processes.get(i).getMaxResources() - tempAllocated[i]; // get the claim
                    if (additionalNeed <= tempAvailable) { // check if it's even possible
                        tempAvailable += tempAllocated[i]; // simulate the net gain of resources
                        finished[i] = true; // 'removal' of the process from the list
                        foundProcess = true; // indicate the process can finish
                        break;
                    }
                }
            }
            if (!foundProcess) {
                break;
            }
        }

        // check if all processes could finish
        for (boolean f : finished) {
            if (!f) return false;
        }
        return true;
    }

    private static void printStatus(ArrayList<Proc> processes) {
        System.out.println("\n***** STATUS *****");
        System.out.println("Total Available: " + (TOTAL_RESOURCES - totalHeldResources));
        for (int k = 0; k < processes.size(); k++) {
            System.out.println("Process " + k + " holds: " + processes.get(k).getHeldResources() + ", max: " +
                    processes.get(k).getMaxResources() + ", claim: " +
                    (processes.get(k).getMaxResources() - processes.get(k).getHeldResources()));
        }
        System.out.println("***** STATUS *****\n");
    }
}
