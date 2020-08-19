import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { NGXLogger } from 'ngx-logger';
import { SgiAuthConfig } from './auth.config';
import { HttpRequest } from '@angular/common/http';

export interface IAuthService {
  /**
   * Observable that emits authentication status changes. It's a event emiter, so the suscriber must take care about it.
   */
  readonly authStatus$: BehaviorSubject<IAuthStatus>;
  /**
   * Initialize the service. Must be called only one time during application initialization.
   * 
   * Returns true if the initialization succeeded
   */
  init(authConfig: SgiAuthConfig): Observable<boolean>;
  /**
   * Returns the JWT token that would be used to query protected service resources.
   */
  getToken(): Observable<string>;
  /**
   * Indicates if the current user is authenticated
   */
  isAuthenticated(): boolean;
  /**
   * Performs a redirect to the login page and after sucessfully login the user is redirected 
   * to the previous page or to the provided redirectUri.
   * 
   * @param redirectUri The uri to redirect after login. Must be relative
   */
  login(redirectUri?: string): Observable<void>;
  /**
   * Performs a logout of the current user. If redirectUri is provided, then the user is redirected 
   * to that uri after successfully logout.
   *
   * @param redirectUri The uri to redirect after logout. Must be relative
   */
  logout(redirectUri?: string): Observable<void>;
  /**
   * Returns the authorities of the current user
   */
  getAuthorities(): string[];
}

/**
 * Authentication status
 */
export interface IAuthStatus {
  /** Indicates if the user is authenticated */
  isAuthenticated: boolean;
  /** The authorities of the authenticated user */
  authorities: string[];
  /** Indicates if the user is considered as investigador entity */
  isInvestigador: boolean;
  /** The external user reference ID */
  userRefId: string;
}

/**
 * Default authentication status where isn't an authenticated user
 */
export const defaultAuthStatus: IAuthStatus = {
  isAuthenticated: false,
  isInvestigador: false,
  authorities: [],
  userRefId: ''
};

/**
 * Base class for Auth services
 */
@Injectable()
export abstract class SgiAuthService implements IAuthService {

  readonly authStatus$ = new BehaviorSubject<IAuthStatus>(defaultAuthStatus);

  protected protectedResources: RegExp[] = [];

  constructor(protected logger: NGXLogger) { }

  abstract init(authConfig: SgiAuthConfig): Observable<boolean>;
  abstract getToken(): Observable<string>;
  abstract isAuthenticated(): boolean;
  abstract login(redirectUri?: string): Observable<void>;
  abstract logout(redirectUri?: string): Observable<void>;
  abstract getAuthorities(): string[];

  public isProtectedRequest(request: HttpRequest<any>): boolean {
    return this.protectedResources.findIndex((regex) => regex.test(request.url)) !== -1;
  }
}
