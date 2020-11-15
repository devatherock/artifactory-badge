package io.github.devatherock.artifactory.service;

import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.annotation.Client;

@Client(value = "${artifactory.badge.shields-io.url}")
public interface ShieldsIOClient {
	
	@Get(value = "/static/v1")
	String downloadBadge(@QueryValue String label, @QueryValue String message, @QueryValue String color);
}
