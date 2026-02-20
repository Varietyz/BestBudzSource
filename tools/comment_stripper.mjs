#!/usr/bin/env node
// Java Comment Stripper - state machine parser
// Handles: single-line, multi-line, and javadoc comments
// Preserves: strings, char literals
// Cleans up: blank lines left behind, trailing whitespace

import { readFileSync, writeFileSync, readdirSync, statSync } from 'fs';
import { join, relative } from 'path';

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

/**
 * Strip all comments from Java source using a character-level state machine.
 * States: NORMAL, IN_STRING, IN_CHAR, IN_LINE_COMMENT, IN_BLOCK_COMMENT
 */
function stripComments(src) {
  const out = [];
  let i = 0;
  const len = src.length;

  while (i < len) {
    const c = src[i];
    const next = i + 1 < len ? src[i + 1] : '';

    // ── String literal ──
    if (c === '"') {
      out.push(c);
      i++;
      // Walk to end of string, respecting escapes
      while (i < len) {
        if (src[i] === '\\') {
          out.push(src[i], src[i + 1] || '');
          i += 2;
        } else if (src[i] === '"') {
          out.push(src[i]);
          i++;
          break;
        } else {
          out.push(src[i]);
          i++;
        }
      }
      continue;
    }

    // ── Char literal ──
    if (c === "'") {
      out.push(c);
      i++;
      while (i < len) {
        if (src[i] === '\\') {
          out.push(src[i], src[i + 1] || '');
          i += 2;
        } else if (src[i] === "'") {
          out.push(src[i]);
          i++;
          break;
        } else {
          out.push(src[i]);
          i++;
        }
      }
      continue;
    }

    // ── Line comment ──
    if (c === '/' && next === '/') {
      // Skip to end of line
      i += 2;
      while (i < len && src[i] !== '\n') i++;
      // Don't consume the newline — let it be handled normally
      continue;
    }

    // ── Block comment (includes javadoc) ──
    if (c === '/' && next === '*') {
      i += 2;
      while (i < len) {
        if (src[i] === '*' && i + 1 < len && src[i + 1] === '/') {
          i += 2;
          break;
        }
        i++;
      }
      continue;
    }

    // ── Normal character ──
    out.push(c);
    i++;
  }

  return out.join('');
}

/**
 * Clean up the result: remove trailing whitespace, collapse multiple blank lines,
 * remove blank lines after opening braces and before closing braces.
 */
function cleanupWhitespace(src) {
  let lines = src.split('\n');

  // Remove trailing whitespace from each line
  lines = lines.map(l => l.trimEnd());

  // Collapse 3+ consecutive blank lines to 1
  const cleaned = [];
  let consecutiveBlanks = 0;

  for (const line of lines) {
    if (line === '') {
      consecutiveBlanks++;
      if (consecutiveBlanks <= 1) cleaned.push(line);
    } else {
      consecutiveBlanks = 0;
      cleaned.push(line);
    }
  }

  // Remove leading blank lines at start of file (after package/imports)
  // and trailing blank lines at end
  while (cleaned.length > 0 && cleaned[cleaned.length - 1] === '') cleaned.pop();

  return cleaned.join('\n') + '\n';
}

// ── Main ─────────────────────────────────────────────────────────────

console.time('Total');
const files = walkDir(SRC);
let totalCommentsRemoved = 0;
let totalLinesRemoved = 0;
let filesModified = 0;

for (const f of files) {
  const original = readFileSync(f, 'utf-8');
  const stripped = stripComments(original);
  const cleaned = cleanupWhitespace(stripped);

  const origLines = original.split('\n').length;
  const newLines = cleaned.split('\n').length;
  const diff = origLines - newLines;

  if (diff > 0) {
    filesModified++;
    totalLinesRemoved += diff;

    // Count actual comment tokens removed for stats
    const commentMatches = original.match(/\/\/|\/\*/g);
    const commentCount = commentMatches ? commentMatches.length : 0;
    totalCommentsRemoved += commentCount;

    if (diff >= 5) {
      console.log(`  ${relative(SRC, f)}: -${diff} lines`);
    }

    if (!DRY_RUN) {
      writeFileSync(f, cleaned);
    }
  }
}

console.timeEnd('Total');
console.log(`\n═══ SUMMARY ═══`);
console.log(`  Files modified:  ${filesModified}`);
console.log(`  Lines removed:   ${totalLinesRemoved}`);
if (DRY_RUN) console.log('  (DRY RUN — no files modified)');
