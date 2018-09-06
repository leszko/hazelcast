/*
 *
 *  * Copyright (c) 2008-2018, Hazelcast, Inc. All Rights Reserved.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.hazelcast.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class DiscoveryAliasMapper {
    public static final List<String> DISCOVERY_ALIASES = asList("aws");

    public List<DiscoveryStrategyConfig> map(JoinConfig joinConfig) {
        List<DiscoveryStrategyConfig> result = new ArrayList<DiscoveryStrategyConfig>();
        for (DiscoveryAliasConfig config : discoveryAliasConfigs(joinConfig)) {
            if (config.isEnabled()) {
                result.add(createAwsDiscoveryStrategy(config));
            }
        }
        return result;
    }

    private static List<DiscoveryAliasConfig> discoveryAliasConfigs(JoinConfig joinConfig) {
        List<DiscoveryAliasConfig> configs = new ArrayList<DiscoveryAliasConfig>(joinConfig.getDiscoveryAliasConfigs());
        configs.add(joinConfig.getAwsConfig());
        return configs;
    }

    private static DiscoveryStrategyConfig createAwsDiscoveryStrategy(DiscoveryAliasConfig config) {
        String className = "com.hazelcast.aws.AwsDiscoveryStrategy";
        Map<String, Comparable> properties = new HashMap<String, Comparable>();
        for (String key : config.getProperties().keySet()) {
            putIfNotNull(properties, key, config.getProperties().get(key));
        }
        return new DiscoveryStrategyConfig(className, properties);
    }

    private static void putIfNotNull(Map<String, Comparable> properties, String key, String value) {
        if (key != null && value != null) {
            properties.put(key, value);
        }
    }
}
