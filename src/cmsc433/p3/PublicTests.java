package cmsc433.p3;

import junit.framework.TestCase;
import java.util.List;

public class PublicTests extends TestCase
{
	private static final boolean printFailingSolutions = true;
	private static final double timeAllowance = 1.3;
	private static final int MAX_TIME_LIMIT = 5000;
	private static final String animals = "dict_animals.txt";
	private static final String random = "dict_random.txt";

	private void compareScore(String gridName, String dictName, int timeLimit, int scoreToBeat, boolean beatST, boolean checkValid, int numruns)
	{
		Grid board = Grid.makeGrid(gridName);
		Dictionary dict = Dictionary.makeDict(dictName);
		if(beatST) { // make the cache hot
			List<Path> pathsST = STGridSolver.solve(board, dict, timeLimit).getPaths();
			scoreToBeat = board.score(dict,pathsST);
		}
		boolean passing = false;
		for(int i=0;i<numruns;i++)
		{
			long startTime = System.currentTimeMillis();
			Solution sol = GridSolver.solve(board, dict, timeLimit);
			List<Path> paths = sol.getPaths();
			long runTime = System.currentTimeMillis()-startTime;
			int score = board.score(dict, paths);
			boolean valid = validatePaths(dict, board, sol);
			if(beatST) {
				List<Path> pathsST = STGridSolver.solve(board, dict, timeLimit).getPaths();
				scoreToBeat = board.score(dict, pathsST);
			}
			boolean betterScore = score>=scoreToBeat;
			
			System.out.println("\n"+gridName);
			System.out.println("---Valid paths: "+valid);
			System.out.println("---Score to beat: "+scoreToBeat);
			System.out.println("------Your score: "+score);
			System.out.println("---Time limit: "+timeLimit);
			System.out.println("------Your time: "+runTime);

			if(printFailingSolutions && (!betterScore || (!validatePaths(dict, board, sol) && checkValid)))
			{
				System.out.println("---Failing solution: ");
				for(Path path : sol.getPaths())
				{
					for(Point point : path.getPoints())
						System.out.print("("+point.row+","+point.col+"),");
					System.out.println();
				}
			}

			if(runTime<=timeLimit*timeAllowance)
			{
				if(!checkValid)
					valid=true;
				passing = passing || (valid && betterScore);
			}
		}
		TestCase.assertTrue(passing);
	}

	private boolean validatePaths(Dictionary dict, Grid board, Solution sol)
	{
		int numRows = board.numRows();
		int numCols = board.numCols();
		boolean[][] mark = new boolean[numRows][numCols];
		for (Path path : sol.getPaths()) {
			Point last = null;
			for (Point p : path.getPoints()) {
				// make sure we have not used this point before
				if (mark[p.row][p.col])
					return false;
				mark[p.row][p.col] = true;
				// check that this point is next to the last one
				if (last == null) last = p;
				else if (!last.isAdjacent(p))
					return false;
				last = p;
			}
			if (dict.score(board.getWord(path))==0)
				return false;
		}
		return true;
	}

	
	//Basic functionality tests
	
	public void testPublicGrid1()
	{
		compareScore("public_grid_1.txt", animals, 100, 11, false, true, 3);
	}

	public void testPublicGrid2()
	{
		compareScore("public_grid_2.txt", animals, 100, 5, false, true, 3);
	}
	
	
	//Animal dictionary tests
	public void testPublicGrid30x30Valid()
	{
		//no time limit, non-zero valid solution
		compareScore("public_grid_30x30.txt", animals, MAX_TIME_LIMIT, 1, false, true, 3);
	}
	public void testPublicGrid100x100Timed()
	{
		//valid solution within time limit
		compareScore("public_grid_100x100.txt", animals, 100, 1, false, true, 10);
	}
	public void testPublicGrid100x100Scored()
	{
		//beat the ST Solver score
		compareScore("public_grid_100x100.txt", animals, 200, 1, true, false, 10);
	}
	public void testPublicGrid200x200Score()
	{
		//larger grid
		compareScore("public_grid_200x200.txt", animals, 500, 1, true, false, 5);
	}
	
	
	//Random dictionary tests
	public void testPublicGrid30x30rValid()
	{
		//no time limit, non-zero valid solution
		compareScore("public_grid_30x30r.txt", random, MAX_TIME_LIMIT, 1, false, true, 3);
	}
	public void testPublicGrid100x100rTimed()
	{
		//valid solution within time limit
		compareScore("public_grid_100x100r.txt", random, 100, 1, false, true, 10);
	}
	public void testPublicGrid100x100rScored()
	{
		//beat the ST Solver score
		compareScore("public_grid_100x100r.txt", random, 200, 1, true, false, 10);
	}
	public void testPublicGrid200x200rScored()
	{
		//larger grid
		compareScore("public_grid_200x200r.txt", random, 500, 1, true, false, 5);
	}
	
	
	
	
}
