# Mappers SAML del SGI para Keycloak

El SGI incorpora varios mappers SAML para transformar los atributos que envía un proveedor de identidad (IdP) en grupos y atributos de usuario de Keycloak.

Se recomienda utilizar los mappers declarativos, que cubren los casos habituales sin ejecutar código JavaScript y cuyo comportamiento puede validarse mediante pruebas automatizadas. Los mappers JavaScript siguen siendo funcionales, pero están desactivados por defecto y no se recomiendan para configuraciones nuevas.

El código de los mappers vive en [`sgi-auth/broker-saml-mappers`](../sgi-auth/broker-saml-mappers) y
se empaqueta dentro de la imagen de `sgi-auth`.

## ¿Qué mapper utilizar?

| Caso de uso | Mapper |
|---|---|
| Asignar grupos según el valor de un atributo SAML | **JSON Rules Group Importer** |
| Guardar parte de un atributo SAML como atributo de usuario (quitándole un prefijo) | **Prefix Match & Strip Attribute Importer** |
| Construir un atributo de usuario a partir de una plantilla | **Attribute Template Importer** |
| Ejecutar lógica JavaScript a medida | **Script to Group** / **Script to Attribute** |

## Mappers disponibles

Todos se configuran en **Admin Console → Identity Providers → *tu IdP SAML* → Mappers → Add mapper**.

### JSON Rules Group Importer

**Para qué sirve.** Asigna grupos de Keycloak según el valor de uno o varios atributos SAML.

**Configuración.**

| Campo | Significado |
|---|---|
| **Rules (JSON)** | Las reglas en formato JSON (ver ejemplo). |
| **Clear groups before resolve** | Si se activa, el usuario abandona sus grupos actuales antes de aplicar las reglas (el IdP pasa a ser la fuente de verdad). |

**Ejemplo.**

```json
{
  "splitters": {
     "grupos": "," 
  },
  "rules": [
    {
      "name": "Gestor CSP",
      "grant": ["GESTOR-CSP"],
      "when": { 
        "attribute": "grupos",
        "anyOf": ["sgi-gestor-csp"]
      }
    },
    {
      "name": "Gestor UGI",
      "grant": ["GESTOR-CSP-UGI", "VISOR-CSP"],
      "when": {
        "all": [
          { 
            "attribute": "grupos",
            "anyOf": ["sgi-gestor-csp-ugi", "sgi-responsable-csp-ugi"] 
          },
          { 
            "not": {
              "attribute": "grupos",
              "anyOf": ["sgi-gestor-csp"] 
            }
          }
        ]
      }
    },
    {
      "name": "Investigador por afiliación",
      "grant": ["INVESTIGADOR"],
      "when": {
        "any": [
          { "attribute": "eduPersonScopedAffiliation", "startsWithAny": ["staff@", "faculty@"] },
          { "attribute": "eduPersonScopedAffiliation", "regex": ".*@universidad\\.es$" }
        ]
      }
    }
  ],
  "fallback": {
    "whenNoRulesMatched": ["VISOR-CSP"]
  }
}
```

> - `splitters` divide el atributo `grupos` (que llega como una cadena separada por comas) en valores individuales.
> - Si `grupos` contiene `sgi-gestor-csp`, se concede `GESTOR-CSP`.
> - Si contiene `sgi-gestor-csp-ugi` o `sgi-responsable-csp-ugi` **y no** `sgi-gestor-csp`, se
>   conceden `GESTOR-CSP-UGI` y `VISOR-CSP`.
> - Si `eduPersonScopedAffiliation` empieza por `staff@` o `faculty@`, o cumple la expresión regular, se concede `INVESTIGADOR`.
> - Si no se cumple ninguna regla, se concede `VISOR-CSP` (fallback).

**Opciones del ruleset.** Un ruleset puede contener los siguientes elementos:

| Elemento | Descripción | Contiene |
|---|---|---|
| `rules` | Lista de reglas evaluadas sobre los atributos. | `name`, `grant`, `when` |
| `splitters` *(opcional)* | Divide un atributo que llega como una sola cadena con varios valores (p.ej. separados por comas) antes de evaluar. | pares `atributo: separador` |
| `fallback` *(opcional)* | Grupos a conceder si no se cumple ninguna regla. | `whenNoRulesMatched` |

Cada regla contiene:

| Elemento | Descripción |
|---|---|
| `name` | Nombre descriptivo de la regla (informativo). |
| `grant` | Grupos de Keycloak a conceder si se cumple la condición (uno o varios). |
| `when` | Condición que debe cumplirse (ver operadores). |

**Operadores admitidos en `when`.** Una condición es o bien una *condición* sobre un atributo, o
bien un *operador lógico* que agrupa otras condiciones.

Condiciones:

| Operador | Se cumple cuando… |
|---|---|
| `anyOf` | algún valor del atributo es exactamente uno de los indicados |
| `startsWithAny` | algún valor del atributo empieza por uno de los prefijos indicados |
| `regex` | algún valor del atributo cumple la expresión regular |

