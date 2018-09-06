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

import java.util.HashMap;
import java.util.Map;

import static com.hazelcast.util.Preconditions.checkHasText;

/**
 * Configuration for the AWS Discovery Strategy.
 *
 * @deprecated Use {@link DiscoveryAliasConfig} instead.
 */
@Deprecated
public class AwsConfig {
    private boolean enabled;
    private final Map<String, String> properties = new HashMap<String, String>();

    /**
     * Gets the access key to access AWS. Returns null if no access key is configured.
     *
     * @return the access key to access AWS
     * @see #setAccessKey(String)
     */
    public String getAccessKey() {
        return properties.get("access-key");
    }

    /**
     * Gets the secret key to access AWS. Returns {@code null} if no access key is configured.
     *
     * @return the secret key
     * @see #setSecretKey(String)
     */
    public String getSecretKey() {
        return properties.get("secret-key");
    }

    /**
     * Gets the region where the EC2 instances running the Hazelcast members will be running.
     *
     * @return the region where the EC2 instances running the Hazelcast members will be running
     * @see #setRegion(String)
     */
    public String getRegion() {
        return properties.get("region");
    }

    /**
     * Gets the host header; the address where the EC2 API can be found.
     *
     * @return the host header; the address where the EC2 API can be found
     */
    public String getHostHeader() {
        return properties.get("host-header");
    }

    /**
     * Enables or disables the AWS join mechanism.
     *
     * @param enabled {@code true} if enabled, {@code false} otherwise
     * @return the updated AwsConfig
     */
    public AwsConfig setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public int getConnectionTimeoutSeconds() {
        return Integer.parseInt(properties.get("connection-timeout-seconds"));
    }

    /**
     * Checks if the AWS join mechanism is enabled.
     *
     * @return {@code true} if enabled, {@code false} otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Gets the security group name. If nothing has been configured, {@code null} is returned.
     *
     * @return the security group name; {@code null} if nothing has been configured
     */
    public String getSecurityGroupName() {
        return properties.get("security-group-name");
    }

    /**
     * Gets the tag key. If nothing is specified, {@code null} is returned.
     *
     * @return the tag key or {@code null} if nothing is returned
     */
    public String getTagKey() {
        return properties.get("tag-key");
    }

    /**
     * Gets the tag value. If nothing is specified, {@code null} is returned.
     *
     * @return the tag value or {@code null} if nothing is returned
     */
    public String getTagValue() {
        return properties.get("tag-value");
    }

    public String getIamRole() {
        return properties.get("iam-role");
    }

    public String getHzPort() {
        return properties.get("hz-port");
    }

    public AwsConfig addProperty(String key, String value) {
        properties.put(key, value);
        return this;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public AwsConfig setAccessKey(String accessKey) {
        this.properties.put("access-key", checkHasText(accessKey, "accessKey must contain text"));
        return this;
    }

    public AwsConfig setSecretKey(String secretKey) {
        this.properties.put("secret-key", checkHasText(secretKey, "secretKey must contain text"));
        return this;
    }

    public AwsConfig setRegion(String region) {
        this.properties.put("region", checkHasText(region, "region must contain text"));
        return this;
    }

    public AwsConfig setHostHeader(String hostHeader) {
        this.properties.put("host-header", checkHasText(hostHeader, "hostHeader must contain text"));
        return this;
    }

    public AwsConfig setSecurityGroupName(String securityGroupName) {
        this.properties.put("security-group-name", securityGroupName);
        return this;
    }

    public AwsConfig setTagKey(String tagKey) {
        this.properties.put("tag-key", tagKey);
        return this;
    }

    public AwsConfig setTagValue(String tagValue) {
        this.properties.put("tag-value", tagValue);
        return this;
    }

    public AwsConfig setConnectionTimeoutSeconds(final int connectionTimeoutSeconds) {
        if (connectionTimeoutSeconds < 0) {
            throw new IllegalArgumentException("connection timeout can't be smaller than 0");
        }
        this.properties.put("connection-timeout-seconds", String.valueOf(connectionTimeoutSeconds));
        return this;
    }

    public AwsConfig setIamRole(String iamRole) {
        this.properties.put("iam-role", iamRole);
        return this;
    }

    @Override
    public String toString() {
        return "AwsConfig{" + "properties=" + properties + ", enabled=" + enabled + '}';
    }
}
