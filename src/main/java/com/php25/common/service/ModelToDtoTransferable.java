package com.php25.common.service;

/**
 * Created by penghuiping on 2017/9/29.
 */
@FunctionalInterface
public interface ModelToDtoTransferable<MODEL, DTO> {

    void modelToDto(MODEL model, DTO dto);
}
