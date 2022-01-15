package com.php25.common.core.mess;

/**
 * dto对象向model对象转换方法
 *
 * @author penghuiping
 * @date 2017-09-29
 */
@FunctionalInterface
public interface DtoToModelTransferable<MODEL, DTO> {

    /**
     * dto转model
     *
     * @param dto   数据传输对象
     * @param model 业务实体对象
     */
    void dtoToModel(DTO dto, MODEL model);
}
