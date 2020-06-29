SGI Framework

## Development

This project is configured to be run on **[VSCode](https://code.visualstudio.com/)** with **[Remote Development](https://code.visualstudio.com/docs/remote/remote-overview)** extensions.

In this case we are using a **Docker** container (Docker must be installed in your system) with an image that contains **Java 8** and **Maven 3.6** needed by the project and **Java 11** needed by the IDE itself.

Before running the container using **VSCode** you must adapt the following parameters in **./devcontainer/devcontainer.json**:
*  **user**
    *  **"1000:1000"**: 1000:1000 must be set to your UID, GID.  You can get them by running  `echo "$(id -u):$(id -g)"` in your terminal.

Aditionally the following folders must exist inside you **HOME** directory:
* **.sdkman/archives**: Used to share downloaded Java and Maven distributions between host and container.
* **.m2/repository**: Used to share local Maven repository between host and container.
* **.ssh**: Used to share ssh credentials between host and container.
* **/.vscode-server/extensionsCache**: Used to share VSCode extensions downloaded by vscode-server between host and container.

The following files are also needed (in your **HOME**):
* **.m2/settings.xml**: Used to share Maven settings between host and container.
* **.gitconfig**: Used to share your git config between host and container.
