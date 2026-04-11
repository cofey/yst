import { httpRequest } from "@/api/http";
import type {
  DictDataItem,
  DictOptionItem,
  DictTypeItem,
  DictTypeListQueryReq
} from "@/modules/system/dict/types";
import type { PageQueryParams, PageResult } from "@/shared/types/api";

export function listDictTypesApi(params?: DictTypeListQueryReq) {
  return httpRequest.get<PageResult<DictTypeItem>>("/dict/types", {
    pageNum: 1,
    pageSize: 10,
    sortField: "create_time",
    sortOrder: "desc",
    ...params
  });
}

export function createDictTypeApi(data: {
  dictName: string;
  dictType: string;
  status?: number;
  remark?: string;
}) {
  return httpRequest.post<void>("/dict/types", data);
}

export function updateDictTypeApi(
  dictId: string,
  data: {
    dictName: string;
    dictType: string;
    status?: number;
    remark?: string;
  }
) {
  return httpRequest.put<void>(`/dict/types/${dictId}`, data);
}

export function deleteDictTypeApi(dictId: string) {
  return httpRequest.delete<void>(`/dict/types/${dictId}`);
}

export function listDictDataApi(params?: {
  dictType?: string;
  dictLabel?: string;
  status?: number;
} & PageQueryParams) {
  return httpRequest.get<PageResult<DictDataItem>>("/dict/data", {
    pageNum: 1,
    pageSize: 10,
    sortField: "create_time",
    sortOrder: "desc",
    ...params
  });
}

export function createDictDataApi(data: {
  dictSort?: number;
  dictLabel: string;
  dictValue: string;
  dictType: string;
  cssClass?: string;
  listClass?: string;
  isDefault?: "Y" | "N";
  status?: number;
  remark?: string;
}) {
  return httpRequest.post<void>("/dict/data", data);
}

export function updateDictDataApi(
  dictCode: string,
  data: {
    dictSort?: number;
    dictLabel: string;
    dictValue: string;
    dictType: string;
    cssClass?: string;
    listClass?: string;
    isDefault?: "Y" | "N";
    status?: number;
    remark?: string;
  }
) {
  return httpRequest.put<void>(`/dict/data/${dictCode}`, data);
}

export function deleteDictDataApi(dictCode: string) {
  return httpRequest.delete<void>(`/dict/data/${dictCode}`);
}

export function listDictOptionsApi(dictType: string) {
  return httpRequest.get<DictOptionItem[]>(`/dict/data/type/${dictType}`);
}
