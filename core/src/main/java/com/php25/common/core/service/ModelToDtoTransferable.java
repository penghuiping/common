package com.php25.common.core.service;

/**
 * @author penghuiping
 * @date 2017/9/29
 *
 * model对象转dto对象
 */
@FunctionalInterface
public interface ModelToDtoTransferable<MODEL, DTO> {

    /**
     * model转dto
     *
     * @param model
     * @param dto
     */
    void modelToDto(MODEL model, DTO dto);
}
