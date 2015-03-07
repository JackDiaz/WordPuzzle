package cmsc433.p3;

import java.util.Comparator;

public class PathComparator implements Comparator<Path> {
	Dictionary dict;
	public PathComparator(Dictionary d){
		dict = d;
	}
	public int compare(Path path1, Path path2) {
		int p1 = dict.getScore(path1.getPoints().size());
		int p2 = dict.getScore(path2.getPoints().size());
		if(p1 == p2){
			return 0;
		}else if (p1 > p2){
			return 1;
		}else if (p1 < p2){
			return -1;
		}
		return 0;
	}
}
