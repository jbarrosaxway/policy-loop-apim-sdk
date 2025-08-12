#!/usr/bin/env groovy

/**
 * SimplePolicyLoop.groovy
 * 
 * Versão simplificada do PolicyLoopExecutor que implementa a mesma lógica
 * do CircuitLoopProcessor para executar policies em loop.
 * 
 * Uso: groovy SimplePolicyLoop.groovy <caminho_policy> [tipo] [condicao] [max] [timeout]
 */

class SimplePolicyLoop {
    
    // Constantes para tipos de loop (baseado no CircuitLoopProcessor)
    static final int LOOPTYPE_WHILE = 1
    static final int LOOPTYPE_DOWHILE = 2
    
    // Configurações do loop
    int loopType = LOOPTYPE_WHILE
    String loopCondition = "true"
    int loopMax = 10
    int loopTimeout = 10000
    
    // Contadores e estado
    int currentCount = 0
    long startTime
    String policyPath
    
    // Contexto da policy (simula o Message do projeto original)
    Map<String, Object> messageContext = [:]
    
    /**
     * Construtor principal
     */
    SimplePolicyLoop(String policyPath) {
        this.policyPath = policyPath
        this.startTime = System.currentTimeMillis()
        initializeMessageContext()
    }
    
    /**
     * Inicializa o contexto da mensagem (simula o Message do projeto original)
     */
    private void initializeMessageContext() {
        messageContext = [
            'loopCount': 0,
            'startTime': startTime,
            'currentTime': System.currentTimeMillis(),
            'elapsedTime': 0,
            'policyPath': policyPath
        ]
    }
    
    /**
     * Executa o loop principal
     */
    boolean executeLoop() {
        println "🚀 Iniciando execução da policy em loop: ${policyPath}"
        println "📋 Configurações:"
        println "   - Tipo de loop: ${loopType == LOOPTYPE_WHILE ? 'WHILE' : 'DO-WHILE'}"
        println "   - Condição: ${loopCondition}"
        println "   - Máximo de iterações: ${loopMax}"
        println "   - Timeout: ${loopTimeout}ms"
        println ""
        
        boolean result = true
        boolean shouldLoop = false
        int count = 0
        
        // Lógica baseada no CircuitLoopProcessor.invoke()
        switch (loopType) {
            case LOOPTYPE_WHILE:
                // Verifica condição antes da primeira execução
                shouldLoop = evaluateCondition()
                if (!shouldLoop) {
                    println "⏭️  Condição inicial não atendida, pulando loop"
                    return true
                }
                break
                
            case LOOPTYPE_DOWHILE:
                // Executa pelo menos uma vez
                shouldLoop = true
                break
                
            default:
                throw new IllegalArgumentException("Tipo de loop inválido: ${loopType} (apenas 1 e 2 são permitidos)")
        }
        
        // Loop principal
        while (shouldLoop) {
            count++
            currentCount = count
            
            // Atualiza o contexto da mensagem (simula m.put("loopCount", count))
            messageContext.loopCount = count
            messageContext.currentTime = System.currentTimeMillis()
            messageContext.elapsedTime = System.currentTimeMillis() - startTime
            
            println "🔄 Executando iteração ${count}..."
            
            // Executa a policy (simula executeLoop do projeto original)
            boolean loopResult = executePolicy()
            
            if (!loopResult) {
                println "❌ Erro na execução da policy (iteração ${count})"
                result = false
                break
            }
            
            // Verifica timeout
            if (loopTimeout > 0) {
                long elapsed = System.currentTimeMillis() - startTime
                if (elapsed > loopTimeout) {
                    println "⏰ Timeout atingido após ${elapsed}ms"
                    result = false
                    break
                }
            }
            
            // Verifica máximo de iterações
            if (loopMax > 0 && count >= loopMax) {
                println "🔢 Máximo de iterações atingido (${loopMax})"
                result = false
                break
            }
            
            // Verifica condição para próxima iteração
            shouldLoop = evaluateCondition()
            if (!shouldLoop) {
                println "✅ Condição de loop não mais atendida"
            }
        }
        
        println ""
        println "🏁 Execução finalizada:"
        println "   - Total de iterações: ${count}"
        println "   - Tempo total: ${System.currentTimeMillis() - startTime}ms"
        println "   - Resultado: ${result ? 'SUCESSO' : 'ERRO'}"
        
        return result
    }
    
