#!/usr/bin/env bash
# ==============================================================================
# DEPLOY PRODUCTION SCRIPT
# ==============================================================================
# Builds and deploys the Security Spacee application in production mode.
#
# Usage: ./scripts/deploy-prod.sh [OPTIONS]
#
# Options:
#   --build-only    Only build the Docker image, don't start containers
#   --no-cache      Build without Docker cache
#   --with-mailpit  Include Mailpit for email testing
#   --down          Stop and remove all containers
#   --logs          Follow container logs after starting
#   --clean         Remove all containers, volumes, and images
#
# Prerequisites:
#   - Docker and Docker Compose installed
#   - .env.prod file configured (copy from .env.prod.example)
#   - PostgreSQL accessible (external or via host.docker.internal)
# ==============================================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
COMPOSE_FILE="$PROJECT_DIR/docker-compose.prod.yaml"
ENV_FILE="$PROJECT_DIR/.env.prod"
ENV_EXAMPLE="$PROJECT_DIR/.env.prod.example"

# Default options
BUILD_ONLY=false
NO_CACHE=""
WITH_MAILPIT=""
FOLLOW_LOGS=false

# Functions
print_header() {
    echo ""
    echo -e "${CYAN}╔══════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║        🚀 SECURITY SPACEE - PRODUCTION DEPLOYMENT 🚀         ║${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

print_step() {
    echo -e "${BLUE}▶ $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

check_prerequisites() {
    print_step "Checking prerequisites..."
    
    # Check Docker
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed. Please install Docker first."
        exit 1
    fi
    
    # Check Docker Compose
    if ! docker compose version &> /dev/null; then
        print_error "Docker Compose is not available. Please install Docker Compose."
        exit 1
    fi
    
    # Check .env.prod file
    if [[ ! -f "$ENV_FILE" ]]; then
        print_warning ".env.prod file not found!"
        echo ""
        echo -e "   Creating from template..."
        cp "$ENV_EXAMPLE" "$ENV_FILE"
        echo ""
        print_warning "Please edit .env.prod with your production values:"
        echo -e "   ${CYAN}nano $ENV_FILE${NC}"
        echo ""
        echo -e "   Then run: ${CYAN}./scripts/generate-secrets.sh${NC} to generate secure secrets"
        exit 1
    fi
    
    # Validate required variables
    source "$ENV_FILE"
    
    if [[ "$JWT_SECRET" == "GENERATE_ME_RUN_SCRIPTS_GENERATE_SECRETS_SH" ]] || [[ -z "$JWT_SECRET" ]]; then
        print_error "JWT_SECRET is not configured!"
        echo -e "   Run: ${CYAN}./scripts/generate-secrets.sh${NC}"
        exit 1
    fi
    
    if [[ "$DB_PASSWORD" == "CHANGE_ME_SECURE_PASSWORD" ]] || [[ -z "$DB_PASSWORD" ]]; then
        print_error "DB_PASSWORD is not configured!"
        exit 1
    fi
    
    if [[ "$REDIS_PASSWORD" == "CHANGE_ME_REDIS_PASSWORD" ]] || [[ -z "$REDIS_PASSWORD" ]]; then
        print_error "REDIS_PASSWORD is not configured!"
        exit 1
    fi
    
    print_success "Prerequisites check passed"
}

stop_containers() {
    print_step "Stopping existing containers..."
    docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" down --remove-orphans 2>/dev/null || true
    print_success "Containers stopped"
}

clean_all() {
    print_step "Cleaning all containers, volumes, and images..."
    docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" down -v --rmi all --remove-orphans 2>/dev/null || true
    print_success "Cleanup complete"
}

build_image() {
    print_step "Building Docker image..."
    
    local build_args="--file $COMPOSE_FILE"
    
    if [[ -n "$NO_CACHE" ]]; then
        build_args="$build_args --no-cache"
    fi
    
    docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" build $NO_CACHE app
    
    print_success "Docker image built successfully"
}

start_containers() {
    print_step "Starting containers..."
    
    local compose_args="-f $COMPOSE_FILE --env-file $ENV_FILE"
    
    if [[ -n "$WITH_MAILPIT" ]]; then
        compose_args="$compose_args --profile with-mailpit"
    fi
    
    docker compose $compose_args up -d
    
    print_success "Containers started"
}

show_status() {
    echo ""
    print_step "Container Status:"
    echo ""
    docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" ps
    echo ""
}

show_endpoints() {
    source "$ENV_FILE"
    
    echo ""
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo -e "${GREEN}🌐 APPLICATION ENDPOINTS${NC}"
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    echo -e "   📱 API Base URL:      ${YELLOW}http://localhost:${SERVER_PORT}/api/v1${NC}"
    echo -e "   📚 Swagger UI:        ${YELLOW}http://localhost:${SERVER_PORT}/api/v1/swagger-ui.html${NC}"
    echo -e "   📄 OpenAPI JSON:      ${YELLOW}http://localhost:${SERVER_PORT}/api/v1/api-docs${NC}"
    echo -e "   💚 Health Check:      ${YELLOW}http://localhost:${SERVER_PORT}/api/v1/actuator/health${NC}"
    
    if [[ -n "$WITH_MAILPIT" ]]; then
        echo -e "   📧 Mailpit UI:        ${YELLOW}http://localhost:${MAILPIT_UI_PORT:-8025}${NC}"
    fi
    
    echo ""
    echo -e "${CYAN}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
}

follow_logs() {
    print_step "Following container logs (Ctrl+C to exit)..."
    docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" logs -f app
}

show_usage() {
    echo "Usage: $0 [OPTIONS]"
    echo ""
    echo "Options:"
    echo "  --build-only    Only build the Docker image"
    echo "  --no-cache      Build without Docker cache"
    echo "  --with-mailpit  Include Mailpit for email testing"
    echo "  --down          Stop and remove all containers"
    echo "  --logs          Follow container logs after starting"
    echo "  --clean         Remove all containers, volumes, and images"
    echo "  --help          Show this help message"
    echo ""
}

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --build-only)
            BUILD_ONLY=true
            shift
            ;;
        --no-cache)
            NO_CACHE="--no-cache"
            shift
            ;;
        --with-mailpit)
            WITH_MAILPIT="true"
            shift
            ;;
        --down)
            print_header
            stop_containers
            exit 0
            ;;
        --logs)
            FOLLOW_LOGS=true
            shift
            ;;
        --clean)
            print_header
            clean_all
            exit 0
            ;;
        --help)
            show_usage
            exit 0
            ;;
        *)
            print_error "Unknown option: $1"
            show_usage
            exit 1
            ;;
    esac
done

# Main execution
print_header

check_prerequisites

# Stop existing containers
stop_containers

# Build
build_image

if [ "$BUILD_ONLY" = true ]; then
    print_success "Build complete. Image ready for deployment."
    exit 0
fi

# Start
start_containers

# Wait for health check
print_step "Waiting for application to start (this may take up to 60 seconds)..."
sleep 10

# Show status
show_status

# Show endpoints
show_endpoints

print_success "Deployment complete! 🎉"
echo ""

if [ "$FOLLOW_LOGS" = true ]; then
    follow_logs
else
    echo -e "💡 ${CYAN}TIP: Run with --logs to follow container logs${NC}"
    echo -e "   ${BLUE}./scripts/deploy-prod.sh --logs${NC}"
    echo ""
fi