Operadores lógicos:

| Operador | Se cumple cuando… |
|---|---|
| `all` | se cumplen **todas** las condiciones anidadas |
| `any` | se cumple **al menos una** de las condiciones anidadas |
| `not` | **no** se cumple la condición anidada |

Todas las reglas que se cumplen contribuyen (no hay cortocircuito) y un atributo ausente hace que la
condición sea `false`. Estas mismas opciones se validan en los tests del módulo
([`ruleset-example.json`](../sgi-auth/broker-saml-mappers/src/test/resources/rulesets/ruleset-example.json)).

### Prefix Match & Strip Attribute Importer

**Para qué sirve.** Toma el valor de un atributo SAML que empieza por un prefijo dado, le quita ese
prefijo y guarda el resto en un atributo de usuario (o en `email` / `firstName` / `lastName`).

**Configuración.**

| Campo | Significado |
|---|---|
| **SAML attribute name** | Atributo SAML (o su FriendlyName) cuyos valores se examinan. |
| **Prefix to match and strip** | Prefijo a buscar y quitar. Se usa el primer valor que empieza por él, los valores que no lo llevan se ignoran. |
| **User attribute name** | Atributo de usuario donde se guarda el resultado. Usa `email`, `firstName` o `lastName` para las propiedades estándar del usuario. |

**Ejemplo.** El atributo `personalUniqueCode` con el valor:

```
urn:schac:personalUniqueCode:es:employeeID:12345
```

y el prefijo:

```
urn:schac:personalUniqueCode:es:employeeID:
```

guarda `12345` en el atributo de usuario destino (p.ej. `userRefId`).

El prefijo actúa como **selector** entre los valores del atributo: si ninguno lo lleva, no se escribe nada.

### Attribute Template Importer

**Para qué sirve.** Rellena un atributo de usuario a partir de una plantilla que combina texto y
valores del IdP.

**Configuración.**

| Campo | Significado |
|---|---|
| **Template** | Plantilla del valor a guardar. Las sustituciones van entre `${...}` y referencian un atributo SAML por su `Name` o `FriendlyName`; se pueden convertir con `\| uppercase` / `\| lowercase` (p.ej. `${NAMEID \| lowercase}`). |
| **User Attribute Name** | Atributo de usuario donde se guarda el resultado. Usa `email`, `firstName` o `lastName` para las propiedades estándar del usuario. |

**Ejemplo.** Con la plantilla `${mail | lowercase}` y un atributo SAML `mail` con valor
`Nombre.Apellido@UNI.ES`, guarda `nombre.apellido@uni.es` en el atributo de usuario destino (p.ej.
`email`).

### Mappers JavaScript (legacy)