    /**
     * Avalia a condição do loop usando Groovy (simula condition(m) do projeto original)
     */
    private boolean evaluateCondition() {
        try {
            // Cria um binding com variáveis úteis (simula o contexto da mensagem)
            def binding = new Binding()
            binding.loopCount = messageContext.loopCount
            binding.startTime = messageContext.startTime
            binding.currentTime = messageContext.currentTime
            binding.elapsedTime = messageContext.elapsedTime
            binding.policyPath = messageContext.policyPath
            
            // Avalia a expressão Groovy (simula Selector.substitute(m))
            def shell = new GroovyShell(binding)
            def result = shell.evaluate(loopCondition)
            
            return result as boolean
        } catch (Exception e) {
            println "⚠️  Erro ao avaliar condição '${loopCondition}': ${e.message}"
            return false
        }
    }
    
    /**
     * Executa a policy real (simula executeLoop do projeto original)
     * Baseado em: InvocationEngine.invokeCircuit(loopCircuit, loopContext, m)
     */
    private boolean executePolicy() {
        try {
            println "   📄 Executando policy: ${policyPath}"
            
            // Simula a execução da policy usando diferentes estratégias
            // baseadas no tipo de arquivo/caminho fornecido
            
            def policyFile = new File(policyPath)
            
            if (policyFile.exists()) {
                // Se é um arquivo local, executa baseado na extensão
                return executeLocalPolicy(policyFile)
            } else if (policyPath.startsWith("http://") || policyPath.startsWith("https://")) {
                // Se é uma URL, executa como API REST
                return executeRestPolicy(policyPath)
            } else if (policyPath.contains("://")) {
                // Se é outro protocolo, executa como comando
                return executeCommandPolicy(policyPath)
            } else {
                // Tenta executar como comando shell
                return executeShellPolicy(policyPath)
            }
            
        } catch (Exception e) {
            println "   ❌ Erro na execução: ${e.message}"
            return false
        }
    }
    
    /**
     * Executa policy local baseada na extensão do arquivo
     */
    private boolean executeLocalPolicy(File policyFile) {
        try {
            def extension = policyFile.extension?.toLowerCase()
            
            switch (extension) {
                case 'groovy':
                    // Executa script Groovy
                    println "   🔧 Executando script Groovy..."
                    def result = new GroovyShell().evaluate(policyFile.text)
                    return result != null
                    
                case 'java':
                    // Compila e executa Java
                    println "   ☕ Executando arquivo Java..."
                    def process = "javac ${policyFile.absolutePath}".execute()
                    process.waitFor()
                    if (process.exitValue() == 0) {
                        def className = policyFile.name - '.java'
                        def mainProcess = "java -cp ${policyFile.parent} ${className}".execute()
                        mainProcess.waitFor()
                        return mainProcess.exitValue() == 0
                    }
                    return false
                    
                case 'sh':
                case 'bash':
                    // Executa script shell
                    println "   🐚 Executando script shell..."
                    def process = "bash ${policyFile.absolutePath}".execute()
                    process.waitFor()
                    return process.exitValue() == 0
                    
                case 'py':
                    // Executa script Python
                    println "   🐍 Executando script Python..."
                    def process = "python3 ${policyFile.absolutePath}".execute()
                    process.waitFor()
                    return process.exitValue() == 0
                    
                case 'js':
                    // Executa script Node.js
                    println "   📜 Executando script Node.js..."
                    def process = "node ${policyFile.absolutePath}".execute()
                    process.waitFor()
                    return process.exitValue() == 0
                    
                case 'xml':
                case 'yaml':
                case 'yml':
                case 'json':
                    // Processa arquivo de configuração
                    println "   ⚙️  Processando arquivo de configuração..."
                    def content = policyFile.text
                    // Simula processamento (você pode implementar lógica específica)
                    return content.length() > 0
                    
                default:
                    // Arquivo genérico - tenta executar como texto
                    println "   📝 Processando arquivo genérico..."
                    def content = policyFile.text
                    return content.length() > 0
            }
            
        } catch (Exception e) {
            println "   ❌ Erro ao executar policy local: ${e.message}"
            return false
        }
    }
    
