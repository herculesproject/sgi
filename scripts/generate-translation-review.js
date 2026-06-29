#!/usr/bin/env node
/*
 * Genera el anexo de revisión de traducciones de una versión.
 *
 * Compara las claves i18n en español (front y back) entre una referencia git y el
 * estado actual, y lista las claves nuevas con su valor en los 4 idiomas (ES, CA,
 * EN, EU). Genera CSV y/o Markdown con una fila por clave.
 *
 * Uso:
 *   node scripts/generate-translation-review.js [opciones]
 *
 * Opciones:
 *   --since <ref>      Referencia git (commit o tag) desde la que detectar textos nuevos.
 *                      Por defecto: último tag de versión (git describe --tags --abbrev=0).
 *   --out-dir <dir>    Directorio de salida (por defecto: docs/upgrade).
 *   --name <basename>  Nombre base de los ficheros de salida
 *                      (por defecto: <VERSION>-revision-traducciones).
 *   --formats <list>   Formatos separados por comas: csv,md (por defecto: csv,md).
 *
 * Ejemplos:
 *   # Textos nuevos desde el último tag de versión:
 *   node scripts/generate-translation-review.js
 *   # Acotando a una referencia concreta (p. ej. al cerrar una versión que no se revisó):
 *   node scripts/generate-translation-review.js --since <commit-o-tag>
 */
'use strict';

const fs = require('node:fs');
const path = require('node:path');
const cp = require('node:child_process');

const ROOT = path.resolve(__dirname, '..');

// ---- argumentos ----
function arg(name, def) {
  const i = process.argv.indexOf(name);
  return i !== -1 && process.argv[i + 1] ? process.argv[i + 1] : def;
}
function git(args) {
  return cp.execSync(`git -C "${ROOT}" ${args}`, { stdio: ['pipe', 'pipe', 'ignore'] }).toString().trim();
}
function gitShow(ref, relPath) {
  try { return cp.execSync(`git -C "${ROOT}" show "${ref}:${relPath}"`, { stdio: ['pipe', 'pipe', 'ignore'] }).toString(); }
  catch { return null; } // el fichero no existía en esa ref => todas sus claves son nuevas
}

const VERSION = fs.existsSync(path.join(ROOT, 'VERSION'))
  ? fs.readFileSync(path.join(ROOT, 'VERSION'), 'utf8').trim() : 'SNAPSHOT';
let SINCE = arg('--since', null);
if (!SINCE) {
  try { SINCE = git('describe --tags --abbrev=0'); }
  catch { console.error('No se pudo resolver --since automáticamente. Indícalo con --since <ref>.'); process.exit(1); }
}
const OUT_DIR = path.resolve(ROOT, arg('--out-dir', 'docs/upgrade'));
const NAME = arg('--name', `${VERSION}-revision-traducciones`);
const FORMATS = arg('--formats', 'csv,md').split(',').map(s => s.trim()).filter(Boolean);

