package com.shunbo.yst.modules.system.dict.vo;

import com.shunbo.yst.common.query.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DictDataQueryRequest extends PageQuery {

    private String dictType;
    private String dictLabel;
    private Integer status;
}
