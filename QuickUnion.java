package csc402;

public class QuickUnion {
	private int[] id;
	private int [] qu;
	private int components;

	public QuickUnion(int N) {			
		this.id = new int[N];
		this.qu = new int[N];
		for (int i = 0; i < N; i++) {
			this.id[i] = i;
			this.qu[i]= 1;
		}
		this.components = N;
	}
	
	public int getSize(int i) {

		return this.qu[i];
	}

	private int root(int i) {
		while (i != id[i]) {
			id[i] =id[id[i]];
			i=id[i];
		}
		return i;
	}
	
	public boolean connected(int p, int q) {
		return this.root(p) == this.root(q);
	}
	

	public void union(int p, int q) {
		int i = this.root(p);
		int j = this.root(q);
		
		if (i == j) {
			return;
		}

		if (this.qu[i] < this.qu[j]) {
			this.id[i] = j;
			this.qu[j] += this.qu[i];
		} else {
			this.id[j] = i;
			this.qu[i] += this.qu[j];
		}

		this.components--;
	}
	
	public int find(int p) {
		//return this.id[p];
		return this.root(p);
	}
	
	public int count() {
		return this.components;
	}
}

class ConnectedComponent implements Comparable<ConnectedComponent> {
	private int root;
	private int minX;
	private int minY;
	private int maxX;
	private int maxY;
	private double ratio;

	public ConnectedComponent(int root, int minX, int minY, int maxX, int maxY, double ratio) {
		this.root = root;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		this.ratio = ratio;
	}

	public int getRoot() {
		return root;
	}

	@Override
	public int compareTo(ConnectedComponent o) {
		if (this.getSize() != o.getSize()) {
			return Integer.compare(this.getSize(), o.getSize());
		} else if (this.minX != o.minX) {
			return Integer.compare(this.minX, o.minX);
		} else {
			return Integer.compare(this.minY, o.minY);
		}
	}

	public int getSize() {
		return (maxX - minX + 1) * (maxY - minY + 1);
	}

	@Override
	public String toString() {
		return "ConnectedComponent{" +
				"root=" + getRoot()+
				", size=" + getSize() +
				", ratio=" + ratio +
				'}';
	}
}





