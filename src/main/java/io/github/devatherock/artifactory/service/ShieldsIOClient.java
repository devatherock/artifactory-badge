package io.github.devatherock.artifactory.service;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.client.annotation.Client;

@Client(value = "https://img.shields.io/badge")
public interface ShieldsIOClient {
	
	@Get(value = "/{fileName}")
	String downloadBadge(@PathVariable String fileName);
}
