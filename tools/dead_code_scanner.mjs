#!/usr/bin/env node
/**
 * BestBudz Dead Code Scanner
 *
 * Single-pass approach:
 *   1. Read all .java files into memory (once)
 *   2. Extract all type declarations, method declarations, field declarations
 *   3. Build a full-text index per file
 *   4. Cross-reference symbols against the index
 *   5. Report unreferenced types, methods, fields, imports
 *
 * Usage: node tools/dead_code_scanner.mjs [src_root]
 */

import { readFileSync, writeFileSync, readdirSync, statSync } from 'fs';
import { join, relative } from 'path';

const SRC = process.argv[2] || 'src';
const REPORT_PATH = 'tools/dead_code_report.txt';

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

console.time('Total scan');
console.time('Read files');

const files = walkDir(SRC);
const fileData = new Map(); // path → { content, lines }

for (const f of files) {
  const content = readFileSync(f, 'utf-8');
  fileData.set(f, { content, lines: content.split('\n') });
}

console.timeEnd('Read files');
console.log(`Loaded ${files.length} files into memory\n`);

// ── Phase 1: Extract all type declarations ───────────────────────────

console.time('Phase 1: Types');

const TYPE_RE = /^(?:public\s+|protected\s+|private\s+)?(?:abstract\s+|static\s+|final\s+)*(?:class|enum|interface)\s+([A-Za-z_]\w*)/;
const typeDeclarations = []; // { name, file, line }

for (const [file, { lines }] of fileData) {
  for (let i = 0; i < lines.length; i++) {
    const m = lines[i].match(TYPE_RE);
    if (m) {
      typeDeclarations.push({ name: m[1], file, line: i + 1 });
    }
  }
}

// Deduplicate by name (keep first/primary declaration)
const primaryTypes = new Map(); // name → { file, line }
for (const t of typeDeclarations) {
  // Prefer the one where filename matches classname
  const existing = primaryTypes.get(t.name);
  if (!existing) {
    primaryTypes.set(t.name, t);
  } else if (t.file.includes(t.name + '.java') && !existing.file.includes(t.name + '.java')) {
    primaryTypes.set(t.name, t);
  }
}

console.timeEnd('Phase 1: Types');
console.log(`  Found ${primaryTypes.size} unique type declarations\n`);

// ── Phase 2: Find unreferenced types ─────────────────────────────────

console.time('Phase 2: Dead types');

// Build word index: for each file, the set of all word-like tokens
const fileWordSets = new Map(); // path → Set<string>
const WORD_RE = /\b([A-Za-z_]\w*)\b/g;

for (const [file, { content }] of fileData) {
  const words = new Set();
  let m;
  while ((m = WORD_RE.exec(content)) !== null) {
    words.add(m[1]);
  }
  fileWordSets.set(file, words);
}

// Known entry points / framework-invoked types that won't have explicit callers
const ENTRY_TYPES = new Set([
  'Server', 'GameThread', 'Main',
]);

const deadTypes = [];
for (const [name, { file, line }] of primaryTypes) {
  if (ENTRY_TYPES.has(name)) continue;
  if (name.length < 3) continue;

  // Check if ANY other file references this type name
  let referenced = false;
  for (const [otherFile, words] of fileWordSets) {
    if (otherFile === file) continue;
    if (words.has(name)) {
      referenced = true;
      break;
    }
  }

  if (!referenced) {
    const lineCount = fileData.get(file).lines.length;
    deadTypes.push({ name, file, line, lineCount });
  }
}

deadTypes.sort((a, b) => b.lineCount - a.lineCount);

console.timeEnd('Phase 2: Dead types');
console.log(`  Found ${deadTypes.length} unreferenced types\n`);

// ── Phase 3: Find unreferenced methods ───────────────────────────────

console.time('Phase 3: Dead methods');

