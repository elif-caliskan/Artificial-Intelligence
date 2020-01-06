//Elif �al??kan 2016400183
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

//a node consists of their children, specific position on matrix, cost of being on that node and cost of that step and also the path of that node
class Node{
	Integer[] position;
	int costUntil;
	Node left = null;
	Node right = null;
	Node up = null;
	Node down = null;
	String parentPath = "";
	int moveCost;
	public Node(Integer[] position, int cost, String parentPath, int moveCost) {
		this.position = position;
		this.costUntil = cost;
		this.parentPath = parentPath;
		this.moveCost = moveCost;
	}
}
//this is used in uniform cost search
class CostComparator implements Comparator<Node>{ 
    
    // Overriding compare()method of Comparator  
    public int compare(Node s1, Node s2) { 
        if (s1.costUntil < s2.costUntil) 
            return -1; 
        else if (s1.costUntil > s2.costUntil) 
            return 1; 
        return 0; 
    } 
} 

//this is used in greedy search
//the heuristic is: find the node with least sum of distance to goal from that node and cost of the step
class DistComparator implements Comparator<Node>{ 
    
    // Overriding compare()method of Comparator  
    public int compare(Node s1, Node s2) { 
        if ( Math.abs(distanceToGoal(s1)) + s1.costUntil  < Math.abs(distanceToGoal(s2)) + s2.costUntil) 
            return -1; 
        else if ( Math.abs(distanceToGoal(s1)) + s1.costUntil >Math.abs(distanceToGoal(s1)) + s2.costUntil)
            return 1; 
        return 0; 
    }
    public double distanceToGoal(Node node) {
    	double dist;
    	if(node.position[2] == -1)
    		dist = node.position[0]-Main.goal[0]+ node.position[1]-Main.goal[1];
    	else {
    		
    		dist = Math.min(node.position[0]-Main.goal[0]+ node.position[1]-Main.goal[1],
    				node.position[2]-Main.goal[0]+ node.position[3]-Main.goal[1]);
    	}
    	return dist;
    }
    
} 
//this is used in a* search
//the heuristic is: find the node with least sum of distance to goal from that node and cost of arriving to that step from the beginning
class AsComparator implements Comparator<Node>{ 
    
