import java.util.*;

public class Kohonen extends ClusteringAlgorithm
{
	// Size of clustersmap
	private int n;

	// Number of epochs
	private int epochs;
	
	// Dimensionality of the vectors
	private int dim;
	
	// Threshold above which the corresponding html is prefetched
	private double prefetchThreshold;

	private double initialLearningRate; 
	
	// This class represents the clusters, it contains the prototype (the mean of all it's members)
	// and a memberlist with the ID's (Integer objects) of the datapoints that are member of that cluster.  
	private Cluster[][] clusters;

	// Vector which contains the train/test data
	private Vector<float[]> trainData;
	private Vector<float[]> testData;
	
	// Results of test()
	private double hitrate;
	private double accuracy;
	
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
	
	public Kohonen(int n, int epochs, Vector<float[]> trainData, Vector<float[]> testData, int dim)
	{
		this.n = n;
		this.epochs = epochs;
		prefetchThreshold = 0.5;
		initialLearningRate = 0.8;
		this.trainData = trainData;
		this.testData = testData; 
		this.dim = dim;       
		
		Random rnd = new Random();
		


		// Here n*n new cluster are initialized
		clusters = new Cluster[n][n];
		for (int i = 0; i < n; i++)  {
			for (int i2 = 0; i2 < n; i2++) {
				clusters[i][i2] = new Cluster();
				clusters[i][i2].prototype = new float[dim];
				int R = rnd.nextInt(trainData.size());
				
				//cluster centers are initialized as random
				clusters[i][i2].currentMembers.add(R);
				clusters[i][i2].prototype = calculatePrototype(clusters[i][i2]);
			}
		}
	}

	
	public boolean train()
	{
        
		for(int k =0; k<epochs; k++){
			//calculate new neighborehoodsize and learning rate
		    int rad = (n/2) * ( 1 - (k/epochs));
		    double learnr= initialLearningRate * (1 - (k/epochs));
		    
            	// Step 2: get the BMU which is the closest cluster center of the datapoint and adjust its prototype to the formula. 
    			///Go through all the people. i is current person.
    			for (int i = 0; i < trainData.size(); i++) {
    				int closestCenter = -1;
    				int closestCenter2 = -1;
    				float shortestDistance = 9999;
    				float newDistance = 0;

    				///Calculate distances to each initial point of each cluster. There are n*n clusters. j is current cluster.
    				for (int j = 0; j < n; j++) {
    					for (int j2 = 0; j2 < n; j2++) {
    					///Calculate distance
    					for (int h = 0; h < dim; h++) {
    						newDistance = newDistance + (float) Math.pow((trainData.get(i)[h] - clusters[j][j2].prototype[h]), 2);
    						///System.out.println("newDistance = "+ newDistance+" at website "+h);
    					}

    					///System.out.println("newDistance is berekend op "+ newDistance+" voor cluster "+j);

    					if (newDistance <= shortestDistance) {
    						closestCenter = j;
    						closestCenter2 = j2;
    						shortestDistance = newDistance;
    						///System.out.println("Cluster "+closestCenter+" is now the closest with distance "+shortestDistance);
    					}
    					newDistance = 0; /// clean the variable for the next cluster.
    				 }
    				}	
    				///In case something went wrong.
    				if (closestCenter == -1) {
    					System.out.println("\nWARNING! Something went wrong with assigning to clusters: closestCenter == -1\n");
    				}
    				///find all clusters in neighborehood and adjust prototype of them
    				for (int r = (closestCenter- rad); r < (closestCenter +rad); r++ ){
    					for (int s = (closestCenter2- rad); s < (closestCenter2 +rad); s++ ){
    				if((r != closestCenter && s!= closestCenter2) &&(r>=0 )&& (s>=0) && (r<n) && (s<n)){
    					for (int h = 0; h < dim; h++) {
    				//clusters[r][s].prototype[h] = (float)((1- learnr) * clusters[r][s].prototype[h]) + (float)(learnr * trainData.get(i)[h]);
    		         		}
    				      }
    					}
    				}
    			}
		//print out progress 
		System.out.println("progress: " + k + "/" + epochs);
		}
		
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
	
	public boolean test()
	{
		// iterate along all clients
		// for each client find the cluster of which it is a member
		// get the actual testData (the vector) of this client
		// iterate along all dimensions
		// and count prefetched htmls
		// count number of hits
		// count number of requests
		// set the global variables hitrate and accuracy to their appropriate value
		return true;
	}


	public void showTest()
	{
		System.out.println("Initial learning Rate=" + initialLearningRate);
		System.out.println("Prefetch threshold=" + prefetchThreshold);
		System.out.println("Hitrate: " + hitrate);
		System.out.println("Accuracy: " + accuracy);
		System.out.println("Hitrate+Accuracy=" + (hitrate + accuracy));
	}
 
 
	public void showMembers()
	{
		for (int i = 0; i < n; i++)
			for (int i2 = 0; i2 < n; i2++)
				System.out.println("\nMembers cluster["+i+"]["+i2+"] :" + clusters[i][i2].currentMembers);
	}

	public void showPrototypes()
	{
		for (int i = 0; i < n; i++) {
			for (int i2 = 0; i2 < n; i2++) {
				System.out.print("\nPrototype cluster["+i+"]["+i2+"] :");
				
				for (int i3 = 0; i3 < dim; i3++)
					System.out.print(" " + clusters[i][i2].prototype[i3]);
				
				System.out.println();
			}
		}
	}

	public void setPrefetchThreshold(double prefetchThreshold)
	{
		this.prefetchThreshold = prefetchThreshold;
	}
}

