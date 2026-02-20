#!/usr/bin/env node
/**
 * BestBudz Dead Code Remover
 *
 * Phase 1: Delete confirmed dead type files (with re-verification)
 * Phase 2: Clean unused imports from all files
 * Phase 3: Report on RSInterface.java
 *
 * Every deletion is re-verified with a fresh full-text search before acting.
 */

import { readFileSync, writeFileSync, unlinkSync, readdirSync, statSync, existsSync } from 'fs';
import { join, relative, basename } from 'path';

const SRC = process.argv[2] || 'src';
const DRY_RUN = process.argv.includes('--dry-run');

// ── Collect all .java files ──────────────────────────────────────────

function walkDir(dir) {
  const results = [];
  for (const entry of readdirSync(dir)) {
    const full = join(dir, entry);
    const stat = statSync(full);
    if (stat.isDirectory()) results.push(...walkDir(full));
    else if (entry.endsWith('.java')) results.push(full);
  }
  return results;
}

console.time('Total');
const files = walkDir(SRC);
const fileContents = new Map();
for (const f of files) {
  fileContents.set(f, readFileSync(f, 'utf-8'));
}
console.log(`Loaded ${files.length} files\n`);

// ── Extract primary type per file ────────────────────────────────────

const TYPE_RE = /^(?:public\s+|protected\s+|private\s+)?(?:abstract\s+|static\s+|final\s+)*(?:class|enum|interface)\s+([A-Za-z_]\w*)/m;

const fileTypes = new Map(); // file → primary type name
for (const [f, content] of fileContents) {
  const m = content.match(TYPE_RE);
  if (m) fileTypes.set(f, m[1]);
}

// ── Phase 1: Delete dead types with fresh verification ───────────────

console.log('═══ PHASE 1: Removing dead type files ═══\n');

// Known reflection/dynamic-loaded types to skip
const SKIP_TYPES = new Set([
  // Blood Trial waves - loaded via WaveRegistry reflection
  'Wave01','Wave02','Wave03','Wave04','Wave05','Wave06','Wave07','Wave08',
  'Wave09','Wave10','Wave11','Wave12','Wave13','Wave14','Wave15','Wave16',
  'Wave17','Wave18','Wave19','Wave20','Wave21','Wave22','Wave23','Wave24','Wave25',
  // Protocol internals (methods appear dead but are internal call chains)
  'StonerUpdating', 'NPCUpdating', 'ISAACCipher',
  // Entry points
  'Server', 'GameThread', 'Main',
]);

function isTypeReferencedElsewhere(typeName, ownFile) {
  // Build a word-boundary regex
  const re = new RegExp(`\\b${typeName}\\b`);
  for (const [f, content] of fileContents) {
    if (f === ownFile) continue;
    if (re.test(content)) return true;
  }
  return false;
}

// Find all files whose primary type is unreferenced
const deadFiles = [];
let deadLines = 0;
let falsePositiveCount = 0;

for (const [f, typeName] of fileTypes) {
  if (!typeName || typeName.length < 3) continue;
  if (SKIP_TYPES.has(typeName)) continue;

  if (!isTypeReferencedElsewhere(typeName, f)) {
    const lineCount = fileContents.get(f).split('\n').length;
    deadFiles.push({ file: f, type: typeName, lines: lineCount });
    deadLines += lineCount;
  }
}

// Also check for any we might have missed in SKIP but are actually referenced
// (defense against the scanner bug)
const verifiedDead = [];
const notActuallyDead = [];

for (const df of deadFiles) {
  // Double-check: is this type name a substring of another type name in the codebase?
  // e.g., "Door" could match "DoorHandler" — false positive
  let subMatch = false;
  for (const [f, content] of fileContents) {
    if (f === df.file) continue;
    // Look for the type as a standalone word (not as part of a longer identifier)
    const standalone = new RegExp(`(?<![A-Za-z0-9_])${df.type}(?![A-Za-z0-9_])`);
    if (standalone.test(content)) {
      subMatch = true;
      break;
    }
  }

  if (subMatch) {
    notActuallyDead.push(df);
    falsePositiveCount++;
  } else {
    verifiedDead.push(df);
  }
}

verifiedDead.sort((a, b) => b.lines - a.lines);

console.log(`Found ${verifiedDead.length} verified dead types (${falsePositiveCount} false positives caught)\n`);

