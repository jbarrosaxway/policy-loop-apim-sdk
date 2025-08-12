#!/usr/bin/env groovy

/**
 * PolicyLoopExecutor.groovy
 * 
 * Script Groovy simples que simula a funcionalidade do CircuitLoopProcessor
 * para executar uma policy em loop com controle de condição, máximo de iterações
 * e timeout.
 * 
 * Uso: groovy PolicyLoopExecutor.groovy <caminho_policy> [opções]
 */

import groovy.cli.commons.CliBuilder
import groovy.cli.commons.OptionAccessor

class PolicyLoopExecutor {
    
    // Constantes para tipos de loop (baseado no CircuitLoopProcessor)
    static final int LOOPTYPE_WHILE = 1
    static final int LOOPTYPE_DOWHILE = 2
    
    // Configurações do loop
    int loopType = LOOPTYPE_WHILE
    String loopCondition = "true"  // Expressão Groovy para condição
    int loopMax = 10               // Máximo de iterações
    int loopTimeout = 10000        // Timeout em milissegundos
    boolean loopErrorCircuit = true
    boolean loopErrorCondition = false
    boolean loopErrorMax = false
    boolean loopTimeout = false
    boolean loopErrorEmpty = false
    
    // Contadores e estado
    int currentCount = 0
    long startTime
    String policyPath
    
    /**
     * Construtor principal
     */
    PolicyLoopExecutor(String policyPath) {
        this.policyPath = policyPath
        this.startTime = System.currentTimeMillis()
    }
    
    /**
     * Executa o loop principal baseado no tipo selecionado
     */
    boolean executeLoop() {
        println "🚀 Iniciando execução da policy em loop: ${policyPath}"
        println "📋 Configurações:"
        println "   - Tipo de loop: ${loopType == LOOPTYPE_WHILE ? 'WHILE' : 'DO-WHILE'}"
        println "   - Condição: ${loopCondition}"
        println "   - Máximo de iterações: ${loopMax}"
        println "   - Timeout: ${loopTimeout}ms"
        println "   - Tratamento de erros: ${getErrorHandlingSummary()}"
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
                    result = !loopErrorEmpty
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
            
            println "🔄 Executando iteração ${count}..."
            
            // Simula execução da policy
            boolean loopResult = executePolicy()
            
            if (!loopResult) {
                println "❌ Erro na execução da policy (iteração ${count})"
                result = !loopErrorCircuit
                break
            }
            
            // Verifica timeout
            if (loopTimeout > 0) {
                long elapsed = System.currentTimeMillis() - startTime
                if (elapsed > loopTimeout) {
                    println "⏰ Timeout atingido após ${elapsed}ms"
                    result = !loopTimeout
                    break
                }
            }
            
            // Verifica máximo de iterações
            if (loopMax > 0 && count >= loopMax) {
                println "🔢 Máximo de iterações atingido (${loopMax})"
                result = !loopErrorMax
                break
            }
            
            // Verifica condição para próxima iteração
            shouldLoop = evaluateCondition()
            if (!shouldLoop) {
                println "✅ Condição de loop não mais atendida"
                result = !loopErrorCondition
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
     * Avalia a condição do loop usando Groovy
     */
    private boolean evaluateCondition() {
        try {
            // Cria um binding com variáveis úteis
            def binding = new Binding()
            binding.loopCount = currentCount
            binding.startTime = startTime
            binding.currentTime = System.currentTimeMillis()
            binding.elapsedTime = System.currentTimeMillis() - startTime
            
            // Avalia a expressão Groovy
            def shell = new GroovyShell(binding)
            def result = shell.evaluate(loopCondition)
            
            return result as boolean
        } catch (Exception e) {
            println "⚠️  Erro ao avaliar condição '${loopCondition}': ${e.message}"
            return false
        }
    }
    
    /**
     * Simula a execução da policy
     */
    private boolean executePolicy() {
        try {
            // Simula execução da policy
            println "   📄 Executando policy: ${policyPath}"
            
            // Aqui você pode adicionar a lógica real de execução da policy
            // Por exemplo, chamar uma API, executar um comando, etc.
            
            // Simula um pequeno delay para demonstração
            Thread.sleep(100)
            
            // Simula sucesso (você pode modificar para retornar o resultado real)
            return true
            
        } catch (Exception e) {
            println "   ❌ Erro na execução: ${e.message}"
            return false
        }
    }
    
    /**
     * Retorna resumo do tratamento de erros
     */
    private String getErrorHandlingSummary() {
        def errors = []
        if (loopErrorCircuit) errors << "erro de circuito"
        if (loopErrorCondition) errors << "erro de condição"
        if (loopErrorMax) errors << "erro de máximo"
        if (loopTimeout) errors << "erro de timeout"
        if (loopErrorEmpty) errors << "erro de vazio"
        
        return errors.isEmpty() ? "nenhum" : errors.join(", ")
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
    
    /**
     * Configura tratamento de erros
     */
    void configureErrorHandling(boolean circuit, boolean condition, boolean max, boolean timeout, boolean empty) {
        this.loopErrorCircuit = circuit
        this.loopErrorCondition = condition
        this.loopErrorMax = max
        this.loopTimeout = timeout
        this.loopErrorEmpty = empty
    }
}

/**
 * Função principal para executar o script
 */
def main() {
    def cli = new CliBuilder(usage: 'groovy PolicyLoopExecutor.groovy <caminho_policy> [opções]')
    cli.with {
        h longOpt: 'help', 'Mostra esta ajuda'
        t longOpt: 'type', args: 1, argName: 'tipo', 'Tipo de loop (1=WHILE, 2=DO-WHILE)', defaultValue: '1'
        c longOpt: 'condition', args: 1, argName: 'condicao', 'Condição do loop (expressão Groovy)', defaultValue: 'true'
        m longOpt: 'max', args: 1, argName: 'maximo', 'Máximo de iterações', defaultValue: '10'
        o longOpt: 'timeout', args: 1, argName: 'timeout', 'Timeout em milissegundos', defaultValue: '10000'
        e longOpt: 'error-circuit', 'Tratar erro de circuito como erro'
        n longOpt: 'no-error-condition', 'Não tratar erro de condição como erro'
        x longOpt: 'no-error-max', 'Não tratar erro de máximo como erro'
        s longOpt: 'no-error-timeout', 'Não tratar erro de timeout como erro'
        v longOpt: 'no-error-empty', 'Não tratar erro de vazio como erro'
    }
    
    OptionAccessor options = cli.parse(args)
    
    if (options.help || options.arguments().size() < 1) {
        cli.usage()
        System.exit(0)
    }
    
    String policyPath = options.arguments()[0]
    
    // Cria o executor
    def executor = new PolicyLoopExecutor(policyPath)
    
    // Configura parâmetros
    executor.configureLoop(
        options.type.toInteger(),
        options.condition,
        options.max.toInteger(),
        options.timeout.toInteger()
    )
    
    // Configura tratamento de erros
    executor.configureErrorHandling(
        options.'error-circuit',
        !options.'no-error-condition',
        !options.'no-error-max',
        !options.'no-error-timeout',
        !options.'no-error-empty'
    )
    
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
if (this.class.name == 'PolicyLoopExecutor') {
    main()
}
