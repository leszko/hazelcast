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

package com.hazelcast.client.spi.impl;

import com.hazelcast.client.connection.AddressTranslator;
import com.hazelcast.core.InitialMembershipEvent;
import com.hazelcast.core.InitialMembershipListener;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberAttributeEvent;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.nio.Address;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MembershipAddressTranslator
        implements AddressTranslator, MembershipListener, InitialMembershipListener {
    private volatile Map<Address, Address> privateToPublic = new HashMap<Address, Address>();

    @Override
    public Address translate(Address address) {
        if (privateToPublic.containsKey(address)) {
            return privateToPublic.get(address);
        }
        return address;
    }

    @Override
    public void refresh() {
    }

    @Override
    public void memberAdded(MembershipEvent membershipEvent) {
        refresh(membershipEvent.getMembers());
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        refresh(membershipEvent.getMembers());
    }

    @Override
    public void memberAttributeChanged(MemberAttributeEvent memberAttributeEvent) {
        refresh(memberAttributeEvent.getMembers());
    }

    @Override
    public void init(InitialMembershipEvent event) {
        refresh(event.getMembers());
    }

    private void refresh(Set<Member> members) {
        Map<Address, Address> newPrivateToPublic = new HashMap<Address, Address>();
        for (Member member : members) {
            if (member.getAttributes().containsKey("publicAddress")) {
                String[] publicHostPort = member.getAttributes().get("publicAddress").toString().split(":");
                String host = publicHostPort[0];
                Integer port = Integer.parseInt(publicHostPort[1]);
                try {
                    newPrivateToPublic.put(member.getAddress(), new Address(host, port));
                } catch (UnknownHostException e) {
                    // ignore and return the default address
                }
            }
        }

        this.privateToPublic = newPrivateToPublic;
    }
}
