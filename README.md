# maven-bom-plugin

Supplements Maven BOM import with importing properties defined in the source BOM project.

See https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html

## motivation

Basically this plugin is a syntactic sugar for this setup:

1. Export properties in a BOM project:

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>bom</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <packaging>jar</packaging> <!-- can be changed to pom by using maven-bom-plugin, but needs to be jar here -->

    <build>
        <plugins>
	    <!-- use properties-maven-plugin to import bom.properties in the pom, but not necessary in this example 
	         however, with maven-bom-plugin one can define properties normally in pom.xml -->
            <plugin>
                <artifactId>maven-remote-resources-plugin</artifactId>
                <executions>
                    <execution>
                        <goals>
                            <goal>bundle</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <includes>
                        <include>bom.properties</include>
                    </includes>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <dependencyManagement>
        <dependencies>
        ....
        </dependencies>
    </dependencyManagement>
</project>
```

2. Import properties in another project:

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>bom-client-project</artifactId>
    <version>0.1.0-SNAPSHOT</version>

    <properties>
        <bom.version>0.1.0-SNAPSHOT</bom.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.example</groupId>
            <artifactId>bom</artifactId>
            <version>${bom.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-remote-resources-plugin</artifactId>
                <configuration>
                    <resourceBundles>
                        <resourceBundle>org.example:bom:${bom.version}</resourceBundle>
                    </resourceBundles>
                </configuration>
                <executions>
                    <execution>
                        <phase>initialize</phase>
                        <goals>
                            <goal>process</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>properties-maven-plugin</artifactId>
                <executions>
                    <execution>
                        <phase>initialize</phase>
                        <goals>
                            <goal>read-project-properties</goal>
                        </goals>
                        <configuration>
                            <files>
                                <file>${project.build.directory}/maven-shared-archive-resources/bom.properties</file>
                            </files>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

</project>
```

