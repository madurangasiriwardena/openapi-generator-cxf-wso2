package org.wso2.carbon.codegen;

import io.swagger.v3.oas.models.OpenAPI;
import org.openapitools.codegen.*;
import io.swagger.models.properties.*;
import org.openapitools.codegen.languages.JavaJAXRSCXFCDIServerCodegen;

import java.util.*;
import java.io.File;

public class CxfWso2Generator extends JavaJAXRSCXFCDIServerCodegen {

  // Property to denote whether to include request/response objects in the generated code.
  private static final String X_GEN_INCLUDE_REQ_RES = "x-gen-include-req-res";

  /**
   * Configures the type of generator.
   * 
   * @return  the CodegenType for this generator
   * @see     org.openapitools.codegen.CodegenType
   */
  public CodegenType getTag() {
    return CodegenType.SERVER;
  }

  /**
   * Configures a friendly name for the generator.  This will be used by the generator
   * to select the library with the -g flag.
   * 
   * @return the friendly name for the generator
   */
  public String getName() {
    return "cxf-wso2";
  }

  /**
   * Returns human-friendly help for the generator.  Provide the consumer with help
   * tips, parameters here
   * 
   * @return A string value for the help message
   */
  public String getHelp() {
    return "Generates a cxf-wso2 client library.";
  }

  public CxfWso2Generator() {

    super();
    outputFolder = "generated-code/CXF-WSO2";
    artifactId = "openapi-cxf-wso2-server";
    updateOption(CodegenConstants.SOURCE_FOLDER, this.getSourceFolder());
    apiTemplateFiles.put("apiServiceFactory.mustache", ".java");

    // Updated template directory
    embeddedTemplateDir = templateDir = "cxf-wso2";
  }

  @Override
  public String apiFilename(String templateName, String tag) {
    String result = super.apiFilename(templateName, tag);

    if (templateName.endsWith("Factory.mustache")) {
      result = result.replace(implFileFolder(implFolder), apiFileFolder());
    }
    return result;
  }

  private String implFileFolder(String output) {

    return outputFolder + "/" + output + "/" + apiPackage().replace('.', '/');
  }

  @Override
  public void processOpts() {
    super.processOpts();

    if (additionalProperties.containsKey(USE_BEANVALIDATION)) {
      this.setUseBeanValidation(convertPropertyToBoolean(USE_BEANVALIDATION));
    }
    writePropertyBack(USE_BEANVALIDATION, useBeanValidation);

    supportingFiles.clear(); // Don't need extra files provided by AbstractJAX-RS & Java Codegen
  }

  @Override
  public void preprocessOpenAPI(OpenAPI openAPI) {
    super.preprocessOpenAPI(openAPI);

    if (openAPI != null && openAPI.getExtensions() != null) {
      Object genIncludeReqRes = openAPI.getExtensions().get(X_GEN_INCLUDE_REQ_RES);
      if (genIncludeReqRes != null) {
        additionalProperties.put(X_GEN_INCLUDE_REQ_RES, Boolean.parseBoolean(genIncludeReqRes.toString()));
      }
    }
  }
}
