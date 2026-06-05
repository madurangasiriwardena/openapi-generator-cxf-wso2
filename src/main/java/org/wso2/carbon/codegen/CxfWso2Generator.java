package org.wso2.carbon.codegen;

import io.swagger.v3.oas.models.OpenAPI;
import org.openapitools.codegen.*;
import io.swagger.models.properties.*;
import org.openapitools.codegen.languages.JavaJAXRSCXFCDIServerCodegen;

import java.util.*;
import java.util.stream.Stream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CxfWso2Generator extends JavaJAXRSCXFCDIServerCodegen {

  // Property to denote whether to include request/response objects in the generated code.
  private static final String X_GEN_INCLUDE_REQ_RES = "x-gen-include-req-res";

  // Snapshot of already-generated .java files (absolute path -> content) taken before generation,
  // used to avoid rewriting a file when only its license header (e.g. the year) changed while the
  // class content stayed the same.
  private final Map<String, String> existingFileContents = new HashMap<>();

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

    // Expose the current year so license headers stay up to date automatically.
    additionalProperties.put("currentYear", String.valueOf(java.time.Year.now().getValue()));

    // Skip rewriting files whose content is unchanged, and let postProcessFile keep files
    // untouched when the license-header year is the only difference.
    setEnableMinimalUpdate(true);
    setEnablePostProcessFile(true);
    snapshotExistingFiles();

    if (additionalProperties.containsKey(USE_BEANVALIDATION)) {
      this.setUseBeanValidation(convertPropertyToBoolean(USE_BEANVALIDATION));
    }
    writePropertyBack(USE_BEANVALIDATION, useBeanValidation);

    supportingFiles.clear(); // Don't need extra files provided by AbstractJAX-RS & Java Codegen
  }

  /**
   * Captures the current content of every already-generated {@code .java} file under the output
   * directory so that {@link #postProcessFile(File, String)} can later tell whether a regenerated
   * file differs only by its license-header year.
   */
  private void snapshotExistingFiles() {
    existingFileContents.clear();
    String outputDir = getOutputDir();
    if (outputDir == null) {
      return;
    }
    Path root = Paths.get(outputDir);
    if (!Files.isDirectory(root)) {
      return;
    }
    try (Stream<Path> paths = Files.walk(root)) {
      paths.filter(Files::isRegularFile)
          .filter(p -> p.toString().endsWith(".java"))
          .forEach(p -> {
            try {
              existingFileContents.put(p.toAbsolutePath().toString(),
                  new String(Files.readAllBytes(p), StandardCharsets.UTF_8));
            } catch (IOException e) {
              // Ignore unreadable files; they will simply be regenerated normally.
            }
          });
    } catch (IOException e) {
      // If the tree can't be walked, fall back to normal generation (no year preservation).
    }
  }

  /**
   * If a regenerated file already existed and differs from the previous version only within its
   * license header (for example the copyright year), restores the previous content so the file is
   * left untouched in version control. Files whose class content actually changed (and brand new
   * files) are written normally with the current year.
   */
  @Override
  public void postProcessFile(File file, String fileType) {
    super.postProcessFile(file, fileType);

    if (file == null || !file.getName().endsWith(".java")) {
      return;
    }
    String oldContent = existingFileContents.get(file.getAbsolutePath());
    if (oldContent == null) {
      return; // Brand new file: keep the current year.
    }
    try {
      String newContent = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
      if (newContent.equals(oldContent)) {
        return; // Nothing changed.
      }
      if (bodyAfterLicenseHeader(newContent).equals(bodyAfterLicenseHeader(oldContent))) {
        // Only the license header changed; the class content is identical: keep the existing file.
        Files.write(file.toPath(), oldContent.getBytes(StandardCharsets.UTF_8));
      }
    } catch (IOException e) {
      // On any I/O error, leave the regenerated file as-is.
    }
  }

  /**
   * Returns the file content with its leading license-header block comment removed, so two
   * versions of a file can be compared by class content alone. If no leading block comment is
   * present, the original content is returned unchanged.
   */
  private static String bodyAfterLicenseHeader(String content) {
    int start = 0;
    while (start < content.length() && Character.isWhitespace(content.charAt(start))) {
      start++;
    }
    if (content.startsWith("/*", start)) {
      int end = content.indexOf("*/", start + 2);
      if (end >= 0) {
        return content.substring(end + 2);
      }
    }
    return content;
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
