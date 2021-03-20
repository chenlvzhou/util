package com.util.comutil.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;

@Component
public class AliyunUtil {
	
	@Value("${aliyun.oss.endpoint}")
	private String endpoint;
	@Value("${aliyun.oss.accessKeyId}")
	private String accessKeyId;
	@Value("${aliyun.oss.accessKeySecret}")
	private String accessKeySecret;
	@Value("${aliyun.oss.bucket}")
	private String bucket;
	@Value("${aliyun.oss.bucketDomain}")
	private String bucketDomain;
	
	private OSSClientBuilder builder = new OSSClientBuilder();
	
	public void updateFile(String key, File file) {
		OSS oss = builder.build(endpoint, accessKeyId, accessKeySecret);
		try {
			oss.putObject(bucket, key, file);
		}finally {
			oss.shutdown();
		}
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getBucketDomain() {
		return bucketDomain;
	}

	public void updateAudio(String key, InputStream stream) {

		OSS oss = builder.build(endpoint, accessKeyId, accessKeySecret);
		try {
			oss.putObject(bucket, key, stream);
		}finally {
			oss.shutdown();
		}
	}

}
