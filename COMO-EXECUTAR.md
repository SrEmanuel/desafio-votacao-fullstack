# Instruções de Execução da Aplicação

## Método 1: Docker Compose Geral

Este método utiliza um único comando para inicializar o frontend, o backend e o banco de dados.

1.  Execute o comando na raiz do projeto:
    ```bash
    docker compose up
    ```

## Método 2: Execução Manual

Use este método para executar cada componente separadamente. A ordem é importante.

1.  **Banco de Dados:**
    * No diretório `./docker-compose`, execute:
        ```bash
        docker compose up
        ```

2.  **Backend (Spring Boot):**
    * No diretório `./backend`, execute:
        ```bash
        mvn spring-boot:run
        ```

3.  **Frontend:**
    * No diretório do frontend, execute:
        ```bash
        npm install
        npm run start
        ```