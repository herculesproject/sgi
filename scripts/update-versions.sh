#!/bin/bash
#
# Updates versions of all Maven and NPM modules.
#
# Usage:
#   ./scripts/update-versions.sh <version>
#
# Example:
#   ./scripts/update-versions.sh 1.0.0-SNAPSHOT

set -euo pipefail

NEW_VERSION="${1:-}"

if [ -z "$NEW_VERSION" ]; then
  echo "Error: Version argument is required"
  echo "Usage: $0 <version>"
  exit 1
fi

# Resolve repo root (works regardless of where the script is invoked from)
REPO_ROOT="$(git rev-parse --show-toplevel)"

# Maven flags: -B (batch), -o (offline — skips remote repo checks, big speedup),
# -q (quiet — reduces output overhead)
MVN_OPTS="-B -o -q"

echo ""
echo "=================================================="
echo "  Updating versions to: $NEW_VERSION"
echo "=================================================="
echo ""

# ============================================
# Update VERSION file
# ============================================
echo "$NEW_VERSION" > "$REPO_ROOT/VERSION"
echo "✓ VERSION file updated"
echo ""

# ============================================
# Discover Maven and NPM modules
# ============================================
MAVEN_MODULES=$(find "$REPO_ROOT" -maxdepth 2 -name "pom.xml" \
  -not -path "*/target/*" \
  -not -path "*/.mvn/*" | xargs -n1 dirname | sort -u)

NPM_MODULES=$(find "$REPO_ROOT" -maxdepth 2 -name "package.json" \
  -not -path "*/node_modules/*" \
  -not -path "*/target/*" | xargs -n1 dirname | sort -u)

# ============================================
# Update Maven modules
# ============================================
MAVEN_UPDATED=0

if [ -n "$MAVEN_MODULES" ]; then
  echo "Updating Maven modules..."
  echo ""

  # Update version in each Maven module
  for MODULE in $MAVEN_MODULES; do
    if [ -f "$MODULE/pom.xml" ]; then
      MODULE_NAME=$(basename "$MODULE")
      echo "  - Updating version in $MODULE_NAME"
      mvn $MVN_OPTS versions:set \
          -DnewVersion="$NEW_VERSION" \
          -DgenerateBackupPoms=false \
          -f "$MODULE/pom.xml" && ((MAVEN_UPDATED++)) || true
    fi
  done
  echo ""

  # Update sgi-framework-spring.version property (only in service modules that have it)
  echo "  - Updating sgi-framework-spring.version property"
  for MODULE in $MAVEN_MODULES; do
    if grep -q "sgi-framework-spring.version" "$MODULE/pom.xml" 2>/dev/null; then
      MODULE_NAME=$(basename "$MODULE")
      echo "    $MODULE_NAME"
      mvn $MVN_OPTS versions:set-property \
          -Dproperty=sgi-framework-spring.version \
          -DnewVersion="$NEW_VERSION" \
          -DgenerateBackupPoms=false \
          -f "$MODULE/pom.xml"
    fi
  done
  echo ""

  # Install parent module for resolution by child modules
  if [ -f "$REPO_ROOT/sgi-starter-parent/pom.xml" ]; then
    echo "  - Installing sgi-starter-parent"
    mvn -B install -f "$REPO_ROOT/sgi-starter-parent/pom.xml" -DskipTests --quiet
    echo ""
  fi

  # Update parent reference in child modules
  echo "  - Updating parent reference in child modules"
  CHILDREN_POMS=$(grep -rl "<artifactId>sgi-starter-parent</artifactId>" "$REPO_ROOT" --include pom.xml | grep -v "sgi-starter-parent/pom.xml$")
  for POM in $CHILDREN_POMS; do
    mvn $MVN_OPTS versions:update-parent \
        -DparentVersion="[$NEW_VERSION]" \
        -DallowSnapshots \
        -DskipResolution=true \
        -DgenerateBackupPoms=false \
        -f "$POM" 2>&1 | grep -v "^\[INFO\]" || true
  done
  echo ""
  
  echo "✓ Maven modules updated successfully"
  echo ""
fi

# ============================================
# Update NPM modules
# ============================================
NPM_UPDATED=0

if [ -n "$NPM_MODULES" ]; then
  echo "Updating NPM modules..."
  echo ""

  for MODULE in $NPM_MODULES; do
    MODULE_NAME=$(basename "$MODULE")

    echo "  - Updating $MODULE_NAME"

    cd "$MODULE"

    # Set new version in package.json
    if npm version "$NEW_VERSION" \
      --no-git-tag-version \
      --allow-same-version \
      > /dev/null 2>&1; then
      NPM_UPDATED=$((NPM_UPDATED + 1))
    fi

    cd "$REPO_ROOT"
  done

  echo ""
  echo "✓ NPM modules updated successfully"
  echo ""
fi


# ============================================
# Summary
# ============================================
MAVEN_COUNT=$(echo "$MAVEN_MODULES" | wc -l)
NPM_COUNT=$(echo "$NPM_MODULES" | wc -l)

echo "=================================================="
echo "  Update completed successfully"
echo "=================================================="
echo "  Target version:  $NEW_VERSION"
echo "  Maven modules:   $MAVEN_UPDATED/$MAVEN_COUNT updated"
echo "  NPM modules:     $NPM_UPDATED/$NPM_COUNT updated"
echo "=================================================="
echo ""
