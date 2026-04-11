import type { PageQueryParams } from "@/shared/types/api";

export interface DictTypeItem {
  dictId: string;
  dictName: string;
  dictType: string;
  status: number;
  remark?: string;
  createTime?: string;
  updateTime?: string;
}

export interface DictTypeListQueryReq extends PageQueryParams {
  dictName?: string;
  dictType?: string;
  status?: number;
}

export interface DictDataItem {
  dictCode: string;
  dictSort: number;
  dictLabel: string;
  dictValue: string;
  dictType: string;
  cssClass?: string;
  listClass?: string;
  isDefault: "Y" | "N";
  status: number;
  remark?: string;
  createTime?: string;
  updateTime?: string;
}

export interface DictOptionItem {
  label: string;
  value: string;
  cssClass?: string;
  listClass?: string;
  isDefault?: "Y" | "N";
}