const METHOD_RE = /^\s*(?:public|protected)\s+(?:static\s+|final\s+|synchronized\s+|abstract\s+)*(?:<[^>]+>\s+)?(?:\w+(?:<[^>]*>)?(?:\[\])*)\s+([a-z]\w*)\s*\(/;

// Methods to skip — framework callbacks, lifecycle, overrides, standard patterns
const SKIP_METHODS = new Set([
  'main', 'declare', 'execute', 'run', 'process', 'reset', 'toString', 'equals',
  'hashCode', 'compareTo', 'clone', 'finalize', 'init', 'start', 'stop', 'close',
  'clickButton', 'onLogin', 'onLogout', 'encode', 'decode', 'channelRead',
  'channelActive', 'channelInactive', 'exceptionCaught', 'handleMessage',
  'onEvent', 'handle', 'apply', 'accept', 'test', 'iterator', 'size', 'isEmpty',
  'clear', 'add', 'remove', 'contains', 'values', 'valueOf', 'onMovement',
  'onTeleport', 'onEat', 'onDeath', 'onCommand', 'onEquip', 'onUnequip',
  'canTeleport', 'canEat', 'canTrade', 'canLogout', 'canAttack', 'canMove',
  'canEquip', 'canDrink', 'canUseSpecial', 'onControllerTick', 'update',
  'save', 'load', 'tick', 'cycle', 'interact', 'use', 'drop', 'pickup',
  'buy', 'sell', 'open', 'write', 'read', 'send', 'receive', 'parse',
  'validate', 'check', 'verify', 'create', 'destroy', 'spawn', 'despawn',
  'spawnNpcs', 'onWaveStart', 'onWaveComplete', 'splitNpc',
]);

const methodDeclarations = []; // { name, file, line }

for (const [file, { lines }] of fileData) {
  let prevLineOverride = false;
  for (let i = 0; i < lines.length; i++) {
    const trimmed = lines[i].trim();

    // Track @Override on previous line
    if (trimmed.startsWith('@Override')) {
      prevLineOverride = true;
      continue;
    }

    const m = trimmed.match(METHOD_RE);
    if (m && !prevLineOverride) {
      const methodName = m[1];
      if (!SKIP_METHODS.has(methodName) && methodName.length >= 3) {
        // Skip getters/setters/is/has
        if (!/^(get|set|is|has|can|should|with)[A-Z]/.test(methodName)) {
          methodDeclarations.push({ name: methodName, file, line: i + 1 });
        }
      }
    }
    prevLineOverride = false;
  }
}

// Check each method against all OTHER files
const deadMethods = [];
for (const { name, file, line } of methodDeclarations) {
  let referenced = false;
  for (const [otherFile, words] of fileWordSets) {
    if (otherFile === file) continue;
    if (words.has(name)) {
      referenced = true;
      break;
    }
  }
  if (!referenced) {
    deadMethods.push({ name, file, line });
  }
}

console.timeEnd('Phase 3: Dead methods');
console.log(`  Found ${deadMethods.length} unreferenced methods\n`);

// ── Phase 4: Unused imports ──────────────────────────────────────────

console.time('Phase 4: Unused imports');

const IMPORT_RE = /^import\s+(?:static\s+)?[\w.]+\.(\w+)\s*;/;
const unusedImports = []; // { className, file, line }

for (const [file, { lines }] of fileData) {
  // Find where imports end
  const imports = [];
  for (let i = 0; i < lines.length; i++) {
    const m = lines[i].match(IMPORT_RE);
    if (m && m[1] !== '*') {
      imports.push({ className: m[1], line: i + 1 });
    }
  }

  // Check each import against the rest of the file (excluding import lines)
  const nonImportContent = lines.filter(l => !l.startsWith('import ')).join('\n');
  for (const imp of imports) {
    const re = new RegExp(`\\b${imp.className}\\b`);
    if (!re.test(nonImportContent)) {
      unusedImports.push({ className: imp.className, file, line: imp.line });
    }
  }
}

// Group by file
const unusedImportsByFile = new Map();
for (const imp of unusedImports) {
  if (!unusedImportsByFile.has(imp.file)) unusedImportsByFile.set(imp.file, []);
  unusedImportsByFile.get(imp.file).push(imp);
}

console.timeEnd('Phase 4: Unused imports');
console.log(`  Found ${unusedImports.length} unused imports across ${unusedImportsByFile.size} files\n`);

// ── Phase 5: Stub/near-empty files ───────────────────────────────────

console.time('Phase 5: Stubs');

const stubs = [];
for (const [file, { lines }] of fileData) {
  const codeLines = lines.filter(l => {
    const t = l.trim();
    return t && !t.startsWith('//') && !t.startsWith('*') && !t.startsWith('/*')
      && !t.startsWith('package ') && !t.startsWith('import ') && t !== '{' && t !== '}';
  }).length;
  if (codeLines < 8) {
    stubs.push({ file, codeLines, totalLines: lines.length });
  }
}

console.timeEnd('Phase 5: Stubs');
console.log(`  Found ${stubs.length} stub/near-empty files\n`);

// ── Phase 6: Write-only private fields ───────────────────────────────

console.time('Phase 6: Dead fields');

const FIELD_RE = /^\s*private\s+(?:static\s+|final\s+|transient\s+|volatile\s+)*(?:\w+(?:<[^>]*>)?(?:\[\])*)\s+([a-z]\w*)\s*[=;]/;
const deadFields = [];

for (const [file, { lines, content }] of fileData) {
  for (let i = 0; i < lines.length; i++) {
    const m = lines[i].match(FIELD_RE);
    if (!m) continue;

    const fieldName = m[1];
    if (fieldName.length < 3) continue;
    if (/^(serialVersionUID|instance|INSTANCE|logger|log|gson)$/.test(fieldName)) continue;

    // Count references outside the declaration line
    const re = new RegExp(`\\b${fieldName}\\b`, 'g');
    const allMatches = content.match(re);
    // If it only appears once (the declaration), it's unused
    if (allMatches && allMatches.length <= 1) {
      deadFields.push({ name: fieldName, file, line: i + 1 });
    }
  }
}

console.timeEnd('Phase 6: Dead fields');
console.log(`  Found ${deadFields.length} unused private fields\n`);

// ── Generate Report ──────────────────────────────────────────────────

console.timeEnd('Total scan');

const report = [];
const hr = '═'.repeat(60);
const sr = '─'.repeat(60);

report.push(`${'═'.repeat(60)}`);
report.push(`  BESTBUDZ DEAD CODE REPORT`);
report.push(`  Scanned ${files.length} files | ${[...fileData.values()].reduce((s, f) => s + f.lines.length, 0)} total lines`);
report.push(`  Generated: ${new Date().toISOString()}`);
report.push(hr);
report.push('');

// ── Dead types (sorted by line count, biggest waste first) ──
const deadTypeLines = deadTypes.reduce((s, t) => s + t.lineCount, 0);
report.push(sr);
report.push(`  UNREFERENCED TYPES: ${deadTypes.length} types, ${deadTypeLines} total lines`);
report.push(`  Never imported, instantiated, or referenced outside their own file.`);
report.push(sr);
report.push('');

for (const t of deadTypes) {
  report.push(`  [${t.lineCount} lines] ${t.name}`);
  report.push(`    ${relative(SRC, t.file)}:${t.line}`);
}
report.push('');

// ── Dead methods ──
report.push(sr);
report.push(`  UNREFERENCED METHODS: ${deadMethods.length}`);
report.push(`  Public/protected methods never called outside their own file.`);
report.push(`  (Excludes: getters/setters, @Override, framework callbacks)`);
report.push(sr);
report.push('');

// Group by file
const methodsByFile = new Map();
for (const m of deadMethods) {
  if (!methodsByFile.has(m.file)) methodsByFile.set(m.file, []);
  methodsByFile.get(m.file).push(m);
}
for (const [file, methods] of [...methodsByFile.entries()].sort()) {
  report.push(`  ${relative(SRC, file)}`);
  for (const m of methods) {
    report.push(`    :${m.line}  ${m.name}()`);
  }
}
report.push('');

// ── Unused imports ──
report.push(sr);
report.push(`  UNUSED IMPORTS: ${unusedImports.length} across ${unusedImportsByFile.size} files`);
report.push(sr);
report.push('');

for (const [file, imps] of [...unusedImportsByFile.entries()].sort()) {
  report.push(`  ${relative(SRC, file)}`);
  for (const imp of imps) {
    report.push(`    :${imp.line}  ${imp.className}`);
  }
}
report.push('');

// ── Stubs ──
report.push(sr);
report.push(`  STUB / NEAR-EMPTY FILES: ${stubs.length} (< 8 lines of actual code)`);
report.push(sr);
report.push('');

for (const s of stubs.sort((a, b) => a.codeLines - b.codeLines)) {
  report.push(`  [${s.codeLines} code lines / ${s.totalLines} total] ${relative(SRC, s.file)}`);
}
report.push('');

// ── Dead fields ──
report.push(sr);
report.push(`  UNUSED PRIVATE FIELDS: ${deadFields.length}`);
report.push(sr);
report.push('');

const fieldsByFile = new Map();
for (const f of deadFields) {
  if (!fieldsByFile.has(f.file)) fieldsByFile.set(f.file, []);
  fieldsByFile.get(f.file).push(f);
}
for (const [file, fields] of [...fieldsByFile.entries()].sort()) {
  report.push(`  ${relative(SRC, file)}`);
  for (const f of fields) {
    report.push(`    :${f.line}  ${f.name}`);
  }
}
report.push('');

// ── Summary ──
report.push(hr);
report.push('  SUMMARY');
report.push(hr);
report.push(`  Dead types:      ${deadTypes.length} (${deadTypeLines} lines removable)`);
report.push(`  Dead methods:    ${deadMethods.length}`);
report.push(`  Unused imports:  ${unusedImports.length}`);
report.push(`  Stub files:      ${stubs.length}`);
report.push(`  Unused fields:   ${deadFields.length}`);
report.push(hr);

const reportText = report.join('\n');
writeFileSync(REPORT_PATH, reportText);
console.log(`\nReport written to ${REPORT_PATH}`);
console.log(reportText);
