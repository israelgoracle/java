package es.telefonica.geal;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;

public class UploadFiles {
	
	
	private ArrayList<File> files;

	
	
	public UploadFiles()
	{
		
	}
	
	public ArrayList<File> upload(String namespaceName, String bucketName,  int expirationMinutes, ArrayList<File> files)
	{
		ArrayList<File> resultado = new ArrayList();
		
		try {
			
			
			String configurationFilePath = "~/.oci/config";
			String profile = "DEFAULT";

			Long start = System.currentTimeMillis();
			AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configurationFilePath, profile);
			Long end = System.currentTimeMillis();
			System.out.println("provider " + (end-start) + "ms.");
			
			start = System.currentTimeMillis();
			ObjectStorage client = new ObjectStorageClient(provider);
			client.setRegion(Region.EU_FRANKFURT_1);
			end = System.currentTimeMillis();
			System.out.println("client " + (end-start) + "ms.");
			start = System.currentTimeMillis();
			
			
			ExecutorService executor = Executors.newFixedThreadPool(files.size());
			CompletionService<String> service = new ExecutorCompletionService(executor);
			List<Callable<File>> callables = new ArrayList<Callable<File>>();
			for (int i=0; i< files.size(); i++)
			{
				callables.add(new UploadFile(namespaceName, bucketName,  expirationMinutes, client, files.get(i)));
		    }
			
			
			List<Future<File>> futures = executor.invokeAll(callables);
			awaitTerminationAfterShutdown(executor);
			
			for (int i=0; i< files.size(); i++)
			{
				resultado.add(futures.get(i).get());
		    }
			
			
			/*
			 * ExecutorService executor = Executors.newFixedThreadPool(files.size());
			ArrayList<Future<File>> resultFuture = new ArrayList<Future<File>>(files.size());
		    for (int i=0; i< files.size(); i++)
			{
		    	resultFuture.set(i, executor.submit(new UploadFile(namespaceName, bucketName,  expirationMinutes, client, files.get(i))));
		     
		    }
		    */
		    end = System.currentTimeMillis();
			System.out.println("hilos " + (end-start) + "ms.");
		    
		    client.close();
		    System.out.println("fin");
	    
	    
		} catch (Exception e) {
			e.printStackTrace();
		}
		return files;
	}
	
	public void awaitTerminationAfterShutdown(ExecutorService threadPool) {
	    threadPool.shutdown();
	    try {
	        if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
	            threadPool.shutdownNow();
	        }
	    } catch (InterruptedException ex) {
	        threadPool.shutdownNow();
	        Thread.currentThread().interrupt();
	    }
	}
		
		
}
