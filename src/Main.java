import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class Main {
	static Video videos[];
	static EndPoint endPoints[];
	static Cache caches[];
	static int numOfVideos, numOfEndPoints, numOfRequests, numOfCaches, cacheSize;

	static class Video implements Comparable<Video> {
		int index;
		int size;
		int freq; //
		TreeMap<Integer, Integer> myEndPoints;

		Video(int index, int size) {
			this.index = index;
			this.size = size;
			myEndPoints = new TreeMap<>();
		}

		@Override
		public int compareTo(Video o) {
			return -Integer.compare(freq, o.freq);
		}

		public void addEndPoint(int epId, int numOfR) {
			myEndPoints.put(epId, numOfR);
		}
	}

	static class Cache {
		LinkedList<Edge> connectedEndPoint;
		int index;

		Cache(int i) {
			index = i;
			connectedEndPoint = new LinkedList<>();
		}

	}

	static class Edge {
		int connectedEp;
		int latency;

		Edge(int connectedEp, int latency) {
			this.connectedEp = connectedEp;
			this.latency = latency;
		}
	}

	static class EndPoint {
		int latencyFromDataCenter;
		TreeMap<Integer, Integer> connectedVideos;

		EndPoint(int latencyFromDataCenter) {
			this.latencyFromDataCenter = latencyFromDataCenter;
			this.connectedVideos = new TreeMap<>();
		}

		void addVideo(int vidId, int numOfReq) {
			connectedVideos.put(vidId, numOfReq);
		}
	}

	public static void main(String[] args) throws Exception {
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		String[] l = bf.readLine().split(" ");
		numOfVideos = Integer.parseInt(l[0]);
		numOfEndPoints = Integer.parseInt(l[1]);
		numOfRequests = Integer.parseInt(l[2]);
		numOfCaches = Integer.parseInt(l[3]);
		cacheSize = Integer.parseInt(l[4]);
		endPoints = new EndPoint[numOfEndPoints];
		caches = new Cache[numOfCaches];
		for (int i = 0; i < numOfCaches; i++) {
			caches[i] = new Cache(i);
		}
		l = bf.readLine().split(" ");
		videos = new Video[numOfVideos];
		for (int i = 0; i < l.length; i++) {
			videos[i] = new Video(i, Integer.parseInt(l[i]));
		}
		for (int i = 0; i < numOfEndPoints; i++) {
			l = bf.readLine().split(" ");
			int latencyFromDataCenter = Integer.parseInt(l[0]);
			int nc = Integer.parseInt(l[1]);
			endPoints[i] = new EndPoint(latencyFromDataCenter);
			for (int j = 0; j < nc; j++) {
				l = bf.readLine().split(" ");
				int cacheId = Integer.parseInt(l[0]);
				int latencyFromCache = Integer.parseInt(l[1]);
				caches[cacheId].connectedEndPoint.add(new Edge(i, latencyFromCache));
			}
		}
		for (int i = 0; i < numOfRequests; i++) {
			l = bf.readLine().split(" ");
			int vidId = Integer.parseInt(l[0]);
			int epId = Integer.parseInt(l[1]);
			int numOfR = Integer.parseInt(l[2]);
			videos[vidId].addEndPoint(epId, numOfR);
			videos[vidId].freq += numOfR;
		}
		Arrays.sort(videos);

	}
}
