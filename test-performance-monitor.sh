#!/bin/bash

# Script de monitoring des performances des tests MedHead
# Collecte et analyse les métriques de performance des tests
# Auteur: Assistant IA
# Version: 1.0

set -e

# Configuration des couleurs
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
WHITE='\033[1;37m'
NC='\033[0m'

# Configuration des variables
PROJECT_ROOT="/home/hedi/projects/medhead"
REPORTS_DIR="$PROJECT_ROOT/reports"
PERFORMANCE_DIR="$REPORTS_DIR/performance"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
LOG_FILE="$PERFORMANCE_DIR/performance-monitor-${TIMESTAMP}.log"

# Variables de configuration
MONITOR_DURATION=300  # 5 minutes par défaut
COLLECT_SYSTEM_METRICS=true
COLLECT_DOCKER_METRICS=true
COLLECT_NETWORK_METRICS=true
GENERATE_PERFORMANCE_REPORT=true

# Fonctions utilitaires
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1" | tee -a "$LOG_FILE"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1" | tee -a "$LOG_FILE"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a "$LOG_FILE"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$LOG_FILE"
}

log_section() {
    echo -e "\n${PURPLE}========================================${NC}" | tee -a "$LOG_FILE"
    echo -e "${PURPLE}$1${NC}" | tee -a "$LOG_FILE"
    echo -e "${PURPLE}========================================${NC}\n" | tee -a "$LOG_FILE"
}

# Fonction pour créer les répertoires nécessaires
setup_directories() {
    mkdir -p "$PERFORMANCE_DIR"
    mkdir -p "$PERFORMANCE_DIR/system"
    mkdir -p "$PERFORMANCE_DIR/docker"
    mkdir -p "$PERFORMANCE_DIR/network"
    mkdir -p "$PERFORMANCE_DIR/applications"
}

# Fonction pour collecter les métriques système
collect_system_metrics() {
    if [ "$COLLECT_SYSTEM_METRICS" = true ]; then
        log_section "📊 Collecte des métriques système"
        
        local system_file="$PERFORMANCE_DIR/system/system-metrics-${TIMESTAMP}.csv"
        
        # En-tête du fichier CSV
        echo "timestamp,cpu_usage,memory_usage,disk_usage,load_average" > "$system_file"
        
        log_info "Collecte des métriques système pendant ${MONITOR_DURATION}s..."
        
        local start_time=$(date +%s)
        local end_time=$((start_time + MONITOR_DURATION))
        
        while [ $(date +%s) -lt $end_time ]; do
            local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
            local cpu_usage=$(top -bn1 | grep "Cpu(s)" | awk '{print $2}' | awk -F'%' '{print $1}')
            local memory_usage=$(free | grep Mem | awk '{printf("%.2f", $3/$2 * 100.0)}')
            local disk_usage=$(df / | tail -1 | awk '{print $5}' | sed 's/%//')
            local load_average=$(uptime | awk -F'load average:' '{print $2}' | awk '{print $1}' | sed 's/,//')
            
            echo "$timestamp,$cpu_usage,$memory_usage,$disk_usage,$load_average" >> "$system_file"
            
            sleep 5
        done
        
        log_success "Métriques système collectées dans $system_file"
    fi
}

# Fonction pour collecter les métriques Docker
collect_docker_metrics() {
    if [ "$COLLECT_DOCKER_METRICS" = true ] && command -v docker &> /dev/null; then
        log_section "🐳 Collecte des métriques Docker"
        
        local docker_file="$PERFORMANCE_DIR/docker/docker-metrics-${TIMESTAMP}.csv"
        
        # En-tête du fichier CSV
        echo "timestamp,container_name,cpu_percent,memory_usage,memory_limit,network_rx,network_tx" > "$docker_file"
        
        log_info "Collecte des métriques Docker pendant ${MONITOR_DURATION}s..."
        
        local start_time=$(date +%s)
        local end_time=$((start_time + MONITOR_DURATION))
        
        while [ $(date +%s) -lt $end_time ]; do
            local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
            
            # Obtenir les métriques de tous les conteneurs
            docker stats --no-stream --format "table {{.Container}},{{.CPUPerc}},{{.MemUsage}},{{.MemPerc}},{{.NetIO}}" | tail -n +2 | while read line; do
                if [ ! -z "$line" ]; then
                    local container=$(echo "$line" | awk '{print $1}')
                    local cpu_percent=$(echo "$line" | awk '{print $2}' | sed 's/%//')
                    local memory_usage=$(echo "$line" | awk '{print $3}' | awk -F'/' '{print $1}')
                    local memory_limit=$(echo "$line" | awk '{print $3}' | awk -F'/' '{print $2}')
                    local network_io=$(echo "$line" | awk '{print $4}')
                    local network_rx=$(echo "$network_io" | awk -F'/' '{print $1}')
                    local network_tx=$(echo "$network_io" | awk -F'/' '{print $2}')
                    
                    echo "$timestamp,$container,$cpu_percent,$memory_usage,$memory_limit,$network_rx,$network_tx" >> "$docker_file"
                fi
            done
            
            sleep 10
        done
        
        log_success "Métriques Docker collectées dans $docker_file"
    else
        log_warning "Docker non disponible, métriques Docker ignorées"
    fi
}

