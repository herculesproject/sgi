import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Module } from '@core/module';
import { SgiAuthService } from '@herculesproject/framework/auth';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root'
})
export class AllowModuleGuard implements CanActivate {

  constructor(private authService: SgiAuthService, private router: Router) {

  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    const module: Module = Module.fromPath(route.routeConfig.path);
    if (this.authService.hasModuleAccess(module.code)) {
      return true;
    }
    return this.router.createUrlTree(['/']);
  }

}