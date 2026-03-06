const fs = require('fs');
const path = require('path');

// Load .env file if SGI_ENV_FILE is set, otherwise use defaults
const envFile = process.env.SGI_ENV_FILE
  ? path.resolve(process.env.SGI_ENV_FILE)
  : null;

const env = {};
if (envFile && fs.existsSync(envFile)) {
  console.log(`[proxy.conf] Parsing environment file: ${envFile}`);
  fs.readFileSync(envFile, 'utf8').split('\n').forEach(line => {
    const match = line.match(/^\s*([\w.-]+)\s*=\s*"?([^"]*)"?\s*$/);
    if (match) env[match[1]] = match[2];
  });

  // Resolve ${VAR} references
  let changed = true;
  while (changed) {
    changed = false;
    for (const key of Object.keys(env)) {
      const resolved = env[key].replace(/\$\{(\w+)\}/g, (_, ref) => {
        if (env[ref] != null) { changed = true; return env[ref]; }
        return `\${${ref}}`;
      });
      env[key] = resolved;
    }
  }
} else {
  console.log('[proxy.conf] No env file provided, using default values');
}

function getTarget(key, defaultValue) {
  const target = env[key] ?? defaultValue;
  console.log(`  ${key}: ${target}`);
  return target;
}

module.exports = {
  '/auth': {
    target: getTarget('SGI_AUTH_URL', 'http://localhost:8080'),
    changeOrigin: true,
    xfwd: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/cnf': {
    target: getTarget('SGI_CNF_URL', 'http://localhost:4288'),
    pathRewrite: { '^/api/cnf': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/com': {
    target: getTarget('SGI_COM_URL', 'http://localhost:4286'),
    pathRewrite: { '^/api/com': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/csp': {
    target: getTarget('SGI_CSP_URL', 'http://localhost:4281'),
    pathRewrite: { '^/api/csp': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/eer': {
    target: getTarget('SGI_EER_URL', 'http://localhost:4290'),
    pathRewrite: { '^/api/eer': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/eti': {
    target: getTarget('SGI_ETI_URL', 'http://localhost:4280'),
    pathRewrite: { '^/api/eti': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/pii': {
    target: getTarget('SGI_PII_URL', 'http://localhost:4283'),
    pathRewrite: { '^/api/pii': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/prc': {
    target: getTarget('SGI_PRC_URL', 'http://localhost:4289'),
    pathRewrite: { '^/api/prc': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/rel': {
    target: getTarget('SGI_REL_URL', 'http://localhost:4284'),
    pathRewrite: { '^/api/rel': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/rep': {
    target: getTarget('SGI_REP_URL', 'http://localhost:4287'),
    pathRewrite: { '^/api/rep': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/sgdoc': {
    target: getTarget('SGI_SGDOC_URL', 'http://localhost:8290'),
    pathRewrite: { '^/api': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/sge/': {
    target: getTarget('SGI_SGE_URL', 'http://localhost:8290'),
    pathRewrite: { '^/api': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/sgemp': {
    target: getTarget('SGI_SGEMP_URL', 'http://localhost:8290'),
    pathRewrite: { '^/api': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/sgepii': {
    target: getTarget('SGI_SGEPII_URL', 'http://localhost:8290'),
    pathRewrite: { '^/api': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/sgo': {
    target: getTarget('SGI_SGO_URL', 'http://localhost:8290'),
    pathRewrite: { '^/api': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/sgp': {
    target: getTarget('SGI_SGP_URL', 'http://localhost:8290'),
    pathRewrite: { '^/api': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/tp': {
    target: getTarget('SGI_TP_URL', 'http://localhost:4285'),
    pathRewrite: { '^/api/tp': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  },
  '/api/usr': {
    target: getTarget('SGI_USR_URL', 'http://localhost:4282'),
    pathRewrite: { '^/api/usr': '' },
    changeOrigin: true,
    secure: false,
    logLevel: 'debug'
  }
};
