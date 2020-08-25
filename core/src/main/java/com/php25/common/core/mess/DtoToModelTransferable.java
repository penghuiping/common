package com.php25.common.core.mess;

/**
 * @author penghuiping
 * @date 2017-09-29
 * <p>
 * dto对象向model对象转换方法
 */
@FunctionalInterface
public interface DtoToModelTransferable<MODEL, DTO> {

    /**
     * dto转model
     *
     * @param dto
     * @param model
     */
    void dtoToModel(DTO dto, MODEL model);
}