Siguen disponibles para facilitar la migración de instalaciones existentes y para los [casos de uso que no pueden cubrirse](#casos-no-cubiertos) con los mappers declarativos, pero **no se recomiendan para configuraciones nuevas**.

Al ejecutar código JavaScript en el servidor de autenticación son más difíciles de validar y de mantener. Los mappers declarativos cubren los mismos casos sin ejecutar código y se comprueban con pruebas automatizadas, por lo que son la opción preferible.

Vienen **desactivados por defecto**: no aparecen en la lista de mappers de la consola hasta que se
activan con su variable de entorno en el despliegue (`extraEnvVariables` del `values.yaml`).

#### Script to Group

Recibe los atributos SAML y devuelve la lista de grupos a asignar. Por ejemplo, asignar un grupo si
un atributo contiene un valor:

```javascript
var ArrayList = Java.type("java.util.ArrayList");
var grp = new ArrayList();

// Roles del SGI
var ROL_GESTOR_CSP = 'GESTOR-CSP';

// Grupos recibidos del IdP
var GRP_GESTOR_CSP = 'sgi-gestor-csp';

var KEY_GRUPOS = 'grupos';

for each (var entry in attributes.entrySet()) {
  if (entry.getKey().equals(KEY_GRUPOS)) {
    var value = entry.getValue();

    if (value.contains(GRP_GESTOR_CSP)) {
      grp.add(ROL_GESTOR_CSP);
    }
  }
}

exports = grp;
```

Para usar este mapper hay que activarlo con la variable de entorno:

```
KC_SPI_IDENTITY_PROVIDER_MAPPER__SAML_SCRIPT_GROUP_IDP_MAPPER__ENABLED=true
```

#### Script to Attribute

Se configura con un atributo de origen (**Attribute Name**) y uno de destino (**User Attribute
Name**); el script recibe los valores del origen en `sourceAttributeValues` y devuelve el valor a
guardar. Por ejemplo, extraer un id quitando un prefijo:

```javascript
var userRefId = null;
var PREFIX = 'urn:schac:personalUniqueCode:es:employeeID:';

for each (var attribute in sourceAttributeValues) {
  if (attribute.startsWith(PREFIX)) {
    userRefId = attribute.replace(PREFIX, '');
  }
}

exports = userRefId;
```

Para usar este mapper hay que activarlo con la variable de entorno:

```
KC_SPI_IDENTITY_PROVIDER_MAPPER__SAML_SCRIPT_ATTRIBUTE_IDP_MAPPER__ENABLED=true
```

## Migración a los mappers declarativos

Se trata de reproducir con reglas/configuración lo que hacía el script. Los ejemplos siguientes muestran la conversión de cada tipo de script.

### Script to Group → JSON Rules Group Importer

Ejemplo de un script que asigna grupos según un atributo:

```javascript
// Script to Group (legacy)
var ArrayList = Java.type("java.util.ArrayList");
var grp = new ArrayList();

// Roles del SGI
var ROL_GESTOR_CSP = 'GESTOR-CSP';
var ROL_GESTOR_CSP_UGI = 'GESTOR-CSP-UGI';
var ROL_VISOR_CSP = 'VISOR-CSP';
var ROL_INVESTIGADOR = 'INVESTIGADOR';

// Grupos recibidos del IdP
var GRP_GESTOR_CSP = 'sgi-gestor-csp';
var GRP_GESTOR_CSP_UGI = 'sgi-gestor-csp-ugi';
var GRP_RESPONSABLE_CSP_UGI = 'sgi-responsable-csp-ugi';

var KEY_GRUPOS = 'grupos';

for each (var entry in attributes.entrySet()) {
  if (entry.getKey().equals(KEY_GRUPOS)) {
    var value = entry.getValue();

    if (value.contains(GRP_GESTOR_CSP)) {
      grp.add(ROL_GESTOR_CSP);
    }

    if ((value.contains(GRP_GESTOR_CSP_UGI) || value.contains(GRP_RESPONSABLE_CSP_UGI))
        && !value.contains(GRP_GESTOR_CSP)) {
      grp.add(ROL_GESTOR_CSP_UGI);
      grp.add(ROL_VISOR_CSP);
    }
  }
}

if (grp.isEmpty()) {
  grp.add(ROL_INVESTIGADOR);
}

exports = grp;
```

Se traduce a este ruleset del [JSON Rules Group Importer](#json-rules-group-importer) (la misma
lógica, pero declarativa y testeable, sin ejecutar código):

```json
{
  "rules": [
    {
      "name": "Gestor CSP",
      "grant": ["GESTOR-CSP"],
      "when": { 
        "attribute": "grupos",
        "anyOf": ["sgi-gestor-csp"]
      }
    },
    {
      "name": "Gestor UGI",
      "grant": ["GESTOR-CSP-UGI", "VISOR-CSP"],
      "when": {
        "all": [
          { 
            "attribute": "grupos",
            "anyOf": ["sgi-gestor-csp-ugi", "sgi-responsable-csp-ugi"]
          },
          { 
            "not": {
              "attribute": "grupos",
              "anyOf": ["sgi-gestor-csp"] 
            }
          }
        ]
      }
    }
  ],
  "fallback": { 
    "whenNoRulesMatched": ["INVESTIGADOR"] 
  }
}
```

### Script to Attribute → Prefix Match & Strip Attribute Importer

El mapper *Script to Attribute* se configura con un atributo de origen, uno de destino y un script.
Por ejemplo, para extraer un id quitando un prefijo:

- **Mapper Type**: `Script to Attribute`
- **Attribute Name** (origen): `personalUniqueCode`
- **User Attribute Name** (destino): `userRefId`
- **Script**:
  ```javascript
  var userRefId = null;
  var PREFIX = 'urn:schac:personalUniqueCode:es:employeeID:';

  for each (var attribute in sourceAttributeValues) {
    if (attribute.startsWith(PREFIX)) {
      userRefId = attribute.replace(PREFIX, '');
    }
  }

  exports = userRefId;
  ```

Se sustituye por un **Prefix Match & Strip Attribute Importer** con el mismo origen y destino, y el
prefijo en un campo (sin script):

- **Mapper Type**: `Prefix Match & Strip Attribute Importer`
- **SAML attribute name** (origen): `personalUniqueCode`
- **Prefix to match and strip**: `urn:schac:personalUniqueCode:es:employeeID:`
- **User attribute name** (destino): `userRefId`

## Casos no cubiertos

Los mappers declarativos cubren los casos habituales de las integraciones del SGI. Si una implantación necesita un comportamiento que no puede expresarse con ellos, se recomienda abrir una incidencia describiendo el caso de uso en las [issues del proyecto](https://github.com/herculesproject/sgi/issues).

Siempre que sea posible, esa funcionalidad se añadirá a un mapper existente o se incorporará un nuevo mapper declarativo para que pueda reutilizarse en futuras instalaciones.

Si el caso de uso es demasiado específico o no resulta adecuado incorporarlo a los mappers declarativos, sigue siendo posible utilizar los [mappers JavaScript](#mappers-javascript-legacy).
