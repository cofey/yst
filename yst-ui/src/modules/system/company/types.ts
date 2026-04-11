import type { PageQueryParams } from "@/shared/types/api";

export interface CompanyItem {
  companyId: string;
  companyCode: string;
  companyName: string;
  status: number;
  createTime?: string;
  updateTime?: string;
}

export interface CompanyListQueryReq extends PageQueryParams {
  companyCode?: string;
  companyName?: string;
  status?: number;
}
