import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpEventType,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, EMPTY, } from 'rxjs';
import { catchError, switchMap, tap } from 'rxjs/operators';

import { SgiAuthService } from './auth.service';
import { NGXLogger } from 'ngx-logger';

/**
 * Interceptor that injects authorization header with the JWT token.
 *
 * If the token cannot be obtained from the AuthService, and the user isn't authenticated or the session is expired,
 * then the request is aborted and the user is redirected to login page
 */
@Injectable()
export class SgiAuthHttpInterceptor implements HttpInterceptor {
  constructor(private authService: SgiAuthService, private router: Router, private logger: NGXLogger) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.logger.debug(`intercept(${req.method} to ${req.url}) - START`);
    return this.authService.getToken().pipe(
      catchError((error) => {
        this.logger.error(JSON.stringify(error));
        // We check if the user has session. If no session, then redirect to login
        if (!this.authService.isAuthenticated()) {
          this.logger.error('User is not authenticated');
          this.authService.login();
          return EMPTY;
        }
        else {
          return next.handle(req);
        }
      }),
      switchMap((token) => {
        const authRequest = req.clone({ setHeaders: { authorization: `Bearer ${token}` } });
        // TODO: Handle 401 error and redirect to login? 
        // NO, if the user is truly authenticated, a 401 could meaning any other issue such bad or tampered token
        return next.handle(authRequest);
      }),
      tap((re) => {
        if (re.type === HttpEventType.Response) {
          this.logger.debug(`intercept(${req.method} to ${req.url}): - END`);
        }
      })
    );
  }
}
