package csc402;

/* 
 * CSC 402 - Mini Project 2
 * 
 * Created by: Kenny Davila
 * 
 * Completed by: Akintunde Eyisanmi
 */


import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;


public class MP2_Main {
	/*
	 * DO NOT MODIFY UNLESS YOU WANT 0 
	 * 
	 * This function loads a binary image from the specified file name, and 
	 * returns a boolean array representing the image 
	 * */
	public static boolean[][] loadImage(String filename){
		File f = new File(filename);
		try {
			BufferedImage img_buff = ImageIO.read(f);
			Raster raster = img_buff.getData();
			
			int h = img_buff.getHeight();
			int w = img_buff.getWidth();
			int[] pixel = new int[3];
			boolean[][] img_bool = new boolean[h][w];
			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					raster.getPixel(x, y, pixel);
					img_bool[y][x] = pixel[0] > 128;					 
				}
			}
			
			return img_bool;
		} catch (Exception e) {
			System.out.println("Invalid image file");
			return null;
		}		
	}
	
	/*
	 * This function helps you "visualize" a given image as text
	 *
	 * You can modify this to your convenience
	 * */
	public static String boolImgToString(boolean[][] img_bool) {
		StringBuffer buffer = new StringBuffer();
		for (int y = 0; y < img_bool.length; y++) {
			for (int x = 0; x < img_bool[y].length; x++) {
				buffer.append(img_bool[y][x] ? "#" : "-");
			}
			buffer.append("\n");
		}
		
		return buffer.toString();
	}
	public static QuickUnion createUnion(boolean[][]img){
		int height = img.length;
		int width = img[0].length;
		int totalPixels = height * width;
		QuickUnion unionFind = new QuickUnion(totalPixels);


		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixelIndex = y * width + x;
				if (img[y][x]) {
					if (x > 0 && img[y][x - 1]) {
						unionFind.union(pixelIndex, pixelIndex - 1);
					}
					if (x < width - 1 && img[y][x + 1]) {
						unionFind.union(pixelIndex, pixelIndex + 1);
					}
					if (y > 0 && img[y - 1][x]) {
						unionFind.union(pixelIndex, pixelIndex - width);
					}
					if (y < height - 1 && img[y + 1][x]) {
						unionFind.union(pixelIndex, pixelIndex + width);
					}
				}
			}
		}
		return unionFind;
	}

	public static void seperateShapes(boolean[][] img, QuickUnion unionFind,List<ConnectedComponent> rectangles, List<ConnectedComponent> triangles){
		int height = img.length;
		int width = img[0].length;
		boolean[][] visited = new boolean[height][width];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (img[y][x] && !visited[y][x]) {
					int root = unionFind.find(y * width + x);
					int minX = width;
					int minY = height;
					int maxX = -1;
					int maxY = -1;

					Queue<Integer> queue = new LinkedList<>();
					queue.add(y * width + x);
					visited[y][x] = true;

					while (!queue.isEmpty()) {
						int pixelIndex = queue.poll();
						int currY = pixelIndex / width;
						int currX = pixelIndex % width;

						minX = Math.min(minX, currX);
						minY = Math.min(minY, currY);
						maxX = Math.max(maxX, currX);
						maxY = Math.max(maxY, currY);

						if (currX > 0 && img[currY][currX - 1] && !visited[currY][currX - 1]) {
							unionFind.union(pixelIndex, pixelIndex - 1);
							queue.add(pixelIndex - 1);
							visited[currY][currX - 1] = true;
						}
						if (currX < width - 1 && img[currY][currX + 1] && !visited[currY][currX + 1]) {
							unionFind.union(pixelIndex, pixelIndex + 1);
							queue.add(pixelIndex + 1);
							visited[currY][currX + 1] = true;
						}
						if (currY > 0 && img[currY - 1][currX] && !visited[currY - 1][currX]) {
							unionFind.union(pixelIndex, pixelIndex - width);
							queue.add(pixelIndex - width);
							visited[currY - 1][currX] = true;
						}
						if (currY < height - 1 && img[currY + 1][currX] && !visited[currY + 1][currX]) {
							unionFind.union(pixelIndex, pixelIndex + width);
							queue.add(pixelIndex + width);
							visited[currY + 1][currX] = true;
						}
					}

					int area = unionFind.getSize(root);
					int boundingBoxArea = (maxX - minX + 1) * (maxY - minY + 1);
					double ratio = (double) area / boundingBoxArea;
					ConnectedComponent component = new ConnectedComponent(root, minX, minY, maxX, maxY, ratio);
					if (ratio > 0.75) {
						rectangles.add(component);
					} else {
						triangles.add(component);
					}
				}
			}
		}

	}



	public static void sortShapes(List<ConnectedComponent> shapes){
		Collections.sort(shapes);
	}

	public static void analyzeoneImage(Scanner scanner){
		System.out.print("Enter the filename of the image to analyze: ");
		String filename = scanner.next();

		boolean[][] img = loadImage(filename);
		if (img == null) {
			System.out.println("Could not load the input image");
			return;
		}
		if (img.length <= 100) {
			System.out.println(boolImgToString(img));
		}

		long start =System.nanoTime();

		QuickUnion unionFind = createUnion(img);


		List<ConnectedComponent> rectangles = new ArrayList<>();
		List<ConnectedComponent> triangles = new ArrayList<>();
		seperateShapes(img, unionFind, rectangles, triangles);


		sortShapes(rectangles);
		sortShapes(triangles);
		long finish = System.nanoTime();

		System.out.println("Rectangles:");
		for (ConnectedComponent rectangle : rectangles) {
			System.out.println(rectangle);
		}

		System.out.println("Triangles:");
		for (ConnectedComponent triangle : triangles) {
			System.out.println(triangle);
		}
		long total = finish -start;
		System.out.println(total +" nanoseconds");
	}
	public static void performDataCollection(Scanner scanner){
		System.out.println("Enter directory path: ");
		String path = scanner.next();

		File directory = new File(path);
		if(!directory.exists() || !directory.isDirectory()){
			System.out.println("Invalid Directory, try again");
			return;
		}

		File[] imageFiles = directory.listFiles();
		if (imageFiles == null || imageFiles.length==0) {
			System.out.println("No images found in the directory.");
			return;
		}

		System.out.println("Data Collection:");
		System.out.println("Filename\tAverage Time (ms)\tCC Count\tTotal Pixels");
		for (File file : imageFiles) {
			if (file.isFile()) {
				String filename = file.getName();
				boolean[][] img = loadImage(file.getPath());
				if (img != null) {
					long totalTime = 0;
					int ccCount = 0;
					long totalPixels = img.length * img[0].length;
					for (int i = 0; i < 5; i++) {
						long startTime = System.nanoTime();
						QuickUnion unionFind = createUnion(img);
						totalTime += System.nanoTime() - startTime;
						List<ConnectedComponent> rectangles = new  ArrayList<>();
						List<ConnectedComponent> triangles = new ArrayList<>();
						seperateShapes(img, unionFind, rectangles, triangles);
						ccCount = rectangles.size() + triangles.size();
					}
					long avgTime = totalTime / 5;
					System.out.printf("%s\t%d\t%d\t%d%n", filename, avgTime, ccCount, totalPixels);
				} else {
					System.out.println("Invalid image file: " + filename);
				}
			}
		}
	}



	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
		while (true){
			System.out.println("Menu: ");
			System.out.println("Enter 1 to analyze just one image.");
			System.out.println("Enter 2 for Data collection.");
			System.out.println("Enter 3 to exit");

			System.out.println("Enter your choice: ");
			int input = scanner.nextInt();

			switch (input){
				case 1:
					analyzeoneImage(scanner);
					break;
				case 2:
					long timer = System.nanoTime();
					performDataCollection(scanner);
					long timeout =System.nanoTime();
					long finale = timeout-timer;
					System.out.println(finale + " nanoseconds");
					break;
				case 3:
					System.out.println("Done");
					return;
				default:
					System.out.println("Invalid, try again.");
			}


		}

	}

}
