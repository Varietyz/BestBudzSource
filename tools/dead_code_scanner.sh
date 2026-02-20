#!/bin/bash
# Dead Code Scanner for BestBudz RSPS
# Finds: unreferenced classes, unreferenced methods, unused imports, orphan files
# Usage: bash tools/dead_code_scanner.sh [src_root]

SRC="${1:-src}"
REPORT="tools/dead_code_report.txt"
TEMP_DIR=$(mktemp -d)

echo "=== BestBudz Dead Code Scanner ===" | tee "$REPORT"
echo "Scanning: $SRC" | tee -a "$REPORT"
echo "Started: $(date)" | tee -a "$REPORT"
echo "" | tee -a "$REPORT"

# ────────────────────────────────────────────
# Phase 1: Find all declared types (classes, enums, interfaces, abstract classes)
# ────────────────────────────────────────────
echo "[Phase 1] Extracting all type declarations..." >&2

find "$SRC" -name "*.java" -print0 | xargs -0 grep -Hn "^\(public \|protected \|private \|\)\(abstract \|\)\(class\|enum\|interface\) " | \
  sed 's/.*[ .]\(class\|enum\|interface\) \+\([A-Za-z0-9_]*\).*/\2/' | \
  sort -u > "$TEMP_DIR/all_types.txt"

# Also extract with file paths for mapping
find "$SRC" -name "*.java" -print0 | while IFS= read -r -d '' f; do
  classname=$(grep -m1 "^\(public \|protected \|private \|\)\(abstract \|\)\(class\|enum\|interface\) " "$f" | \
    sed 's/.*[ .]\(class\|enum\|interface\) \+\([A-Za-z0-9_]*\).*/\2/')
  if [ -n "$classname" ]; then
    echo "$classname|$f"
  fi
done > "$TEMP_DIR/type_file_map.txt"

TOTAL_TYPES=$(wc -l < "$TEMP_DIR/all_types.txt")
echo "  Found $TOTAL_TYPES types" >&2

# ────────────────────────────────────────────
# Phase 2: Find unreferenced types
# A type is "dead" if its name appears ONLY in its own file
# ────────────────────────────────────────────
echo "[Phase 2] Checking type references across codebase..." >&2
echo "============================================" >> "$REPORT"
echo "UNREFERENCED CLASSES / ENUMS / INTERFACES" >> "$REPORT"
echo "These types are never imported, instantiated, or referenced outside their own file." >> "$REPORT"
echo "============================================" >> "$REPORT"
echo "" >> "$REPORT"

