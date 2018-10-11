package com.redhat.cloudnative.inventory;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InventoryRepository extends CrudRepository<Inventory, Integer> {
    public List<Inventory> findByItemId(String itemId);
}
