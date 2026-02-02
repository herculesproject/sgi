# Guía de Rotación de Claves y Secretos del SGI

Esta guía documenta los procedimientos para la rotación periódica de credenciales y claves criptográficas en el **SGI**, garantizando la seguridad de la plataforma y la continuidad del servicio.

## 1. Client Secrets

Los **Client Secret** son las credenciales que permiten a los microservicios autenticarse ante Keycloak. 

La rotación afecta a  los clientes de tipo **Confidential** (`com-service`, `csp-service`, `eti-service`, etc.). No aplica al cliente `front` (Public).

### 1.1 Regenerar Client Secret en Keycloak

1. Acceder a la **Administration Console** de **Keycloak** en `https://<sgi-url>/auth`.
2. Seleccionar el **Realm** del **SGI**.
3. En el menú lateral, ir a **Clients**.
4. Seleccionar el client correspondiente (ej. `sgi-com-service`).
5. Acceder a la pestaña **Credentials**.
6. Hacer clic en **Regenerate Secret** y copiar el nuevo valor.
    > Al regenerar el **secret**, el valor anterior queda invalidado de forma instantánea. El microservicio perderá la capacidad de obtener tokens hasta que se actualice su configuración en Kubernetes, pero seguirán funcionando hasta que agoten su token actual (5 min si no se ha modificado el `Access Token Lifespan`).
7. Repetir el proceso para el resto de **Clients**.



### 1.2 Actualización en Kubernetes

La actualización en el clúster depende del método de inyección de configuración definido en el `values.yaml` del Helm Chart.

#### 1.2.1 Secrets en plano

Si el secreto está definido directamente en la propiedad `value`:

* **Acción:** Actualizar el campo `value` y ejecutar `helm upgrade`.
* **Comportamiento:** Kubernetes detecta el cambio en el objeto *Deployment* y realiza un *Rolling Update* automático de los pods.

  ```yaml
  sgi-com-service:
    extraEnvVariables:
      - name: SPRING_SECURITY_OAUTH2_CLIENT_COM-SERVICE_CLIENT-SECRET
        value: "nuevo-secret-xxxx-xxxx"
  ```

#### 1.2.2 Referencia a Objetos Secret

Si se utilizan **Secrets** (**Kubernetes Secrets**, **Sealed Secrets**, **External Secrets**, ...):

* **Acción:** Actualizar el valor en la fuente de origen (ej. Vault) o aplicar el nuevo manifiest del Secret.

  ```yaml
  # Ejemplo de sgi-keycloak-clients-secret.yaml
  apiVersion: v1
  kind: Secret
  metadata:
    name: sgi-keycloak-clients-secret
  type: Opaque
  stringData:
    com_service_client_secret: "nuevo-secret1-xxxx-xxxx"
    csp_service_client_secret: "nuevo-secret2-xxxx-xxxx"
  ```

  ```bash
  kubectl apply -f sgi-keycloak-clients-secret.yaml
  ```

* **Comportamiento:** Kubernetes no reinicia los pods automáticamente al cambiar el contenido de un Secret. Es necesario forzar el reinicio manual para que los microservicios usen los nuevos valores:

  ```bash
  kubectl rollout restart deployment sgi-com-service sgi-csp-service sgi-eti-service sgi-pii-service sgi-prc-service sgi-rep-service sgi-sgp-service sgi-tp-service
  ```

## 2. Realm Keys

Las **Realm Keys** se utilizan para firmar tokens JWT (RS256/HS256) y cifrar cookies o datos (AES). A diferencia de los secretos de cliente, su rotación puede realizarse **sin tiempo de inactividad** mediante el uso de prioridades que permite una transición progresiva.

### 2.1 Procedimiento de Rotación

Para rotar cualquiera de estas claves sin invalidar las sesiones activas de los usuarios:

1. Ir a **Realm Settings** > **Keys** > **Providers**.
2. Hacer clic en **Add provider** y seleccionar el tipo deseado (ej. `rsa-generated`).
3. Configurar los campos:
    * **Console Display Name:** Nombre descriptivo (ej. `rsa-2026-q1`).
    * **Priority:** Asignar un valor **superior** al de la clave actual (ej. si la actual tiene 100, asignar 200) o editar la clave anterior y hacerla pasiva (**Active**: `OFF`).
4. **Guardar:** La nueva clave se convertirá en la clave activa para **firmar nuevos tokens**.
5. **Periodo de Coexistencia:** La clave antigua seguirá disponible para validar tokens antiguos que aún no hayan expirado.
6. **Limpieza:** Tras un periodo prudencial, la clave antigua puede ser eliminada o desactivada.


## 3. Frecuencia de Rotación Recomendada

| Elemento | Rotación (Crear nueva) | Eliminación (Borrar antigua) |
| --- | --- | --- |
| **Client Secrets** | Cada 3 - 6 meses | - |
| **Realm Keys** | Cada 3 - 6 meses | 1 - 2 meses después |


## 4. Consideraciones de Seguridad y Gestión de Incidentes

### 4.1 Client Secret comprometido

Si existe la sospecha o la confirmación de que un **Client Secret** ha sido comprometido (filtración en logs, repositorio, incidencia de seguridad, etc.), se debe actuar **de forma inmediata**:

1. Regenerar el Client Secret en Keycloak:
    * Acceder al cliente afectado y hace click en **Regenerate Secret**.

2. Actualizar el secreto en Kubernetes:
    * Actualizar el valor correspondiente en el método de inyección configurado (valor plano, Secret, Vault, etc.).
    * Forzar el reinicio de los pods afectados para que usen el nuevo secreto.

4. Análisis posterior:
    * Identificar el origen de la filtración (logs, acceso no autorizado, etc.).
    * Revocar accesos innecesarios y reforzar controles si aplica.

### 4.2 Realm Key comprometida

Que una **Realm Key** este comprometida tiene un impacto mayor, ya que puede permitir la falsificación de cualquier token.

1. Crear inmediatamente una nueva clave:
    * Añadir un nuevo provider con mayor **Priority**.
    * La nueva clave se usará para firmar todos los tokens nuevos.

2. Reducir el tiempo de exposición:
    * Ajustar temporalmente el **Token Lifespan** (**Realm Settings** > **Tokens**) para acelerar la expiración de tokens firmados con la clave comprometida.
    * Valorar invalidar sesiones activas si el riesgo lo justifica.

3. Desactivar o eliminar la clave comprometida:
    * Una vez que los tokens antiguos hayan expirado, desactivar o eliminar la clave anterior.
    * En escenarios críticos, puede eliminarse de inmediato asumiendo el impacto en sesiones activas.

4. Auditoría:
    * Revisar accesos recientes y posibles usos indebidos.
    * Notificar al equipo de seguridad si procede.

### 4.3 Buenas Prácticas

* Evitar exponer secrets en:
    * Logs.
    * Variables de entorno visibles.
    * Repositorios (incluidos privados).
* Restringir el acceso a:
    * Consola de administración de Keycloak.
    * Herramientas de gestión de secretos.
* Automatizar o procedimentar la rotación de los secrets.