# Fonction pour collecter les métriques réseau
collect_network_metrics() {
    if [ "$COLLECT_NETWORK_METRICS" = true ]; then
        log_section "🌐 Collecte des métriques réseau"
        
        local network_file="$PERFORMANCE_DIR/network/network-metrics-${TIMESTAMP}.csv"
        
        # En-tête du fichier CSV
        echo "timestamp,bytes_received,bytes_sent,packets_received,packets_sent" > "$network_file"
        
        log_info "Collecte des métriques réseau pendant ${MONITOR_DURATION}s..."
        
        # Obtenir les statistiques réseau initiales
        local initial_stats=$(cat /proc/net/dev | grep -E "(eth0|enp|wlan)" | head -1)
        local initial_bytes_rx=$(echo "$initial_stats" | awk '{print $2}')
        local initial_bytes_tx=$(echo "$initial_stats" | awk '{print $10}')
        local initial_packets_rx=$(echo "$initial_stats" | awk '{print $3}')
        local initial_packets_tx=$(echo "$initial_stats" | awk '{print $11}')
        
        local start_time=$(date +%s)
        local end_time=$((start_time + MONITOR_DURATION))
        
        while [ $(date +%s) -lt $end_time ]; do
            local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
            
            local current_stats=$(cat /proc/net/dev | grep -E "(eth0|enp|wlan)" | head -1)
            local current_bytes_rx=$(echo "$current_stats" | awk '{print $2}')
            local current_bytes_tx=$(echo "$current_stats" | awk '{print $10}')
            local current_packets_rx=$(echo "$current_stats" | awk '{print $3}')
            local current_packets_tx=$(echo "$current_stats" | awk '{print $11}')
            
            local bytes_rx_diff=$((current_bytes_rx - initial_bytes_rx))
            local bytes_tx_diff=$((current_bytes_tx - initial_bytes_tx))
            local packets_rx_diff=$((current_packets_rx - initial_packets_rx))
            local packets_tx_diff=$((current_packets_tx - initial_packets_tx))
            
            echo "$timestamp,$bytes_rx_diff,$bytes_tx_diff,$packets_rx_diff,$packets_tx_diff" >> "$network_file"
            
            sleep 5
        done
        
        log_success "Métriques réseau collectées dans $network_file"
    fi
}

# Fonction pour surveiller les applications
monitor_applications() {
    log_section "🔍 Surveillance des applications"
    
    local app_file="$PERFORMANCE_DIR/applications/app-metrics-${TIMESTAMP}.csv"
    
    # En-tête du fichier CSV
    echo "timestamp,backend_status,frontend_status,postgres_status,pgadmin_status" > "$app_file"
    
    log_info "Surveillance des applications pendant ${MONITOR_DURATION}s..."
    
    local start_time=$(date +%s)
    local end_time=$((start_time + MONITOR_DURATION))
    
    while [ $(date +%s) -lt $end_time ]; do
        local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
        
        # Vérifier le statut du backend
        local backend_status="DOWN"
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            backend_status="UP"
        fi
        
        # Vérifier le statut du frontend
        local frontend_status="DOWN"
        if curl -s http://localhost:4200 > /dev/null 2>&1; then
            frontend_status="UP"
        fi
        
        # Vérifier le statut de PostgreSQL
        local postgres_status="DOWN"
        if docker ps | grep -q "medhead-postgres"; then
            postgres_status="UP"
        fi
        
        # Vérifier le statut de pgAdmin
        local pgadmin_status="DOWN"
        if curl -s http://localhost:8082 > /dev/null 2>&1; then
            pgadmin_status="UP"
        fi
        
        echo "$timestamp,$backend_status,$frontend_status,$postgres_status,$pgadmin_status" >> "$app_file"
        
        sleep 10
    done
    
    log_success "Surveillance des applications terminée dans $app_file"
}

