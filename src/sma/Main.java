package sma;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Main {
	
	static final String[] exampleAllocs = {
			"1, 100, 200, 10\n2, 100, 200, 10\n3, 10, 30, 90\n4, 40\n5, 300",
			"1, 100, 100, 10\n2, 200\n1, -40, 0, 0\n2, -10, 300\n3, 180, 60",
			"1, [100, 0], 200, 10\n2, 100, [200, 0], [10, 0]\n3, [10, 1], [30, 1], [90, 1]\n4, [40, 0]\n5, 300",
			"1, [100, 0], [200, 0, 2, 3, 4], 10\n1, [-10, 1], [100, 0, 2, 5], 10",
			"1, [100, 0], [200, 0, 2, 3]\n2, [200, 0, 1, 3]",
			"a, b, c, d",
			"1, [100, 0], [200, 0, 2, 3, 4, 10",
			"2, [100, 0], [200, 0, 2, 3, 4, 10",
			"1, 1000"
	};
	
	// Translation Look-Aside Buffer
	static List<int[]> TLB = new ArrayList<>();
	static final int TLB_SIZE = 4;
	
	
	public static void main (String args[]) {
		// Example A.1.2
		System.out.println("\nStart Example A.1.2\n");
		handleInput(exampleAllocs[0], 1200);
		System.out.println("\nEnd example A.1.2\n");
		
		// Code location A.2.1
		System.out.println("\nStart Code Location A.2.1 read-write protection\n");
		System.out.println("Segment.java, Line 8");
		System.out.println("Process.java, Line 58");
		System.out.println("\nEnd Code Location A.2.1 read-write protection\n");
		
		// Example A.2.1
		System.out.println("\nStart Example A.2.1 read-write protection\n");
		handleInput(exampleAllocs[2], 1200);
		System.out.println("\nEnd Example A.2.1 read-write protection\n");
		
		// Code location A.2.2
		System.out.println("\nStart Code Location A.2.2 sharing of segments\n");
		System.out.println("Segment.java, Line 9");
		System.out.println("Process.java, Line 60");
		System.out.println("\nEnd Code Location A.2.2 sharing of segments\n");
		
		// Example A.2.2 + A.2.3
		System.out.println("\nStart Example A.2.2 sharing of segments");
		System.out.println("Start Example A.2.3\n");
		handleInput(exampleAllocs[4], 900);
		System.out.println("End Example A.2.3\n");
		System.out.println("\nEnd Example A.2.2 sharing of segments");
		
		// Code location A.3.1
		System.out.println("\nStart Code Location A.3.1 TLB\n");
		System.out.println("Main.java, Line 22 and 269");
		System.out.println("\nEnd Code Location A.3.1 TLB\n");
		
		// Example A.3.1
		

		MemoryAllocator malloc = new MemoryAllocator(900);
		List<Process> processes = new ArrayList<>();
		Process process = new Process();
		processes.add(process);
		
		List<Integer> toAdd = new ArrayList<>();
		toAdd.add(200);
		toAdd.add(1);
		process.updateSegment(null, toAdd, malloc);
		process.updateSegment(null, toAdd, malloc);
		process.updateSegment(null, toAdd, malloc);
		
		System.out.println("TLB (before): " + TLB);
		System.out.println("Getting base for segment 2: " + getBase(processes, 1, 2));
		System.out.println("TLB (after): " + TLB);
		
		System.out.println("\nEnd Example A.3.1 TLB miss\n");
		System.out.println("\nStart Example A.3.1 TLB hit\n");
		
		System.out.println("TLB (before): " + TLB);
		System.out.println("Getting base for segment 2: " + getBase(processes, 1, 2));
		System.out.println("TLB (after): " + TLB);
		
		System.out.println("\nEnd Example A.3.1 TLB hit\n");
		
		
		// Code location A.3.2
		System.out.println("\nStart Code Location A.3.2 compaction\n");
		System.out.println("MemoryAllocator.java, Line 78");
		System.out.println("Main.java, Line 185");
		System.out.println("\nEnd Code Location A.3.2 compaction\n");
		
		// Example A.3.2
		System.out.println("\nStart Example A.3.2 compaction\n");
		handleInput(exampleAllocs[1], 900);
		System.out.println("\nEnd Example A.3.2 compaction\n");
		
		// Testing NumberFormatException
		System.out.println("Start Test of NumberFormatException");
		handleInput(exampleAllocs[5], 900);
		System.out.println("End Test of NumberFormatException\n");
		
		// Testing IllegalArgumentException
		System.out.println("Start Test of IllegalArgumentException");
		handleInput(exampleAllocs[6], 900);
		handleInput(exampleAllocs[7], 900);
		System.out.println("End Test of IllegalArgumentException\n");
		
		// Testing OutOfMemoryError
		System.out.println("Start Test of OutOfMemoryError");
		handleInput(exampleAllocs[8], 900);
		System.out.println("End Test of OutOfMemoryError");
	}
	
	// Handle input of the form <processnum> <segment>, where segments are just a bare number or a list of numbers in brackets
	public static void handleInput(String example, int memSize) {
		MemoryAllocator malloc = new MemoryAllocator(memSize);
		List<Process> processes = new ArrayList<>();
		
		for (String line : example.split("\n")) {
			String[] vals = line.split(", ");
			try {
				int processNum = Integer.valueOf(vals[0]);
				List<List<Integer>> segmentAllocs = new ArrayList<>();
				
				// Turn input into the correct format
				for (int i = 1; i < vals.length; i++) {
					if (vals[i].startsWith("[")) {
						List<Integer> alloc = new ArrayList<>();
						segmentAllocs.add(alloc);
						// Make the first value the segment size
						alloc.add(Integer.valueOf(vals[i].substring(1)));
						
						for (i++; i < vals.length && !vals[i].endsWith("]"); i++) {
							
							alloc.add(Integer.valueOf(vals[i]));
						}
						if (i >= vals.length) throw new IllegalArgumentException("Unclosed brackets []!"); 
						// Handle the last value with a ] on the end
						alloc.add(Integer.valueOf(vals[i].substring(0, vals[i].length() - 1)));
					} else {
						// Handle all the values that don't have brackets
						List<Integer> alloc = new ArrayList<>();
						segmentAllocs.add(alloc);
						alloc.add(Integer.valueOf(vals[i]));
						alloc.add(1);
					}
				}
				
				
				// Create process if it doesn't exist
				if (processNum - 1 == processes.size())
					processes.add(new Process());
				else if (processNum - 1 > processes.size()){
					throw new IllegalArgumentException("You need to provide processes in order.");
				}
				
				Process process = processes.get(processNum - 1);
				
				// Create a copy of the process segments
				List<Segment> segs = new ArrayList<>(process.getSegments());
				
				// vals[i] = segmentAllocs[i-1][0]
				for (int i = 0; i < segmentAllocs.size(); i++) {
					System.out.println("Process: " + processNum + ", Bases: " + segs.size() + ", i: " + i);
					
					if (segmentAllocs.get(i).size() <= 2) {
						Segment cur;
						if (i >= segs.size()) {
							// We're going to make a new segment
							cur = null;
						} else {
							cur = segs.get(i);
						}
						Segment newSeg;
						try {
						newSeg = process.updateSegment(cur, segmentAllocs.get(i), malloc);
						} catch (OutOfMemoryError e) {
							System.out.println("Compacting...");
							malloc.compact();
							newSeg = process.updateSegment(cur, segmentAllocs.get(i), malloc); // Don't worry, throws exception
						}
						if (segs.contains(cur)) {
							segs.set(segs.indexOf(cur), newSeg);
						} else {
							segs.add(newSeg);
						}
					} else {
						System.out.println("Shared segment");
						
						// Get the set to match all shared processes
						Set<Integer> shared = new HashSet<>(segmentAllocs.get(i).subList(2, segmentAllocs.get(i).size()));
						shared.add(processNum);
						
						// for each process
						boolean found = false;
						for (int pnum = 0; pnum < processes.size(); pnum++) {
							// The segments in process pnum
							List<Segment> pSegs = processes.get(pnum).getSegments();
							
							// For each segment in the process until a match is found
							for (int j = 0; found == false && j < pSegs.size(); j++) {
								// The processes who share segment j, including the current one
								Set<Integer> segNums = new HashSet<>(pSegs.get(j).getSharedWith());
								
								// Make sure we're not accepting a shared segment if we already have it
								if (segNums.equals(shared) && pSegs.get(j).size == segmentAllocs.get(i).get(0) && !process.getSegments().contains(pSegs.get(j))) {
									found = true;
									process.getSegments().add(pSegs.get(j));
								}
							}
						}
						if (!found) {
							Segment cur;
							if (i >= segs.size()) {
								// We're going to make a new segment
								cur = null;
							} else {
								cur = segs.get(i);
							}
							
							Segment newSeg = process.updateSegment(cur, segmentAllocs.get(i), malloc);
							if (newSeg == null) {
								System.out.println("Compacting...");
								malloc.compact();
								newSeg = process.updateSegment(cur, segmentAllocs.get(i), malloc);
								if (newSeg == null) {
									System.out.println("Error! You have run out of memory!");
									System.exit(1);
								}
							}
							newSeg.getSharedWith().add(processNum);
							if (segs.contains(cur)) {
								segs.set(segs.indexOf(cur), newSeg);
							} else {
								segs.add(newSeg);
							}
						}
					}
	
					for (Process prcs : processes) {
						prcs.clearEmptySegments(malloc);
					}
					process.printSegments();
				}
			} catch (NumberFormatException e) { // for Integer.valueOf()
				System.out.println("Invalid input! You may only enter numbers and brackets surrounded by ', '!");
				return;
			} catch (OutOfMemoryError e) {
				System.out.println(e.getMessage());
				return;
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
				return;
			}
			
		}
		
		// Print segment table
		malloc.printTable();
	}
	
	// Get the base, given the process number and segment number. Uses TLB
	public static int getBase(List<Process> processes, int processNum, int segmentNum) {
		for (int i = 0; i < TLB.size(); i++) {
			if (TLB.get(i)[0] == processNum && TLB.get(i)[1] == segmentNum) {
				return TLB.get(i)[2];
			}
		}
		if (TLB.size() == TLB_SIZE)
			TLB.remove(0);
		
		int base = processes.get(processNum - 1).getSegments().get(segmentNum - 1).base;
		int[] arr = {processNum, segmentNum, base};
		TLB.add(arr);
		return base;
	}
}
