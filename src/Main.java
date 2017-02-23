import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
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
		LinkedList<Integer> myEndPoints;

		Video(int index, int size) {
			this.index = index;
			this.size = size;
			myEndPoints = new LinkedList<>();
		}

		@Override
		public int compareTo(Video o) {
			if (freq == o.freq) {
				return Integer.compare(size, o.size);
			}
			return -Integer.compare(freq, o.freq);
		}

		public void addEndPoint(int epId) {
			myEndPoints.add(epId);
		}
	}

	static class Cache {
		HashMap<Integer, Integer> connectedEndPoint;
		int index;
		int updatedSize;
		double averageLatency;
		LinkedList<Integer> addedVids;

		Cache(int i) {
			index = i;
			connectedEndPoint = new HashMap<>();
			addedVids = new LinkedList<>();
			updatedSize = cacheSize;
		}

	}

	static class EndPoint {
		int index, latencyFromDataCenter;
		TreeMap<Integer, Integer> connectedVideos;

		EndPoint(int indx, int latencyFromDataCenter) {
			this.index = indx;
			this.latencyFromDataCenter = latencyFromDataCenter;
			this.connectedVideos = new TreeMap<>();
		}

		void addVideo(int vidId, int numOfReq) {
			connectedVideos.put(vidId, numOfReq);
		}
	}

	public static void main(String[] args) throws Exception {
		// BufferedReader bf = new BufferedReader(new
		// InputStreamReader(System.in));
		String filename = "kittens";
		//String filename = "videos_worth_spreading";
		//String filename = "trending_today";
		//String filename = "me_at_the_zoo";

		BufferedReader bf = new BufferedReader(new FileReader(filename+".in"));
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
			endPoints[i] = new EndPoint(i, latencyFromDataCenter);
			for (int j = 0; j < nc; j++) {
				l = bf.readLine().split(" ");
				int cacheId = Integer.parseInt(l[0]);
				int latencyFromCache = Integer.parseInt(l[1]);
				caches[cacheId].connectedEndPoint.put(endPoints[i].index, latencyFromCache);
			}
		}
		for (int i = 0; i < numOfRequests; i++) {
			l = bf.readLine().split(" ");
			int vidId = Integer.parseInt(l[0]);
			int epId = Integer.parseInt(l[1]);
			int numOfR = Integer.parseInt(l[2]);
			videos[vidId].addEndPoint(epId);
			videos[vidId].freq += numOfR;
			endPoints[epId].connectedVideos.put(vidId, numOfR);
		}
		int usedCaches = 0;
		Arrays.sort(videos);
		for (int i = 0; i < videos.length; i++) {
			int bestCacheIndex = -1;
			int videoIndex = videos[i].index;
			double bestAverage = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < caches.length; j++) {
				if (caches[j].updatedSize < videos[i].size) {
					continue;
				}
				double averageLatency = calculatedAverageLatency(videos[i].index, caches[j],
						videos[i].myEndPoints);
				if (averageLatency > bestAverage) {
					bestAverage = averageLatency;
					bestCacheIndex = j;
				}
			}
			if (bestCacheIndex != -1) {
				if (caches[bestCacheIndex].addedVids.size() == 0) {
					usedCaches++;
				}
				caches[bestCacheIndex].addedVids.add(videoIndex);
				caches[bestCacheIndex].updatedSize -= videos[i].size;

			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append(usedCaches + "\n");
		for (Cache c : caches) {
			if (c.addedVids.size() == 0)
				continue;
			sb.append(c.index);
			for (int vidIndex : c.addedVids) {
				sb.append(" " + vidIndex);
			}
			sb.append("\n");
		}
		PrintWriter pw = new PrintWriter(new File(filename+".out"));
		pw.print(sb.toString());
		pw.flush();
		pw.close();
	}

	private static double calculatedAverageLatency(int vidInd, Cache cache, LinkedList<Integer> myEndPoints) {
		double average = 0, totalRequests = 0;
		double requests, cacheLatency, saved;
		EndPoint tmp;
		for (int endPointInd : myEndPoints) {
			tmp = endPoints[endPointInd];
			requests = tmp.connectedVideos.get(vidInd);
			// TODO check
			if (!cache.connectedEndPoint.containsKey(tmp.index))
				continue;
			cacheLatency = cache.connectedEndPoint.get(tmp.index);
			saved = tmp.latencyFromDataCenter - cacheLatency;
			average += (saved * requests);
			totalRequests += requests;
		}
		return average / totalRequests;
	}
}
