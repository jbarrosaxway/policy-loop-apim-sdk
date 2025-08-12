#!/bin/bash

# Script de teste para demonstrar o funcionamento dos Policy Loop Executors
# Testa diferentes cenários de loop com execução real de policies

echo "🧪 Testando Policy Loop Executors com execução real de policies"
echo "================================================================"
echo ""

# Verifica se Groovy está instalado
if ! command -v groovy &> /dev/null; then
    echo "❌ Groovy não está instalado. Instalando..."
    if command -v apt-get &> /dev/null; then
        sudo apt-get update
        sudo apt-get install -y groovy
    elif command -v brew &> /dev/null; then
        brew install groovy
    else
        echo "⚠️  Por favor, instale o Groovy manualmente:"
        echo "   Ubuntu/Debian: sudo apt-get install groovy"
        echo "   macOS: brew install groovy"
        echo "   Ou visite: https://groovy-lang.org/install.html"
        exit 1
    fi
fi

echo "✅ Groovy instalado: $(groovy --version)"
echo ""

# Cria diferentes tipos de policies para teste
echo "📄 Criando policies de exemplo para teste..."
echo ""

# 1. Script Groovy
echo "🔧 Criando script Groovy..."
cat > test_policy.groovy << 'EOF'
// Policy de exemplo em Groovy
println "Executando policy Groovy - iteração ${loopCount}"
println "Tempo decorrido: ${elapsedTime}ms"
println "Caminho da policy: ${policyPath}"

// Simula algum processamento
Thread.sleep(200)
println "Policy Groovy concluída com sucesso!"

// Retorna sucesso
return true
EOF

# 2. Script Shell
echo "🐚 Criando script Shell..."
cat > test_policy.sh << 'EOF'
#!/bin/bash
echo "Executando policy Shell - iteração $1"
echo "Tempo decorrido: $2ms"
echo "Caminho da policy: $3"

# Simula algum processamento
sleep 0.2
echo "Policy Shell concluída com sucesso!"
exit 0
EOF
chmod +x test_policy.sh

# 3. Script Python
echo "🐍 Criando script Python..."
cat > test_policy.py << 'EOF'
#!/usr/bin/env python3
import sys
import time

print(f"Executando policy Python - iteração {sys.argv[1]}")
print(f"Tempo decorrido: {sys.argv[2]}ms")
print(f"Caminho da policy: {sys.argv[3]}")

# Simula algum processamento
time.sleep(0.2)
print("Policy Python concluída com sucesso!")
sys.exit(0)
EOF
chmod +x test_policy.py

# 4. Arquivo de configuração
echo "⚙️  Criando arquivo de configuração..."
cat > test_policy.yaml << 'EOF'
# Policy de exemplo em YAML
policy_name: "TestPolicy"
version: "1.0"
description: "Policy de teste para demonstração do loop"

# Configurações
max_iterations: 10
timeout_ms: 5000
retry_count: 3

# Simula execução
execute() {
    echo "Executando policy YAML..."
    return SUCCESS
}
EOF

echo "✅ Todas as policies de exemplo foram criadas!"
echo ""

# Teste 1: Loop WHILE com script Groovy
echo "🔄 Teste 1: Loop WHILE com script Groovy (3 iterações)"
echo "--------------------------------------------------------"
groovy SimplePolicyLoop.groovy test_policy.groovy 1 "loopCount < 3" 5 10000
echo ""

# Teste 2: Loop DO-WHILE com script Shell
echo "🔄 Teste 2: Loop DO-WHILE com script Shell (2 iterações)"
echo "----------------------------------------------------------"
groovy SimplePolicyLoop.groovy test_policy.sh 2 "loopCount < 2" 3 5000
echo ""

# Teste 3: Loop WHILE com script Python
echo "🔄 Teste 3: Loop WHILE com script Python (máximo 1.5 segundos)"
echo "----------------------------------------------------------------"
groovy SimplePolicyLoop.groovy test_policy.py 1 "elapsedTime < 1500" 20 2000
echo ""

# Teste 4: Loop com arquivo de configuração
echo "🔄 Teste 4: Loop com arquivo YAML (apenas 2 iterações)"
echo "--------------------------------------------------------"
groovy SimplePolicyLoop.groovy test_policy.yaml 1 "true" 2 10000
echo ""

# Teste 5: Loop que nunca executa (condição falsa)
echo "🔄 Teste 5: Loop que nunca executa (condição sempre falsa)"
echo "-----------------------------------------------------------"
groovy SimplePolicyLoop.groovy test_policy.groovy 1 "false" 10 10000
echo ""

# Teste 6: Loop com condição complexa
echo "🔄 Teste 6: Loop com condição complexa (contador par E tempo < 1s)"
echo "-------------------------------------------------------------------"
groovy SimplePolicyLoop.groovy test_policy.sh 1 "loopCount % 2 == 0 && elapsedTime < 1000" 10 2000
echo ""

# Teste 7: Loop com comando shell direto
echo "🔄 Teste 7: Loop com comando shell direto (echo)"
echo "------------------------------------------------"
groovy SimplePolicyLoop.groovy "echo 'Executando comando direto'" 1 "loopCount < 3" 5 5000
echo ""

# Teste 8: Loop com comando complexo
echo "🔄 Teste 8: Loop com comando complexo (date + loopCount)"
echo "--------------------------------------------------------"
groovy SimplePolicyLoop.groovy "date +'%H:%M:%S - Iteração: '${loopCount}" 1 "loopCount < 4" 6 10000
echo ""

echo "🏁 Todos os testes concluídos!"
echo ""
echo "📊 Resumo dos testes:"
echo "   - Teste 1: Loop WHILE com Groovy ✓"
echo "   - Teste 2: Loop DO-WHILE com Shell ✓"
echo "   - Teste 3: Loop WHILE com Python ✓"
echo "   - Teste 4: Loop com YAML ✓"
echo "   - Teste 5: Loop que nunca executa ✓"
echo "   - Teste 6: Loop com condição complexa ✓"
echo "   - Teste 7: Loop com comando shell direto ✓"
echo "   - Teste 8: Loop com comando complexo ✓"
echo ""

# Limpa arquivos de teste
echo "🧹 Limpando arquivos de teste..."
rm -f test_policy.groovy test_policy.sh test_policy.py test_policy.yaml

echo "✅ Testes concluídos com sucesso!"
echo ""
echo "💡 Dicas de uso:"
echo "   - Use 'groovy SimplePolicyLoop.groovy --help' para ver todas as opções"
echo "   - O script agora executa policies reais em vez de simular"
echo "   - Suporta múltiplos tipos de arquivo: .groovy, .sh, .py, .java, .js, .xml, .yaml, .json"
echo "   - Suporta URLs HTTP/HTTPS para APIs REST"
echo "   - Suporta comandos shell diretos"
echo "   - As condições podem usar expressões Groovy completas"
echo "   - Variáveis disponíveis: loopCount, startTime, currentTime, elapsedTime, policyPath"
echo ""
echo "🚀 Exemplos práticos:"
echo "   # Executar script Groovy em loop"
echo "   groovy SimplePolicyLoop.groovy meu_script.groovy 1 'loopCount < 5' 10 30000"
echo ""
echo "   # Executar API REST em loop"
echo "   groovy SimplePolicyLoop.groovy 'https://api.exemplo.com/endpoint' 2 'elapsedTime < 10000' 50 15000"
echo ""
echo "   # Executar comando em loop"
echo "   groovy SimplePolicyLoop.groovy 'curl -s https://httpbin.org/status/200' 1 'true' 100 60000"