DEAD_TYPES=0
while IFS='|' read -r typename filepath; do
  # Skip inner classes, very short names (likely false positives), and known entry points
  if [ ${#typename} -lt 3 ]; then continue; fi

  # Count files that reference this type name (excluding its own file)
  ref_count=$(grep -rl --include="*.java" "\b${typename}\b" "$SRC" | grep -v "^${filepath}$" | wc -l)

  if [ "$ref_count" -eq 0 ]; then
    echo "  DEAD TYPE: $typename" >> "$REPORT"
    echo "    File: $filepath" >> "$REPORT"
    lines=$(wc -l < "$filepath")
    echo "    Lines: $lines" >> "$REPORT"
    echo "" >> "$REPORT"
    DEAD_TYPES=$((DEAD_TYPES + 1))
  fi
done < "$TEMP_DIR/type_file_map.txt"

echo "  Found $DEAD_TYPES unreferenced types" >&2
echo "TOTAL UNREFERENCED TYPES: $DEAD_TYPES" >> "$REPORT"
echo "" >> "$REPORT"

# ────────────────────────────────────────────
# Phase 3: Find unreferenced public/protected methods
# ────────────────────────────────────────────
echo "[Phase 3] Extracting method declarations..." >&2
echo "============================================" >> "$REPORT"
echo "UNREFERENCED PUBLIC/PROTECTED METHODS" >> "$REPORT"
echo "Methods declared but never called outside their own file." >> "$REPORT"
echo "(Excludes: main, declare, execute, run, process, reset, toString, equals, hashCode," >> "$REPORT"
echo " getters/setters under 2 lines, constructors, overrides)" >> "$REPORT"
echo "============================================" >> "$REPORT"
echo "" >> "$REPORT"

# Known framework methods that are called reflectively or by convention
FRAMEWORK_METHODS="main|declare|execute|run|process|reset|toString|equals|hashCode|compareTo|clone|finalize|init|start|stop|close|clickButton|onLogin|onLogout|encode|decode|channelRead|channelActive|channelInactive|exceptionCaught|handleMessage|onEvent|handle|apply|accept|test|get|set|iterator|size|isEmpty|clear|add|remove|contains|values|valueOf"

DEAD_METHODS=0
find "$SRC" -name "*.java" -print0 | while IFS= read -r -d '' f; do
  # Extract public/protected method names (not constructors, not getters/setters starting with get/set/is + uppercase)
  grep -n "public\|protected" "$f" | \
    grep -E "\b(public|protected)\b.*\b[a-z][a-zA-Z0-9]*\s*\(" | \
    grep -v "@Override" | \
    grep -v "class \|interface \|enum " | \
    while read -r line; do
      # Extract method name
      method=$(echo "$line" | sed -n 's/.*[ \t]\([a-z][a-zA-Z0-9]*\)\s*(.*/\1/p')
      lineno=$(echo "$line" | cut -d: -f1)

      if [ -z "$method" ]; then continue; fi
      if [ ${#method} -lt 3 ]; then continue; fi

      # Skip framework/convention methods
      if echo "$method" | grep -qE "^($FRAMEWORK_METHODS)$"; then continue; fi
      # Skip getters/setters
      if echo "$method" | grep -qE "^(get|set|is|has|can|should)[A-Z]"; then continue; fi

      # Check if method name appears in any OTHER file
      ref_count=$(grep -rl --include="*.java" "\b${method}\b" "$SRC" | grep -v "^${f}$" | head -1)

      if [ -z "$ref_count" ]; then
        echo "  DEAD METHOD: $method()" >> "$REPORT"
        echo "    File: $f:$lineno" >> "$REPORT"
        echo "" >> "$REPORT"
        # Can't increment in subshell, count later
        echo "$method|$f:$lineno" >> "$TEMP_DIR/dead_methods.txt"
      fi
    done
done

if [ -f "$TEMP_DIR/dead_methods.txt" ]; then
  DEAD_METHODS=$(wc -l < "$TEMP_DIR/dead_methods.txt")
else
  DEAD_METHODS=0
fi
echo "  Found $DEAD_METHODS unreferenced methods" >&2
echo "TOTAL UNREFERENCED METHODS: $DEAD_METHODS" >> "$REPORT"
echo "" >> "$REPORT"

# ────────────────────────────────────────────
# Phase 4: Find unused imports
# ────────────────────────────────────────────
echo "[Phase 4] Checking for unused imports..." >&2
echo "============================================" >> "$REPORT"
echo "FILES WITH UNUSED IMPORTS" >> "$REPORT"
echo "============================================" >> "$REPORT"
echo "" >> "$REPORT"

UNUSED_IMPORT_FILES=0
find "$SRC" -name "*.java" -print0 | while IFS= read -r -d '' f; do
  file_unused=""
  grep -n "^import " "$f" | while read -r importline; do
    lineno=$(echo "$importline" | cut -d: -f1)
    # Extract the simple class name from the import
    imported_class=$(echo "$importline" | sed 's/.*\.\([A-Za-z0-9_]*\);/\1/')

    # Skip wildcard imports
    if [ "$imported_class" = "*" ]; then continue; fi

    # Check if the imported class name is used in the file (excluding the import line itself)
    used=$(sed "${lineno}d" "$f" | grep -c "\b${imported_class}\b")

    if [ "$used" -eq 0 ]; then
      if [ -z "$file_unused" ]; then
        echo "  File: $f" >> "$REPORT"
        file_unused="yes"
        echo "x" >> "$TEMP_DIR/unused_import_files.txt"
      fi
      echo "    Line $lineno: $imported_class" >> "$REPORT"
    fi
  done
  if [ -n "$file_unused" ]; then
    echo "" >> "$REPORT"
  fi
done

if [ -f "$TEMP_DIR/unused_import_files.txt" ]; then
  UNUSED_IMPORT_FILES=$(wc -l < "$TEMP_DIR/unused_import_files.txt")
else
  UNUSED_IMPORT_FILES=0
fi
echo "  Found $UNUSED_IMPORT_FILES files with unused imports" >&2
echo "" >> "$REPORT"

# ────────────────────────────────────────────
# Phase 5: Find empty / stub classes (< 10 lines of actual code)
# ────────────────────────────────────────────
echo "[Phase 5] Finding stub/empty classes..." >&2
echo "============================================" >> "$REPORT"
echo "STUB / NEAR-EMPTY FILES (< 10 lines of code)" >> "$REPORT"
echo "============================================" >> "$REPORT"
echo "" >> "$REPORT"

STUBS=0
find "$SRC" -name "*.java" -print0 | while IFS= read -r -d '' f; do
  # Count non-blank, non-comment, non-import, non-package lines
  code_lines=$(grep -v '^\s*$\|^\s*//\|^\s*\*\|^\s*/\*\|^package \|^import ' "$f" | wc -l)
  if [ "$code_lines" -lt 10 ]; then
    echo "  $f ($code_lines code lines)" >> "$REPORT"
    echo "x" >> "$TEMP_DIR/stubs.txt"
  fi
done

if [ -f "$TEMP_DIR/stubs.txt" ]; then
  STUBS=$(wc -l < "$TEMP_DIR/stubs.txt")
else
  STUBS=0
fi
echo "  Found $STUBS stub files" >&2
echo "TOTAL STUBS: $STUBS" >> "$REPORT"
echo "" >> "$REPORT"

# ────────────────────────────────────────────
# Phase 6: Find fields declared but never read
# ────────────────────────────────────────────
echo "[Phase 6] Checking for write-only fields..." >&2
echo "============================================" >> "$REPORT"
echo "POTENTIALLY UNUSED FIELDS" >> "$REPORT"
echo "Fields that appear to be declared but never read (only assigned)." >> "$REPORT"
echo "============================================" >> "$REPORT"
echo "" >> "$REPORT"

# Focus on private fields only (safest to identify as dead)
find "$SRC" -name "*.java" -print0 | while IFS= read -r -d '' f; do
  grep -n "private.*[a-z][a-zA-Z0-9]* *[=;]" "$f" | \
    grep -v "static final\|private.*(" | \
    while read -r fieldline; do
      lineno=$(echo "$fieldline" | cut -d: -f1)
      # Extract field name - last word before = or ;
      field=$(echo "$fieldline" | sed 's/.*[ \t]\([a-z][a-zA-Z0-9]*\)\s*[=;].*/\1/')

      if [ -z "$field" ] || [ ${#field} -lt 3 ]; then continue; fi

      # Count all references to this field in the file (excluding the declaration line)
      refs=$(sed "${lineno}d" "$f" | grep -c "\b${field}\b")

      if [ "$refs" -eq 0 ]; then
        echo "  UNUSED FIELD: $field" >> "$REPORT"
        echo "    File: $f:$lineno" >> "$REPORT"
        echo "" >> "$REPORT"
      fi
    done
done

# ────────────────────────────────────────────
# Summary
# ────────────────────────────────────────────
echo "" >> "$REPORT"
echo "============================================" >> "$REPORT"
echo "SCAN COMPLETE" >> "$REPORT"
echo "============================================" >> "$REPORT"
echo "Finished: $(date)" >> "$REPORT"
echo "" >> "$REPORT"

echo "" >&2
echo "=== Scan complete. Report written to: $REPORT ===" >&2

# Cleanup
rm -rf "$TEMP_DIR"