# Fonction pour générer un rapport de performance
generate_performance_report() {
    if [ "$GENERATE_PERFORMANCE_REPORT" = true ]; then
        log_section "📊 Génération du rapport de performance"
        
        local report_file="$PERFORMANCE_DIR/performance-report-${TIMESTAMP}.html"
        
        cat > "$report_file" << EOF
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Rapport de Performance MedHead - $TIMESTAMP</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .header { text-align: center; color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 20px; margin-bottom: 30px; }
        .metrics-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px; }
        .metric-card { background: #ecf0f1; padding: 20px; border-radius: 8px; text-align: center; }
        .metric-card h3 { color: #2c3e50; margin: 0 0 10px 0; }
        .metric-value { font-size: 24px; font-weight: bold; color: #3498db; }
        .section { margin: 30px 0; }
        .section h2 { color: #2c3e50; border-left: 4px solid #3498db; padding-left: 15px; }
        .status-up { color: #27ae60; }
        .status-down { color: #e74c3c; }
        .chart-placeholder { background: #f8f9fa; border: 2px dashed #dee2e6; padding: 40px; text-align: center; color: #6c757d; }
        .timestamp { text-align: center; color: #7f8c8d; margin-top: 30px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>⚡ Rapport de Performance MedHead</h1>
            <p>Monitoring des performances du système et des applications</p>
            <p>Généré le: $(date '+%d/%m/%Y à %H:%M:%S')</p>
            <p>Durée de monitoring: ${MONITOR_DURATION}s</p>
        </div>
        
        <div class="metrics-grid">
            <div class="metric-card">
                <h3>CPU Moyen</h3>
                <div class="metric-value">$(get_average_cpu)</div>
                <p>Utilisation processeur</p>
            </div>
            <div class="metric-card">
                <h3>Mémoire Moyenne</h3>
                <div class="metric-value">$(get_average_memory)</div>
                <p>Utilisation mémoire</p>
            </div>
            <div class="metric-card">
                <h3>Disque</h3>
                <div class="metric-value">$(get_disk_usage)</div>
                <p>Utilisation disque</p>
            </div>
            <div class="metric-card">
                <h3>Applications UP</h3>
                <div class="metric-value status-up">$(get_apps_up_count)</div>
                <p>Services disponibles</p>
            </div>
        </div>
        
        <div class="section">
            <h2>📊 Métriques Collectées</h2>
            <ul>
                <li><strong>Système:</strong> CPU, Mémoire, Disque, Charge système</li>
                <li><strong>Docker:</strong> Utilisation ressources des conteneurs</li>
                <li><strong>Réseau:</strong> Trafic entrant/sortant</li>
                <li><strong>Applications:</strong> Statut des services MedHead</li>
            </ul>
        </div>
        
        <div class="section">
            <h2>📁 Fichiers de Données</h2>
            <ul>
                <li><a href="system/system-metrics-${TIMESTAMP}.csv">Métriques système (CSV)</a></li>
                <li><a href="docker/docker-metrics-${TIMESTAMP}.csv">Métriques Docker (CSV)</a></li>
                <li><a href="network/network-metrics-${TIMESTAMP}.csv">Métriques réseau (CSV)</a></li>
                <li><a href="applications/app-metrics-${TIMESTAMP}.csv">Statut applications (CSV)</a></li>
            </ul>
        </div>
        
        <div class="section">
            <h2>📈 Graphiques de Performance</h2>
            <div class="chart-placeholder">
                <p>Les graphiques peuvent être générés à partir des fichiers CSV</p>
                <p>Utilisez des outils comme Grafana, Excel, ou Python matplotlib</p>
            </div>
        </div>
        
        <div class="timestamp">
            <p>Rapport généré automatiquement par le moniteur de performance MedHead</p>
        </div>
    </div>
</body>
</html>
EOF
        
        log_success "Rapport de performance généré: $report_file"
        
        # Créer un lien symbolique vers le dernier rapport
        ln -sf "performance-report-${TIMESTAMP}.html" "$PERFORMANCE_DIR/latest-performance-report.html"
        log_success "Lien vers le dernier rapport: $PERFORMANCE_DIR/latest-performance-report.html"
    fi
}

# Fonctions utilitaires pour les métriques
get_average_cpu() {
    if [ -f "$PERFORMANCE_DIR/system/system-metrics-${TIMESTAMP}.csv" ]; then
        tail -n +2 "$PERFORMANCE_DIR/system/system-metrics-${TIMESTAMP}.csv" | awk -F',' '{sum+=$2; count++} END {if(count>0) printf "%.1f%%", sum/count; else print "N/A"}'
    else
        echo "N/A"
    fi
}

get_average_memory() {
    if [ -f "$PERFORMANCE_DIR/system/system-metrics-${TIMESTAMP}.csv" ]; then
        tail -n +2 "$PERFORMANCE_DIR/system/system-metrics-${TIMESTAMP}.csv" | awk -F',' '{sum+=$3; count++} END {if(count>0) printf "%.1f%%", sum/count; else print "N/A"}'
    else
        echo "N/A"
    fi
}

get_disk_usage() {
    df / | tail -1 | awk '{print $5}'
}

get_apps_up_count() {
    local count=0
    if [ -f "$PERFORMANCE_DIR/applications/app-metrics-${TIMESTAMP}.csv" ]; then
        local last_line=$(tail -1 "$PERFORMANCE_DIR/applications/app-metrics-${TIMESTAMP}.csv")
        if echo "$last_line" | grep -q "UP"; then
            count=$(echo "$last_line" | grep -o "UP" | wc -l)
        fi
    fi
    echo "$count/4"
}

# Fonction pour afficher le résumé
show_summary() {
    log_section "📋 Résumé du monitoring"
    
    echo -e "${WHITE}⏱️  Durée de monitoring:${NC} ${MONITOR_DURATION}s"
    echo -e "${WHITE}📊 Métriques collectées:${NC}"
    [ "$COLLECT_SYSTEM_METRICS" = true ] && echo -e "  ✅ Métriques système"
    [ "$COLLECT_DOCKER_METRICS" = true ] && echo -e "  ✅ Métriques Docker"
    [ "$COLLECT_NETWORK_METRICS" = true ] && echo -e "  ✅ Métriques réseau"
    echo -e "  ✅ Surveillance des applications"
    
    echo -e "\n${WHITE}📁 Fichiers générés:${NC}"
    echo -e "  📄 $PERFORMANCE_DIR/system/system-metrics-${TIMESTAMP}.csv"
    echo -e "  📄 $PERFORMANCE_DIR/docker/docker-metrics-${TIMESTAMP}.csv"
    echo -e "  📄 $PERFORMANCE_DIR/network/network-metrics-${TIMESTAMP}.csv"
    echo -e "  📄 $PERFORMANCE_DIR/applications/app-metrics-${TIMESTAMP}.csv"
    echo -e "  📄 $PERFORMANCE_DIR/performance-report-${TIMESTAMP}.html"
    
    echo -e "\n${WHITE}🌐 Accès aux rapports:${NC}"
    echo -e "  📊 $PERFORMANCE_DIR/latest-performance-report.html"
    
    log_success "Monitoring de performance terminé avec succès !"
}

# Fonction principale
main() {
    echo -e "${CYAN}"
    echo "⚡ =========================================="
    echo "   MEDHEAD - MONITORING DE PERFORMANCE"
    echo "   Collecte de métriques système et app"
    echo "==========================================${NC}"
    
    # Gestion des arguments
    while [[ $# -gt 0 ]]; do
        case $1 in
            --duration)
                MONITOR_DURATION="$2"
                shift 2
                ;;
            --skip-system)
                COLLECT_SYSTEM_METRICS=false
                shift
                ;;
            --skip-docker)
                COLLECT_DOCKER_METRICS=false
                shift
                ;;
            --skip-network)
                COLLECT_NETWORK_METRICS=false
                shift
                ;;
            --help)
                echo "Usage: $0 [options]"
                echo "Options:"
                echo "  --duration N       Durée de monitoring en secondes (défaut: 300)"
                echo "  --skip-system      Ignorer les métriques système"
                echo "  --skip-docker      Ignorer les métriques Docker"
                echo "  --skip-network     Ignorer les métriques réseau"
                echo "  --help             Afficher cette aide"
                exit 0
                ;;
            *)
                log_error "Option inconnue: $1"
                exit 1
                ;;
        esac
    done
    
    # Initialisation
    setup_directories
    
    log_info "Début du monitoring de performance pour ${MONITOR_DURATION}s"
    
    # Lancement des collectes en parallèle
    collect_system_metrics &
    collect_docker_metrics &
    collect_network_metrics &
    monitor_applications &
    
    # Attendre que toutes les collectes se terminent
    wait
    
    # Génération du rapport final
    generate_performance_report
    show_summary
    
    log_success "🎉 Monitoring de performance terminé avec succès !"
}

# Gestion des signaux pour l'arrêt propre
trap 'log_warning "Arrêt du monitoring..."; exit 0' INT TERM

# Exécution du script principal
main "$@"
