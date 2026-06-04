const btnClockIn = document.getElementById('btn-clock-in');
const btnClockOut = document.getElementById('btn-clock-out');
const statusText = document.getElementById('status-text');
const recordsList = document.getElementById('records-list');
const currentTimeEl = document.getElementById('current-time');
const currentDateEl = document.getElementById('current-date');

function updateClock() {
  const now = new Date();
  const h = String(now.getHours()).padStart(2, '0');
  const m = String(now.getMinutes()).padStart(2, '0');
  const s = String(now.getSeconds()).padStart(2, '0');
  currentTimeEl.textContent = `${h}:${m}:${s}`;

  const days = ['日', '一', '二', '三', '四', '五', '六'];
  const y = now.getFullYear();
  const mo = String(now.getMonth() + 1).padStart(2, '0');
  const d = String(now.getDate()).padStart(2, '0');
  const w = days[now.getDay()];
  currentDateEl.textContent = `${y}年${mo}月${d}日 星期${w}`;
}

function updateUI(status) {
  statusText.classList.remove('working', 'done');
  switch (status.status) {
    case 'idle':
      statusText.textContent = '尚未打卡';
      statusText.className = '';
      btnClockIn.disabled = false;
      btnClockOut.disabled = true;
      break;
    case 'working':
      statusText.textContent = `已上班 · ${status.clockIn}`;
      statusText.className = 'working';
      btnClockIn.disabled = true;
      btnClockOut.disabled = false;
      break;
    case 'done':
      statusText.textContent = `已完成 · ${status.clockIn} ~ ${status.clockOut}`;
      statusText.className = 'done';
      btnClockIn.disabled = true;
      btnClockOut.disabled = true;
      break;
  }
}

function formatDuration(clockIn, clockOut) {
  const [hi, mi, si] = clockIn.split(':').map(Number);
  const [ho, mo, so] = clockOut.split(':').map(Number);
  const diff = (ho * 3600 + mo * 60 + so) - (hi * 3600 + mi * 60 + si);
  const h = Math.floor(diff / 3600);
  const m = Math.floor((diff % 3600) / 60);
  if (h > 0) return `${h}小时${m}分钟`;
  return `${m}分钟`;
}

function renderRecords(records) {
  recordsList.innerHTML = '';
  if (!records || records.length === 0) {
    recordsList.innerHTML = '<div class="empty-hint">暂无打卡记录</div>';
    return;
  }
  records.forEach(r => {
    const item = document.createElement('div');
    item.className = 'record-item';

    const inTime = document.createElement('span');
    inTime.className = 'record-time';
    inTime.innerHTML = `<span class="record-label">上班</span>${r.clock_in}`;

    const outTime = document.createElement('span');
    outTime.className = 'record-time';
    if (r.clock_out) {
      outTime.innerHTML = `<span class="record-label">下班</span>${r.clock_out}`;
    } else {
      outTime.innerHTML = `<span class="record-label">下班</span>进行中...`;
    }

    const duration = document.createElement('span');
    duration.className = 'record-duration';
    if (r.clock_out) {
      duration.textContent = formatDuration(r.clock_in, r.clock_out);
      duration.classList.remove('active');
    } else {
      duration.textContent = '进行中';
      duration.classList.add('active');
    }

    item.appendChild(inTime);
    item.appendChild(outTime);
    item.appendChild(duration);
    recordsList.appendChild(item);
  });
}

async function refresh() {
  const [status, records] = await Promise.all([
    window.api.getStatus(),
    window.api.getTodayRecords()
  ]);
  updateUI(status);
  renderRecords(records);
}

btnClockIn.addEventListener('click', async () => {
  const res = await window.api.clockIn();
  if (!res.success) {
    alert(res.message);
  }
  await refresh();
});

btnClockOut.addEventListener('click', async () => {
  const res = await window.api.clockOut();
  if (!res.success) {
    alert(res.message);
  }
  await refresh();
});

setInterval(updateClock, 500);
updateClock();
refresh();
