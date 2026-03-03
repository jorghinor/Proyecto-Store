package com.gutti.store.services;

import com.gutti.store.dtos.SaveStockItemDetailDto;
import com.gutti.store.dtos.StockItemDetailDto;

import java.util.List;
import java.util.Optional;

public interface StockItemDetailService {

    List<StockItemDetailDto> findAll();

    Optional<StockItemDetailDto> findById(Integer id);

    StockItemDetailDto save(SaveStockItemDetailDto stockItemDetailDto);

    Optional<StockItemDetailDto> update(Integer id, SaveStockItemDetailDto stockItemDetailDto);

    boolean delete(Integer id);
}
