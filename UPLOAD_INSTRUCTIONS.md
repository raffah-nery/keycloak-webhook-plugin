# Instruções para Upload do Plugin Otimizado

Para disponibilizar o plugin otimizado no GitHub, siga estas etapas:

## 1. Criar uma Release no GitHub

1. Acesse seu repositório no GitHub: https://github.com/raffah-nery/keycloak-webhook-plugin
2. Clique em "Releases" na barra lateral direita
3. Clique em "Create a new release"
4. Preencha os campos:
   - Tag version: `v0.8.4-optimized`
   - Release title: `Keycloak Webhook Plugin - Otimizado para Conexões AMQP`
   - Description:
     ```markdown
     Versão otimizada do plugin de webhook para Keycloak com correção para múltiplas conexões AMQP.
     
     ## Problemas Corrigidos
     - Resolvido problema de múltiplas conexões AMQP sendo criadas
     - Implementado padrão Singleton para gerenciar conexões
     - Adicionado suporte a configurações avançadas para conexões
     - Melhorado gerenciamento de recursos
     
     ## Uso
     Veja o arquivo README para instruções detalhadas.
     ```

5. Faça upload dos arquivos JAR compilados:
   - keycloak-webhook-provider-core-0.8.4.jar
   - keycloak-webhook-provider-amqp-0.8.4.jar

6. Clique em "Publish release"

## 2. Como Compilar os JARs

Se você precisar recompilar os JARs:

```bash
cd /Users/rafaelnery/IdeaProjects/development-workbench/infrastructure/k8s/keycloak/plugin
./gradlew clean build
```

Os JARs compilados estarão disponíveis em:
- keycloak-webhook-provider-core/build/libs/keycloak-webhook-provider-core-0.8.4.jar
- keycloak-webhook-provider-amqp/build/libs/keycloak-webhook-provider-amqp-0.8.4.jar

## 3. Validando a Correção

Depois de aplicar o deployment atualizado, monitore os logs do Keycloak para verificar:

1. Mensagens indicando inicialização única das conexões
2. Ausência de múltiplas mensagens de "Creating executor service"
3. Logs de mensagens sendo enviadas com sucesso

Comando para verificar:
```bash
kubectl logs -n huboh-infra <pod-keycloak> | grep -E "Creating executor service|connection|Connection|AmqpWebhookHandler"
```

Você deve ver uma conexão sendo criada apenas uma vez, em vez de múltiplas conexões.