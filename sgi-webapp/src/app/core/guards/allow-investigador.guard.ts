import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { SgiAuthService } from '@sgi/framework/auth';

@Injectable({
  providedIn: 'root'
})
export class AllowInvestigadorGuard implements CanActivate {

  constructor(private authService: SgiAuthService, private router: Router) {

  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    if (this.authService.authStatus$.value.isInvestigador) {
      return true;
    }
    return this.router.createUrlTree(['/']);
  }

}