import { httpRequest } from "@/api/http";
import type { CompanyItem, CompanyListQueryReq } from "@/modules/system/company/types";
import type { PageResult } from "@/shared/types/api";

export function listCompaniesApi(params?: CompanyListQueryReq) {
  return httpRequest.get<PageResult<CompanyItem>>("/companies", {
    pageNum: 1,
    pageSize: 10,
    sortField: "create_time",
    sortOrder: "desc",
    ...params
  });
}

export function createCompanyApi(data: {
  companyCode: string;
  companyName: string;
  status?: number;
}) {
  return httpRequest.post<void>("/companies", data);
}

export function updateCompanyApi(
  companyId: string,
  data: { companyCode: string; companyName: string; status?: number }
) {
  return httpRequest.put<void>(`/companies/${companyId}`, data);
}

export function deleteCompanyApi(companyId: string) {
  return httpRequest.delete<void>(`/companies/${companyId}`);
}
