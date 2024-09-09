package com.bobocode.basics.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseEntity {
    protected UUID uuid;
    protected LocalDateTime createdOn;

    public BaseEntity(UUID uuid) {
        this.uuid = uuid;
        this.createdOn = LocalDateTime.now();
    }

    public UUID getUuid() {
        return uuid;
    }

    public static boolean hasDuplicatesByUUID(Collection<? extends BaseEntity> entities) {
        Set<UUID> seenUUIDs = new HashSet<>();
        for (BaseEntity entity : entities) {
            if (!seenUUIDs.add(entity.getUuid())) {
                return true;
            }
        }
        return false;
    }
}
