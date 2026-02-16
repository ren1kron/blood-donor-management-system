package ifmo.se.coursach_back.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final List<String> SPA_EXCLUDED_PREFIXES = List.of(
            "api/",
            "actuator/",
            "v3/api-docs",
            "swagger-ui/"
    );

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
                .addResourceLocations(
                        "classpath:/static/",
                        "classpath:/public/",
                        "classpath:/META-INF/resources/",
                        "file:src/main/webapp/"
                )
                .setCachePeriod(7 * 24 * 60 * 60)
                .resourceChain(true)
                .addResolver(new PathResourceResolver() {
                    @Override
                    protected Resource getResource(String resourcePath, Resource location) throws IOException {
                        Resource requestedResource = location.createRelative(resourcePath);
                        if (requestedResource.exists() && requestedResource.isReadable()) {
                            return requestedResource;
                        }

                        for (String excludedPrefix : SPA_EXCLUDED_PREFIXES) {
                            if (resourcePath.startsWith(excludedPrefix)) {
                                return null;
                            }
                        }

                        Resource indexResource = location.createRelative("index.html");
                        if (indexResource.exists() && indexResource.isReadable()) {
                            return indexResource;
                        }
                        return null;
                    }
                });
    }
}
