

# Vertx-Base Facilities Classes


## AbstractVerticle


### Install


#### Maven

Enable the repository:

```xml
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>bintray-abneroliveira-vert.xyz</id>
        <name>bintray</name>
        <url>https://dl.bintray.com/abneroliveira/vert.xyz</url>
    </repository>
```

Add the depedency into your `pom.xml`.

```xml
<dependency>
  <groupId>io.abner.vert.xyz</groupId>
  <artifactId>core</artifactId>
  <version>0.1.0</version>
  <type>pom</type>
</dependency>

```


#### Gradle

Add the repository:

```repositories {
    maven {
        url  "https://dl.bintray.com/abneroliveira/vert.xyz" 
    }
}
```

Add the dependency:

```gradle
compile 'io.abner.vert.xyz:core:0.1.0'
```

### How to use

```java


```


### Development

### Publish to Maven Repository

The packages in this repository are being published to the bintray repository:


#### Steps to Publish to Bintray 

https://reflectoring.io/guide-publishing-to-bintray-with-gradle/

###### publish a package to bintray maven repository:

```bash
gradle -p vertx-base-core bintrayUpload -Dbintray.user=abneroliveira -Dbintray.key
```