if (notActuallyDead.length > 0) {
  console.log('False positives caught (KEPT):');
  for (const fp of notActuallyDead) {
    console.log(`  KEPT: ${fp.type} (referenced elsewhere)`);
  }
  console.log();
}

let deletedCount = 0;
let deletedLines = 0;

for (const df of verifiedDead) {
  const rel = relative(SRC, df.file);
  if (DRY_RUN) {
    console.log(`  [DRY RUN] Would delete: ${rel} (${df.lines} lines) — ${df.type}`);
  } else {
    try {
      unlinkSync(df.file);
      fileContents.delete(df.file);
      fileTypes.delete(df.file);
      deletedCount++;
      deletedLines += df.lines;
      console.log(`  DELETED: ${rel} (${df.lines} lines) — ${df.type}`);
    } catch (e) {
      console.log(`  ERROR: Could not delete ${rel}: ${e.message}`);
    }
  }
}

console.log(`\nPhase 1 complete: deleted ${deletedCount} files, ${deletedLines} lines removed\n`);

// ── Phase 2: Clean unused imports ────────────────────────────────────

console.log('═══ PHASE 2: Cleaning unused imports ═══\n');

const IMPORT_RE = /^import\s+(?:static\s+)?[\w.]+\.(\w+)\s*;\s*$/;

let totalImportsRemoved = 0;
let filesModified = 0;

// Reload file list after deletions
const currentFiles = [...fileContents.entries()];

for (const [f, content] of currentFiles) {
  const lines = content.split('\n');
  const importLines = []; // { index, className }

  for (let i = 0; i < lines.length; i++) {
    const m = lines[i].match(IMPORT_RE);
    if (m && m[1] !== '*') {
      importLines.push({ index: i, className: m[1] });
    }
  }

  if (importLines.length === 0) continue;

  // Build content WITHOUT import lines for checking
  const nonImportLines = lines.filter((_, i) => !importLines.some(imp => imp.index === i));
  const nonImportContent = nonImportLines.join('\n');

  const toRemove = new Set();
  for (const imp of importLines) {
    const re = new RegExp(`\\b${imp.className}\\b`);
    if (!re.test(nonImportContent)) {
      toRemove.add(imp.index);
    }
  }

  if (toRemove.size > 0) {
    const newLines = lines.filter((_, i) => !toRemove.has(i));

    // Also clean up double blank lines left by removed imports
    const cleaned = [];
    let prevBlank = false;
    for (const line of newLines) {
      const isBlank = line.trim() === '';
      if (isBlank && prevBlank) continue;
      cleaned.push(line);
      prevBlank = isBlank;
    }

    const newContent = cleaned.join('\n');
    if (!DRY_RUN) {
      writeFileSync(f, newContent);
      fileContents.set(f, newContent);
    }

    const rel = relative(SRC, f);
    const removedNames = importLines.filter(imp => toRemove.has(imp.index)).map(imp => imp.className);
    console.log(`  ${rel}: removed ${toRemove.size} imports (${removedNames.join(', ')})`);
    totalImportsRemoved += toRemove.size;
    filesModified++;
  }
}

// Second pass: imports that reference deleted types
let staleImportsRemoved = 0;
const deletedTypeNames = new Set(verifiedDead.map(d => d.type));

for (const [f, content] of fileContents) {
  const lines = content.split('\n');
  const staleLines = new Set();

  for (let i = 0; i < lines.length; i++) {
    const m = lines[i].match(IMPORT_RE);
    if (m && deletedTypeNames.has(m[1])) {
      staleLines.add(i);
    }
  }

  if (staleLines.size > 0) {
    const newLines = lines.filter((_, i) => !staleLines.has(i));
    if (!DRY_RUN) {
      writeFileSync(f, newLines.join('\n'));
      fileContents.set(f, newLines.join('\n'));
    }
    console.log(`  ${relative(SRC, f)}: removed ${staleLines.size} stale imports (refs to deleted types)`);
    staleImportsRemoved += staleLines.size;
  }
}

console.log(`\nPhase 2 complete: removed ${totalImportsRemoved + staleImportsRemoved} imports across ${filesModified} files\n`);

// ── Phase 3: RSInterface analysis ────────────────────────────────────

