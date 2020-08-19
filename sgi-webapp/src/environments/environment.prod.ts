import { NgxLoggerLevel, LoggerConfig } from 'ngx-logger';
import { SgiAuthMode, SgiAuthConfig } from '@sgi/framework/auth';

export const environment = {
  production: true,
  serviceServers: {
    cat: '/api/cat',
    eti: '/api/eti',
    sgp: '/api/sgp'
  },
  loggerConfig: {
    enableSourceMaps: true, // <-- THIS IS REQUIRED, to make "line-numbers" work in SourceMap Object defition (without evalSourceMap)
    level: NgxLoggerLevel.DEBUG
  } as LoggerConfig,
  authConfig: {
    mode: SgiAuthMode.Keycloak,
    ssoRealm: 'sgi',
    ssoClientId: 'front',
    ssoUrl: '/auth',
    // InMemory  auth config
    inMemoryConfig: {
      userRefId: '',
      authorities: [],
      isInvestigador: false
    },
    protectedResources: [
      /\/api\/cat.*/i,
      /\/api\/eti.*/i
    ]
  } as SgiAuthConfig
};
