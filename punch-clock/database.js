const Database = require('better-sqlite3');
const path = require('path');

let db = null;

function initDatabase(dbPath) {
  const dbFile = path.join(dbPath, 'punch-clock.db');
  db = new Database(dbFile);

  db.pragma('journal_mode = WAL');

  db.exec(`
    CREATE TABLE IF NOT EXISTS records (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      date TEXT NOT NULL,
      clock_in TEXT NOT NULL,
      clock_out TEXT,
      UNIQUE(date)
    )
  `);

  return db;
}

function closeDatabase() {
  if (db) {
    db.close();
    db = null;
  }
}

function clockIn() {
  const now = new Date();
  const date = formatDate(now);
  const time = formatTime(now);

  const existing = db.prepare('SELECT * FROM records WHERE date = ?').get(date);
  if (existing) {
    return { success: false, message: '今天已经打过上班卡了' };
  }

  const info = db.prepare('INSERT INTO records (date, clock_in) VALUES (?, ?)').run(date, time);
  return { success: true, id: info.lastInsertRowid };
}

function clockOut() {
  const now = new Date();
  const date = formatDate(now);
  const time = formatTime(now);

  const record = db.prepare('SELECT * FROM records WHERE date = ?').get(date);
  if (!record) {
    return { success: false, message: '还没有打上班卡' };
  }
  if (record.clock_out) {
    return { success: false, message: '今天已经打过下班卡了' };
  }

  db.prepare('UPDATE records SET clock_out = ? WHERE id = ?').run(time, record.id);
  return { success: true };
}

function getTodayRecords() {
  const date = formatDate(new Date());
  return db.prepare('SELECT * FROM records WHERE date = ? ORDER BY id DESC').all(date);
}

function getCurrentStatus() {
  const date = formatDate(new Date());
  const record = db.prepare('SELECT * FROM records WHERE date = ?').get(date);

  if (!record) {
    return { status: 'idle', label: '尚未打卡' };
  }
  if (!record.clock_out) {
    return { status: 'working', label: '已上班', clockIn: record.clock_in };
  }
  return { status: 'done', label: '已完成', clockIn: record.clock_in, clockOut: record.clock_out };
}

function formatDate(date) {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

function formatTime(date) {
  const h = String(date.getHours()).padStart(2, '0');
  const m = String(date.getMinutes()).padStart(2, '0');
  const s = String(date.getSeconds()).padStart(2, '0');
  return `${h}:${m}:${s}`;
}

module.exports = { initDatabase, closeDatabase, clockIn, clockOut, getTodayRecords, getCurrentStatus };
