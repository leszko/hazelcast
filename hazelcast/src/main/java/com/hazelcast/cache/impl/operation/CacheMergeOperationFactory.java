/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CacheDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.impl.operationservice.Operation;
import com.hazelcast.spi.impl.operationservice.impl.operations.PartitionAwareOperationFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes.CacheMergeTypes;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Inserts the merging entries for all partitions of a member via locally invoked {@link CacheMergeOperation}.
 *
 * @since 3.10
 */
public class CacheMergeOperationFactory extends PartitionAwareOperationFactory {

    private String name;
    private List<CacheMergeTypes>[] mergingEntries;
    private SplitBrainMergePolicy<Data, CacheMergeTypes> mergePolicy;

    public CacheMergeOperationFactory() {
    }

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public CacheMergeOperationFactory(String name, int[] partitions, List<CacheMergeTypes>[] mergingEntries,
                                      SplitBrainMergePolicy<Data, CacheMergeTypes> mergePolicy) {
        this.name = name;
        this.partitions = partitions;
        this.mergingEntries = mergingEntries;
        this.mergePolicy = mergePolicy;
    }

    @Override
    public Operation createPartitionOperation(int partitionId) {
        for (int i = 0; i < partitions.length; i++) {
            if (partitions[i] == partitionId) {
                return new CacheMergeOperation(name, mergingEntries[i], mergePolicy);
            }
        }
        throw new IllegalArgumentException("Unknown partitionId " + partitionId + " (" + Arrays.toString(partitions) + ")");
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(name);
        out.writeIntArray(partitions);
        for (List<CacheMergeTypes> list : mergingEntries) {
            out.writeInt(list.size());
            for (CacheMergeTypes mergingEntry : list) {
                out.writeObject(mergingEntry);
            }
        }
        out.writeObject(mergePolicy);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        name = in.readUTF();
        partitions = in.readIntArray();
        //noinspection unchecked
        mergingEntries = new List[partitions.length];
        for (int partitionIndex = 0; partitionIndex < partitions.length; partitionIndex++) {
            int size = in.readInt();
            List<CacheMergeTypes> list = new ArrayList<CacheMergeTypes>(size);
            for (int i = 0; i < size; i++) {
                CacheMergeTypes mergingEntry = in.readObject();
                list.add(mergingEntry);
            }
            mergingEntries[partitionIndex] = list;
        }
        mergePolicy = in.readObject();
    }

    @Override
    public int getFactoryId() {
        return CacheDataSerializerHook.F_ID;
    }

    @Override
    public int getClassId() {
        return CacheDataSerializerHook.MERGE_FACTORY;
    }
}