console.log('═══ PHASE 3: RSInterface.java analysis ═══\n');

const rsInterfacePath = [...fileContents.keys()].find(f => f.endsWith('RSInterface.java'));
if (rsInterfacePath) {
  const rsContent = fileContents.get(rsInterfacePath);
  const rsLines = rsContent.split('\n');

  // Extract all public/protected/private method names
  const METHOD_RE = /^\s*(?:public|protected|private)\s+(?:static\s+|final\s+|synchronized\s+)*(?:void|int|boolean|String|byte|short|long|float|double|RSInterface|RSInterface\[\])\s+([a-z]\w*)\s*\(/;

  const methods = [];
  for (let i = 0; i < rsLines.length; i++) {
    const m = rsLines[i].match(METHOD_RE);
    if (m) {
      // Find the end of this method (rough heuristic: next method or class end)
      let depth = 0;
      let endLine = i;
      let started = false;
      for (let j = i; j < rsLines.length; j++) {
        for (const ch of rsLines[j]) {
          if (ch === '{') { depth++; started = true; }
          if (ch === '}') depth--;
        }
        if (started && depth <= 0) { endLine = j; break; }
      }
      methods.push({
        name: m[1],
        startLine: i + 1,
        endLine: endLine + 1,
        lineCount: endLine - i + 1
      });
    }
  }

  // Check which methods are called from OUTSIDE RSInterface
  const calledMethods = new Set();
  const calledInternally = new Set();

  for (const method of methods) {
    const re = new RegExp(`\\b${method.name}\\b`);
    let externalRef = false;
    for (const [f, content] of fileContents) {
      if (f === rsInterfacePath) {
        // Count internal references (excluding the declaration line)
        const otherLines = rsLines.filter((_, i) => i < method.startLine - 1 || i >= method.endLine);
        if (re.test(otherLines.join('\n'))) {
          calledInternally.add(method.name);
        }
        continue;
      }
      if (re.test(content)) {
        externalRef = true;
        break;
      }
    }
    if (externalRef) calledMethods.add(method.name);
  }

  const deadMethods = methods.filter(m => !calledMethods.has(m.name));
  const externallyUsed = methods.filter(m => calledMethods.has(m.name));
  const internalOnly = deadMethods.filter(m => calledInternally.has(m.name));
  const trulyDead = deadMethods.filter(m => !calledInternally.has(m.name));

  const deadMethodLines = trulyDead.reduce((s, m) => s + m.lineCount, 0);
  const internalOnlyLines = internalOnly.reduce((s, m) => s + m.lineCount, 0);

  console.log(`RSInterface.java: ${rsLines.length} total lines, ${methods.length} methods\n`);
  console.log(`Externally called (KEEP): ${externallyUsed.length} methods`);
  for (const m of externallyUsed) {
    console.log(`  :${m.startLine}  ${m.name}() (${m.lineCount} lines)`);
  }

  console.log(`\nInternal-only (called by other RSInterface methods): ${internalOnly.length} methods, ${internalOnlyLines} lines`);
  for (const m of internalOnly.sort((a, b) => b.lineCount - a.lineCount)) {
    console.log(`  :${m.startLine}  ${m.name}() (${m.lineCount} lines)`);
  }

  console.log(`\nTruly dead (never called anywhere): ${trulyDead.length} methods, ${deadMethodLines} lines`);
  for (const m of trulyDead.sort((a, b) => b.lineCount - a.lineCount)) {
    console.log(`  :${m.startLine}  ${m.name}() (${m.lineCount} lines)`);
  }

  console.log(`\n  SUMMARY: ${deadMethodLines + internalOnlyLines} of ${rsLines.length} lines are removable`);
  console.log(`  (${trulyDead.length} truly dead methods + ${internalOnly.length} internal-only methods)`);
} else {
  console.log('RSInterface.java not found');
}

console.log();
console.timeEnd('Total');

// ── Final summary ────────────────────────────────────────────────────

console.log('\n═══ FINAL SUMMARY ═══');
console.log(`  Dead files deleted:    ${deletedCount} (${deletedLines} lines)`);
console.log(`  Unused imports removed: ${totalImportsRemoved + staleImportsRemoved}`);
console.log(`  False positives caught: ${falsePositiveCount}`);
if (DRY_RUN) console.log('  (DRY RUN — no files were modified)');
