# Policy Loop Executor

Este é um script Groovy que implementa a mesma funcionalidade do `CircuitLoopProcessor` para executar policies em loop com controle de condição, máximo de iterações e timeout.

## Funcionalidades

O script implementa as mesmas funcionalidades principais do `CircuitLoopProcessor`:

- **Tipos de Loop**: 
  - `WHILE` (1): Verifica condição antes de executar
  - `DO-WHILE` (2): Executa pelo menos uma vez e depois verifica condição

- **Controles**:
  - Condição de loop (expressão Groovy)
  - Máximo de iterações
  - Timeout em milissegundos
  - Tratamento configurável de erros

- **Execução Real de Policies**:
  - Scripts Groovy, Java, Shell, Python, Node.js
  - Arquivos de configuração (XML, YAML, JSON)
  - APIs REST (HTTP/HTTPS)
  - Comandos shell diretos

## Pré-requisitos

- Groovy instalado (versão 2.0+)
- Dependências opcionais para funcionalidades específicas:
  - Python 3 para scripts .py
  - Node.js para scripts .js
  - Java JDK para arquivos .java

## Instalação das Dependências

```bash
# Instalar Groovy (Ubuntu/Debian)
sudo apt-get install groovy

# Ou usando SDKMAN
sdk install groovy

# Dependências opcionais
sudo apt-get install python3 nodejs openjdk-11-jdk
```

## Uso Básico

```bash
# Execução simples com valores padrão
groovy SimplePolicyLoop.groovy /caminho/para/policy

# Execução com parâmetros personalizados
groovy SimplePolicyLoop.groovy /caminho/para/policy 2 "loopCount < 5" 3 5000
```

## Parâmetros

| Posição | Descrição | Padrão |
|----------|-----------|---------|
| 1 | Caminho para a policy | Obrigatório |
| 2 | Tipo de loop (1=WHILE, 2=DO-WHILE) | 1 |
| 3 | Condição do loop (expressão Groovy) | "true" |
| 4 | Máximo de iterações | 10 |
| 5 | Timeout em milissegundos | 10000 |

## Tipos de Policy Suportados

### 1. Arquivos Locais
- **`.groovy`**: Scripts Groovy executados diretamente
- **`.java`**: Arquivos Java compilados e executados
- **`.sh`/`.bash`**: Scripts shell executados com bash
- **`.py`**: Scripts Python executados com python3
- **`.js`**: Scripts Node.js executados com node
- **`.xml`/`.yaml`/`.yml`/`.json`**: Arquivos de configuração processados

### 2. URLs
- **`http://`/`https://`**: APIs REST executadas via HTTP

### 3. Comandos
- **Comandos shell**: Executados diretamente no sistema
- **Protocolos customizados**: Executados como comandos

## Exemplos de Uso

### 1. Loop WHILE com script Groovy
```bash
groovy SimplePolicyLoop.groovy meu_script.groovy 1 "loopCount < 3" 5 10000
```

### 2. Loop DO-WHILE com script Shell
```bash
groovy SimplePolicyLoop.groovy meu_script.sh 2 "elapsedTime < 2000" 100 3000
```

### 3. Loop com script Python
```bash
groovy SimplePolicyLoop.groovy meu_script.py 1 "loopCount < 10 && elapsedTime < 5000" 20 10000
```

### 4. Loop com API REST
```bash
groovy SimplePolicyLoop.groovy "https://api.exemplo.com/endpoint" 1 "true" 50 30000
```

### 5. Loop com comando shell
```bash
groovy SimplePolicyLoop.groovy "curl -s https://httpbin.org/status/200" 1 "loopCount < 5" 10 15000
```

## Variáveis Disponíveis na Condição

Na expressão de condição, você pode usar as seguintes variáveis:

- `loopCount`: Número da iteração atual
- `startTime`: Timestamp de início (milissegundos)
- `currentTime`: Timestamp atual (milissegundos)
- `elapsedTime`: Tempo decorrido desde o início (milissegundos)
- `policyPath`: Caminho da policy sendo executada

### Exemplos de Condições

