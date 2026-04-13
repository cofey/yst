import "vue-router";

declare module "vue-router" {
  interface RouteMeta {
    title?: string;
    affix?: boolean;
    cache?: boolean;
    tab?: boolean;
    keepAliveName?: string;
  }
}
