const initSqlJs = require('sql.js');
const fs = require('fs');
const path = require('path');

let db = null;
let dbPath = '';

async function initDatabase(userDataPath) {
  const SQL = await initSqlJs();
  dbPath = path.join(userDataPath, 'punch-clock.db');

  if (fs.existsSync(dbPath)) {
    const buffer = fs.readFileSync(dbPath);
    db = new SQL.Database(buffer);
  } else {
    db = new SQL.Database();
  }

  db.run('PRAGMA journal_mode = WAL');

  db.run(`CREATE TABLE IF NOT EXISTS records (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    date TEXT NOT NULL,
    clock_in TEXT NOT NULL,
    clock_out TEXT,
    UNIQUE(date)
  )`);

  saveDb();
  return db;
}

function saveDb() {
  if (db && dbPath) {
    const data = db.export();
    fs.writeFileSync(dbPath, Buffer.from(data));
  }
}

function closeDatabase() {
  if (db) {
    db.close();
    db = null;
  }
}

// ----- helper: prepare → bind → step → getAsObject / free -----
function getOne(sql, params = []) {
  const stmt = db.prepare(sql);
  stmt.bind(params);
  let result = null;
  if (stmt.step()) {
    result = stmt.getAsObject();
  }
  stmt.free();
  return result;
}

function getAll(sql, params = []) {
  const stmt = db.prepare(sql);
  stmt.bind(params);
  const results = [];
  while (stmt.step()) {
    results.push(stmt.getAsObject());
  }
  stmt.free();
  return results;
}

function run(sql, params = []) {
  db.run(sql, params);
  const rowid = getOne('SELECT last_insert_rowid() AS id');
  saveDb();
  return { lastInsertRowid: rowid ? rowid.id : null };
}

// ----- business logic -----
function clockIn() {
  const now = new Date();
  const date = formatDate(now);
  const time = formatTime(now);

  const existing = getOne('SELECT * FROM records WHERE date = ?', [date]);
  if (existing) {
    return { success: false, message: '今天已经打过上班卡了' };
  }

  const info = run('INSERT INTO records (date, clock_in) VALUES (?, ?)', [date, time]);
  return { success: true, id: info.lastInsertRowid };
}

function clockOut() {
  const now = new Date();
  const date = formatDate(now);
  const time = formatTime(now);

  const record = getOne('SELECT * FROM records WHERE date = ?', [date]);
  if (!record) {
    return { success: false, message: '还没有打上班卡' };
  }
  if (record.clock_out) {
    return { success: false, message: '今天已经打过下班卡了' };
  }

  run('UPDATE records SET clock_out = ? WHERE id = ?', [time, record.id]);
  return { success: true };
}

function getTodayRecords() {
  const date = formatDate(new Date());
  return getAll('SELECT * FROM records WHERE date = ? ORDER BY id DESC', [date]);
}

function getCurrentStatus() {
  const date = formatDate(new Date());
  const record = getOne('SELECT * FROM records WHERE date = ?', [date]);

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
