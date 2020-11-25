package org.apereo.cas.configuration;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.Arrays;

/**
 * This is {@link CasConfigurationPropertiesEnvironmentManager}.
 *
 * @author Misagh Moayyed
 * @since 5.1.0
 */

@Slf4j
@RequiredArgsConstructor
@Getter
public class CasConfigurationPropertiesEnvironmentManager {
    /**
     * Configuration directories for CAS, listed in order.
     */
    private static final File[] DEFAULT_CAS_CONFIG_DIRECTORIES = {
        new File("/etc/cas/config"),
        new File("/opt/cas/config"),
        new File("/var/cas/config")
    };

    private final @NonNull ConfigurationPropertiesBindingPostProcessor binder;

    private final Environment environment;

    /**
     * Rebind cas configuration properties.
     *
     * @param binder             the binder
     * @param applicationContext the application context
     */
    public static void rebindCasConfigurationProperties(final ConfigurationPropertiesBindingPostProcessor binder,
                                                        final ApplicationContext applicationContext) {

        val map = applicationContext.getBeansOfType(CasConfigurationProperties.class);
        val name = map.keySet().iterator().next();
        LOGGER.trace("Reloading CAS configuration via [{}]", name);
        val e = applicationContext.getBean(name);
        binder.postProcessBeforeInitialization(e, name);
        val bean = applicationContext.getAutowireCapableBeanFactory().initializeBean(e, name);
        applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
        LOGGER.debug("Reloaded CAS configuration [{}]", name);
    }

    /**
     * Rebind cas configuration properties.
     *
     * @param applicationContext the application context
     */
    public void rebindCasConfigurationProperties(final ApplicationContext applicationContext) {
        rebindCasConfigurationProperties(this.binder, applicationContext);
    }

    /**
     * Gets standalone profile configuration directory.
     *
     * @return the standalone profile configuration directory
     */
    public File getStandaloneProfileConfigurationDirectory() {
        val file = environment.getProperty("cas.standalone.configurationDirectory", File.class);
        if (file != null && file.exists()) {
            LOGGER.trace("Received standalone configuration directory [{}]", file);
            return file;
        }

        return Arrays.stream(DEFAULT_CAS_CONFIG_DIRECTORIES)
            .filter(File::exists)
            .findFirst()
            .orElse(null);
    }

    /**
     * Gets standalone profile configuration file.
     *
     * @return the standalone profile configuration file
     */
    public File getStandaloneProfileConfigurationFile() {
        return environment.getProperty("cas.standalone.configurationFile", File.class);
    }

    public String getApplicationName() {
        return environment.getRequiredProperty("spring.application.name");
    }
}
