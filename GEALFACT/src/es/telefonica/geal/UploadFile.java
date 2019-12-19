package es.telefonica.geal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails.AccessType;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;

public class UploadFile implements Callable{
	private String namespaceName;
	private String bucketName;
	private int expirationMinutes;
	private File file;
	ObjectStorage client;
	
	public UploadFile(String namespaceName, String bucketName,  int expirationMinutes, ObjectStorage client, File file)
	{
		this.namespaceName = namespaceName;//"frgpfwwong2h";
		this.bucketName = bucketName;//"FACT";
		this.expirationMinutes = expirationMinutes;//1;
		this.file=file;
		this.client=client;
	}
	
	public File call() {
		
		try {
			//¿Existe?
			Long start = System.currentTimeMillis();
			ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().namespaceName(namespaceName).bucketName(bucketName).prefix(file.getFileName()).build();
			Long end = System.currentTimeMillis();
			System.out.println("comprobar si existe " + (end-start) + "ms.");
			client.listObjects(listObjectsRequest);
			if (client.listObjects(listObjectsRequest).getListObjects().getObjects().size()<1)
			{
				//Lo Subo
				start = System.currentTimeMillis();
				PutObjectRequest putObjectRequest = PutObjectRequest.builder().namespaceName(namespaceName)
						.bucketName(bucketName).objectName(file.getFileName()).contentLength(Long.valueOf(file.getFileContent().available()))
						.putObjectBody(file.getFileContent()).build();
				client.putObject(putObjectRequest);
				end = System.currentTimeMillis();
				System.out.println("subir fichero " + (end-start) + "ms.");
			}
			
			//Expiracion
			AccessType accessType = AccessType.create("ObjectRead");		
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, expirationMinutes); 
			Date expiration = calendar.getTime();
			
			//Enlace
			start = System.currentTimeMillis();
			CreatePreauthenticatedRequestDetails preauthenticatedRequestDetails = CreatePreauthenticatedRequestDetails
					.builder().accessType(accessType).name(file.getFileName()).objectName(file.getFileName()).timeExpires(expiration).build();
			CreatePreauthenticatedRequestRequest preRequest = CreatePreauthenticatedRequestRequest.builder()
					.createPreauthenticatedRequestDetails(preauthenticatedRequestDetails).namespaceName(namespaceName).bucketName(bucketName).build();
			end = System.currentTimeMillis();
			file.setLink(client.createPreauthenticatedRequest(preRequest).getPreauthenticatedRequest().getAccessUri());
			System.out.println("crear enlace " + (end-start) + "ms.");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		/*
		try {
			System.out.println("Hilo esperando 4 segundos.");
			Thread.sleep(1000);
			file.setLink(file.getFileName()+System.currentTimeMillis()+"");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Hilo ejecutado");
		*/
		return file;
	}

}
