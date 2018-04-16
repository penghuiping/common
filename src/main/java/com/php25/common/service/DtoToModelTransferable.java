package com.php25.common.service;

/**
 * Created by penghuiping on 2017/9/29.
 */
@FunctionalInterface
public interface DtoToModelTransferable<MODEL,DTO> {

    void dtoToModel(DTO dto, MODEL model);
}
