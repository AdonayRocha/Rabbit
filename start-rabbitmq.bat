@echo off
echo Iniciando RabbitMQ via Docker...
echo.
echo Certifique-se de que o Docker Desktop está rodando!
echo.
docker run -d --name rabbitmq-dev -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=guest -e RABBITMQ_DEFAULT_PASS=guest rabbitmq:3-management
echo.
echo RabbitMQ iniciado!
echo Interface de gerenciamento: http://localhost:15672
echo Usuario: guest
echo Senha: guest
echo.
pause