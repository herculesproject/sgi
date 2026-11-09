#! /bin/bash

### Node ###
# Add npm bash completion
npm completion >> ~/.bashrc
######

### Maven Bash Completion ###
curl -sSL --connect-timeout 10 --max-time 15 https://raw.github.com/juven/maven-bash-completion/master/bash_completion.bash >> ~/.bashrc || echo "No se pudo descargar bash_completion"
######

### Docker bash completion ###
docker completion bash >> ~/.bashrc
######

## Instala Google Chrome en el entorno de desarrollo para ejecutar los tests de Karma en modo headless
######
############

### Google Chrome (headless Karma tests) ###
curl -fsSL --connect-timeout 10 -o /tmp/google-chrome-stable_current_amd64.deb https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
  && sudo apt-get update \
  && sudo apt-get install -y /tmp/google-chrome-stable_current_amd64.deb \
  || echo "No se pudo instalar Google Chrome"
rm -f /tmp/google-chrome-stable_current_amd64.deb
######

### Docker sgi dev network ###
docker network inspect sgi-dev-network >/dev/null 2>&1 || docker network create --driver bridge sgi-dev-network
######