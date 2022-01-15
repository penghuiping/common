package com.php25.common.core.mess;

/**
 * @author penghuiping
 * @date 2017/9/29
 * <p>
 * model对象转dto对象
 */
@FunctionalInterface
public interface ModelToDtoTransferable<MODEL, DTO> {

    /**
     * model转dto
     *
     * @param model 业务实体对象
     * @param dto   数据传输对象
     */
    void modelToDto(MODEL model, DTO dto);
}
