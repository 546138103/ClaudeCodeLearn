const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('path');
const { initDatabase, closeDatabase, clockIn, clockOut, getTodayRecords, getCurrentStatus } = require('./database');

let mainWindow = null;

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 480,
    height: 620,
    resizable: false,
    title: '打卡',
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false
    }
  });

  mainWindow.setMenuBarVisibility(false);
  mainWindow.loadFile(path.join(__dirname, 'renderer', 'index.html'));
}

app.whenReady().then(async () => {
  await initDatabase(app.getPath('userData'));
  createWindow();

  ipcMain.handle('clock-in', () => clockIn());
  ipcMain.handle('clock-out', () => clockOut());
  ipcMain.handle('get-today-records', () => getTodayRecords());
  ipcMain.handle('get-status', () => getCurrentStatus());

  app.on('activate', () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('window-all-closed', () => {
  closeDatabase();
  if (process.platform !== 'darwin') app.quit();
});
