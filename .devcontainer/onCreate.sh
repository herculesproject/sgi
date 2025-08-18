#! /bin/bash

### Node ###
# Add npm bash completion
npm completion >> ~/.bashrc
######

### Maven Bash Completion ###
curl -sSL https://raw.github.com/juven/maven-bash-completion/master/bash_completion.bash >> ~/.bashrc
######

### Docker bash completion ###
docker completion bash >> ~/.bashrc
######