```groovy
// Executar apenas 3 vezes
"loopCount < 3"

// Executar por no máximo 5 segundos
"elapsedTime < 5000"

// Executar até que o tempo seja par
"currentTime % 2 == 0"

// Executar enquanto o contador for menor que 10 E tempo menor que 3s
"loopCount < 10 && elapsedTime < 3000"

// Executar apenas em iterações pares
"loopCount % 2 == 0"

// Executar baseado no caminho da policy
"policyPath.contains('test') && loopCount < 5"
```

## Arquitetura Interna

O script simula a arquitetura do `CircuitLoopProcessor`:

- **Message Context**: Simula o objeto `Message` do projeto original
- **Condition Evaluation**: Simula `Selector.substitute(m)` usando Groovy
- **Policy Execution**: Simula `InvocationEngine.invokeCircuit()` com execução real
- **Error Handling**: Implementa a mesma lógica de tratamento de erros

### Mapeamento com o Projeto Original

| Projeto Original | Script Groovy |
|------------------|----------------|
| `Message m` | `Map<String, Object> messageContext` |
| `Selector.substitute(m)` | `GroovyShell.evaluate(condition)` |
| `InvocationEngine.invokeCircuit()` | `executePolicy()` com execução real |
| `m.put("loopCount", count)` | `messageContext.loopCount = count` |

## Personalização Avançada

### Adicionar Novos Tipos de Policy

Para adicionar suporte a novos tipos de arquivo, modifique o método `executeLocalPolicy()`:

```groovy
case 'novo':
    // Sua lógica de execução aqui
    println "   🆕 Executando novo tipo de policy..."
    return executeNovoTipo(policyFile)
```

### Integração com Sistemas Externos

Para integrar com sistemas específicos, modifique o método `executePolicy()`:

```groovy
private boolean executePolicy() {
    try {
        // Sua lógica de integração aqui
        // Por exemplo, chamar APIs específicas, executar workflows, etc.
        
        return true // ou false baseado no resultado
    } catch (Exception e) {
        println "Erro na execução: ${e.message}"
        return false
    }
}
```

## Saída do Script

O script fornece saída detalhada com emojis para facilitar a leitura:

```
🚀 Iniciando execução da policy em loop: /path/to/policy
📋 Configurações:
   - Tipo de loop: WHILE
   - Condição: loopCount < 3
   - Máximo de iterações: 5
   - Timeout: 10000ms

🔄 Executando iteração 1...
   📄 Executando policy: /path/to/policy
   🔧 Executando script Groovy...
🔄 Executando iteração 2...
   📄 Executando policy: /path/to/policy
   🔧 Executando script Groovy...
🔄 Executando iteração 3...
   📄 Executando policy: /path/to/policy
   🔧 Executando script Groovy...
✅ Condição de loop não mais atendida

🏁 Execução finalizada:
   - Total de iterações: 3
   - Tempo total: 650ms
   - Resultado: SUCESSO
```

## Códigos de Saída

- `0`: Execução bem-sucedida
- `1`: Erro na execução ou condição de erro atendida

## Compatibilidade

Este script é compatível com:
- Groovy 2.0+
- Java 8+
- Sistemas Unix/Linux/macOS
- Windows (com Groovy instalado)

## Troubleshooting

### Erro: "groovy: command not found"
```bash
# Instalar Groovy
sudo apt-get install groovy
# ou
sdk install groovy
```

### Erro: "python3: command not found"
```bash
# Instalar Python 3
sudo apt-get install python3
```

### Erro: "node: command not found"
```bash
# Instalar Node.js
sudo apt-get install nodejs
```

### Erro de permissão
```bash
# Tornar executável
chmod +x SimplePolicyLoop.groovy
```

### Erro ao executar policy
- Verifique se o arquivo existe e tem permissões de execução
- Para scripts, verifique se as dependências estão instaladas
- Para APIs, verifique conectividade de rede

## Testes

Execute o script de teste para verificar todas as funcionalidades:

```bash
chmod +x test_policy_loop.sh
./test_policy_loop.sh
```

## Contribuição

Para contribuir com melhorias:
1. Modifique o script conforme necessário
2. Teste com diferentes tipos de policies
3. Atualize a documentação
4. Execute os testes para verificar funcionalidade
