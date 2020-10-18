[![CircleCI](https://circleci.com/gh/devatherock/artifactory-badge.svg?style=svg)](https://circleci.com/gh/devatherock/artifactory-badge)
[![Docker Pulls](https://img.shields.io/docker/pulls/devatherock/artifactory-badge.svg)](https://hub.docker.com/r/devatherock/artifactory-badge/)
[![Docker Image Size](https://img.shields.io/docker/image-size/devatherock/artifactory-badge.svg?sort=date)](https://hub.docker.com/r/devatherock/artifactory-badge/)
[![Docker Image Layers](https://img.shields.io/microbadger/layers/devatherock/artifactory-badge.svg)](https://microbadger.com/images/devatherock/artifactory-badge)
# artifactory-badge
Badge generator for docker registries hosted in jfrog artifactory

## Sample badge generated by the custom generator
```xml
<svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="144" height="20" role="img" aria-label="docker pulls: 47">
    <title>docker pulls: 47</title>
    <linearGradient id="s" x2="0" y2="100%">
        <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
        <stop offset="1" stop-opacity=".1"/>
    </linearGradient>
    <clipPath id="r">
        <rect width="144" height="20" rx="3" fill="#fff"/>
    </clipPath>
    <g clip-path="url(#r)">
        <rect width="112" height="20" fill="#555"/>
        <rect x="112" width="32" height="20" fill="#007ec6"/>
        <rect width="144" height="20" fill="url(#s)"/>
    </g>
    <g font-family="monospace">
        <text aria-hidden="true" x="0" y="15" fill="#fff" xml:space="preserve">docker pulls</text>
        <text aria-hidden="true" x="112" y="15" fill="#fff" xml:space="preserve">47</text>
    </g>
</svg>
```
