package com.renney.s3;

import java.io.File;
import java.util.List;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.iterable.S3Objects;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class AwsSdkSample {

	/**
	 to package a jar with dependencies embedded use the command:
	 
	 mvn assembly:assembly -DdescriptorId=jar-with-dependencies
	*/
	
	private static AmazonS3 s3;

	private static void init() throws Exception {
		// get credentials
		File configFile = new File(System.getProperty("user.home"), ".aws/credentials");
		AWSCredentialsProvider credentialsProvider = new ProfileCredentialsProvider(new ProfilesConfigFile(configFile), "default");

		// build S3 client
		s3 = AmazonS3ClientBuilder.standard().withCredentials(credentialsProvider).build();
	}

	public static void main(String[] args) throws Exception {
		init();

		try {
			List<Bucket> buckets = s3.listBuckets();
			System.out.println("You have " + buckets.size() + " Amazon S3 bucket(s).");

			if (buckets.size() > 0) {
				Bucket bucket = buckets.get(0);

				long totalSize = 0;
				long totalItems = 0;

				for (S3ObjectSummary objectSummary : S3Objects.inBucket(s3, bucket.getName())) {
					totalSize += objectSummary.getSize();
					totalItems++;
				}

				System.out.println("The bucket '" + bucket.getName() + "' contains " + totalItems + " objects with a total size of " + totalSize + " bytes.");
			}
		} catch (AmazonServiceException ase) {
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
}
