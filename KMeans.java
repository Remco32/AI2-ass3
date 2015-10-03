import java.util.*;

public class KMeans extends ClusteringAlgorithm
{
	// Number of clusters
	private int k;

	// Dimensionality of the vectors.
	private int dim;
	
	// Threshold above which the corresponding html is prefetched
	private double prefetchThreshold;
	
	// Array of k clusters, class cluster is used for easy bookkeeping
	private Cluster[] clusters;
	
	// This class represents the clusters, it contains the prototype (the mean of all it's members)
	// and memberlists with the ID's (which are Integer objects) of the datapoints that are member of that cluster.
	// You also want to remember the previous members so you can check if the clusters are stable.
	static class Cluster
	{
		float[] prototype;

		Set<Integer> currentMembers;
		Set<Integer> previousMembers;
		  
		public Cluster()
		{
			currentMembers = new HashSet<Integer>();
			previousMembers = new HashSet<Integer>();
		}
	}
	// These vectors contains the feature vectors you need; the feature vectors are float arrays.
	// Remember that you have to cast them first, since vectors return objects.
	private Vector<float[]> trainData;
	private Vector<float[]> testData;

	// Results of test()
	private double hitrate;
	private double accuracy;
	
	public KMeans(int k, Vector<float[]> trainData, Vector<float[]> testData, int dim)
	{
		this.k = k;
		this.trainData = trainData;
		this.testData = testData; 
		this.dim = dim;
		prefetchThreshold = 0.5;
		
		// Here k new cluster are initialized
		clusters = new Cluster[k];
		for (int ic = 0; ic < k; ic++)
			clusters[ic] = new Cluster();
	}


	public boolean train()
	{
	 	//implement k-means algorithm here:
		// Step 1: Select an initial random partioning with k clusters

		///Select k initial points to become center of the cluster.
		for(int i = 0; i < k; i++ ){
			///Get random number between 0 and dim.
			Random r = new Random();
			int R = r.nextInt(dim-0);
			///Add random person R to cluster i
			clusters[i].currentMembers.add(R);
		}
		/*
		///Dim is amount of people, go through all the people /// moet volgens mij enkel k punten uitzoeken ipv alles verdelen
		for(int i = 0; i < dim; i++){
			///Get random number between 0 and k.
			Random r = new Random();
			int R = r.nextInt(k-0);
			///assign them to cluster R
			clusters[R].currentMembers.add(i); ///add person i to cluster R
		}
		*/

		// Step 2: Generate a new partition by assigning each datapoint to its closest cluster center
		///Go through all the points. i is current person.
		for(int i = 0; i < dim; i++){
			int closestCenter = -1;
			///Calculate distances to each initial point of each cluster. There are k clusters. j is current cluster.
			for(int j = 0; j < k; j++){
				int distance = -1;
				///Calculate distance ///TODO figure out how to
				/// if (newDistance < distance){
				///		closestCenter = j; }


			}
			///In case something went wrong.
			if (closestCenter == -1){
				System.out.println("\nWARNING! Something went wrong with assigning to clusters: closestCenter == -1");
			}

			///Add point to correct cluster
			clusters[closestCenter].currentMembers.add(i);
		}




		// Step 3: recalculate cluster centers
		// Step 4: repeat until clustermembership stabilizes
		return false;
	}

	public boolean test()
	{
		// iterate along all clients. Assumption: the same clients are in the same order as in the testData
		// for each client find the cluster of which it is a member
		// get the actual testData (the vector) of this client
		// iterate along all dimensions
		// and count prefetched htmls
		// count number of hits
		// count number of requests
		// set the global variables hitrate and accuracy to their appropriate value
		return true;
	}


	// The following members are called by RunClustering, in order to present information to the user
	public void showTest()
	{
		System.out.println("Prefetch threshold=" + this.prefetchThreshold);
		System.out.println("Hitrate: " + this.hitrate);
		System.out.println("Accuracy: " + this.accuracy);
		System.out.println("Hitrate+Accuracy=" + (this.hitrate + this.accuracy));
	}
	
	public void showMembers()
	{
		for (int i = 0; i < k; i++)
			System.out.println("\nMembers cluster["+i+"] :" + clusters[i].currentMembers);
	}
	
	public void showPrototypes()
	{
		for (int ic = 0; ic < k; ic++) {
			System.out.print("\nPrototype cluster["+ic+"] :");
			
			for (int ip = 0; ip < dim; ip++)
				System.out.print(clusters[ic].prototype[ip] + " ");
			
			System.out.println();
		 }
	}

	// With this function you can set the prefetch threshold.
	public void setPrefetchThreshold(double prefetchThreshold)
	{
		this.prefetchThreshold = prefetchThreshold;
	}
}
