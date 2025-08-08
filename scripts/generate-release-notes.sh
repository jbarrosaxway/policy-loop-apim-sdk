#!/bin/bash
set -e

PREVIOUS_TAG="$1"; NEW_TAG="$2"; RELEASE_VERSION="$3"
if [ -z "$PREVIOUS_TAG" ] || [ -z "$NEW_TAG" ] || [ -z "$RELEASE_VERSION" ]; then
  echo "Usage: $0 <previous_tag> <new_tag> <release_version>"; exit 1; fi

COMMIT_RANGE="$PREVIOUS_TAG"
if [ "$PREVIOUS_TAG" = "none" ]; then COMMIT_RANGE="HEAD"; else COMMIT_RANGE="$PREVIOUS_TAG..$NEW_TAG"; fi

COMMITS=$(git log --pretty=format:"%h|%s|%an|%ad" --date=short "$COMMIT_RANGE")
echo "# Release v$RELEASE_VERSION" > .release_notes
echo "" >> .release_notes

if [ -z "$COMMITS" ]; then
  echo "No changes detected in this release." >> .release_notes; exit 0; fi

echo "## 📋 Summary" >> .release_notes
TOTAL=$(echo "$COMMITS" | wc -l)
echo "This release includes **$TOTAL** commits." >> .release_notes
echo "" >> .release_notes

echo "## 🔍 Detailed Changes" >> .release_notes
echo "" >> .release_notes
while IFS='|' read -r hash message author date; do
  echo "- [$hash] $message — $author ($date)" >> .release_notes
done <<< "$COMMITS"

echo "" >> .release_notes
echo "## 🔧 Technical Details" >> .release_notes
echo "- Release Date: $(date +%Y-%m-%d)" >> .release_notes
echo "- Commit Range: $COMMIT_RANGE" >> .release_notes

