# Sistema de Mensageria com RabbitMQ - Estudo Guiado

Este projeto implementa um sistema de mensageria usando Spring Boot e RabbitMQ, seguindo o padrão de **comunicação assíncrona** entre microserviços.

## 🏗️ Arquitetura

O sistema simula um **e-commerce moderno** com os seguintes componentes:

- **Produtor**: Serviço que recebe pedidos via HTTP e publica mensagens no broker
- **Broker**: RabbitMQ gerenciando filas, exchanges e roteamento
- **Consumidor**: Serviço que processa as mensagens de pedidos criados

## 🚀 Como Executar

### 1. Pré-requisitos
- Java 21
- Docker e Docker Compose
- Gradle

### 2. Executar o projeto
```bash
./gradlew bootRun
```

O Spring Boot automaticamente:
- Detecta o `compose.yaml`
- Sobe o container RabbitMQ
- Configura as filas, exchanges e bindings

### 3. Verificar RabbitMQ
Acesse a interface de gerenciamento: http://localhost:15672/
- Login: `guest` / `guest`
- Verifique se existe a fila `order.queue` e exchange `order.exchange`

## 📡 Testando o Sistema

### Enviar uma mensagem (Produtor)
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "order-001",
    "clientId": "client-123",
    "items": [
      { "productId": "prod-1", "quantity": 2 },
      { "productId": "prod-2", "quantity": 1 }
    ]
  }'
```

### Observar o processamento
Nos logs da aplicação você verá:
1. **Produtor**: "Published OrderCreatedMessage: ..."
2. **Consumidor**: "Processing message: ..." 
3. **Consumidor**: "Order processed successfully for orderId: ..."
4. **Consumidor**: "Message acknowledged successfully for orderId: ..."

## 🔧 Configurações Importantes

### Acknowledgment Manual
- Configurado em `RabbitConfig.rabbitListenerContainerFactory()`
- Permite controle fino sobre confirmação/rejeição de mensagens
- Evita perda de mensagens em caso de falhas

### Tratamento de Erros
- **Validações de negócio**: orderId, clientId, items não podem ser nulos/vazios
- **Requeue estratégico**: 
  - `InterruptedException` → requeue = true (tentar novamente)
  - Outros erros → requeue = false (evitar loop infinito)

### Concorrência
- Consumidores mínimos: 1
- Consumidores máximos: 5
- Processamento paralelo controlado

## 🎯 Conceitos Implementados

| Conceito | Implementação |
|----------|---------------|
| **Exchange** | `order.exchange` (Direct) |
| **Queue** | `order.queue` (durável) |
| **Routing Key** | `order.created` |
| **Binding** | Liga queue → exchange via routing key |
| **Producer** | `OrderPublisher` via `RabbitTemplate` |
| **Consumer** | `OrderConsumer` com `@RabbitListener` |

## 🧪 Cenários de Teste

### 1. Sucesso Normal
- Envie um pedido válido
- Verifique logs de processamento
- Confirme acknowledgment

### 2. Validação de Erro
```json
{
  "orderId": "",
  "clientId": "client-123",
  "items": []
}
```
- Deve gerar erro e rejeitar sem requeue

### 3. Múltiplas Mensagens
- Envie vários pedidos rapidamente
- Observe processamento paralelo (até 5 consumidores)

## 📊 Monitoramento

### Interface RabbitMQ
- **Queues**: Visualizar mensagens pendentes/processadas
- **Exchanges**: Verificar roteamento
- **Connections**: Monitorar conexões ativas

### Logs da Aplicação
- Publicação de mensagens
- Processamento e validações
- Confirmações e rejeições
- Tratamento de erros