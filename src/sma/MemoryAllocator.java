package sma;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class MemoryAllocator {
	List<Segment> segmentTable;
	int totalSize;
	
	public MemoryAllocator (int totalSize) {
		segmentTable = new LinkedList<>();
		this.totalSize = totalSize;
	}
	
	// Allocates a segment of size `size` and returns the base of the segment, or -1 if it failed
	public Segment allocate(int size) {
		Iterator<Segment> it = segmentTable.iterator();
		int lastEnd = 0;
		Segment toAdd = new Segment(-1, 0);
		while (it.hasNext()) {
			// If we have enough space before the next segment
			Segment next = it.next();
			if (next.base - lastEnd >= size) {
				toAdd.expand(size);
				toAdd.rebase(lastEnd);
				System.out.println("Allocating at: " + lastEnd);
				segmentTable.add(toAdd);
				break;
			}
			lastEnd = next.base + next.size;
		}
		
		// If there is no room in the gaps
		if (toAdd.base == -1 && totalSize - lastEnd >= size) {
			toAdd.expand(size);
			toAdd.rebase(lastEnd);
			System.out.println("Allocating at: " + lastEnd);
			segmentTable.add(toAdd);
		} else if (toAdd.base == -1) {
			toAdd = null;
		}
		
		// Sort the segment table by base so that this function works again next time
		Collections.sort(segmentTable, (seg1, seg2) -> (seg1.base - seg2.base));
		return toAdd;
	}
	
	// Frees the segment starting at base `base`
	public void free(Segment seg) {
		segmentTable.remove(seg);
	}
	
	// Resize an allocated segment, relocating if needed. Returns the base.
	public Segment resize(Segment seg, int toAdd) {
		int newSize = seg.size + toAdd;
		
		// Get the amount of space between the start of this segment and the start of the next.
		int nextStart = totalSize;
		if (segmentTable.size() > segmentTable.indexOf(seg) + 1)
			nextStart = segmentTable.get(segmentTable.indexOf(seg) + 1).base;
		int space = nextStart - seg.base;
		
		
		// If we don't need to reallocate
		if (space > newSize) {
			System.out.println("seg.size = " + newSize);
			seg.size = newSize;
			return seg;
		} else {
			free(seg);
			return allocate(newSize);
		}
	}
	
	// Compact the segment table
	public void compact() {
		int earliest = 0;
		for (Segment segment : segmentTable) {
			segment.rebase(earliest);
			earliest += segment.size;
		}
	}
	
	// For debugging
	public void printTable() {
		String out = "SEGMENTS:\n";
		for (Segment segment : segmentTable) {
			out += "Base: " + segment.base + ", Size: " + segment.size + ", ReadWrite: " + segment.readWriteFlag + ", shared with: " + segment.getSharedWith();
			out += "\n";
		}
		System.out.println(out);
	}
}
