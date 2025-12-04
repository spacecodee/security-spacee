#!/usr/bin/env bash
# ==============================================================================
# GENERATE SECRETS SCRIPT
# ==============================================================================
# Generates secure secrets for production environment:
#   - JWT Secret (512+ bits for HS512)
#   - Redis Password
#   - Database Password
#
# Usage: ./scripts/generate-secrets.sh [--output-env]
#   --output-env: Outputs in .env format ready to copy
# ==============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Functions
print_header() {
    echo ""
    echo -e "${CYAN}╔══════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║           🔐 SECURITY SPACEE - SECRET GENERATOR 🔐           ║${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

generate_random_string() {
    local length=$1
    # Use /dev/urandom for cryptographically secure random bytes
    LC_ALL=C tr -dc 'A-Za-z0-9!@#$%^&*()_+-=' < /dev/urandom | head -c "$length"
}

generate_alphanumeric() {
    local length=$1
    LC_ALL=C tr -dc 'A-Za-z0-9' < /dev/urandom | head -c "$length"
}

generate_base64_secret() {
    local bytes=$1
    # Generate random bytes and encode as base64
    head -c "$bytes" /dev/urandom | base64 | tr -d '\n'
}

# Parse arguments
OUTPUT_ENV=false
if [[ "$1" == "--output-env" ]]; then
    OUTPUT_ENV=true
fi

print_header

echo -e "${BLUE}📦 Generating cryptographically secure secrets...${NC}"
echo ""

# Generate JWT Secret (64 bytes = 512 bits for HS512)
JWT_SECRET=$(generate_base64_secret 64)

# Generate Redis Password (32 characters alphanumeric)
REDIS_PASSWORD=$(generate_alphanumeric 32)

# Generate Database Password (24 characters with special chars)
DB_PASSWORD=$(generate_random_string 24)

if [ "$OUTPUT_ENV" = true ]; then
    echo -e "${GREEN}# ===== GENERATED SECRETS - Copy to .env.prod =====${NC}"
    echo ""
    echo "JWT_SECRET=${JWT_SECRET}"
    echo "REDIS_PASSWORD=${REDIS_PASSWORD}"
    echo "DB_PASSWORD=${DB_PASSWORD}"
    echo ""
else
    echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    
    echo -e "${GREEN}🔑 JWT SECRET (512 bits / Base64 encoded):${NC}"
    echo -e "${YELLOW}   For HS512 algorithm - DO NOT SHARE!${NC}"
    echo ""
    echo "   JWT_SECRET=${JWT_SECRET}"
    echo ""
    
    echo -e "${GREEN}🗄️  REDIS PASSWORD (32 chars alphanumeric):${NC}"
    echo ""
    echo "   REDIS_PASSWORD=${REDIS_PASSWORD}"
    echo ""
    
    echo -e "${GREEN}🐘 DATABASE PASSWORD (24 chars with special):${NC}"
    echo ""
    echo "   DB_PASSWORD=${DB_PASSWORD}"
    echo ""
    
    echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
    echo ""
    echo -e "${CYAN}📋 INSTRUCTIONS:${NC}"
    echo -e "   1. Copy the values above to your ${GREEN}.env.prod${NC} file"
    echo -e "   2. Update your PostgreSQL user password to match DB_PASSWORD"
    echo -e "   3. ${RED}NEVER commit these secrets to version control!${NC}"
    echo ""
    echo -e "${CYAN}💡 TIP: Run with --output-env for copy-paste format:${NC}"
    echo -e "   ${BLUE}./scripts/generate-secrets.sh --output-env${NC}"
    echo ""
fi

echo -e "${GREEN}✅ Secrets generated successfully!${NC}"
