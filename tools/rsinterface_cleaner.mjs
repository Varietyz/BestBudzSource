#!/usr/bin/env node
/**
 * RSInterface.java Dead Method Remover
 *
 * Strategy:
 *   1. Parse all methods with their line ranges
 *   2. Find externally-called methods (called from other files)
 *   3. Build internal call graph (which methods call which)
 *   4. Walk from external entry points — anything NOT reachable is dead
 *   5. Remove dead methods
 */

import { readFileSync, writeFileSync, readdirSync, statSync } from 'fs';
import { join } from 'path';

const SRC = process.argv[2] || 'src';
const DRY_RUN = process.argv.includes('--dry-run');

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

const files = walkDir(SRC);
const rsPath = files.find(f => f.endsWith('RSInterface.java'));
if (!rsPath) { console.log('RSInterface.java not found'); process.exit(1); }

const rsContent = readFileSync(rsPath, 'utf-8');
const rsLines = rsContent.split('\n');

// ── Parse all methods ────────────────────────────────────────────────

// Match method declarations (both static and instance)
const METHOD_DECL_RE = /^\s*(?:public|protected|private)\s+(?:static\s+|final\s+|synchronized\s+)*(?:void|int|boolean|String|byte|short|long|float|double|RSInterface|RSInterface\[\])\s+([a-zA-Z]\w*)\s*\(/;

const methods = []; // { name, startLine, endLine, body }

for (let i = 0; i < rsLines.length; i++) {
  const m = rsLines[i].match(METHOD_DECL_RE);
  if (!m) continue;

  const name = m[1];

  // Find method end by brace counting
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

  const body = rsLines.slice(i, endLine + 1).join('\n');
  methods.push({ name, startLine: i, endLine, body });
}

console.log(`Found ${methods.length} methods in RSInterface.java (${rsLines.length} lines)\n`);

// ── Find externally-called methods ───────────────────────────────────

const otherFileContents = [];
for (const f of files) {
  if (f === rsPath) continue;
  otherFileContents.push(readFileSync(f, 'utf-8'));
}

const externallyUsed = new Set();
for (const m of methods) {
  const re = new RegExp(`\\b${m.name}\\b`);
  for (const content of otherFileContents) {
    if (re.test(content)) {
      externallyUsed.add(m.name);
      break;
    }
  }
}

console.log(`Externally called methods: ${externallyUsed.size}`);
for (const name of externallyUsed) {
  console.log(`  ${name}()`);
}

// ── Build internal call graph ────────────────────────────────────────

// For each method, find which other RSInterface methods it calls
const callGraph = new Map(); // methodName → Set of called method names

const allMethodNames = new Set(methods.map(m => m.name));

for (const m of methods) {
  const calls = new Set();
  for (const otherName of allMethodNames) {
    if (otherName === m.name) continue;
    const re = new RegExp(`\\b${otherName}\\s*\\(`);
    if (re.test(m.body)) {
      calls.add(otherName);
    }
  }
  callGraph.set(m.name, calls);
}

// ── Walk reachability from external entry points ─────────────────────

// Also add constructor, static initializer blocks, and field initializers as roots
// The unpack() method is the main entry and boss(), teleport(), etc.
const reachable = new Set();

function markReachable(name) {
  if (reachable.has(name)) return;
  reachable.add(name);
  const calls = callGraph.get(name);
  if (calls) {
    for (const callee of calls) {
      markReachable(callee);
    }
  }
}

// Start from all externally used methods
for (const name of externallyUsed) {
  markReachable(name);
}

// Also check: is the method referenced in non-method code (field initializers, static blocks)?
const nonMethodCode = [];
let inMethod = false;
let depth = 0;

for (let i = 0; i < rsLines.length; i++) {
  // Check if we're at any method start
  const isMethodStart = methods.some(m => m.startLine === i);
  const isMethodEnd = methods.some(m => m.endLine === i);

  if (isMethodStart) inMethod = true;
  if (!inMethod) nonMethodCode.push(rsLines[i]);
  if (isMethodEnd) inMethod = false;
}

const nonMethodText = nonMethodCode.join('\n');
for (const m of methods) {
  const re = new RegExp(`\\b${m.name}\\s*\\(`);
  if (re.test(nonMethodText)) {
    markReachable(m.name);
  }
}

const deadMethods = methods.filter(m => !reachable.has(m.name));
const keepMethods = methods.filter(m => reachable.has(m.name));

const deadLineCount = deadMethods.reduce((s, m) => s + (m.endLine - m.startLine + 1), 0);
const keepLineCount = keepMethods.reduce((s, m) => s + (m.endLine - m.startLine + 1), 0);

console.log(`\nReachable methods: ${keepMethods.length} (${keepLineCount} lines)`);
console.log(`Dead methods: ${deadMethods.length} (${deadLineCount} lines)\n`);

console.log('Dead methods to remove:');
for (const m of deadMethods.sort((a, b) => (b.endLine - b.startLine) - (a.endLine - a.startLine))) {
  console.log(`  :${m.startLine + 1}  ${m.name}() (${m.endLine - m.startLine + 1} lines)`);
}

// ── Remove dead methods ──────────────────────────────────────────────

if (!DRY_RUN) {
  // Build a set of line ranges to remove
  const removeRanges = deadMethods.map(m => [m.startLine, m.endLine]).sort((a, b) => a[0] - b[0]);

  // Also remove blank lines immediately before each dead method (cleanup)
  const removeLines = new Set();
  for (const [start, end] of removeRanges) {
    for (let i = start; i <= end; i++) removeLines.add(i);
    // Remove preceding blank line if any
    if (start > 0 && rsLines[start - 1].trim() === '') removeLines.add(start - 1);
  }

  const newLines = rsLines.filter((_, i) => !removeLines.has(i));

  // Clean up multiple consecutive blank lines
  const cleaned = [];
  let prevBlank = false;
  for (const line of newLines) {
    const isBlank = line.trim() === '';
    if (isBlank && prevBlank) continue;
    cleaned.push(line);
    prevBlank = isBlank;
  }

  writeFileSync(rsPath, cleaned.join('\n'));
  console.log(`\nWrote cleaned RSInterface.java: ${cleaned.length} lines (was ${rsLines.length})`);
  console.log(`Removed ${rsLines.length - cleaned.length} lines`);
} else {
  console.log(`\n[DRY RUN] Would remove ${deadLineCount} lines from RSInterface.java`);
}
