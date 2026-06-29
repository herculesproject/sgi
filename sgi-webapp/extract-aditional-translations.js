const fs = require('fs');

const basePath = 'src/assets/i18n/'
const defaultLang = 'es';
const langs = ['ca', 'en', 'eu'];

const defaultJson = JSON.parse(fs.readFileSync(basePath + defaultLang + '.json'));

langs.forEach(lang => {
  const langJson = JSON.parse(fs.readFileSync(basePath + lang + '.json'));
  const outputJson = JSON.parse('{}');
  Object.keys(defaultJson).sort((a, b) => a - b).forEach(key => {
    if (langJson[key] && langJson[key] !== key) {
      outputJson[key] = langJson[key];
    } else {
      outputJson[key] = key;
    }
  });
  fs.writeFileSync(basePath + lang + '.json', JSON.stringify(outputJson, null, '  '));
});
