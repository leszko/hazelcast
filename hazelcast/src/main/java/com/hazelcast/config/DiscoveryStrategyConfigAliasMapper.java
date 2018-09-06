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

public class DiscoveryStrategyConfigAliasMapper {
    public List<DiscoveryStrategyConfig> map(JoinConfig joinConfig) {
        List<DiscoveryStrategyConfig> result = new ArrayList<DiscoveryStrategyConfig>();
        if (joinConfig.getAwsConfig().isEnabled()) {
            result.add(createAwsDiscoveryStrategy(joinConfig.getAwsConfig()));
        }
        return result;
    }

    private static DiscoveryStrategyConfig createAwsDiscoveryStrategy(AwsConfig awsConfig) {
        String className = "com.hazelcast.aws.AwsDiscoveryStrategy";
        Map<String, Comparable> properties = new HashMap<String, Comparable>();
        putIfNotNull(properties, "access-key", awsConfig.getAccessKey());
        putIfNotNull(properties, "secret-key", awsConfig.getSecretKey());
        putIfNotNull(properties, "iam-role", awsConfig.getIamRole());
        putIfNotNull(properties, "region", awsConfig.getRegion());
        putIfNotNull(properties, "host-header", awsConfig.getHostHeader());
        putIfNotNull(properties, "security-group-name", awsConfig.getSecurityGroupName());
        putIfNotNull(properties, "tag-key", awsConfig.getTagKey());
        putIfNotNull(properties, "tag-value", awsConfig.getTagValue());
        return new DiscoveryStrategyConfig(className, properties);
    }

    private static void putIfNotNull(Map<String, Comparable> properties, String key, String value) {
        if (key != null && value != null) {
            properties.put(key, value);
        }
    }
}
