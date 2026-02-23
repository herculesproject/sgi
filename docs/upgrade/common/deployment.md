# Guía de despliegue del SGI con Helm

## 1. Preparación del entorno

### Actualizar repositorio de Helm

Sincronizar el repositorio local con el remoto para obtener la última versión de los charts del SGI:

```bash
helm repo update
```

### Verificar contexto de Kubernetes

Confirmar que se está operando en el clúster correcto:

```bash
kubectl config current-context
```

### Preparación del fichero `values.yaml`

Verificar que el fichero `values.yaml` está configurado para el entorno objetivo.
Este fichero será usado durante la instalación o actualización en el paso siguiente.



## 2. Instalación o Actualización del SGI

El despliegue de nuevas versiones debe realizarse siempre actualizando el fichero `values.yaml` del entorno correspondiente para facilitar la trazabilidad y reproducibilidad del entorno.

Además de los cambios que puedan ser necesarios en cada versión, como regla general se debe actualizar la versión del **SGI** ajustando `global.image.tag` a la nueva versión.

```yaml
global:
  image:
    tag: 1.0.0                # Sustituir por la versión adecuada
    pullPolicy: IfNotPresent
```

Instalar o actualizar el SGI:

```bash
helm upgrade sgi sgi-helm/sgi-umbrella \
  --install \
  --namespace <namespace> \
  --timeout 10m0s \
  --wait --wait-for-jobs \
  --create-namespace \
  -f ./values.yaml
```

**Parámetros del comando:**
- `--install`: Instala el release si no existe.
- `--namespace <namespace>`: Namespace de Kubernetes donde se desplegará.
- `--timeout 10m0s`: Tiempo máximo de espera para completar el despliegue.
- `--wait`: Espera hasta que todos los pods estén en estado listos.
- `--wait-for-jobs`: Espera a que los Jobs terminen.
- `--create-namespace`: Crea el namespace si no existe.
- `-f ./values.yaml`: Fichero de valores adaptado al entorno.


## 3. Verificación del despliegue

### Estado del release de Helm

Verificar que el release se ha actualizado correctamente:

```bash
# Ver el historial de revisiones del release
helm history sgi -n <namespace>

# Obtener información detallada del release
helm status sgi -n <namespace>
```

### Estado de los Deployments

Verificar que los deployments están utilizando la versión de imagen esperada:

```bash
# Listar todos los deployments con sus imágenes
kubectl get deployments -n <namespace> -o wide

# Ver detalles de un deployment específico si es necesario
kubectl describe deployment <deployment-name> -n <namespace>
```

### Estado de los Pods

Verificar que todos los pods están en estado `Running` y con todos sus contenedores `Ready`:

```bash
# Listar todos los pods del namespace
kubectl get pods -n <namespace> -o wide

# Filtrar pods que no están en estado Running
kubectl get pods -n <namespace> --field-selector=status.phase!=Running
```

### Revisión de logs

Revisar los logs de los servicios para descartar errores de arranque o conexión:

```bash
# Ver logs de un pod específico
kubectl logs <pod-name> -n <namespace> --tail=100

# Ver logs en tiempo real
kubectl logs <pod-name> -n <namespace> -f

# Ver logs de todos los pods del SGI
kubectl logs -l app.kubernetes.io/instance=sgi -n <namespace> -f --tail=100 --max-log-requests=20 --prefix

# Ver logs de todos los pods del SGI (usando stern)
stern . -n <namespace> --tail=100
```

### Pruebas funcionales básicas

1. Acceder a la URL de la aplicación y verificar que carga correctamente.
2. Verificar el correcto funcionamiento del **SGI** prestando especial atención a los cambios incluidos en la versión.

## 4. Solución de problemas

### Consultar logs de pods con problemas

Si un pod está fallando, revisar sus logs:

```bash
# Describir el pod para ver eventos y errores
kubectl describe pod <pod-name> -n <namespace>

# Ver logs del contenedor actual
kubectl logs <pod-name> -n <namespace> --tail=200

# Ver logs del contenedor anterior (si el pod se reinició)
kubectl logs <pod-name> -n <namespace> --previous
```

### Rollback a versión anterior

Si el despliegue presenta problemas, se puede revertir a la versión anterior:

```bash
helm rollback sgi -n <namespace>
```
El rollback de Helm revierte los deployments, configuraciones y servicios, pero **no revierte automáticamente los cambios aplicados en la base de datos**. Liquibase registra las migraciones ejecutadas pero no las deshace.

Si la versión incluía cambios en el esquema de base de datos y es necesario revertir completamente:
1. Restaurar un backup de la base de datos previo a la actualización.
2. Ejecutar el rollback de Helm.
3. Verificar la consistencia de los datos.
