const API_URL = 'http://localhost:8080/api';

async function getGroups() {
    const response = await fetch(`${API_URL}/groups`);
    const groups = await response.json();
    const groupList = document.getElementById('groupList');
    const groupSelect = document.getElementById('groupSelect');
    groupList.innerHTML = '';
    groupSelect.innerHTML = '<option value="">Select Group</option>';

    groups.forEach(group => {
        const li = document.createElement('li');
        li.innerHTML = `
            ${group.groupNumber}
            <button onclick="editGroup(${group.id}, '${group.groupNumber}')" class="edit-btn">Edit</button>
            <button onclick="deleteGroup(${group.id})" class="delete-btn">Delete</button>
            <button onclick="getSchedules(${group.id})" class="schedule-btn">Schedules</button>
        `;
        groupList.appendChild(li);

        const option = document.createElement('option');
        option.value = group.id;
        option.textContent = group.groupNumber;
        groupSelect.appendChild(option);
    });
}

async function addGroup() {
    const groupNumber = document.getElementById('groupNumber').value;
    if (!groupNumber) {
        alert('Enter group number');
        return;
    }

    const response = await fetch(`${API_URL}/groups`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ groupNumber })
    });

    if (response.ok) {
        document.getElementById('groupNumber').value = '';
        getGroups();
    } else {
        alert('Failed to add group');
    }
}

async function editGroup(id, currentNumber) {
    const newNumber = prompt('Enter new group number:', currentNumber);
    if (!newNumber) return;

    const response = await fetch(`${API_URL}/groups/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id, groupNumber: newNumber })
    });

    if (response.ok) {
        getGroups();
    } else {
        alert('Failed to update group');
    }
}

async function deleteGroup(id) {
    if (!confirm('Delete this group?')) return;

    const response = await fetch(`${API_URL}/groups/${id}`, {
        method: 'DELETE'
    });

    if (response.ok) {
        getGroups();
    } else {
        alert('Failed to delete group');
    }
}

async function getSchedules(groupId) {
    const response = await fetch(`${API_URL}/schedules/group/${groupId}`);
    const schedules = await response.json();
    const scheduleList = document.getElementById('scheduleList');
    scheduleList.innerHTML = '<h2>Schedules</h2>';

    if (schedules.length === 0) {
        scheduleList.innerHTML += '<p>No schedules found</p>';
        return;
    }

    schedules.forEach(schedule => {
        const li = document.createElement('li');
        li.innerHTML = `
            ${schedule.subject} (${schedule.lessonType}) at ${schedule.time}, ${schedule.auditorium}
            <button onclick="editSchedule(${schedule.id}, ${schedule.groupId}, '${schedule.subject}', '${schedule.lessonType}', '${schedule.time}', '${schedule.auditorium}')" class="edit-btn">Edit</button>
            <button onclick="deleteSchedule(${schedule.id}, ${schedule.groupId})" class="delete-btn">Delete</button>
        `;
        scheduleList.appendChild(li);
    });
}

async function addSchedule() {
    const groupId = document.getElementById('groupSelect').value;
    const subject = document.getElementById('subject').value;
    const lessonType = document.getElementById('lessonType').value;
    const time = document.getElementById('time').value;
    const auditorium = document.getElementById('auditorium').value;

    if (!groupId || !subject || !lessonType || !time || !auditorium) {
        alert('Fill all schedule fields');
        return;
    }

    const response = await fetch(`${API_URL}/schedules`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ groupId, subject, lessonType, time, auditorium })
    });

    if (response.ok) {
        document.getElementById('subject').value = '';
        document.getElementById('lessonType').value = '';
        document.getElementById('time').value = '';
        document.getElementById('auditorium').value = '';
        getSchedules(groupId);
    } else {
        alert('Failed to add schedule');
    }
}

async function editSchedule(id, groupId, currentSubject, currentLessonType, currentTime, currentAuditorium) {
    const subject = prompt('Enter subject:', currentSubject);
    const lessonType = prompt('Enter lesson type:', currentLessonType);
    const time = prompt('Enter time:', currentTime);
    const auditorium = prompt('Enter auditorium:', currentAuditorium);

    if (!subject || !lessonType || !time || !auditorium) return;

    const response = await fetch(`${API_URL}/schedules/${id}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id, groupId, subject, lessonType, time, auditorium })
    });

    if (response.ok) {
        getSchedules(groupId);
    } else {
        alert('Failed to update schedule');
    }
}

async function deleteSchedule(id, groupId) {
    if (!confirm('Delete this schedule?')) return;

    const response = await fetch(`${API_URL}/schedules/${id}`, {
        method: 'DELETE'
    });

    if (response.ok) {
        getSchedules(groupId);
    } else {
        alert('Failed to delete schedule');
    }
}

async function getCounter() {
    const response = await fetch(`${API_URL}/counter`);
    const count = await response.text();
    document.getElementById('counter').textContent = `Requests: ${count}`;
}

async function resetCounter() {
    const response = await fetch(`${API_URL}/counter/reset`, { method: 'POST' });
    if (response.ok) {
        getCounter();
    } else {
        alert('Failed to reset counter');
    }
}

document.getElementById('addGroupBtn').addEventListener('click', addGroup);
document.getElementById('addScheduleBtn').addEventListener('click', addSchedule);
document.getElementById('getCounterBtn').addEventListener('click', getCounter);
document.getElementById('resetCounterBtn').addEventListener('click', resetCounter);
getGroups();
getCounter();