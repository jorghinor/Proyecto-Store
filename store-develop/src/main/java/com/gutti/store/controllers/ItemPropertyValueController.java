package com.gutti.store.controllers;

import com.gutti.store.domain.ItemPropertyValue;
import com.gutti.store.services.ItemPropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(ItemPropertyValueController.PATH)
public class ItemPropertyValueController {
    public static final String PATH = Constants.BASE_PATH + "/item-property-values";

    @Autowired
    private ItemPropertyValueService service;

    @GetMapping
    public List<ItemPropertyValue> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Optional<ItemPropertyValue> getById(@PathVariable Integer id) {
        return service.findById(id);
    }

    @PostMapping
    public ItemPropertyValue create(@RequestBody ItemPropertyValue value) {
        return service.save(value);
    }

    @PutMapping("/{id}")
    public ItemPropertyValue update(@PathVariable Integer id, @RequestBody ItemPropertyValue value) {
        value.setId(id);
        return service.save(value);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.deleteById(id);
    }
}
