export interface MenuItem {
  menuId: string;
  parentId: string | null;
  menuName: string;
  menuType: "M" | "C" | "F";
  path?: string;
  component?: string;
  icon?: string;
  perms?: string;
  visible: number;
  status: number;
  sort: number;
}

export interface MenuTreeItem extends MenuItem {
  children?: MenuTreeItem[];
}
