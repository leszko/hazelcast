/*
 * Copyright (c) 2008-2018, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Utility class for Aliased Discovery Configs.
 */
public final class AliasedDiscoveryConfigUtils {
    private static final Map<Class, String> CONFIG_TO_DISCOVERY_STRATEGY = new HashMap<Class, String>();
    private static final Map<Class, String> CONFIG_TO_XML_TAG = new HashMap<Class, String>();

    private AliasedDiscoveryConfigUtils() {
    }

    static {
        CONFIG_TO_DISCOVERY_STRATEGY.put(AwsConfig.class, "com.hazelcast.aws.AwsDiscoveryStrategy");
        CONFIG_TO_DISCOVERY_STRATEGY.put(GcpConfig.class, "com.hazelcast.gcp.GcpDiscoveryStrategy");
        CONFIG_TO_DISCOVERY_STRATEGY.put(AzureConfig.class, "com.hazelcast.azure.AzureDiscoveryStrategy");
        CONFIG_TO_DISCOVERY_STRATEGY.put(KubernetesConfig.class, "com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategy");
        CONFIG_TO_DISCOVERY_STRATEGY.put(EurekaConfig.class, "com.hazelcast.eureka.one.HazelcastKubernetesDiscoveryStrategy");

        CONFIG_TO_XML_TAG.put(AwsConfig.class, "aws");
        CONFIG_TO_XML_TAG.put(GcpConfig.class, "gcp");
        CONFIG_TO_XML_TAG.put(AzureConfig.class, "azure");
        CONFIG_TO_XML_TAG.put(KubernetesConfig.class, "kubernetes");
        CONFIG_TO_XML_TAG.put(EurekaConfig.class, "eureka");
    }

    /**
     * Checks whether the given XML {@code tag} is supported.
     */
    public static boolean supports(String tag) {
        return CONFIG_TO_XML_TAG.containsValue(tag);
    }

    /**
     * Returns an XML tag (e.g. {@literal <gcp>}) for the given config class.
     */
    public static String tagFor(Class clazz) {
        return CONFIG_TO_XML_TAG.get(clazz);
    }

    public static List<DiscoveryStrategyConfig> createDiscoveryStrategyConfigs(JoinConfig config) {
        return map(aliasedDiscoveryConfigsFrom(config));
    }

    public static List<DiscoveryStrategyConfig> createDiscoveryStrategyConfigs(WanPublisherConfig config) {
        return map(aliasedDiscoveryConfigsFrom(config));
    }

    public static List<DiscoveryStrategyConfig> map(List<AliasedDiscoveryConfig<?>> aliasedDiscoveryConfigs) {
        List<DiscoveryStrategyConfig> result = new ArrayList<DiscoveryStrategyConfig>();
        for (AliasedDiscoveryConfig config : aliasedDiscoveryConfigs) {
            if (config.isEnabled()) {
                result.add(createDiscoveryStrategyConfig(config));
            }
        }
        return result;
    }

    private static DiscoveryStrategyConfig createDiscoveryStrategyConfig(AliasedDiscoveryConfig<?> config) {
        validateConfig(config);

        String className = CONFIG_TO_DISCOVERY_STRATEGY.get(config.getClass());
        Map<String, Comparable> properties = new HashMap<String, Comparable>();
        for (String key : config.getProperties().keySet()) {
            putIfKeyNotNull(properties, key, config.getProperties().get(key));
        }
        return new DiscoveryStrategyConfig(className, properties);
    }

    private static void validateConfig(AliasedDiscoveryConfig config) {
        if (!CONFIG_TO_DISCOVERY_STRATEGY.containsKey(config.getClass())) {
            throw new InvalidConfigurationException(
                    String.format("Invalid configuration class: '%s'", config.getClass().getName()));
        }
    }

    private static void putIfKeyNotNull(Map<String, Comparable> properties, String key, String value) {
        if (key != null) {
            properties.put(key, value);
        }
    }

    public static AliasedDiscoveryConfig getConfigByTag(JoinConfig joinConfig, String tag) {
        if ("aws".equals(tag)) {
            return joinConfig.getAwsConfig();
        } else if ("gcp".equals(tag)) {
            return joinConfig.getGcpConfig();
        } else if ("azure".equals(tag)) {
            return joinConfig.getAzureConfig();
        } else if ("kubernetes".equals(tag)) {
            return joinConfig.getKubernetesConfig();
        } else if ("eureka".equals(tag)) {
            return joinConfig.getEurekaConfig();
        } else {
            throw new IllegalArgumentException(String.format("Invalid tag: '%s'", tag));
        }
    }

    public static AliasedDiscoveryConfig getConfigByTag(WanPublisherConfig config, String tag) {
        if ("aws".equals(tag)) {
            return config.getAwsConfig();
        } else if ("gcp".equals(tag)) {
            return config.getGcpConfig();
        } else if ("azure".equals(tag)) {
            return config.getAzureConfig();
        } else if ("kubernetes".equals(tag)) {
            return config.getKubernetesConfig();
        } else if ("eureka".equals(tag)) {
            return config.getEurekaConfig();
        } else {
            throw new IllegalArgumentException(String.format("Invalid tag: '%s'", tag));
        }
    }

    public static List<AliasedDiscoveryConfig<?>> aliasedDiscoveryConfigsFrom(JoinConfig config) {
        return asList(config.getAwsConfig(), config.getGcpConfig(), config.getAzureConfig(), config.getKubernetesConfig(),
                config.getEurekaConfig());
    }

    public static List<AliasedDiscoveryConfig<?>> aliasedDiscoveryConfigsFrom(WanPublisherConfig config) {
        return asList(config.getAwsConfig(), config.getGcpConfig(), config.getAzureConfig(), config.getKubernetesConfig(),
                config.getEurekaConfig());
    }

    public static boolean allUsePublicAddress(List<AliasedDiscoveryConfig<?>> configs) {
        boolean atLeastOneEnabled = false;
        for (AliasedDiscoveryConfig config : configs) {
            if (config.isEnabled()) {
                atLeastOneEnabled = true;
                if (!config.isUsePublicIp()) {
                    return false;
                }
            }
        }
        return atLeastOneEnabled;
    }
}
