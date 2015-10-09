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


	public boolean train() {

		int iteration = 1;

		///Loop as long as the members are still changing ///TODO currentMembers and previousMembers seem to be always equal
		while (clusters[0].currentMembers != clusters[0].previousMembers) { ///TODO 0 is a placeholder


			///Update previousMembers for all clusters
			for (int currentCluster = 0; currentCluster < k; currentCluster++) {
					clusters[currentCluster].previousMembers = clusters[currentCluster].currentMembers;
					///clean currentmembers so it can start fresh
					clusters[currentCluster].currentMembers.clear();
			}




			//implement k-means algorithm here:
			// Step 1: Select an initial random partioning with k clusters

			///Select k initial points to become center of the cluster.
			for (int i = 0; i < k; i++) {
				///Get random number between 0 and dim.
				Random r = new Random();
				int R = r.nextInt(trainData.size());
				///Add random person R to cluster i
				clusters[i].currentMembers.add(R);
			}

			///Calculate the prototypes of each cluster for the first time
			if(iteration == 1) {
				for (int i = 0; i < k; i++) { /// go through all clusters
					clusters[i].prototype = calculatePrototype(clusters[i]);
				}
			}

/*
			///DEBUG
			for (int i = 0; i < k; i++) { /// go through all clusters{
				System.out.println("\nOur prototype for cluster[" + i + "] is:");
				for (int j = 0; j < 200; j++) {
					System.out.printf(" " + clusters[i].prototype[j]);
				}
			}
			*/



			// Step 2: Generate a new partition by assigning each datapoint to its closest cluster center ///TODO check if it works with >1 person in each cluster
			///Go through all the people. i is current person.
			for (int i = 0; i < trainData.size(); i++) {
				int closestCenter = -1;
				float shortestDistance = 9999;
				float newDistance = 0;

				///Calculate distances to each initial point of each cluster. There are k clusters. j is current cluster.
				for (int j = 0; j < k; j++) {
					///Calculate distance
					for (int h = 0; h < dim; h++) {
						newDistance = newDistance + (float) Math.pow((trainData.get(i)[h] - clusters[j].prototype[h]), 2);
						///System.out.println("newDistance = "+ newDistance+" at website "+h);
					}

					///System.out.println("newDistance is berekend op "+ newDistance+" voor cluster "+j);

					if (newDistance <= shortestDistance) {
						closestCenter = j;
						shortestDistance = newDistance;
						///System.out.println("Cluster "+closestCenter+" is now the closest with distance "+shortestDistance);
					}
					newDistance = 0; /// clean the variable for the next cluster.
				}
				///In case something went wrong.
				if (closestCenter == -1) {
					System.out.println("\nWARNING! Something went wrong with assigning to clusters: closestCenter == -1\n");
				}
				///Add point to correct cluster
				clusters[closestCenter].currentMembers.add(i);
				//System.out.println("\n");
			}


			// Step 3: recalculate cluster centers
			///Compute new cluster centers as the centroid of the data vectors assigned to the considered cluster. == Take the average of the members and make it the new prototype

			float meanWebsite = 0;
			///Go through all clusters
			for (int currentCluster = 0; currentCluster < k; currentCluster++) {
				///Adjust the prototype for each of the websites. Dim is amount of websites
				for (int currentWebsite = 0; currentWebsite < dim; currentWebsite++) {
					///Take the mean of all members for the currentWebsite of the current cluster
					for (int currentPerson : clusters[currentCluster].currentMembers) {
						///sum all the values for this website
						meanWebsite += trainData.get(currentPerson)[currentWebsite];
					}
					///calculate the actual mean
					meanWebsite /= clusters[currentCluster].currentMembers.size();
					///adjust the prototype to new found mean
					clusters[currentCluster].prototype[currentWebsite] = meanWebsite;
					///reset the variable
					meanWebsite = 0;
				}
			}

/*
			///DEBUG
			for (int i = 0; i < k; i++) { /// go through all clusters{
				System.out.println("\nOur NEW prototype for cluster[" + i + "] is:");
				for (int j = 0; j < 200; j++) {
					System.out.printf(" " + clusters[i].prototype[j]);
				}
			}
			*/



			///DEBUG
			System.out.printf("\n\nCurrentMembers of cluster 0 at iteration "+iteration+" are " + clusters[0].currentMembers);
			System.out.printf("\nPreviousMembers of cluster 0 at iteration "+iteration+" are " + clusters[0].previousMembers);
			System.out.printf("\nCurrentMembers of cluster 1 at iteration "+iteration+" are " + clusters[1].currentMembers);
			System.out.printf("\nPreviousMembers of cluster 1 at iteration "+iteration+" are " + clusters[1].previousMembers);


			///Compute the mean by summing over all cluster members and then dividing by their number.//TODO figure out if this step is redundant

			// Step 4: repeat until clustermembership stabilizes

			iteration++;
		}
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

	public float[] calculatePrototype(Cluster cluster){
		float[] prototype = new float[dim];

		///Get amount of members in the cluster
		int size = cluster.currentMembers.size();

		///System.out.println("Our cluster contains persons " + cluster.currentMembers);

		///Calculate the mean of all members of the cluster
		for(int j = 0; j < dim; j++) { /// go through all 200 elements that each vector contains

			float mean = 0; /// initialize to 0 so we can reuse it

			for (int n:cluster.currentMembers){ /// and go through all of the members of the cluster
				mean = mean + trainData.elementAt(n)[j];
			}
			mean = mean/size; /// Calculate the actual mean
			prototype[j] = mean; /// add it to our prototype
		}
		return prototype;
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