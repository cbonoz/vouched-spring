package com.vouched.service.email;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Service
public class TemplateService {

    private final Configuration cfg;

    // https://freemarker.apache.org/docs/pgui_quickstart_all.html
    public String getTemplate(String templateName, Map<String, Object> root) {
        try {
            Template temp = cfg.getTemplate(templateName);  //  Template temp = cfg.getTemplate("test.ftlh");
            // process the template
            // Create a StringWriter to capture the template processing result
            StringWriter stringWriter = new StringWriter();

            // Process the template and write the result to the StringWriter
            temp.process(root, stringWriter);

            // Convert the StringWriter to a String
            return stringWriter.toString();
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject
    public TemplateService() throws IOException {
        /* Create and adjust the configuration singleton */
        cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        // Recommended settings for new projects:
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    }

}