    /**
     * Executa policy como API REST
     */
    private boolean executeRestPolicy(String url) {
        try {
            println "   🌐 Executando API REST..."
            
            // Simula chamada HTTP (você pode usar bibliotecas como HTTPBuilder)
            def connection = new URL(url).openConnection() as HttpURLConnection
            connection.requestMethod = 'GET'
            connection.connectTimeout = 5000
            connection.readTimeout = 10000
            
            def responseCode = connection.responseCode
            return responseCode >= 200 && responseCode < 300
            
        } catch (Exception e) {
            println "   ❌ Erro ao executar API REST: ${e.message}"
            return false
        }
    }
    
    /**
     * Executa policy como comando
     */
    private boolean executeCommandPolicy(String command) {
        try {
            println "   ⚡ Executando comando..."
            def process = command.execute()
            process.waitFor()
            return process.exitValue() == 0
            
        } catch (Exception e) {
            println "   ❌ Erro ao executar comando: ${e.message}"
            return false
        }
    }
    
    /**
     * Executa policy como comando shell
     */
    private boolean executeShellPolicy(String command) {
        try {
            println "   🐚 Executando comando shell..."
            def process = "bash -c '${command}'".execute()
            process.waitFor()
            return process.exitValue() == 0
            
        } catch (Exception e) {
            println "   ❌ Erro ao executar comando shell: ${e.message}"
            return false
        }
    }
    
    /**
     * Configura parâmetros do loop
     */
    void configureLoop(int type, String condition, int max, int timeout) {
        this.loopType = type
        this.loopCondition = condition
        this.loopMax = max
        this.loopTimeout = timeout
    }
}

/**
 * Função principal
 */
def main(args) {
    if (args.length < 1) {
        println "Uso: groovy SimplePolicyLoop.groovy <caminho_policy> [tipo] [condicao] [max] [timeout]"
        println ""
        println "Parâmetros:"
        println "  caminho_policy: Caminho para a policy a ser executada"
        println "  tipo: Tipo de loop (1=WHILE, 2=DO-WHILE) [padrão: 1]"
        println "  condicao: Condição do loop (expressão Groovy) [padrão: 'true']"
        println "  max: Máximo de iterações [padrão: 10]"
        println "  timeout: Timeout em milissegundos [padrão: 10000]"
        println ""
        println "Exemplos:"
        println "  groovy SimplePolicyLoop.groovy /path/to/policy"
        println "  groovy SimplePolicyLoop.groovy /path/to/policy 1 'loopCount < 3' 5 5000"
        println "  groovy SimplePolicyLoop.groovy /path/to/policy 2 'elapsedTime < 3000' 100 3000"
        println ""
        println "Tipos de Policy suportados:"
        println "  - Arquivos: .groovy, .java, .sh, .py, .js, .xml, .yaml, .json"
        println "  - URLs: http://, https://"
        println "  - Comandos: comandos shell diretos"
        System.exit(1)
    }
    
    String policyPath = args[0]
    
    // Cria o executor
    def executor = new SimplePolicyLoop(policyPath)
    
    // Configura parâmetros se fornecidos
    if (args.length >= 2) {
        int type = args[1].toInteger()
        String condition = args.length >= 3 ? args[2] : "true"
        int max = args.length >= 4 ? args[3].toInteger() : 10
        int timeout = args.length >= 5 ? args[4].toInteger() : 10000
        
        executor.configureLoop(type, condition, max, timeout)
    }
    
    // Executa o loop
    try {
        boolean result = executor.executeLoop()
        System.exit(result ? 0 : 1)
    } catch (Exception e) {
        println "💥 Erro fatal: ${e.message}"
        e.printStackTrace()
        System.exit(1)
    }
}

// Executa o script se for chamado diretamente
if (this.class.name == 'SimplePolicyLoop') {
    main(args)
}
