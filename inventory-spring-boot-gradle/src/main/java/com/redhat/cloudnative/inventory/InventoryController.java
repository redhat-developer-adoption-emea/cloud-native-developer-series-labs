package com.redhat.cloudnative.inventory;

import com.redhat.cloudnative.inventory.exception.NotFoundException;
import com.redhat.cloudnative.inventory.exception.UnprocessableEntityException;
import com.redhat.cloudnative.inventory.exception.UnsupportedMediaTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping(value = "/api/inventory")
public class InventoryController {

    private final InventoryRepository repository;

    public InventoryController(InventoryRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Inventory get(@PathVariable("id") String id) {
        List<Inventory> items = repository.findByItemId(id);

        return items.size() >= 0 ? items.get(0) : null;
    }

    @GetMapping
    public List<Inventory> getAll() {
        Spliterator<Inventory> items = repository.findAll()
                .spliterator();

        return StreamSupport
                .stream(items, false)
                .collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Inventory post(@RequestBody(required = false) Inventory inventory) {
        verifyCorrectPayload(inventory);

        return repository.save(inventory);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/{id}")
    public Inventory put(@PathVariable("id") String id, @RequestBody(required = false) Inventory inventory) {
        verifyExists(id);
        verifyCorrectPayload(inventory);

        inventory.setItemId(id);
        return repository.save(inventory);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") String id) {
        Inventory item = get(id);

        if(item != null) {
            repository.delete(item);
        }
    }

    private void verifyExists(String id) {
        Inventory item = get(id);

        if (item == null) {
            throw new NotFoundException(String.format("Inventory with id=%d was not found", id));
        }
    }

    private void verifyCorrectPayload(Inventory inventory) {
        if (Objects.isNull(inventory)) {
            throw new UnsupportedMediaTypeException("Invalid payload!");
        }

        if (Objects.isNull(inventory.getQuantity()) || inventory.getQuantity() < 0) {
            throw new UnprocessableEntityException("Quantity is required and >= 0!");
        }

        if (!Objects.isNull(inventory.getItemId())) {
            throw new UnprocessableEntityException("Id was invalidly set on request.");
        }
    }

}
