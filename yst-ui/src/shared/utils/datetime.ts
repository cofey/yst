type DateDisplayMode = "date" | "minute" | "second";

const pad = (value: number) => String(value).padStart(2, "0");

const formatByDate = (date: Date, mode: DateDisplayMode) => {
  const year = date.getFullYear();
  const month = pad(date.getMonth() + 1);
  const day = pad(date.getDate());
  if (mode === "date") {
    return `${year}-${month}-${day}`;
  }
  const hours = pad(date.getHours());
  const minutes = pad(date.getMinutes());
  if (mode === "minute") {
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  }
  const seconds = pad(date.getSeconds());
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

export const formatDateTime = (value?: string | null, mode: DateDisplayMode = "second") => {
  if (!value) {
    return "-";
  }
  const trimmed = value.trim();
  if (!trimmed) {
    return "-";
  }
  if (trimmed.length >= 19 && /^\d{4}-\d{2}-\d{2}\s\d{2}:\d{2}:\d{2}$/.test(trimmed)) {
    if (mode === "date") {
      return trimmed.slice(0, 10);
    }
    if (mode === "minute") {
      return trimmed.slice(0, 16);
    }
    return trimmed.slice(0, 19);
  }
  const parsed = new Date(trimmed);
  if (Number.isNaN(parsed.getTime())) {
    return trimmed;
  }
  return formatByDate(parsed, mode);
};
