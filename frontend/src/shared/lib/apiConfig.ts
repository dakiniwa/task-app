const DEFAULT_API_BASE_URL = "http://localhost:8080";

/** 設定済みのbackendAPI(baseURL)を末尾スラッシュなしで返す */
export function getApiBaseUrl(): string {
  const configuredUrl = process.env.NEXT_PUBLIC_API_BASE_URL?.trim();
  const baseUrl = configuredUrl || DEFAULT_API_BASE_URL;

  return baseUrl.replace(/\/+$/, "");
}
