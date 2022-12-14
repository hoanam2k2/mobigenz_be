package com.api.mobigenz_be.services;

import com.api.mobigenz_be.DTOs.ImeiDto;
import com.api.mobigenz_be.DTOs.ImeiUpload;
import com.api.mobigenz_be.entities.Imei;
import org.springframework.stereotype.Service;

import java.util.List;

public interface ImeiService {

    List<ImeiDto> getImeisByProductDetailId(Integer productDetailId);

    List<ImeiDto> getImeisInStockByProductDetailId(Integer productDetailId);

    List<ImeiDto> addImeisToOrderDetail(List<ImeiDto> imeiDtoList, Integer order_detail_id,Integer product_detail_id);

    ImeiDto save(ImeiDto imeiDto);

    List<ImeiDto> batchSave(Integer productDetailId, List<ImeiUpload> imeiUploads);

    List<ImeiUpload> validateBatchImei(Integer productDetailId, List<ImeiUpload> imeiUploads);

    boolean removeImei(Integer id);

    boolean deleteOrderDetailToImei(Integer id);

    boolean exchangeImeiTheSameOrderDetail(Integer newImei, Integer oldImei, Integer orderDetailId);
}
