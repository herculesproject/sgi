const fs = require('fs');

const basePath = 'src/assets/i18n/'
const defaultLang = 'es';
const langs = ['en', 'eu'];

const defaultJson = JSON.parse(fs.readFileSync(basePath + defaultLang + '.json'));

langs.forEach(lang => {
  const langJson = JSON.parse(fs.readFileSync(basePath + lang + '.json'));
  const outputJson = JSON.parse('{}');
  Object.keys(defaultJson).sort().forEach(key => {
    // Si el fichero de idioma auxiliar contiene valor para la key existente y el valor de la misma no es igual a la key, lo trasladamos a la salida
    if (langJson[key] && langJson[key] !== key) {
      outputJson[key] = langJson[key];
    }
  });
  fs.writeFileSync(basePath + lang + '.json', JSON.stringify(outputJson, null, '  '));
});
