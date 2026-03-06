const fs = require('fs');
const path = require('path');
const { DateTime } = require('luxon');
const handlebars = require('handlebars');
const version = require('./package.json').version;


generateConfigJson();
initEnvironmentFile();
updateEnvironmentTs();


/**
 * Generates environment.ts from environment.example.ts if it does not already exist.
 * This allows environment.ts to be gitignored while the template stays in version control.
 */
function initEnvironmentFile() {
  const envTs = 'src/environments/environment.ts';
  const envExample = 'src/environments/environment.example.ts';

  if (!fs.existsSync(envTs)) {
    if (!fs.existsSync(envExample)) {
      throw new Error(`Neither ${envTs} nor ${envExample} exist. Cannot continue.`);
    }
    fs.copyFileSync(envExample, envTs);
    console.log(`[environment.ts] Generated from ${envExample}`);
  }
}

/**
 * Generate config.json from template.
 */
function generateConfigJson() {
  const inFile = 'src/assets/config/config.json.tmpl';
  const outFile = 'src/assets/config/config.json';

  const timeStamp = DateTime.now().setZone('Europe/Madrid').toFormat('yyyyLLddHHmmss');
  const source = fs.readFileSync(inFile, 'utf8');
  const template = handlebars.compile(source, { strict: true });
  const result = template({ appVersion: version, buildTimeStamp: timeStamp });

  fs.writeFileSync(outFile, result);
  console.log('[config.json] Build timestamp: ' + timeStamp);
  console.log('[config.json] App version: ' + version);
}

/**
 * Update environment.ts with overrides from env-file.
 * 
 * Any env variable starting with "environment_" will be used as an override.
 * Underscores are converted to dots (ej. environment_authConfig_ssoUrl -> environment.authConfig.ssoUrl)
 */
function updateEnvironmentTs() {
  const envTsFile = 'src/environments/environment.ts';
  const START = '// SGI_ENV_OVERRIDES_START';
  const END = '// SGI_ENV_OVERRIDES_END';

  // Parse .env if it exists
  const envFile = process.env.SGI_ENV_FILE ? path.resolve(process.env.SGI_ENV_FILE) : null;
  const overrides = [];

  if (envFile && fs.existsSync(envFile)) {
    console.log(`[environment.ts] Parsing environment file: ${envFile}`);

    const env = {};
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

    // Collect variables prefixed with "environment_"
    const ENV_PREFIX = 'environment_';
    for (const [key, value] of Object.entries(env)) {
      if (!key.startsWith(ENV_PREFIX)) continue;
      const propPath = key.substring(ENV_PREFIX.length).replace(/_/g, '.');
      overrides.push(`environment.${propPath} = '${value}';`);
      console.log(`  environment.${propPath} = '${value}'`);
    }
  }

  // Read environment.ts file and apply overrides
  let content = fs.readFileSync(envTsFile, 'utf8');
  const blockRegex = new RegExp(`\\n?${START}[\\s\\S]*?${END}\\n?`);
  const hadOverrides = blockRegex.test(content);
  content = content.replace(blockRegex, '').trimRight() + '\n';

  if (overrides.length > 0 || hadOverrides) {
    content = content.replace(blockRegex, '').trimRight() + '\n';

    if (overrides.length > 0) {
      content += `\n${START}\n// --- Generated from env file ---\n${overrides.join('\n')}\n${END}\n`;
    } else {
      console.log('[environment.ts] Cleared previous overrides (env file missing or empty)');
    }
    fs.writeFileSync(envTsFile, content);
  } else {
    console.log('[environment.ts] No overrides to apply');
  }
}
