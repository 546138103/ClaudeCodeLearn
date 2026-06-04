const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('api', {
  clockIn: () => ipcRenderer.invoke('clock-in'),
  clockOut: () => ipcRenderer.invoke('clock-out'),
  getTodayRecords: () => ipcRenderer.invoke('get-today-records'),
  getStatus: () => ipcRenderer.invoke('get-status')
});
