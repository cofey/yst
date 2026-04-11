export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

export interface PageResult<T> {
  records: T[];
  total: number;
  pageNum: number;
  pageSize: number;
}

export interface PageQueryParams {
  pageNum?: number;
  pageSize?: number;
  sortField?: string;
  sortOrder?: "asc" | "desc" | "ascending" | "descending" | "ascend" | "descend";
  orderByColumn?: string;
  isAsc?: "asc" | "desc" | "ascending" | "descending" | "ascend" | "descend";
}
