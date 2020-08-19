import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { Router, RouterEvent, NavigationEnd } from '@angular/router';

export interface BreadcrumbData {
  title: string;
  segments: {
    title: string;
    path: string;
  }[];
}

@Injectable({
  providedIn: 'root'
})
export class LayoutService {
  private readonly REGEX = /(?:[^\/]+)/g;

  activeModule$ = new BehaviorSubject<string>(undefined);
  menuOpened$ = new BehaviorSubject<boolean>(true);
  menuAutoclose$ = new BehaviorSubject<boolean>(false);
  breadcrumData$ = new BehaviorSubject<BreadcrumbData>(undefined);

  constructor(protected logger: NGXLogger, private router: Router) {
    this.logger.debug(LayoutService.name, 'constructor(protected logger: NGXLogger)', 'start');
    this.router.events.subscribe((event: RouterEvent) => this.processEvent(event));
    this.logger.debug(LayoutService.name, 'constructor(protected logger: NGXLogger)', 'end');
  }

  private processEvent(event: RouterEvent): void {
    if (event instanceof NavigationEnd) {
      const segments = this.REGEX.test(event.url) ? event.url.match(this.REGEX) : [];
      const data: BreadcrumbData = {
        title: '',
        segments: []
      };
      const stack: string[] = [];
      for (let i = 0; i < segments.length; i++) {
        const segment = segments[i];
        stack.push(segment);
        if (isNaN(Number(segment))) {
          data.segments.push({
            title: i > 0 ? segment : 'inicio',
            path: '/' + stack.join('/')
          });
        }
      }
      if (data.segments.length > 0) {
        data.title = data.segments[data.segments.length - 1].title;
      }
      if (stack.length > 0) {
        this.activeModule$.next(stack[0]);
      }
      this.breadcrumData$.next(data);
      if (data.segments.length > 2) {
        this.menuAutoclose$.next(true);
        this.closeMenu();
      }
      else {
        this.menuAutoclose$.next(false);
        this.openMenu();
      }
    }
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
