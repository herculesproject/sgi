const fs = require('fs');
const { DateTime } = require('luxon');
const handlebars = require('handlebars');
const version = require('./package.json').version;

const inFile = 'src/assets/config/config.json.tmpl';
const outFile = 'src/assets/config/config.json';

/**
 * Generates environment.ts from environment.example.ts if it does not already exist.
 * This allows environment.ts to be gitignored while the template stays in version control.
 */
initEnvironmentFile();

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

try {
  var timeStamp = DateTime.now().setZone('Europe/Madrid').toFormat('yyyyLLddHHmmss');
  const source = fs.readFileSync(inFile, 'utf8');
  const template = handlebars.compile(source, {
    strict: true
  });

  const result = template({
    "appVersion": version,
    "buildTimeStamp": timeStamp
  });

  fs.writeFileSync(outFile, result);
  console.log('Build timestamp set to: ' + timeStamp);
} catch (error) {
  console.error('Error occurred:', error);
  throw error
}
