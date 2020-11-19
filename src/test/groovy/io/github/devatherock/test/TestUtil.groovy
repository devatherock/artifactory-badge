package io.github.devatherock.test

/**
 * Utility class for tests
 */
class TestUtil {

    static String getFoldersResponse(String path, String modifiedTime) {
        """{
            "repo": "docker",
            "path": "${path}",
            "created": "2018-09-23T18:02:56.147Z",
            "createdBy": "devatherock",
            "lastModified": "${modifiedTime}",
            "modifiedBy": "devatherock",
            "lastUpdated": "${modifiedTime}",
            "children": [
                {
                    "uri": "/1.1.0",
                    "folder": true
                },
                {
                    "uri": "/1.1.2",
                    "folder": true
                },
                {
                    "uri": "/latest",
                    "folder": true
                },
                {
                    "uri": "/abcdefgh",
                    "folder": true
                }
            ]
        }"""
    }

    static String getFolderWithFileResponse() {
        """{
            "repo": "docker",
            "path": "io/github/devatherock/simple-yaml",
            "created": "2020-10-01T00:00:00.000Z",
            "createdBy": "devatherock",
            "lastModified": "2020-10-01T00:00:00.000Z",
            "modifiedBy": "devatherock",
            "lastUpdated": "2020-10-01T00:00:00.000Z",
            "children": [
                {
                    "uri": "/1.1.0",
                    "folder": true
                },
                {
                    "uri": "/maven-metadata.xml",
                    "folder": false
                }
            ]
        }"""
    }

    static String getFolderWithOnlyFileResponse() {
        """{
            "repo": "docker",
            "path": "io/github/devatherock/simple-yaml",
            "created": "2020-10-01T00:00:00.000Z",
            "createdBy": "devatherock",
            "lastModified": "2020-10-01T00:00:00.000Z",
            "modifiedBy": "devatherock",
            "lastUpdated": "2020-10-01T00:00:00.000Z",
            "children": [
                {
                    "uri": "/maven-metadata.xml",
                    "folder": false
                }
            ]
        }"""
    }

    static String getManifestStats(int downloadCount) {
        """{
            "downloadCount": ${downloadCount},
            "lastDownloaded": 1602863958001,
            "lastDownloadedBy": "devatherock",
            "remoteDownloadCount": 0,
            "remoteLastDownloaded": 0
        }"""
    }

    static String getManifest() {
        """{
            "schemaVersion": 2,
            "mediaType": "application/vnd.docker.distribution.manifest.v2+json",
            "config": {
                "mediaType": "application/vnd.docker.container.image.v1+json",
                "size": 3027,
                "digest": "sha256:9fe1c24da9391a4d7346200a997c06c7c900466181081af7953a2a15c9fffd7c"
            },
            "layers": [
                {
                    "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
                    "size": 10485760,
                    "digest": "sha256:e7c96db7181be991f19a9fb6975cdbbd73c65f4a2681348e63a141a2192a5f10"
                },
                {
                    "mediaType": "application/vnd.docker.image.rootfs.diff.tar.gzip",
                    "size": 1048576,
                    "digest": "sha256:f910a506b6cb1dbec766725d70356f695ae2bf2bea6224dbe8c7c6ad4f3664a2"
                }
            ]
        }"""
    }
}
