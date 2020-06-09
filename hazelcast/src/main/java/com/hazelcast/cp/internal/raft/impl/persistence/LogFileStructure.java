/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.cp.internal.raft.impl.persistence;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nonnull;

/**
 * Used in the EE side
 */
public class LogFileStructure {
    private final String filename;
    private final long[] tailEntryOffsets;
    private final long indexOfFirstTailEntry;

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public LogFileStructure(
            @Nonnull String filename,
            @Nonnull long[] tailEntryOffsets,
            long indexOfFirstTailEntry
    ) {
        this.filename = filename;
        this.tailEntryOffsets = tailEntryOffsets;
        this.indexOfFirstTailEntry = indexOfFirstTailEntry;
    }

    @Nonnull
    public String filename() {
        return filename;
    }

    @Nonnull
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public long[] tailEntryOffsets() {
        return tailEntryOffsets;
    }

    public long indexOfFirstTailEntry() {
        return indexOfFirstTailEntry;
    }
}
