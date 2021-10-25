package sma;

import java.util.LinkedList;
import java.util.List;

public class Process {
	
	List<Segment> segments;
	
	public Process () {
		segments = new LinkedList<>();
	}
	
	public Segment getSegment(int base) {
		for (Segment segment : segments)
			if (segment.base == base)
				return segment;
		return null;
	}
	
	public List<Segment> getSegments() {
		return segments;
	}
	
	// When you want to add a segment or change the size
	public Segment updateSegment(Segment segment, List<Integer> toAdd, MemoryAllocator malloc) {
		Segment seg = segment;
		if (seg == null) {
			System.out.println("Allocating " + toAdd.get(0) + " bytes...");
			seg = malloc.allocate(toAdd.get(0));
			
			// If the allocation failed
			if (seg == null) {
				throw new OutOfMemoryError("Not enough space for your segment in the virtual memory!");
			}
			
			// Set the read-write flag
			seg.setReadWrite(toAdd.get(1) == 1);
			
			// Update the shared-with list
			seg.clearSharedWith();
			for (int i = 2; i < toAdd.size(); i++) {
				seg.addSharedWith(toAdd.get(i));
			}
			
			segments.add(seg);
		} else {
			System.out.println("Resizing base " + seg.base + " to " + seg.size + " bytes...");
			Segment newSeg = malloc.resize(seg, toAdd.get(0));
			
			if (newSeg == null) {
				// Let the calling function handle it
				throw new OutOfMemoryError("Not enough space for your segment in the virtual memory!");
			}
			
			// Set the read-write flag
			newSeg.setReadWrite(toAdd.get(1) == 1);
			
			// Update the shared-with list
			newSeg.clearSharedWith();
			for (int i = 2; i < toAdd.size(); i++) {
				newSeg.addSharedWith(toAdd.get(i));
			}
			
			if (seg != newSeg) {
				segments.set(segments.indexOf(seg), newSeg);
			}
			System.out.println("New base: " + seg.base);
		}
		return seg;
	}
	
	// Clear segments or zero size
	public void clearEmptySegments(MemoryAllocator malloc) {
		for (Segment segment : segments) {
			if (segment.size == 0) {
				// Double frees are no problem :)
				malloc.free(segment);
				segments.remove(segment);
			} else if (segment.size < 0) {
				System.out.println("Fatal error! You tried to allocate less than zero to a segment!");
				System.exit(1);
			}
		}
	}
	
	// For debugging
	public void printSegments() {
		String out = "";
		for (Segment segment : segments) {
			out += "Number: " + (segments.indexOf(segment) + 1) + ", Base: " + segment.base + ", Size: " + segment.size + ", ReadWrite: " + segment.readWriteFlag + ", shared with: " + segment.getSharedWith();
			out += "\n";
		}
		System.out.println(out);
	}
}
