# Keycloak Webhook AMQP - Versão Otimizada para Conexões

Este é um fork do plugin [vymalo/keycloak-webhook](https://github.com/vymalo/keycloak-webhook) com correções para o problema de múltiplas conexões AMQP sendo criadas.

## Problema Resolvido

No plugin original, para cada evento processado pelo Keycloak (login, registro, etc.), uma nova conexão AMQP era criada, levando a:
- Sobrecarga no servidor RabbitMQ
- Vazamento de recursos no Keycloak
- Alto consumo de memória
- Erros de conexão frequentes

## Melhorias Implementadas

1. **Padrão Singleton para conexões AMQP**
   - Agora apenas uma conexão AMQP é criada e reutilizada
   - Uso de `AmqpConnectionSingleton` para garantir instância única

2. **Gerenciamento otimizado de conexões**
   - Verificação de inicialização para evitar reinicializações desnecessárias
   - Sistema de locks para acesso thread-safe à conexão
   - Fechamento adequado de recursos quando não mais necessários

3. **Suporte a configurações avançadas de conexão**
   - Timeout de conexão configurável
   - Recuperação automática de falhas
   - Heartbeat configurável
   - Limite máximo de canais por conexão

## Como usar

### Opção 1: Baixar releases pré-compiladas
```
https://github.com/[seu-usuario]/keycloak-webhook/releases
```

### Opção 2: Compilar do código fonte
```bash
./gradlew clean build
```

## Configurações de Conexão Disponíveis

| Variável de Ambiente | Descrição | Valor Padrão |
|----------------------|-----------|--------------|
| WEBHOOK_AMQP_CONNECTION_TIMEOUT | Timeout de conexão em ms | 30000 |
| WEBHOOK_AMQP_HEARTBEAT | Intervalo de heartbeat em segundos | 60 |
| WEBHOOK_AMQP_AUTOMATIC_RECOVERY | Habilitar recuperação automática | true |
| WEBHOOK_AMQP_TOPOLOGY_RECOVERY_ENABLED | Habilitar recuperação de topologia | true |
| WEBHOOK_AMQP_CONNECTION_RECOVERY_ENABLED | Habilitar recuperação de conexão | true |
| WEBHOOK_AMQP_NETWORK_RECOVERY_INTERVAL | Intervalo de tentativas de reconexão em ms | 5000 |
| WEBHOOK_AMQP_REQUESTED_CHANNEL_MAX | Número máximo de canais por conexão | 10 |
| WEBHOOK_AMQP_REQUESTED_FRAME_MAX | Tamanho máximo de frame | 0 |