// ---- parsers ----
const PROP_RE = /^(\s*[^#!\s][^=:]*?)\s*[=:]\s?(.*)$/;
function propMap(text) {
  const m = {};
  if (text == null) return m;
  for (const l of text.split(/\r?\n/)) { const x = l.match(PROP_RE); if (x) m[x[1].trim()] = x[2]; }
  return m;
}
function propFile(f) { return fs.existsSync(f) ? propMap(fs.readFileSync(f, 'utf8')) : {}; }
function jsonFile(f) { return fs.existsSync(f) ? JSON.parse(fs.readFileSync(f, 'utf8')) : {}; }
// decodifica \uXXXX y secuencias comunes para que el texto sea legible en el anexo
function decode(s) {
  return (s == null ? '' : String(s))
    .replace(/\\u([0-9a-fA-F]{4})/g, (_, h) => String.fromCodePoint(Number.parseInt(h, 16)))
    .replace(/\\n/g, ' ').replace(/\\t/g, ' ').replace(/\\=/g, '=').replace(/\\:/g, ':');
}

const rows = []; // servicio, fichero, clave, es, ca, en, eu

// ---- FRONT (sgi-webapp) ----
{
  const rel = 'sgi-webapp/src/assets/i18n';
  const oldEs = (() => { try { return JSON.parse(gitShow(SINCE, `${rel}/es.json`) || '{}'); } catch { return {}; } })();
  const es = jsonFile(path.join(ROOT, rel, 'es.json'));
  const ca = jsonFile(path.join(ROOT, rel, 'ca.json'));
  const en = jsonFile(path.join(ROOT, rel, 'en.json'));
  const eu = jsonFile(path.join(ROOT, rel, 'eu.json'));
  const oldK = new Set(Object.keys(oldEs));
  for (const k of Object.keys(es)) if (!oldK.has(k)) {
    rows.push({ servicio: 'sgi-webapp', fichero: `${rel}/es.json`, clave: k, es: es[k], ca: ca[k], en: en[k], eu: eu[k] });
  }
}

// ---- BACK (servicios) ----
const services = fs.readdirSync(ROOT).filter(d => /^sgi-.*-service$/.test(d)).sort();
const BASES = ['messages', 'ProblemMessages', 'ValidationMessages'];
for (const svc of services) {
  const dir = `${svc}/src/main/resources`;
  for (const b of BASES) {
    const esRel = `${dir}/${b}_es.properties`;
    if (!fs.existsSync(path.join(ROOT, esRel))) continue;
    const oldEs = propMap(gitShow(SINCE, esRel));
    const es = propFile(path.join(ROOT, esRel));
    const en = propFile(path.join(ROOT, dir, `${b}.properties`));     // base sin sufijo = inglés
    const ca = propFile(path.join(ROOT, dir, `${b}_ca.properties`));
    const eu = propFile(path.join(ROOT, dir, `${b}_eu.properties`));
    const oldK = new Set(Object.keys(oldEs));
    for (const k of Object.keys(es)) if (!oldK.has(k)) {
      rows.push({ servicio: svc, fichero: esRel, clave: k, es: decode(es[k]), ca: decode(ca[k]), en: decode(en[k]), eu: decode(eu[k]) });
    }
  }
}

rows.sort((a, b) => `${a.servicio}/${a.fichero}/${a.clave}`.localeCompare(`${b.servicio}/${b.fichero}/${b.clave}`));

// ---- salida ----
fs.mkdirSync(OUT_DIR, { recursive: true });
const written = [];

if (FORMATS.includes('csv')) {
  const q = v => { v = v == null ? '' : String(v); return /[",\n;]/.test(v) ? `"${v.replace(/"/g, '""')}"` : v; };
  const lines = ['Servicio,Fichero,Clave,ES,CA,EN,EU'];
  for (const r of rows) lines.push([r.servicio, r.fichero, r.clave, r.es, r.ca, r.en, r.eu].map(q).join(','));
  const f = path.join(OUT_DIR, `${NAME}.csv`);
  fs.writeFileSync(f, lines.join('\n') + '\n');
  written.push(f);
}

if (FORMATS.includes('md')) {
  const esc = v => (v == null ? '' : String(v)).replace(/\|/g, '\\|').replace(/\r?\n/g, ' ');
  const lines = [
    `# Revisión de traducciones: ${VERSION}`,
    '',
    `Textos i18n nuevos de esta versión y su traducción (${rows.length} entradas).`,
    '',
    '| Servicio | Fichero | Clave | ES | CA | EN | EU |',
    '|---|---|---|---|---|---|---|'
  ];
  for (const r of rows) {
    const relLink = path.relative(OUT_DIR, path.join(ROOT, r.fichero)).split(path.sep).join('/');
    lines.push(`| ${r.servicio} | [${r.fichero.split('/').pop()}](${relLink}) | \`${esc(r.clave)}\` | ${esc(r.es)} | ${esc(r.ca)} | ${esc(r.en)} | ${esc(r.eu)} |`);
  }
  const f = path.join(OUT_DIR, `${NAME}.md`);
  fs.writeFileSync(f, lines.join('\n') + '\n');
  written.push(f);
}

const front = rows.filter(r => r.servicio === 'sgi-webapp').length;
console.log(`Referencia base (--since): ${SINCE}`);
console.log(`Textos nuevos: ${rows.length} (front: ${front}, back: ${rows.length - front})`);
written.forEach(f => console.log('Generado: ' + path.relative(ROOT, f)));