    // Overriding compare()method of Comparator  
    public int compare(Node s1, Node s2) { 
        if (Math.abs(distanceToGoal(s1)) + s1.costUntil < Math.abs(distanceToGoal(s2))+ s2.costUntil) 
            return -1; 
        else if (Math.abs(distanceToGoal(s1))+ s1.costUntil > Math.abs(distanceToGoal(s2))+ s2.costUntil)
            return 1; 
        return 0; 
    }
    public double distanceToGoal(Node node) {
    	double dist;
    	if(node.position[2] == -1)
    		dist = node.position[0]-Main.goal[0]+ node.position[1]-Main.goal[1];
    	else {
    		/*int row = (node.position[0] + node.position[2])/2;
    		int col = (node.position[1] + node.position[3])/2;
    		dist = row-Main.goal[0]+ col-Main.goal[1];*/
    		dist = Math.min(node.position[0]-Main.goal[0]+ node.position[1]-Main.goal[1],
    				node.position[2]-Main.goal[0]+ node.position[3]-Main.goal[1]);
    	}
    	return dist;
    }
} 
public class Main {
	static int matrix[][];
	static Integer position[];
	static int goal[];
	static int row;
	static int col;
	public static void main(String[] args){ 
		String path = args[0];
		String mode = args[1];
		File file = new File(path);
    	try {
    		BufferedReader sc = new BufferedReader(new FileReader(file)); 
    		String lineNum = sc.readLine();
    		Scanner scan = new Scanner(lineNum);
    		col = scan.nextInt();
    		row = scan.nextInt();
			goal = new int[2];
			position = new Integer[4];
			matrix = new int[row][col]; //0 for available -1 for unavailable 2 for goal
			
			for(int i = 0; i < row; i++) {
				String line = sc.readLine();
				int size = line.length();
				for(int a = 0;a < col - size; a++)
					line +=" ";
				for(int j = 0; j < col; j++) {
					char ch = line.charAt(j);
					if(ch == 'o') {
						matrix[i][j] = 0;
					}
					else if(ch == ' ') {
						matrix[i][j] = -1;
					}
					else if(ch == 'g') {
						matrix[i][j] = 2;
						goal[0] = i;
						goal[1] = j;
					}
					else if(ch == 's') {
						matrix[i][j] = 0;
						position[0] = i;
						position[1] = j;
						position[2] = -1;
						position[3] = -1;
					}
				}
			}
			if(mode.equalsIgnoreCase("dfs")) {
				dfs();
			}
			else if(mode.equalsIgnoreCase("bfs")) {
				bfs();
			}
			else if(mode.equalsIgnoreCase("ucs")) {
				uniform();
			}
			else if(mode.equalsIgnoreCase("gs")) {
				greedy();
			}
			else if(mode.equalsIgnoreCase("as")) {
				as();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	//in bfs, the positions are held in hashset and queue is used for traversing the graph
	//parentSet is used for finding expanded nodes (if there is a node, its path until that node was expanded)
	//first start node is added to queue, then for each node that was polled, its children are added to queue if they weren't traversed
	private static void bfs() {
		int depthTree = 0;
		Node start = new Node(position, 0, "",0);
		HashSet<List<Integer>> positionSet = new HashSet<List<Integer>>();
		HashSet<String> parentSet = new HashSet<String>();
		LinkedList<Node> queue = new LinkedList<Node>(); 
		queue.add(start);
		while(queue.size()>0 && !goalAchieved(queue.peek())) {
			Node node = queue.poll();
			depthTree = Math.max(depthTree, node.parentPath.length());
			positionSet.add(Arrays.asList(node.position));
			String path = node.parentPath;
			
			node.left = moveLeft(node.position, node.costUntil, node.parentPath);
			node.up = moveUp(node.position, node.costUntil, node.parentPath);
			node.right = moveRight(node.position, node.costUntil, node.parentPath);
			node.down = moveDown(node.position, node.costUntil, node.parentPath);
			int add = 0;
			if(node.left!= null&& !positionSet.contains(Arrays.asList(node.left.position))) {
				queue.add(node.left);
				add = 1;
			}
			if(node.up!= null&& !positionSet.contains(Arrays.asList(node.up.position))) {
				queue.add(node.up);
				add = 1;
			}
			if(node.right!= null&& !positionSet.contains(Arrays.asList(node.right.position))) {
				queue.add(node.right);
				add = 1;

			}
			if(node.down!= null&& !positionSet.contains(Arrays.asList(node.down.position))) {
				queue.add(node.down);				
				add = 1;

			}
			if(add > 0 && path.length()>1) {
				parentSet.add(path.substring(0, path.length()-1));
			}
		}
		if(goalAchieved(queue.peek())){
			depthTree = Math.max(depthTree, queue.peek().parentPath.length());
			System.out.println(queue.peek().costUntil+ " "+ parentSet.size() + " "+ depthTree+ 
					" "+queue.peek().parentPath.length());
			System.out.println(queue.peek().parentPath);
		}
	}
	//in dfs, the positions are held in hashset and stack is used for traversing the graph
	//first start node is added to stack, then for each node that was popped, its children are pushed to stack if they weren't traversed
	private static void dfs() {
		int depthTree = 0;
		int nodeCount = 0;
		HashSet<List<Integer>> positionSet = new HashSet<List<Integer>>();
		Node start = new Node(position, 0, "", 0);
		Stack<Node> stack = new Stack<Node>(); 
		stack.push(start);
		while(stack.size()>0 && !goalAchieved(stack.peek())) {
			Node node = stack.pop();
			depthTree = Math.max(depthTree, node.parentPath.length());
			positionSet.add(Arrays.asList(node.position));
			
			node.left = moveLeft(node.position, node.costUntil, node.parentPath);
			node.up = moveUp(node.position, node.costUntil, node.parentPath);
			node.right = moveRight(node.position, node.costUntil, node.parentPath);
			node.down = moveDown(node.position, node.costUntil, node.parentPath);
			
			if(node.down!= null && !positionSet.contains(Arrays.asList(node.down.position))) {
				stack.push(node.down);
			}
			if(node.right!= null && !positionSet.contains(Arrays.asList(node.right.position))) {
				stack.push(node.right);
			}
			
			if(node.up!= null && !positionSet.contains(Arrays.asList(node.up.position))) {
				stack.push(node.up);
			}
			if(node.left!= null && !positionSet.contains(Arrays.asList(node.left.position))) {
				stack.push(node.left);
			}
			nodeCount++;
		}
		if(goalAchieved(stack.peek())){
			depthTree = Math.max(depthTree, stack.peek().parentPath.length());
			System.out.println(stack.peek().costUntil+ " "+ nodeCount + " "+ depthTree+ 
					" "+stack.peek().parentPath.length());
			System.out.println(stack.peek().parentPath);
		}
		
	}
	//in uniform-cost search, the positions are held in hashset and priority queue is used for traversing the graph
	//parentSet is used for finding expanded nodes (if there is a node, its path until that node was expanded)
	//first start node is added to queue, then for each node that was polled, its children are added to queue if they weren't traversed
	//every node that wasn't traversed, still stays in the queue and search continues with the least expensive node
	//CostComparator is used in order to create a min heap
	private static void uniform() {
		int depthTree = 0;
		HashSet<String> parentSet = new HashSet<String>();
		HashSet<List<Integer>> positionSet = new HashSet<List<Integer>>();
		PriorityQueue<Node> pq = new PriorityQueue<Node>(100, new CostComparator()); 
		Node start = new Node(position, 0, "", 0);
		pq.add(start);
		while(pq.size()>0 && !goalAchieved(pq.peek())) {
			Node node = pq.poll();
			
			depthTree = Math.max(depthTree, node.parentPath.length());
			positionSet.add(Arrays.asList(node.position));
			
			node.left = moveLeft(node.position, node.costUntil, node.parentPath);
			node.up = moveUp(node.position, node.costUntil, node.parentPath);
			node.right = moveRight(node.position, node.costUntil, node.parentPath);
			node.down = moveDown(node.position, node.costUntil, node.parentPath);
						
			int add = 0;
			if(node.left!= null && !positionSet.contains(Arrays.asList(node.left.position))) {
				pq.add(node.left);
				add = 1;
			}
			if(node.up!= null && !positionSet.contains(Arrays.asList(node.up.position))) {
				pq.add(node.up);
				add = 1;
			}
			if(node.right!= null && !positionSet.contains(Arrays.asList(node.right.position))) {
				pq.add(node.right);
				add = 1;
			}
			if(node.down!= null && !positionSet.contains(Arrays.asList(node.down.position))) {
				pq.add(node.down);
				add = 1;
			}
			String path = node.parentPath;
			if(add > 0) {
				parentSet.add(path.substring(0, path.length()));
			}
		}
		if(goalAchieved(pq.peek())){
			depthTree = Math.max(depthTree, pq.peek().parentPath.length());
			System.out.println(pq.peek().costUntil+ " "+ parentSet.size() + " "+ depthTree+ 
					" "+pq.peek().parentPath.length());
			System.out.println(pq.peek().parentPath);
		}
		
	}
	//in greedy search priority queue is used for traversing the graph(it has at most 4 nodes since it is greedy and we try to find the best local solution)
	//first start node is added to priority queue, then for each node that was polled, the queue is cleared and its children are added to queue since 
	//the aim is finding the best local solution
	private static void greedy() {
		int depthTree = 0;
		PriorityQueue<Node> pq = new PriorityQueue<Node>(4, new DistComparator()); 
		Node start = new Node(position, 0, "", 0);
		pq.add(start);
		int nodeCount =0;
		while(pq.size()>0 && !goalAchieved(pq.peek())) {
			Node node = pq.poll();
			pq.clear();
			nodeCount++;
			depthTree = Math.max(depthTree, node.parentPath.length());
			node.left = moveLeft(node.position, node.costUntil, node.parentPath);
			node.up = moveUp(node.position, node.costUntil, node.parentPath);
			node.right = moveRight(node.position, node.costUntil, node.parentPath);
			node.down = moveDown(node.position, node.costUntil, node.parentPath);
			
			if(node.left!= null) {
				pq.add(node.left);
			}
			if(node.up!= null) {
				pq.add(node.up);
			}
			if(node.right!= null) {
				pq.add(node.right);
			}
			if(node.down!= null) {
				pq.add(node.down);
			}
		}
		if(goalAchieved(pq.peek())){
			depthTree = Math.max(depthTree, pq.peek().parentPath.length());
			System.out.println(pq.peek().costUntil+ " "+ nodeCount + " "+ depthTree+ 
					" "+pq.peek().parentPath.length());
			System.out.println(pq.peek().parentPath);
		}
	}
	//in a* search priority queue is used for traversing the graph(it has at most 4 nodes since it is greedy and we try to find the best local solution)
	//first start node is added to priority queue, then for each node that was polled, the queue is cleared and its children are added to queue since 
	//the aim is finding the best local solution
	private static void as() {
		int depthTree = 0;
		PriorityQueue<Node> pq = new PriorityQueue<Node>(4, new AsComparator()); 
		Node start = new Node(position, 0, "", 0);
		int nodeCount = 0;
		pq.add(start);
		while(pq.size()>0 && !goalAchieved(pq.peek())) {
			Node node = pq.poll();
			pq.clear();
			depthTree = Math.max(depthTree, node.parentPath.length());
			node.left = moveLeft(node.position, node.costUntil, node.parentPath);
			node.up = moveUp(node.position, node.costUntil, node.parentPath);
			node.right = moveRight(node.position, node.costUntil, node.parentPath);
			node.down = moveDown(node.position, node.costUntil, node.parentPath);
			
			nodeCount++;
			if(node.left!= null) {
				pq.add(node.left);
			}
			if(node.up!= null) {
				pq.add(node.up);
			}
			if(node.right!= null) {
				pq.add(node.right);
			}
			if(node.down!= null) {
				pq.add(node.down);
			}
		}
		if(goalAchieved(pq.peek())){
			depthTree = Math.max(depthTree, pq.peek().parentPath.length());
			System.out.println(pq.peek().costUntil+ " "+ nodeCount + " "+ depthTree+ 
					" "+pq.peek().parentPath.length());
			System.out.println(pq.peek().parentPath);
		}
		
	}
	//based on the previous position, cost and path, it creates a new node by moving left
	private static Node moveLeft(Integer[] position, int parentCost, String path) {
		Integer[] pos = new Integer[4];
		if(position[2] == -1) { //single agent
			if(position[1] > 1 && matrix[position[0]][position[1]-1] != -1 && matrix[position[0]][position[1]-2] != -1) {
				setPosition(pos, position[0], position[1]-2, position[0], position[1]-1);
				return new Node(pos, 1+ parentCost, path+"L", 1);
			}
		}
		else {
			//horizontal
			if(position[0] == position[2]) {
				if(position[1] > 0 && matrix[position[0]][position[1]-1] != -1) {
					setPosition(pos, position[0], position[1] -1, -1, -1);
					return new Node(pos, 3+ parentCost, path+"L", 3);

				}
			}
			//vertical
			if(position[1] == position[3]) {
				if(position[1] > 0 && matrix[position[0]][position[1]-1] != -1 && matrix[position[2]][position[3]-1] != -1) {
					setPosition(pos, position[0], position[1] -1, position[2], position[3] -1);
					return new Node(pos, 1+ parentCost, path+"L", 1);
				}
			}
		}
		return null;
	}
	//based on the previous position, cost and path, it creates a new node by moving up
	private static Node moveUp(Integer[] position, int parentCost, String path) {
		Integer[] pos = new Integer[4];
		if(position[2] == -1) { //single agent
			if(position[0] > 1 && matrix[position[0]-1][position[1]] != -1 && matrix[position[0]-2][position[1]] != -1) {
				setPosition(pos, position[0] -2, position[1], position[0] -1, position[1]);
				return new Node(pos, 1+ parentCost, path+"U", 1);
			}
		}
		else {
			//horizontal
			if(position[0] == position[2]) {
				if(position[0] > 0 && matrix[position[0]-1][position[1]] != -1 && matrix[position[2]-1][position[3]] != -1) {
					setPosition(pos, position[0] -1, position[1], position[2] -1, position[3]);
					return new Node(pos, 1+ parentCost, path+"U", 1);
				}
			}
			//vertical
			if(position[1] == position[3]) { //position 1 yukarıda olan
				if(position[0] > 0 && matrix[position[0]-1][position[1]] != -1 ) {
					setPosition(pos, position[0] -1, position[1], -1,-1);
					return new Node(pos, 3+ parentCost, path+"U", 3);

				}
			}
		}
		return null;
		
	}
	private static Node moveRight(Integer[] position, int parentCost, String path) {
		Integer[] pos = new Integer[4];
		if(position[2] == -1) { //single agent
			if(position[1] < col - 2 && matrix[position[0]][position[1] + 1] != -1 && matrix[position[0]][position[1] + 2] != -1) {
				setPosition(pos, position[0], position[1]+1, position[0], position[1]+2);
				return new Node(pos, 1+ parentCost, path+"R", 1);

			}
		}
		else {
			//horizontal
			if(position[0] == position[2]) {
				if(position[3] < col - 1 && matrix[position[2]][position[3]+1] != -1) {
					setPosition(pos, position[2], position[3]+1, -1, -1);
					return new Node(pos, 3+ parentCost, path+"R", 3);

				}
			}
			//vertical
			if(position[1] == position[3]) {
				if(position[1] < col - 1 && matrix[position[0]][position[1]+1] != -1 && matrix[position[2]][position[3]+1] != -1) {
					setPosition(pos, position[0], position[1]+1, position[2], position[3]+1);
					return new Node(pos, 1+ parentCost, path+"R", 1);
				}
			}
		}
		return null;
		
	}
	private static Node moveDown(Integer[] position, int parentCost, String path) {
		Integer[] pos = new Integer[4];
		if(position[2] == -1) { //single agent
			if(position[0] < row -2 && matrix[position[0]+1][position[1]] != -1 && matrix[position[0]+2][position[1]] != -1) {
				setPosition(pos, position[0]+1, position[1], position[0]+2, position[1]);
				return new Node(pos, 1+ parentCost, path+"D", 1);
			}
		}
		else {
			//horizontal
			if(position[0] == position[2]) {
				if(position[0] < row -1 && matrix[position[0]+1][position[1]] != -1 && matrix[position[2]+1][position[3]] != -1) {
					setPosition(pos, position[0]+1, position[1], position[2]+1, position[3]);
					return new Node(pos, 1+ parentCost, path+"D", 1);
				}
			}
			//vertical
			if(position[1] == position[3]) { //position 1 yukarıda olan
				if(position[2] < row -1 && matrix[position[2]+1][position[3]] != -1 ) {
					setPosition(pos, position[2]+1, position[3], -1,-1);
					return new Node(pos, 3+ parentCost, path+"D", 3);
				}
			}
		}
		return null;
	}
	//changes the position array
	private static void setPosition(Integer[] pos, int row1, int col1, int row2, int col2) {
		pos[0] = row1;
		pos[1] = col1;
		pos[2] = row2;
		pos[3] = col2;
		
	}
	//checks if goal is achieved
	private static boolean goalAchieved(Node node) {
		return node.position[0] == goal[0] && node.position[1] == goal[1] && node.position[2] == -1 && node.position[3] == -1;
		
	}
}

