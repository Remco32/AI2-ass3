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

			public Cluster()
			{
				currentMembers = new HashSet<Integer>();
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
		


		// Here n*n new cluster are initialized and the cluster centers are initialized as random;
		clusters = new Cluster[n][n];
		for (int i = 0; i < n; i++)  {
			for (int i2 = 0; i2 < n; i2++) {
				clusters[i][i2] = new Cluster();
				clusters[i][i2].prototype = new float[dim];
				int R = rnd.nextInt(dim-0);
				clusters[i][i2].currentMembers.add(R);
			}
		}
	}

	public Integer calcBMU(Object element){
		Integer BMU;
		
		//go through all clustercenter and calculate the euclidean distance and the closest cluster center as BMU 
		
		//return BMU;
		return 0; /// Set to 0 so the code can compile
	}
	
	public boolean train()
	{
 
		for(int k =0; k<epochs; k++){
			//calculate new neighborehoodsize and learning rate
		    float rad = (n/2) * ( 1 - (k/epochs));
		    double learnr= initialLearningRate * (1 - (k/epochs));
		    
		    //interate through traindata vectors
			Iterator it = trainData.iterator();
            while(it.hasNext()){
            	Object element = it.next();
            	Integer BMU = calcBMU(element); //where the fuck do i save the BMU
            	//changes the current members within that BMU to the new formula 
            	//go through clusters 
            	for (int i = 0; i < n; i++)  {
        			for (int i2 = 0; i2 < n; i2++) {
        				//find if value is in neighborehood 
        				for (Integer value : clusters[i][i2].currentMembers) {
        					//if value is between BMU +- rad 
        					//if () {
        				    	//change that value according to the formula pnew = (1-learnr) * pold + learr * X;
        				      // value = (1- learnr)* value + learnr * element; 
        				        
        				   // }
        				}
        			}
        		}			    

            }
		
		//print out progress 
		System.out.println("progress: " + k + "/" + epochs);
		}
		
		return true;
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

