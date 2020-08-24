import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { Router, RouterEvent, ActivatedRouteSnapshot, Route, GuardsCheckEnd, UrlSegment, RouterStateSnapshot } from '@angular/router';
import { Module } from '@core/module';

export interface BreadcrumbData {
  title: string;
  path: string;
}

interface Navigation {
  segments: UrlSegment[];
  routeConfig: Route;
}

@Injectable({
  providedIn: 'root'
})
export class LayoutService {

  activeModule$ = new BehaviorSubject<Module>(undefined);
  menuOpened$ = new BehaviorSubject<boolean>(true);
  menuAutoclose$ = new BehaviorSubject<boolean>(false);
  breadcrumData$ = new BehaviorSubject<BreadcrumbData[]>([]);
  title$ = new BehaviorSubject<string>('');

  constructor(protected logger: NGXLogger, private router: Router) {
    this.logger.debug(LayoutService.name, 'constructor(protected logger: NGXLogger)', 'start');
    this.router.events.subscribe((event: RouterEvent) => this.processEvent(event));
    this.logger.debug(LayoutService.name, 'constructor(protected logger: NGXLogger)', 'end');
  }

  private processEvent(event: RouterEvent): void {
    console.log(event);
    if (event instanceof GuardsCheckEnd) {
      const guardsCheckEnd: GuardsCheckEnd = event;
      if (guardsCheckEnd.shouldActivate) {
        this.parseRouterState(guardsCheckEnd.state);
      }
    }
  }

  private parseRouterState(state: RouterStateSnapshot): void {
    // Build the navigation stack
    const navigationStack = this.getNavigation(state.root);

    // Build breadcrumb data
    const breadcrumbData = this.getBreadcrumbData(navigationStack);

    // Publis new active module
    this.activeModule$.next(this.getActiveModule(navigationStack));

    // Publish new breadcrumb data
    this.breadcrumData$.next(breadcrumbData);

    // Publis new title
    this.title$.next(this.getTitle(navigationStack));

    if (breadcrumbData.length > 2) {
      this.menuAutoclose$.next(true);
      this.closeMenu();
    }
    else {
      this.menuAutoclose$.next(false);
      this.openMenu();
    }
  }

  private getNavigation(route: ActivatedRouteSnapshot): Navigation[] {
    const navigations: Navigation[] = [];
    navigations.push({
      routeConfig: route.routeConfig ? route.routeConfig : undefined,
      segments: route.url ? route.url : undefined
    });
    route.children.forEach((child) => {
      navigations.push(...this.getNavigation(child));
    });
    return navigations;
  }

  private getBreadcrumbData(navigationStack: Navigation[]): BreadcrumbData[] {
    const data: BreadcrumbData[] = [];
    const urlStack: string[] = [];

    // The last navigation is discarded every time
    let endPositionsToDiscard = 1;
    // Check reverse navigation to discard nested empty end paths
    let p = navigationStack.length - 1;
    while (navigationStack[p].segments.length < 1 && p >= 0) {
      endPositionsToDiscard++;
      p--;
    }

    for (let i = 0; i < navigationStack.length - endPositionsToDiscard; i++) {
      const navigation = navigationStack[i];
      if (navigation.segments && navigation.segments.length > 0) {
        urlStack.push(...navigation.segments.map((s) => s.path));
        data.push({
          // If no title defined por navigation, find the nearest right title
          title: navigation.routeConfig?.data?.title ? navigation.routeConfig.data.title : this.getNearestTitle(navigationStack.slice(i)),
          path: '/' + urlStack.join('/')
        });
      }
    }
    return data;
  }

  private getNearestTitle(navigations: Navigation[]): string {
    const nav = navigations.find((n) => n.routeConfig?.data?.title);
    return nav?.routeConfig?.data?.title;
  }

  private getActiveModule(navigationStack: Navigation[]): Module {
    const path = navigationStack.find((nav) => nav.segments.length > 0)?.routeConfig?.path;
    return Module.fromPath(path);
  }

  private getTitle(navigationStack: Navigation[]): string {
    if (navigationStack.length === 0) {
      return undefined;
    }
    return navigationStack[navigationStack.length - 1].routeConfig?.data?.title;
  }

  openMenu(): void {
    this.menuOpened$.next(true);
  }

  closeMenu(): void {
    this.menuOpened$.next(false);
  }

  toggleMenu(): void {
    this.menuOpened$.next(!this.menuOpened$.value);
  }
}
