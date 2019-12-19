package es.telefonica.geal;

/**
 * Copyright (c) 2016, 2019, Oracle and/or its affiliates. All rights reserved.
 */
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.BucketSummary;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails.AccessType;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest.Builder;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;

import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import com.oracle.bmc.objectstorage.responses.ListBucketsResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TestUpload {
	public TestUpload() {
		super();
	}

	public static void main(String[] args) {
		try {
			
			
			String objectName = "prueba02";
			String namespaceName = "frgpfwwong2h";
			String bucketName = "FACT";
			int expirationMinutes = 1;
			
			String configurationFilePath = "~/.oci/config";
			String profile = "DEFAULT";
			
			
			ArrayList<File> files = new ArrayList<File>();
			for (int i = 1; i<=5; i++)
			{
				File file = new File();
				file.setFileName("File"+i);
				file.setFileContent(new ByteArrayInputStream(("Contenido"+i).getBytes(StandardCharsets.UTF_8)));
				files.add(file);
			}
			
			UploadFiles up = new UploadFiles();
			ArrayList<File> results = up.upload(namespaceName, bucketName, expirationMinutes, files);
			for (int i = 0; i<results.size(); i++)
			{
			System.out.println(results.get(i).getFileName() +" " +results.get(i).getLink());
			}
			/*
			Long start = System.currentTimeMillis();

			AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configurationFilePath,
					profile);
			
			Long end = System.currentTimeMillis();
			System.out.println("provider " + (end-start) + "ms.");
			
			Long parcial = System.currentTimeMillis();
			ObjectStorage client = new ObjectStorageClient(provider);
			client.setRegion(Region.EU_FRANKFURT_1);
			
			end = System.currentTimeMillis();
			System.out.println("client " + (end-parcial) + "ms.");
			parcial = System.currentTimeMillis();
			//Comprobar si el Fichero Existe.  El get no vale del todo salta excepción, probar con el listObject buscando por prefijo (prueba arriba)
			ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().namespaceName(namespaceName).bucketName(bucketName).prefix(objectName).build();
			end = System.currentTimeMillis();
			System.out.println("comprobar si existe " + (end-parcial) + "ms.");
			if (client.listObjects(listObjectsRequest).getListObjects().getObjects().size()<1)
			{
				parcial = System.currentTimeMillis();
				//Si no existe lo subo
				System.out.println("Creating the source object");
				PutObjectRequest putObjectRequest = PutObjectRequest.builder().namespaceName(namespaceName)
						.bucketName(bucketName).objectName(objectName).contentLength(4L)
						.putObjectBody(new ByteArrayInputStream("Hola".getBytes(StandardCharsets.UTF_8))).build();
				client.putObject(putObjectRequest);
				end = System.currentTimeMillis();
				System.out.println("subir fichero " + (end-parcial) + "ms.");
			}
			
			//Crear el Enlace (Con una expiración de x minutos)
			AccessType accessType = AccessType.create("ObjectRead");		
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, expirationMinutes); 
			
			Date expiration = calendar.getTime();
			//System.out.println(expiration);
			
			parcial = System.currentTimeMillis();
			CreatePreauthenticatedRequestDetails preauthenticatedRequestDetails = CreatePreauthenticatedRequestDetails
					.builder().accessType(accessType).name(objectName).objectName(objectName).timeExpires(expiration).build();
			CreatePreauthenticatedRequestRequest preRequest = CreatePreauthenticatedRequestRequest.builder()
					.createPreauthenticatedRequestDetails(preauthenticatedRequestDetails).namespaceName(namespaceName).bucketName(bucketName).build();
			end = System.currentTimeMillis();
			System.out.println(client.createPreauthenticatedRequest(preRequest).getPreauthenticatedRequest().getAccessUri());
			System.out.println("crear enlace " + (end-parcial) + "ms.");
			System.out.println("Tiempo ejecución total " + (end-start) + "ms.");
			
		
			*/
			/*
			 * GetNamespaceResponse namespaceResponse =
			 * client.getNamespace(GetNamespaceRequest.builder().build()); String
			 * namespaceName = namespaceResponse.getValue();
			 * System.out.println("Using namespace: " + namespaceName);
			 * 
			 * Builder listBucketsBuilder =
			 * ListBucketsRequest.builder().compartmentId(provider.getTenantId());
			 * 
			 * String nextToken = null; do { listBucketsBuilder.page(nextToken);
			 * ListBucketsResponse listBucketsResponse =
			 * client.listBuckets(listBucketsBuilder.build()); for (BucketSummary bucket :
			 * listBucketsResponse.getItems()) { System.out.println("Found bucket: " +
			 * bucket.getName()); } nextToken = listBucketsResponse.getOpcNextPage(); }
			 * while (nextToken != null);
			 */
			
		}  catch (Exception e) {
			// TODO: Add catch code
			e.printStackTrace();
		}
	}
}
