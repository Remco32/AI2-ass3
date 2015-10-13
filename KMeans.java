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
		//TODO remove all debug code
		///int iteration = 1;
		// Step 1: Select an initial random partioning with k clusters

		/*
		///Select k initial points to become center of the cluster.
		for (int i = 0; i < k; i++) {
			///Get random number between 0 and dim.
			Random r = new Random();
			int R = r.nextInt(trainData.size());
			///Add random person R to cluster i
			clusters[i].currentMembers.add(R);
		}
		*/

		///Go through all people and add them to a random cluster
		for (int i = 0; i < trainData.size(); i++) {
			///Get random number between 0 and dim.
			Random r = new Random();
			int R = r.nextInt(k);
			///Add person i to cluster R
			clusters[R].currentMembers.add(i);
		}

		///Calculate the prototypes of each cluster for the first time

		for (int i = 0; i < k; i++) { /// go through all clusters
			clusters[i].prototype = calculatePrototype(clusters[i]);
		}


/*
			///DEBUG
			for (int i = 0; i < k; i++) { /// go through all clusters{
				System.out.println("\nOur prototype for cluster[" + i + "] is:");
				for (int j = 0; j < 200; j++) {
					System.out.printf(" " + clusters[i].prototype[j]);
				}
			}
		System.out.printf("\n\nCurrentMembers of cluster 0 before loop are " + clusters[0].currentMembers);
		System.out.printf("\nPreviousMembers of cluster 0 before loop are " + clusters[0].previousMembers);
		*/

		// Step 4: repeat until clustermembership stabilizes
		///Loop as long as the members are still changing
		while (!clusters[0].previousMembers.equals(clusters[0].currentMembers)) { ///TODO 0 is a placeholder
			///Update previousMembers for all clusters
			for (int currentCluster = 0; currentCluster < k; currentCluster++) {
				///Clean up old members
				clusters[currentCluster].previousMembers.clear();
				///Add all elements from current members to previousMembers
				clusters[currentCluster].previousMembers.addAll(clusters[currentCluster].currentMembers);
				///clean currentMembers so it can start fresh
				clusters[currentCluster].currentMembers.clear();

			}

			// Step 2: Generate a new partition by assigning each datapoint to its closest cluster center
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
					}
					///Check if we found a shorter distance
					if (newDistance <= shortestDistance) {
						closestCenter = j;
						shortestDistance = newDistance;
					}
					newDistance = 0; /// clean the variable for the next cluster.
				}
				///In case something went wrong.
				if (closestCenter == -1) {
					System.out.println("\nWARNING! Something went wrong with assigning to clusters: closestCenter == -1\n");
				}
				///Add point to correct cluster
				clusters[closestCenter].currentMembers.add(i);
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

			///DEBUG
			///System.out.printf("\n\nCurrentMembers of cluster 0 at iteration " + iteration + " are " + clusters[0].currentMembers);
			///System.out.printf("\nPreviousMembers of cluster 0 at iteration " + iteration + " are " + clusters[0].previousMembers);
			///System.out.printf("\nCurrentMembers of cluster 1 at iteration " + iteration + " are " + clusters[1].currentMembers);
			///System.out.printf("\nPreviousMembers of cluster 1 at iteration " + iteration + " are " + clusters[1].previousMembers);


			///iteration++;
		}
		return false;
	}

	public boolean test()
	{
		// iterate along all clients. Assumption: the same clients are in the same order as in the testData

		for (int clientNumber = 0; clientNumber < testData.size(); clientNumber++) {

			int memberOfCluster = -1;
			int correct = 0;
			int incorrect = 0;
			int falsePositive = 0;
			int falseNegative = 0;
			int prefetchRequest = 0;
			int shouldBePrefetched = 0;
			int prefetchedAmount = 0;


			// for each client find the cluster of which it is a member
			for (int currentCluster = 0; currentCluster < k; currentCluster++) { /// go through all clusters
				if(clusters[currentCluster].currentMembers.contains(clientNumber)){
					memberOfCluster = currentCluster;
					break;
				}
			}
			///In case something went wrong.
			if (memberOfCluster == -1) {
				System.out.println("\nWARNING! Something went wrong with finding membership clusters: memberOfCluster == -1\n");
			}

			// get the actual testData (the vector) of this client
			///Get the amount of URLs that should have been prefetched
			for(int i = 0; i < dim; i++){
				shouldBePrefetched +=  testData.get(clientNumber)[i];
			}
			System.out.println("shouldBePrefetched=" + shouldBePrefetched);


			// iterate along all dimensions
			for(int currentPosition = 0; currentPosition < dim ;currentPosition++){
				// and count prefetched htmls
				///If prototype of the cluster is equal or higher than the threshold, it should prefetch
				if(clusters[memberOfCluster].prototype[currentPosition] >= prefetchThreshold){
					prefetchRequest++;
					///check if it should have prefetched
					if(testData.get(clientNumber)[currentPosition] == 1){
						correct++;
					}
					else{
						falsePositive++;
					}
				}
			}

			System.out.println("prefetchRequest=" + prefetchRequest);

			hitrate += prefetchRequest/shouldBePrefetched; /// update hitrate
			accuracy += prefetchRequest/prefetchRequest;

			System.out.println("\n");
		}

		hitrate = hitrate/testData.size(); ///Normalize the value

		// count number of hits
		// count number of requests
		// set the global variables hitrate and accuracy to their appropriate value
		return true;
	}

	public float[] calculatePrototype(Cluster cluster) {
		float[] prototype = new float[dim];

		///Get amount of members in the cluster
		int size = cluster.currentMembers.size();

		///System.out.println("Our cluster contains persons " + cluster.currentMembers);

		///Calculate the mean of all members of the cluster
		for (int j = 0; j < dim; j++) { /// go through all 200 elements that each vector contains

			float mean = 0; /// initialize to 0 so we can reuse it

			for (int n : cluster.currentMembers) { /// and go through all of the members of the cluster
				mean = mean + trainData.elementAt(n)[j];
			}
			mean = mean / size; /// Calculate the actual mean
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