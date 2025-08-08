#!/bin/bash

set -e

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log() { echo -e "${GREEN}[VERSION]${NC} $1"; }
warn() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
error() { echo -e "${RED}[ERROR]${NC} $1"; }

if [ "$GITHUB_EVENT_NAME" = "pull_request" ]; then
  BASE_REF="$GITHUB_BASE_REF"; HEAD_REF="$GITHUB_HEAD_REF"
else
  BASE_REF="HEAD~1"; HEAD_REF="HEAD"
fi

log "Analyzing changes between $BASE_REF and $HEAD_REF"
MODIFIED_FILES=$(git diff --name-only $BASE_REF $HEAD_REF || echo "")
if [ -z "$MODIFIED_FILES" ]; then warn "No modified files"; exit 0; fi

MAJOR=false; MINOR=false; PATCH=false
if echo "$MODIFIED_FILES" | grep -q -E "(build\\.gradle|\\.java|\\.groovy)" && \
   git diff $BASE_REF $HEAD_REF | grep -q -E "(BREAKING CHANGE|breaking change|!:|feat!|fix!)"; then MAJOR=true; fi
if echo "$MODIFIED_FILES" | grep -q -E "(\\.java|\\.groovy|\\.yaml)" && \
   git diff $BASE_REF $HEAD_REF | grep -q -E "(feat:|feature:|new:|add:)" && [ "$MAJOR" = false ]; then MINOR=true; fi
if echo "$MODIFIED_FILES" | grep -q -E "(\\.java|\\.groovy|\\.yaml|\\.md|\\.txt)" && \
   git diff $BASE_REF $HEAD_REF | grep -q -E "(fix:|bugfix:|patch:|docs:|style:|refactor:|perf:|test:|chore:)" && \
   [ "$MAJOR" = false ] && [ "$MINOR" = false ]; then PATCH=true; fi
if [ "$MAJOR" = false ] && [ "$MINOR" = false ] && [ "$PATCH" = false ]; then PATCH=true; fi

CURRENT_VERSION=$(grep "^version " build.gradle | sed "s/version //" | tr -d "'")
if [ -z "$CURRENT_VERSION" ]; then error "Unable to read current version"; exit 1; fi
IFS='.' read -ra V <<< "$CURRENT_VERSION"; MA=${V[0]}; MI=${V[1]}; PA=${V[2]}
if [ "$MAJOR" = true ]; then MA=$((MA+1)); MI=0; PA=0; TYPE="MAJOR";
elif [ "$MINOR" = true ]; then MI=$((MI+1)); PA=0; TYPE="MINOR";
else PA=$((PA+1)); TYPE="PATCH"; fi

NEW_VERSION="$MA.$MI.$PA"
log "New version: $NEW_VERSION ($TYPE)"
sed -i "s/^version '.*'/version '$NEW_VERSION'/" build.gradle

echo "VERSION_TYPE=$TYPE" > .version_info
echo "OLD_VERSION=$CURRENT_VERSION" >> .version_info
echo "NEW_VERSION=$NEW_VERSION" >> .version_info
echo "CHANGES_DETECTED=true" >> .version_info

log "Done"

