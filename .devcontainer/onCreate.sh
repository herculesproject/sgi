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

### Docker sgi dev network ###
docker network inspect sgi-dev-network >/dev/null 2>&1 || docker network create --driver bridge sgi-dev-network
######