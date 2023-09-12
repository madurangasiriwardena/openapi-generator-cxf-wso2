Before you begin, convert the API definition to OpenAPI 3.0 if you already haven't done so.

1. Clone this repository and build the plugin.
2. Include the given plugin to the pom.xml file of the module `org.wso2.carbon.identity.api.server.<resource>.<version>`

```
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>4.1.2</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/<resource>.yaml</inputSpec>
                <generatorName>org.wso2.carbon.codegen.CxfWso2Generator</generatorName>
                <configOptions>
                    <sourceFolder>src/gen/java</sourceFolder>
                    <apiPackage>org.wso2.carbon.identity.api.server.<resource>.<version></apiPackage>
                    <modelPackage>org.wso2.carbon.identity.api.server.<resource>.<version>.model</modelPackage>
                    <packageName>org.wso2.carbon.identity.api.server.<resource>.<version></packageName>
                    <dateLibrary>java8</dateLibrary>
                    <hideGenerationTimestamp>true</hideGenerationTimestamp>
                </configOptions>
                <output>.</output>
                <skipOverwrite>false</skipOverwrite>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.openapitools</groupId>
            <artifactId>cxf-wso2-openapi-generator</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</plugin>
```

For example:

```
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>4.1.2</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/identity-governance-3.yaml</inputSpec>
                <generatorName>org.wso2.carbon.codegen.CxfWso2Generator</generatorName>
                <configOptions>
                    <sourceFolder>src/gen/java</sourceFolder>
                    <apiPackage>org.wso2.carbon.identity.api.server.identity.governance.v1</apiPackage>
                    <modelPackage>org.wso2.carbon.identity.api.server.identity.governance.v1.model</modelPackage>
                    <packageName>org.wso2.carbon.identity.api.server.identity.governance.v1</packageName>
                    <dateLibrary>java8</dateLibrary>
                    <hideGenerationTimestamp>true</hideGenerationTimestamp>
                </configOptions>
                <output>.</output>
                <skipOverwrite>false</skipOverwrite>
            </configuration>
        </execution>
    </executions>
    <dependencies>
        <dependency>
            <groupId>org.openapitools</groupId>
            <artifactId>cxf-wso2-openapi-generator</artifactId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
</plugin>
```

3. Run the following command inside the module org.wso2.carbon.identity.api.server.<resource>.<version> to generate the stubs

```
mvn clean install
```

4. Comment out the plugin added for your API definition before committing to the git.

#### Supported vendor extensions
| Extension | Purpose | Description                                                                                                                                                   | Example |
|---|---|---|---|
| x-gen-include-req-res | Get the HttpServletRequest and the HttpServletResponse objects to the generated code. | Should be added to root of the yaml. If the value is set to `true` then the request and response objects will be available in the generated api impl classes. | x-gen-include-req-res: true |


