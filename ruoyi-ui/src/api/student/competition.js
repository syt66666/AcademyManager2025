import request from "@/utils/request";

// 新增竞赛记录
export function addCompetitionRecord(formData) {
  return request({
    url: "/competition/add", // 确保路径与后端一致
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data", // 确保使用 multipart/form-data
    },
  });
}

// 查询竞赛记录
export function fetchCompetitionRecords(queryParams, currentPage, pageSize) {
  return request({
    url: "/competition/records",
    method: "get",
    params: {
      ...queryParams, // 查询条件
      currentPage, // 当前页码
      pageSize, // 每页显示条数
    },
  });
}
