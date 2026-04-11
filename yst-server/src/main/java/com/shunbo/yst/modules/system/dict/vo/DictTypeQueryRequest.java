package com.shunbo.yst.modules.system.dict.vo;

import com.shunbo.yst.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DictTypeQueryRequest extends PageQuery {

    private String dictName;
    private String dictType;
    private Integer status;
}
