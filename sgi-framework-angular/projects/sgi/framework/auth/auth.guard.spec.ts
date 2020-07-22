import { SgiAuthGuard } from './auth.guard';

import { TestBed } from '@angular/core/testing';
import { Router, ActivatedRouteSnapshot } from '@angular/router';
import { LoggerTestingModule } from 'ngx-logger/testing';
import { of, Observable, throwError } from 'rxjs';
import { SgiAuthRouteData } from './auth.route';
import { SgiAuthService } from './auth.service';

describe('CanAuthenticationGuard', () => {
  let guard: SgiAuthGuard;
  let authService: jasmine.SpyObj<SgiAuthService>;
  let routeMock: ActivatedRouteSnapshot = {
    pathFromRoot: []
  } as ActivatedRouteSnapshot;
  let routeStateMock: any = { snapshot: {}, url: '/cookies' };

  beforeEach(() => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['isAuthenticated', 'login', 'getAuthorities']);
    TestBed.configureTestingModule({
      imports: [LoggerTestingModule],
      providers: [
        { provide: Router, useValue: routeMock },
        { provide: SgiAuthService, useValue: authServiceSpy }
      ]
    });
    authService = TestBed.inject(SgiAuthService) as any;
    guard = TestBed.inject(SgiAuthGuard);
  });

  afterEach(() => {
    // Clean route
    routeMock = {
      // This attribute is mandatory
      pathFromRoot: []
    } as ActivatedRouteSnapshot;
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true and login must be called if not authenticated ', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(false);
    authService.login.and.returnValue(of());

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
    // Check auth login call
    expect(authService.login).toHaveBeenCalledTimes(1);
  });

  it('should return false if not authenticated and login call throws error', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(false);
    authService.login.and.returnValue(throwError('ERROR'));

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
    // Check auth login call
    expect(authService.login).toHaveBeenCalledTimes(1);
  });

  it('should return true if authenticated and no auth route data', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
  });

  it('should return false if authenticated with auth route hasAuthority', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.getAuthorities.and.returnValue(['ONE', 'TWO', 'THREE']);

    // Route data
    routeMock.data = {
      hasAuthority: 'FOUR',
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
  });

  it('should return true if authenticated with auth route hasAuthority', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.getAuthorities.and.returnValue(['ONE', 'TWO', 'THREE']);

    // Route data
    routeMock.data = {
      hasAuthority: 'TWO'
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
  });

  it('should return false if authenticated with auth route hasAnyAuthority', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.getAuthorities.and.returnValue(['ONE', 'TWO', 'THREE']);

    // Route data
    routeMock.data = {
      hasAnyAuthority: ['FOUR', 'FIVE']
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
  });

  it('should return true if authenticated with auth route hasAnyAuthority', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.getAuthorities.and.returnValue(['ONE', 'TWO', 'THREE']);

    // Route data
    routeMock.data = {
      hasAnyAuthority: ['FOUR', 'THREE']
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
  });

  it('should return false if authenticated with auth route hasAuthorityForAnyUO', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.getAuthorities.and.returnValue(['ONE', 'TWO', 'THREE', 'ONE_UO', 'FOUR_UO']);

    // Route data
    routeMock.data = {
      hasAuthorityForAnyUO: 'FIVE'
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
  });

  it('should return true if authenticated with auth route hasAuthorityForAnyUO', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.getAuthorities.and.returnValue(['ONE', 'TWO', 'THREE', 'ONE_UO', 'FOUR_UO']);

    // Route data
    routeMock.data = {
      hasAuthorityForAnyUO: 'FOUR'
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
  });

  it('should return false if authenticated with auth route hasAnyAuthorityForAnyUO', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.getAuthorities.and.returnValue(['ONE', 'TWO', 'THREE', 'ONE_UO', 'FOUR_UO', 'ONE_UO2']);

    // Route data
    routeMock.data = {
      hasAnyAuthorityForAnyUO: ['SIX', 'FIVE']
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
  });

  it('should return true if authenticated with auth route hasAnyAuthorityForAnyUO', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.getAuthorities.and.returnValue(['ONE', 'TWO', 'THREE', 'ONE_UO', 'FOUR_UO', 'ONE_UO2']);

    // Route data
    routeMock.data = {
      hasAnyAuthorityForAnyUO: ['FIVE', 'FOUR']
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(true);
    });
  });

  it('should return false if authenticated with mixed auth data attributes', () => {
    // AuthService responses
    authService.isAuthenticated.and.returnValue(true);
    authService.getAuthorities.and.returnValue(['ONE', 'TWO', 'THREE', 'ONE_UO', 'FOUR_UO', 'ONE_UO2']);

    // Route data
    routeMock.data = {
      hasAuthority: 'ONE',
      hasAnyAuthorityForAnyUO: ['TWO', 'THREE']
    } as SgiAuthRouteData;

    // Resolve value as observable
    const resolvedValue = guard.canActivate(routeMock, routeStateMock) as Observable<boolean>;
    // Must return an observable
    expect(resolvedValue instanceof Observable).toEqual(true);
    // Check value
    resolvedValue.subscribe((res) => {
      expect(res).toEqual(false);
    });
  });
});
