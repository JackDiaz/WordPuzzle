package cmsc433.p3;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * This class contains a static method that solves a Grid.
 * The solution should be multithreaded.
 *
 */
public class GridSolver {

	static ArrayList<Point> filled;
	//static final Object lock1 = new Object();
	static Solution solution;
	//static final Object lock2 = new Object();
	//static ConcurrentSkipListSet<Path> paths;
	static ConcurrentLinkedQueue<Path> paths;


	static long startTime;
	/**
	 * This routine should take no more than timeMS millseconds to
	 * produce a valid solution to this grid. A solution is a list
	 * of paths, each one corresponding to a word in the given
	 * dictionary.
	 * 
	 * @param board - the Grid to solve
	 * @param dict - the Dictionary to use
	 * @param timeMS - the timeout
	 * @return a solution to the grid
	 */
	public static Solution solve(Grid board, Dictionary dict, long timeMS) {
		// IMPLEMENT how you like (can add methods, fields, etc.)
		startTime = System.currentTimeMillis();



		final Grid b = board;
		final Dictionary d = dict;
		final long t = timeMS;

		final int NTHREADS = Runtime.getRuntime().availableProcessors()+1; // Fixed number of threads 
		final ExecutorService exec = Executors.newFixedThreadPool(NTHREADS);

		solution = new STSolution();
		filled = new ArrayList<Point>();
		paths = new ConcurrentLinkedQueue<Path>();//new ConcurrentSkipListSet<Path>(new PathComparator(d));

		final int serial = (int) timeMS/8;
		
		if(timeUp(startTime, timeMS)){
			return solution;
		}
		for (String w : dict) {
			/*if(timeUp(startTime, timeMS)){
				exec.shutdown();
				break;
			}*/
			try{
				final String word = w;
				Runnable task = new Runnable() { 
					public void run() { 
						findWord(b, d, t, word, serial); 
					} 
				};
				exec.execute(task);
			}catch(RejectedExecutionException e){
				if (!exec.isShutdown()) System.err.print("task submission rejected\n"+ e);
			}
		}
		exec.shutdown();
		try {
			exec.awaitTermination(timeMS-((System.currentTimeMillis() - startTime)+serial), TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*	while(!exec.isTerminated()){
			exec.shutdownNow();
		}*/
		//System.out.println(exec.shutdownNow().size());~~~~~
		exec.shutdownNow();
		//System.out.println("NumPaths = "+paths.size());~~~~
		ArrayList<Path> sortedPaths = new ArrayList<Path>();
		Iterator<Path> iter = paths.iterator();
		//System.out.println(System.currentTimeMillis());
		while(iter.hasNext()){
	//		System.out.print("= ");
			sortedPaths.add(iter.next());
		}
		Collections.sort(sortedPaths, new PathComparator(d));
		
		Collections.reverse(sortedPaths);// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	//	System.out.println("\n"+System.currentTimeMillis());
		//System.out.println("SortPaths = "+sortedPaths.size());~~~
		iter = sortedPaths.iterator();
		Iterator<Point> i;
		boolean addPath;
		while(iter.hasNext()){
			//if (timeUp(startTime, timeMS))
			//return solution;
			
			addPath = true;
			Path p = iter.next();
			List<Point> gP = p.getPoints();
	//		System.out.println(d.getScore(gP.size()));
			i = gP.iterator();
			while (i.hasNext() && addPath) {
				Point o = i.next();
				if(filled.contains(o)){
					addPath = false;
				}
			}
			i = gP.iterator();
			while (i.hasNext() && addPath) {
				Point o = i.next();
				filled.add(o);
			}
			if(addPath){
				solution.addPath(p);
			}
			if (timeUp(startTime, timeMS)){
				//System.out.println("NumPaths = "+paths.size());
				//System.out.println("SortPaths = "+sortedPaths.size());
				//System.out.println("Cut short SolSize = "+solution.getPaths().size());~~~
				return solution;
			}
		}
		//System.out.println("NumPaths = "+paths.size());
		//System.out.println("SortPaths = "+sortedPaths.size());
		//System.out.println("SolSize = "+solution.getPaths().size());~~~
		return solution;
	}

	static boolean timeUp(long startTime, long timeMS) {
		return ((System.currentTimeMillis() - startTime) > timeMS);
	}

	static void findWord(Grid board, Dictionary dict, long timeMS, String word, int offset){
		long startTime = System.currentTimeMillis();
		if (timeUp(startTime, timeMS+offset)){
			//System.out.println("~1~");
			return;
		}
		if(Thread.interrupted()){
			return;
		}

		boolean foundWord = false;
		for (int row = 0; row < board.numRows(); ++row) {
			for (int col = 0; col < board.numCols(); ++col) {
				if(Thread.interrupted()){
					return;
				}
				if (board.get(row, col) == word.charAt(0)) {
					ArrayDeque<Point> currentPath = new ArrayDeque<Point>();
					HashMap<Point, ArrayList<Point>> marked = new HashMap<Point, ArrayList<Point>>();
					Point first = new Point(row, col);
					currentPath.push(first);
					marked.put(first, new ArrayList<Point>());
					marked.get(first).add(first);
					while (currentPath.size() != word.length() && !(currentPath.isEmpty())) {
						if (timeUp(startTime, timeMS+offset)){
							//System.out.println("~2~");
							return;
						}
						if(Thread.interrupted()){
							return;
						}

						ArrayList<Point> toConsider = new ArrayList<Point>();
						Point current = currentPath.getFirst();
						for (int rowChange = -1; rowChange <= 1; ++rowChange) {
							for (int colChange = -1; colChange <= 1; ++colChange) {
								int rowCoord = current.row + rowChange;
								int colCoord = current.col + colChange;
								if ( (0 <= rowCoord) && (rowCoord < board.numRows()) && (0 <= colCoord) && (colCoord < board.numCols()) )
									toConsider.add(new Point(rowCoord, colCoord));
							}
						}
						if(Thread.interrupted()){
							return;
						}
						Point next = null;
						for (Point p : toConsider) {
							if (!(marked.get(current).contains(p) || currentPath.contains(p))) {
								next = p;
								break;
							}
						}

						if (next == null) {
							currentPath.pop();
							continue;
						}

						marked.get(current).add(next);
						if (board.get(next.row, next.col) == word.charAt(currentPath.size())) {
							currentPath.push(next);
							marked.put(next, new ArrayList<Point>());
							marked.get(next).add(current);
						}
					}

					if (currentPath.size() == word.length()) {
						STPath path = new STPath();
						Iterator<Point> iter = currentPath.descendingIterator();
						while (iter.hasNext()) {
							Point p = iter.next();
							path.addPoint(p);
						}
						paths.add(path);
						foundWord = true;
						break;
					}
					if(Thread.interrupted()){
						return;
					}
				}
			}

			if (foundWord)
				continue;
		}
		//System.out.println("~3~");
	}
}